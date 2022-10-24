package com.apollo29.calendarwatch.ble

import android.bluetooth.BluetoothDevice

interface DeviceAPI {
    /**
     * Change the value of the GATT characteristic that we're publishing
     */
    fun setMyCharacteristicValue(value: String)

    fun connect(device: BluetoothDevice)
}