package com.apollo29.calendarwatch

import android.app.Application
import com.apollo29.calendarwatch.sync.WatchSync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalendarWatchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // keep the watch up to date while the app is closed (no-op until a watch is paired)
        WatchSync.schedule(this)
    }
}
