package com.apollo29.calendarwatch.ui.main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollo29.calendarwatch.ble.GattService
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import com.apollo29.calendarwatch.model.PairingStatus
import com.apollo29.calendarwatch.repository.Preferences
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val manager: WhatCalendarWatchManager
) :
    ViewModel() {
    private val preferences = Preferences(context)
    private val scanner = BluetoothLeScannerCompat.getScanner()

    val pairing = MutableLiveData(PairingStatus.NONE)

    fun batteryLevel() = manager.batteryLevel

    fun watchId() = preferences.watchId()

    fun connect(device: BluetoothDevice) {
        manager.connectWatch(device)
    }

    fun isFirstShow(): Boolean {
        return preferences.firstShow()
    }

    fun wasFirstShow() {
        preferences.wasFirstShow()
    }

    fun watchConnected(): Boolean {
        return manager.isWatchConnected()
    }

    fun reconnect() {
        Logger.d("reconnect: start scanning")
        pairing.postValue(PairingStatus.IDLE)
        scanner.startScan(reconnectScanCallback)
    }

    private fun stopScanning(status: PairingStatus) {
        Logger.d("reconnect: stopping scanning")
        pairing.postValue(status)
        scanner.stopScan(reconnectScanCallback)
    }

    // Device scan callback.
    @SuppressLint("MissingPermission")
    private val reconnectScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val watchId = preferences.watchId()
            if (watchId != null) {
                if (!result.device.name.isNullOrEmpty() && result.device.name.lowercase() == watchId.lowercase()) {
                    Logger.d("Device Name ${result.device.name} rssi: ${result.rssi}")
                    Logger.d("RSSI %s", result.rssi > GattService.RSSI_VALUE)
                    connect(result.device)

                    stopScanning(PairingStatus.SUCCESS)
                }
            } else {
                Logger.d("No Watch Id")

                stopScanning(PairingStatus.NONE)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Logger.d("Scan Failed: $errorCode")

            stopScanning(PairingStatus.FAILURE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning(PairingStatus.NONE)
    }
}