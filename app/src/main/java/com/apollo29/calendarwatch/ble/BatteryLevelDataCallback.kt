package com.apollo29.calendarwatch.ble

import android.bluetooth.BluetoothDevice
import com.apollo29.calendarwatch.model.BatteryInfo
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data


abstract class BatteryLevelDataCallback : ProfileDataCallback, BatteryLevelCallback {

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.value != null) {
            var b: Byte = 0
            val b2: Byte = data.value!![0]
            if (data.value!!.size == 2) {
                b = data.value!![1]
            } else {
                onInvalidDataReceived(device, data)
            }
            val bi = BatteryInfo(b2.toInt(), b.toInt())
            onStateChanged(device, bi)
        } else {
            onInvalidDataReceived(device, data)
        }
    }
}