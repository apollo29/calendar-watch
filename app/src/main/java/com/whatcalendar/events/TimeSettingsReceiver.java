package com.whatcalendar.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.whatcalendar.service.BackgroundService;

/* loaded from: classes.dex */
public class TimeSettingsReceiver extends BroadcastReceiver {
    private static BackgroundService.EventsChangedCallback mEventsChangedCallback;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d(TimeSettingsReceiver.class.getSimpleName(), "onReceive time settings changes. Action: " + intent.getAction());
        if (mEventsChangedCallback != null) {
            mEventsChangedCallback.onTimeChanged();
        }
    }

    public static void setEventsChangedCallback(BackgroundService.EventsChangedCallback callback) {
        mEventsChangedCallback = callback;
    }
}
