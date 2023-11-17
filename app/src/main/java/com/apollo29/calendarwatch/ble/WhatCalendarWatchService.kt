package com.apollo29.calendarwatch.ble

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WhatCalendarWatchService : Service() {

    @Inject
    lateinit var manager: WhatCalendarWatchManager

    override fun onBind(intent: Intent): IBinder {
        Logger.d("bind service")
        return WatchService()
    }

    override fun onCreate() {
        super.onCreate()

        if (!manager.isBluetoothEnabled()) {
            //Thread(this.mReconnectTask).start()
            Logger.d("reconnect")
        } else {
            Logger.d("start scanning")
        }
    }

    private inner class WatchService : Binder() {
        fun manager(): WhatCalendarWatchManager {
            return manager
        }
    }
}