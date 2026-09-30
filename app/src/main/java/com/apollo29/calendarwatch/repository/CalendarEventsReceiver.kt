package com.apollo29.calendarwatch.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apollo29.calendarwatch.sync.WatchSync
import com.orhanobut.logger.Logger

class CalendarEventsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d(
            "onReceive Calendar events changes. Action: " + intent.action
        )
        WatchSync.syncNow(context)
    }
}
