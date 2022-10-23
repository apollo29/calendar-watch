package com.whatcalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.whatcalendar.entity.BatteryInfo;
import com.whatcalendar.firmware.GWatchResponse;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GlobalPreferences {
    private static final String PREF_NAME_ALERTS_PATTERN = "alerts_pattern";
    private static final String PREF_NAME_ALLDAY_EVENTS = "allday_events";
    private static final String PREF_NAME_ALL_DAY_PATTERN = "all_day_pattern";
    private static final String PREF_NAME_BATTERY_CHARGING_STATUS = "battery_charging_status";
    private static final String PREF_NAME_BATTERY_LEVEL = "battery_level";
    private static final String PREF_NAME_CALENDAR_ENABLED = "calendar_enabled_";
    private static final String PREF_NAME_CONNECTED_WATCH_ID = "connected_watch_id";
    private static final String PREF_NAME_FIRMWARE_URL = "firmware_url";
    private static final String PREF_NAME_FIRMWARE_VERSION = "firmware_version";
    private static final String PREF_NAME_FIRST_SHOW = "first_show";
    private static final String PREF_NAME_FIXED_MODE_VALUE = "fixed_mode_value";
    private static final String PREF_NAME_FLIGHT_MODE = "flight_mode";
    private static final String PREF_NAME_PAIRING_MODE = "pairing_mode";
    private static final String PREF_NAME_SWITCH_MODE = "switch_mode";
    private static final String PREF_NAME_TEMP_WATCH_ID = "temp_watch_id";
    private static final String PREF_NAME_UUID = "uuid";
    private static final String PREF_NAME_VIBRATE_ALERT = "vibrate_alert";
    private static SharedPreferences sSharedPreferences;

    public static void initialize(Context context) {
        sSharedPreferences = context.getSharedPreferences("global_preferences", 0);
    }

    public static void putGWatchResponse(GWatchResponse response) {
        if (response == null) {
            sSharedPreferences.edit().putString(PREF_NAME_FIRMWARE_VERSION, null).putString(PREF_NAME_FIRMWARE_URL, null).apply();
        } else {
            sSharedPreferences.edit().putString(PREF_NAME_FIRMWARE_VERSION, response.fw_ver).putString(PREF_NAME_FIRMWARE_URL, response.fw_url).apply();
        }
    }

    public static GWatchResponse getGWatchResponse() {
        return new GWatchResponse(sSharedPreferences.getString(PREF_NAME_FIRMWARE_VERSION, null), sSharedPreferences.getString(PREF_NAME_FIRMWARE_URL, null));
    }

    public static void putUuid(String uuid) {
        sSharedPreferences.edit().putString(PREF_NAME_UUID, uuid).apply();
    }

    public static String getUuid() {
        return sSharedPreferences.getString(PREF_NAME_UUID, null);
    }

    public static void putConnectedWatchId(String watchId) {
        sSharedPreferences.edit().putString(PREF_NAME_CONNECTED_WATCH_ID, watchId).apply();
    }

    public static String getConnectedWatchId() {
        return sSharedPreferences.getString(PREF_NAME_CONNECTED_WATCH_ID, null);
    }

    public static void putTempWatchId(String watchId) {
        sSharedPreferences.edit().putString(PREF_NAME_TEMP_WATCH_ID, watchId).apply();
    }

    public static String getTempWatchId() {
        return sSharedPreferences.getString(PREF_NAME_TEMP_WATCH_ID, null);
    }

    public static void putBatteryInfo(BatteryInfo bi) {
        sSharedPreferences.edit().putInt(PREF_NAME_BATTERY_CHARGING_STATUS, bi.getChargingStatus()).putInt(PREF_NAME_BATTERY_LEVEL, bi.getLevel()).apply();
    }

    public static BatteryInfo getBatteryInfo() {
        return new BatteryInfo(sSharedPreferences.getInt(PREF_NAME_BATTERY_CHARGING_STATUS, 2), sSharedPreferences.getInt(PREF_NAME_BATTERY_LEVEL, 100));
    }

    public static void setAllDayEventsInfo(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_ALLDAY_EVENTS, enable).apply();
    }

    public static boolean getAllDayEventsInfo() {
        return sSharedPreferences.getBoolean(PREF_NAME_ALLDAY_EVENTS, false);
    }

    public static void setVibrateSwitch(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_VIBRATE_ALERT, enable).apply();
    }

    public static boolean getVibrateSwitch() {
        return sSharedPreferences.getBoolean(PREF_NAME_VIBRATE_ALERT, false);
    }

    public static void setFlightModeSwitch(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_FLIGHT_MODE, enable).apply();
    }

    public static boolean getFlightModeSwitch() {
        return sSharedPreferences.getBoolean(PREF_NAME_FLIGHT_MODE, false);
    }

    public static void setSwitchModeSwitch(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_SWITCH_MODE, enable).apply();
    }

    public static boolean getSwitchModeSwitch() {
        return sSharedPreferences.getBoolean(PREF_NAME_SWITCH_MODE, false);
    }

    public static void setFixedModeValue(int value) {
        sSharedPreferences.edit().putInt(PREF_NAME_FIXED_MODE_VALUE, value).apply();
    }

    public static int getFixedModeValue() {
        return sSharedPreferences.getInt(PREF_NAME_FIXED_MODE_VALUE, 0);
    }

    public static void setPairingMode(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_PAIRING_MODE, enable).apply();
    }

    public static boolean getPairingMode() {
        return sSharedPreferences.getBoolean(PREF_NAME_PAIRING_MODE, false);
    }

    public static void setFirstShowWelcome(boolean enable) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_FIRST_SHOW, enable).apply();
    }

    public static boolean getFirstShowWelcome() {
        return sSharedPreferences.getBoolean(PREF_NAME_FIRST_SHOW, true);
    }

    public static void putPattern(char[] mAllDaysPattern) {
        String pattern = new String(mAllDaysPattern);
        sSharedPreferences.edit().putString(PREF_NAME_ALL_DAY_PATTERN, pattern).apply();
    }

    public static char[] getPattern() {
        String pattern = sSharedPreferences.getString(PREF_NAME_ALL_DAY_PATTERN, null);
        return pattern == null ? new char[288] : pattern.toCharArray();
    }

    public static void putAlerts(ArrayList<Byte> mAlerts) {
        byte[] array = new byte[mAlerts.size()];
        for (int i = 0; i < mAlerts.size(); i++) {
            array[i] = mAlerts.get(i).byteValue();
        }
        String alerts = Base64.encodeToString(array, 0);
        sSharedPreferences.edit().putString(PREF_NAME_ALERTS_PATTERN, alerts).apply();
    }

    public static ArrayList<Byte> getAlerts() {
        String alerts = sSharedPreferences.getString(PREF_NAME_ALERTS_PATTERN, null);
        if (alerts == null) {
            return new ArrayList<>();
        }
        ArrayList<Byte> mAlerts = new ArrayList<>();
        byte[] array = Base64.decode(alerts, 0);
        for (byte b : array) {
            mAlerts.add(Byte.valueOf(b));
        }
        return mAlerts;
    }

    public static void putCalendarEnabled(String id, boolean enabled) {
        sSharedPreferences.edit().putBoolean(PREF_NAME_CALENDAR_ENABLED + id, enabled).apply();
    }

    public static boolean getCalendarEnabled(String id) {
        return sSharedPreferences.getBoolean(PREF_NAME_CALENDAR_ENABLED + id, true);
    }
}
