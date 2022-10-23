package com.whatcalendar.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.whatcalendar.service.BackgroundService;

/* loaded from: classes.dex */
public class CalendarEventsReceiver extends BroadcastReceiver {
    private static BackgroundService.EventsChangedCallback mEventsChangedCallback;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d(CalendarEventsReceiver.class.getSimpleName(), "onReceive Calendar events changes. Action: " + intent.getAction());
        if (mEventsChangedCallback != null) {
            mEventsChangedCallback.onEventsChanged();
        }
    }

    public static void setEventsChangedCallback(BackgroundService.EventsChangedCallback callback) {
        mEventsChangedCallback = callback;
    }
}
