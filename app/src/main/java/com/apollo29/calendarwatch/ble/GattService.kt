package com.apollo29.calendarwatch.ble

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.apollo29.calendarwatch.BuildConfig
import com.apollo29.calendarwatch.R
import com.orhanobut.logger.Logger
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.observer.ServerObserver
import java.nio.charset.StandardCharsets
import java.util.*

class GattService : Service() {

    private var serverManager: ServerManager? = null
    private lateinit var bluetoothObserver: BroadcastReceiver
    private var bleAdvertiseCallback: BleAdvertiser.Callback? = null

    override fun onCreate() {
        super.onCreate()

        // Setup as a foreground service

        val notificationChannel = NotificationChannel(
            GattService::class.java.simpleName,
            resources.getString(R.string.gatt_service_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationService =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationService.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, GattService::class.java.simpleName)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(resources.getString(R.string.gatt_service_name))
            .setContentText(resources.getString(R.string.gatt_service_running_notification))
            .setAutoCancel(true)

        startForeground(1, notification.build())

        // Observe OS state changes in BLE

        bluetoothObserver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val bluetoothState = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            -1
                        )
                        when (bluetoothState) {
                            BluetoothAdapter.STATE_ON -> enableBleServices()
                            BluetoothAdapter.STATE_OFF -> disableBleServices()
                        }
                    }
                }
            }
        }
        registerReceiver(bluetoothObserver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        // Startup BLE if we have it
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager.adapter?.isEnabled == true) enableBleServices()
    }

    override fun onBind(intent: Intent?): IBinder {
        return DataPlane()
    }

    fun connect(device: BluetoothDevice) {
        serverManager?.onDeviceConnectedToServer(device)
    }

    @SuppressLint("MissingPermission")
    private fun enableBleServices() {
        serverManager = ServerManager(this)
        serverManager!!.open()

        bleAdvertiseCallback = BleAdvertiser.Callback()

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter.bluetoothLeAdvertiser?.startAdvertising(
            BleAdvertiser.settings(),
            BleAdvertiser.advertiseData(),
            bleAdvertiseCallback!!
        )
    }

    @SuppressLint("MissingPermission")
    private fun disableBleServices() {
        bleAdvertiseCallback?.let {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter.bluetoothLeAdvertiser?.stopAdvertising(it)
            bleAdvertiseCallback = null
        }

        serverManager?.close()
        serverManager = null
    }

    /**
     * Functionality available to clients
     */
    private inner class DataPlane : Binder(), DeviceAPI {

        override fun setMyCharacteristicValue(value: String) {
            serverManager?.setMyCharacteristicValue(value)
        }

        @SuppressLint("MissingPermission")
        override fun connect(device: BluetoothDevice) {
            Logger.d("connect ${device.name}")
            serverManager?.connect(device)
        }
    }

    /*
     * Manages the entire GATT service, declaring the services and characteristics on offer
     */
    private class ServerManager(val context: Context) : BleServerManager(context), ServerObserver,
        DeviceAPI {

        private val batteryLevel = sharedCharacteristic(
            // UUID:
            UUID_CHARACTERISTIC_BATTERY_LEVEL,
            // Properties:
            BluetoothGattCharacteristic.PROPERTY_READ
                    or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            // Permissions:
            BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM,
            // Descriptors:
            // cccd() - this could have been used called, had no encryption been used.
            // Instead, let's define CCCD with custom permissions:
            descriptor(
                UUID_DESCRIPTOR,
                BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM
                        or BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM,
                byteArrayOf(0, 0)
            ),
            description("A characteristic to be read", false) // descriptors
        )
        private val myGattService = service(
            // UUID:
            UUID_SERVICE,
            // Characteristics (just one in this case):
            batteryLevel
        )

        private val myGattServices = Collections.singletonList(myGattService)

        private val serverConnections = mutableMapOf<String, ServerConnection>()

        override fun setMyCharacteristicValue(value: String) {
            val bytes = value.toByteArray(StandardCharsets.UTF_8)
            batteryLevel.value = bytes
            serverConnections.values.forEach { serverConnection ->
                serverConnection.sendNotificationForMyGattCharacteristic(bytes)
            }
        }

        override fun connect(device: BluetoothDevice) = onDeviceConnectedToServer(device)

        override fun log(priority: Int, message: String) {
            if (BuildConfig.DEBUG || priority == Log.ERROR) {
                Logger.log(priority, "Service", message, null)
            }
        }

        override fun initializeServer(): List<BluetoothGattService> {
            setServerObserver(this)

            return myGattServices
        }

        override fun onServerReady() {
            log(Log.INFO, "Gatt server ready")
        }

        override fun onDeviceConnectedToServer(device: BluetoothDevice) {
            log(Log.DEBUG, "Device connected ${device.address}")

            // A new device connected to the phone. Connect back to it, so it could be used
            // both as server and client. Even if client mode will not be used, currently this is
            // required for the server-only use.
            serverConnections[device.address] = ServerConnection().apply {
                useServer(this@ServerManager)
                connect(device).enqueue()
            }
        }

        override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
            log(Log.DEBUG, "Device disconnected ${device.address}")

            // The device has disconnected. Forget it and close.
            serverConnections.remove(device.address)?.close()
        }

        /*
         * Manages the state of an individual server connection (there can be many of these)
         */
        inner class ServerConnection : BleManager(context) {

            private var gattCallback: GattCallback? = null

            fun sendNotificationForMyGattCharacteristic(value: ByteArray) {
                sendNotification(batteryLevel, value).enqueue()
            }

            override fun log(priority: Int, message: String) {
                this@ServerManager.log(priority, message)
            }

            override fun getGattCallback(): BleManagerGattCallback {
                gattCallback = GattCallback()
                return gattCallback!!
            }

            private inner class GattCallback : BleManagerGattCallback() {

                // There are no services that we need from the connecting device, but
                // if there were, we could specify them here.
                override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                    return true
                }

                override fun onServicesInvalidated() {
                    // This is the place to nullify characteristics obtained above.
                }
            }
        }
    }

    companion object {
        const val RSSI_VALUE = -70

        val UUID_DEVICE_INFORMATION_SERVICE =
            UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB")
        val UUID_CHARACTERISTIC_FIRMWARE =
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
        val UUID_SERVICE =
            UUID.fromString("67E40001-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_UPDATE_TIME =
            UUID.fromString("67E40002-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_PATTERN_CURRENT_DAY =
            UUID.fromString("67E40003-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_PATTERN_TOMORROW =
            UUID.fromString("67E40004-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_PATTERN_DAY_AFTER_TOMORROW =
            UUID.fromString("67E40005-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_CLEAR =
            UUID.fromString("67E40006-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_ALERTS =
            UUID.fromString("67E40007-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_SWITCH_MODE =
            UUID.fromString("67E40008-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_CALIBRATE =
            UUID.fromString("67E40009-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_ACKNOWLEDGMENT =
            UUID.fromString("67E4000A-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_UPDATE_REQUEST =
            UUID.fromString("67E4000C-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_BATTERY_LEVEL =
            UUID.fromString("67E4000D-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_TOTAL_ALERTS_COUNT =
            UUID.fromString("67E4000E-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_REFRESH =
            UUID.fromString("67E4000F-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_AIRPLANE_MODE =
            UUID.fromString("67E40010-5C68-D803-BF31-F83F2B6585FA")
        val UUID_DESCRIPTOR =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}