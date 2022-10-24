package com.apollo29.calendarwatch.ble

import android.bluetooth.BluetoothDevice
import com.apollo29.calendarwatch.model.BatteryInfo


interface BatteryLevelCallback {
    /**
     * Called when a button was pressed or released on device.
     *
     * @param device the target device.
     * @param batteryInfo battery information.
     */
    fun onStateChanged(device: BluetoothDevice, batteryInfo: BatteryInfo)
}