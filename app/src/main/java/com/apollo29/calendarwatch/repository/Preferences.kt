package com.apollo29.calendarwatch.repository

import android.content.Context

class Preferences(context: Context) {

    private val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    companion object {
        private const val PREF = "calendar_watch_preferences"
        private const val PREF_NAME_ALERTS_PATTERN = "alerts_pattern"
        private const val PREF_NAME_ALLDAY_EVENTS = "allday_events"
        private const val PREF_NAME_ALL_DAY_PATTERN = "all_day_pattern"
        private const val PREF_NAME_BATTERY_CHARGING_STATUS = "battery_charging_status"
        private const val PREF_NAME_BATTERY_LEVEL = "battery_level"
        private const val PREF_NAME_CALENDAR_ENABLED = "calendar_enabled_"
        private const val PREF_NAME_CONNECTED_WATCH_ID = "connected_watch_id"
        private const val PREF_NAME_FIRMWARE_URL = "firmware_url"
        private const val PREF_NAME_FIRMWARE_VERSION = "firmware_version"
        private const val PREF_NAME_FIRST_SHOW = "first_show"
        private const val PREF_NAME_FIXED_MODE_VALUE = "fixed_mode_value"
        private const val PREF_NAME_FLIGHT_MODE = "flight_mode"
        private const val PREF_NAME_PAIRING_MODE = "pairing_mode"
        private const val PREF_NAME_SWITCH_MODE = "switch_mode"
        private const val PREF_NAME_TEMP_WATCH_ID = "temp_watch_id"
        private const val PREF_NAME_UUID = "uuid"
        private const val PREF_NAME_VIBRATE_ALERT = "vibrate_alert"
    }
}