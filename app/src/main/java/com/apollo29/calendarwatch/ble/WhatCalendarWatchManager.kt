package com.apollo29.calendarwatch.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollo29.calendarwatch.BuildConfig
import com.apollo29.calendarwatch.model.BatteryInfo
import com.orhanobut.logger.Logger
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.livedata.ObservableBleManager
import java.util.*


class WhatCalendarWatchManager(context: Context) : ObservableBleManager(context) {

    val batteryLevel = MutableLiveData<BatteryInfo>()
    val calibrateState = MutableLiveData<Boolean>()

    private var batteryLevelCharacteristic: BluetoothGattCharacteristic? = null
    private var calibrateCharacteristic: BluetoothGattCharacteristic? = null
    private var updateTimeCharacteristic: BluetoothGattCharacteristic? = null

    override fun log(priority: Int, message: String) {
        if (BuildConfig.DEBUG || priority == Log.ERROR) {
            Logger.log(priority, "WCWM", message, null)
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return WhatCalendarWatchManagerGattCallback()
    }

    private val batteryLevelCallback: BatteryLevelDataCallback =
        object : BatteryLevelDataCallback() {
            override fun onInvalidDataReceived(
                device: BluetoothDevice,
                data: Data
            ) {
                log(Log.WARN, "Invalid data received: $data")
            }

            override fun onStateChanged(device: BluetoothDevice, batteryInfo: BatteryInfo) {
                log(Log.INFO, "data received: $batteryInfo")
                batteryLevel.value = batteryInfo
            }
        }

    fun calibrateStart() {
        Logger.d("calibrateStart")
        calibrateState.postValue(true)
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(1),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun calibrateCancel() {
        Logger.d("calibrateCancel")
        calibrateState.postValue(false)
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(0),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun calibrateWatch(hour: Int, minute: Int, minuteA: Int, second: Int, secondA: Int) {
        Logger.d("calibrateWatch")
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(
                hour.toByte(),
                minute.toByte(),
                minuteA.toByte(),
                second.toByte(),
                secondA.toByte()
            ),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()

        updateTime()
    }

    private fun updateTime() {
        Logger.d("updateTime")
        val now = Calendar.getInstance()
        val value = byteArrayOf(
            (now[1] % 100).toByte(),
            (now[2] + 1).toByte(),
            now[5].toByte(),
            now[11].toByte(),
            now[12].toByte(),
            now[13].toByte(),
            0,
            0
        )
        writeCharacteristic(
            updateTimeCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    /**
     * BluetoothGatt callbacks object.
     */
    private inner class WhatCalendarWatchManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            setNotificationCallback(batteryLevelCharacteristic).with(batteryLevelCallback)
            readCharacteristic(batteryLevelCharacteristic).with(batteryLevelCallback).enqueue()
            enableNotifications(batteryLevelCharacteristic).enqueue()
        }

        public override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            // default service
            val service = gatt.getService(UUID_SERVICE)
            if (service != null) {
                batteryLevelCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_BATTERY_LEVEL)
                calibrateCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_CALIBRATE)
                updateTimeCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_UPDATE_TIME)
            }
            return batteryLevelCharacteristic != null &&
                    calibrateCharacteristic != null &&
                    updateTimeCharacteristic != null
        }

        override fun onServicesInvalidated() {
            batteryLevelCharacteristic = null
        }
    }

    companion object {
        // DEFAULT SERVICE
        val UUID_SERVICE =
            UUID.fromString("67E40001-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_BATTERY_LEVEL =
            UUID.fromString("67E4000D-5C68-D803-BF31-F83F2B6585FA")

        val UUID_CHARACTERISTIC_CALIBRATE =
            UUID.fromString("67E40009-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_UPDATE_TIME =
            UUID.fromString("67E40002-5C68-D803-BF31-F83F2B6585FA")
    }
}