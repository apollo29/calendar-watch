package com.apollo29.calendarwatch.ble

import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.os.ParcelUuid
import com.apollo29.calendarwatch.ble.GattService.Companion.UUID_SERVICE
import com.orhanobut.logger.Logger

object BleAdvertiser {

    class Callback : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Logger.i("LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Logger.w("LE Advertise Failed: $errorCode")
        }
    }

    fun settings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .build()
    }

    fun advertiseData(): AdvertiseData {
        return AdvertiseData.Builder()
            .setIncludeDeviceName(false) // Including it will blow the length
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(ParcelUuid(UUID_SERVICE))
            .build()
    }
}