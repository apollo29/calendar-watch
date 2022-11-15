package com.apollo29.calendarwatch.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeSettingsReceiver : BroadcastReceiver() {
    @Inject
    lateinit var manager: WhatCalendarWatchManager

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d(
            "onReceive time settings changes. Action: " + intent.action
        )
        manager.eventsChangedCallback.onTimeChanged()
    }
}