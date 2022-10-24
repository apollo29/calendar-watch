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

    private var batteryLevelCharacteristic: BluetoothGattCharacteristic? = null
    private var firmwareVersionCharacteristic: BluetoothGattCharacteristic? = null

    override fun log(priority: Int, message: String) {
        if (BuildConfig.DEBUG || priority == Log.ERROR) {
            Logger.log(priority, "WCWM", message, null)
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return WhatCalendarWatchManagerGattCallback()
    }

    /**
     * The Button callback will be notified when a notification from Button characteristic
     * has been received, or its data was read.
     *
     *
     * If the data received are valid (single byte equal to 0x00 or 0x01), the
     * [BlinkyButtonDataCallback.onButtonStateChanged] will be called.
     * Otherwise, the [BlinkyButtonDataCallback.onInvalidDataReceived]
     * will be called with the data received.
     */
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
            }
            val supported = batteryLevelCharacteristic != null
            return supported
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
    }
}