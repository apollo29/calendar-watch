package com.apollo29.calendarwatch.ui.settings

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import com.apollo29.calendarwatch.repository.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import no.nordicsemi.android.ble.ConnectRequest
import javax.inject.Inject

@HiltViewModel
class SettingsDialogViewModel @Inject constructor(@ApplicationContext context: Context) :
    ViewModel() {
    val manager = WhatCalendarWatchManager(context)
    val preferences = Preferences(context)

    private var device: BluetoothDevice? = null
    private var connectRequest: ConnectRequest? = null

    val switchModeValue = MutableLiveData(preferences.switchModeSwitch())

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