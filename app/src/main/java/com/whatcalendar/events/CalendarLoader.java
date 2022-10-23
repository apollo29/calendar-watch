package com.whatcalendar.events;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.whatcalendar.util.GlobalPreferences;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class CalendarLoader {
    private static final String TAG = CalendarLoader.class.getSimpleName();
    private static final long TIME_PERIOD = 259200000;

    /* JADX WARN: Type inference failed for: r27v0, types: [T, java.util.ArrayList] */
    public static DTOResponse loadCalendarEvents(Context context) {
        String selectionClauseEvents;
        String[] selectionsArgsEvents;
        DTOResponse response = new DTOResponse();
        ArrayList arrayList = new ArrayList();
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(11, 0);
        currentDay.set(12, 0);
        currentDay.set(13, 0);
        currentDay.set(14, 0);
        long dtstart = currentDay.getTimeInMillis();
        long dtend = dtstart + TIME_PERIOD;
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, Long.MIN_VALUE);
        ContentUris.appendId(eventsUriBuilder, Long.MAX_VALUE);
        Uri contentEvents = eventsUriBuilder.build();
        Uri contentReminders = CalendarContract.Reminders.CONTENT_URI;
        String[] vec = {"title", "begin", "end", "allDay", "event_id", "calendar_id", "selfAttendeeStatus"};
        if (GlobalPreferences.getAllDayEventsInfo()) {
            selectionClauseEvents = "(end >= ? AND end <= ? OR begin >= ? AND begin <= ? OR begin < ? AND end > ? )";
            selectionsArgsEvents = new String[]{"" + dtstart, "" + dtend, "" + dtstart, "" + dtend, "" + dtstart, "" + dtend};
        } else {
            selectionClauseEvents = "((end >= ? AND end <= ? OR begin >= ? AND begin <= ? OR begin < ? AND end > ? ) AND allDay = ? )";
            selectionsArgsEvents = new String[]{"" + dtstart, "" + dtend, "" + dtstart, "" + dtend, "" + dtstart, "" + dtend, "0"};
        }
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_CALENDAR") != 0) {
            response.errorCode = 0;
            response.data = null;
        } else {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursorEvents = contentResolver.query(contentEvents, vec, selectionClauseEvents, selectionsArgsEvents, null);
            cursorEvents.moveToFirst();
            int eventsCount = cursorEvents.getCount();
            TimeZone tz = TimeZone.getDefault();
            Calendar cal = GregorianCalendar.getInstance(tz);
            int allDayOffset = tz.getOffset(cal.getTimeInMillis());
            for (int i = 0; i < eventsCount; i++) {
                DTOEvent event = new DTOEvent();
                event.id = cursorEvents.getString(4);
                event.title = cursorEvents.getString(0);
                event.startDate = Long.parseLong(cursorEvents.getString(1));
                event.endDate = Long.parseLong(cursorEvents.getString(2));
                event.allDay = Integer.parseInt(cursorEvents.getString(3)) == 1;
                event.calendar_id = cursorEvents.getString(5);
                event.attend_status = cursorEvents.getInt(6);
                if (event.allDay) {
                    event.startDate -= allDayOffset;
                    event.endDate -= allDayOffset;
                }
                Log.d(TAG, "event: " + event.id + " title: " + event.title + " calendar id:" + event.calendar_id + " attend_status: " + event.attend_status);
                cursorEvents.moveToNext();
                if (GlobalPreferences.getCalendarEnabled(event.calendar_id)) {
                    if (event.attend_status != 2) {
                        arrayList.add(event);
                    } else {
                        Log.d(TAG, "event: [" + event.id + "] " + event.title + " - ATTENDEE_STATUS_DECLINED");
                    }
                } else {
                    Log.d(TAG, "event: [" + event.id + "] " + event.title + " - calendar id: " + event.calendar_id + " is disabled in settings");
                }
            }
            if (eventsCount > 0) {
                String[] vec2 = {"_id", "event_id", "minutes", "method"};
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    DTOEvent event2 = (DTOEvent) it.next();
                    String[] selectionsArgsReminders = {event2.id};
                    Cursor cursorReminders = contentResolver.query(contentReminders, vec2, "(event_id = ? )", selectionsArgsReminders, null);
                    cursorReminders.moveToFirst();
                    if (cursorReminders.getCount() > 0) {
                        int remindersCount = cursorReminders.getCount();
                        for (int j = 0; j < remindersCount; j++) {
                            Log.d(TAG, "Reminder: " + cursorReminders.getString(0) + " event: " + cursorReminders.getString(1) + " minutes: " + cursorReminders.getString(2) + " method: " + cursorReminders.getString(3));
                            int alert_type = Integer.parseInt(cursorReminders.getString(3));
                            if (alert_type < 2) {
                                DTOAlert alert = new DTOAlert();
                                alert.id = cursorReminders.getString(0);
                                alert.event_id = cursorReminders.getString(1);
                                alert.minutes = Integer.parseInt(cursorReminders.getString(2));
                                event2.alertsList.add(alert);
                            }
                            cursorReminders.moveToNext();
                        }
                    }
                }
            }
            response.errorCode = 1;
            response.data = arrayList;
        }
        return response;
    }
}
