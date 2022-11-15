package com.apollo29.calendarwatch.repository

import android.content.Context
import android.util.Base64
import java.util.*

class Preferences(context: Context) {

    private val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun wasFirstShow() {
        pref.edit().putBoolean(PREF_NAME_FIRST_SHOW, false).apply()
    }

    fun firstShow(): Boolean {
        return pref.getBoolean(PREF_NAME_FIRST_SHOW, true)
    }

    fun watchId(watchId: String?) {
        pref.edit().putString(PREF_NAME_CONNECTED_WATCH_ID, watchId).apply()
    }

    fun watchId(): String? {
        return pref.getString(PREF_NAME_CONNECTED_WATCH_ID, null)
    }

    fun pairingMode(enable: Boolean) {
        pref.edit()
            .putBoolean(PREF_NAME_PAIRING_MODE, enable)
            .apply()
    }

    fun pairingMode(): Boolean {
        return pref.getBoolean(
            PREF_NAME_PAIRING_MODE,
            false
        )
    }

    fun fixedMode(value: Int) {
        pref.edit()
            .putInt(PREF_NAME_FIXED_MODE_VALUE, value)
            .apply()
    }

    fun fixedMode(): Int {
        return pref.getInt(
            PREF_NAME_FIXED_MODE_VALUE,
            0
        )
    }

    fun allDayEventsInfo(enable: Boolean) {
        pref.edit()
            .putBoolean(PREF_NAME_ALLDAY_EVENTS, enable)
            .apply()
    }

    fun allDayEventsInfo(): Boolean {
        return pref.getBoolean(
            PREF_NAME_ALLDAY_EVENTS,
            false
        )
    }

    fun vibrateSwitch(enable: Boolean) {
        pref.edit()
            .putBoolean(PREF_NAME_VIBRATE_ALERT, enable)
            .apply()
    }

    fun vibrateSwitch(): Boolean {
        return pref.getBoolean(
            PREF_NAME_VIBRATE_ALERT,
            false
        )
    }

    fun flightModeSwitch(enable: Boolean) {
        pref.edit()
            .putBoolean(PREF_NAME_FLIGHT_MODE, enable)
            .apply()
    }

    fun flightModeSwitch(): Boolean {
        return pref.getBoolean(
            PREF_NAME_FLIGHT_MODE,
            false
        )
    }

    fun switchModeSwitch(enable: Boolean) {
        pref.edit()
            .putBoolean(PREF_NAME_SWITCH_MODE, enable)
            .apply()
    }

    fun switchModeSwitch(): Boolean {
        return pref.getBoolean(
            PREF_NAME_SWITCH_MODE,
            false
        )
    }

    fun fixedModeValue(value: Int) {
        pref.edit()
            .putInt(PREF_NAME_FIXED_MODE_VALUE, value)
            .apply()
    }

    fun fixedModeValue(): Int {
        val now = Calendar.getInstance()
        return pref.getInt(
            PREF_NAME_FIXED_MODE_VALUE,
            now.get(11)
        )
    }

    fun pattern(allDaysPattern: CharArray) {
        val pattern = String(allDaysPattern)
        pref.edit()
            .putString(PREF_NAME_ALL_DAY_PATTERN, pattern)
            .apply()
    }

    fun pattern(): CharArray {
        val pattern: String? = pref.getString(
            PREF_NAME_ALL_DAY_PATTERN,
            null
        )
        return pattern?.toCharArray() ?: CharArray(288)
    }

    fun alerts(mAlerts: ArrayList<Byte>) {
        val array = ByteArray(mAlerts.size)
        for (i in mAlerts.indices) {
            array[i] = mAlerts[i]
        }
        val alerts = Base64.encodeToString(array, 0)
        pref.edit()
            .putString(PREF_NAME_ALERTS_PATTERN, alerts)
            .apply()
    }

    fun alerts(): ArrayList<Byte> {
        val alerts: String = pref.getString(
            PREF_NAME_ALERTS_PATTERN,
            null
        ) ?: return ArrayList()

        val mAlerts = ArrayList<Byte>()
        val array = Base64.decode(alerts, 0)
        for (b in array) {
            mAlerts.add(java.lang.Byte.valueOf(b))
        }
        return mAlerts
    }

    fun calendarEnabled(id: String, enabled: Boolean) {
        pref.edit().putBoolean(
            PREF_NAME_CALENDAR_ENABLED + id,
            enabled
        ).apply()
    }

    fun calendarEnabled(id: String): Boolean {
        return pref.getBoolean(
            PREF_NAME_CALENDAR_ENABLED + id,
            true
        )
    }

    companion object {
        private const val PREF = "calendar_watch_preferences"
        private const val PREF_NAME_ALERTS_PATTERN = "alerts_pattern"
        private const val PREF_NAME_ALLDAY_EVENTS = "allday_events"
        private const val PREF_NAME_ALL_DAY_PATTERN = "all_day_pattern"
        private const val PREF_NAME_CALENDAR_ENABLED = "calendar_enabled_"
        private const val PREF_NAME_CONNECTED_WATCH_ID = "connected_watch_id"
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