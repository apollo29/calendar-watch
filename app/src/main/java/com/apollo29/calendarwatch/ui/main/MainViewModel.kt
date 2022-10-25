package com.apollo29.calendarwatch.ui.main

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import no.nordicsemi.android.ble.ConnectRequest


class MainViewModel(application: Application) : AndroidViewModel(application) {
    val manager = WhatCalendarWatchManager(application)

    private var device: BluetoothDevice? = null
    private var connectRequest: ConnectRequest? = null

    fun batteryLevel() = manager.batteryLevel

    fun connect(device: BluetoothDevice) {
        // Prevent from calling again when called again (screen orientation changed).
        if (this.device == null) {
            this.device = device
            reconnect()
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    fun reconnect() {
        if (device != null) {
            connectRequest = manager.connect(device!!)
                .retry(3, 100)
                .useAutoConnect(false)
                .then { connectRequest = null }
            connectRequest!!.enqueue()
        }
    }
}