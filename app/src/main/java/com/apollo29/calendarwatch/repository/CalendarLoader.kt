package com.apollo29.calendarwatch.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import com.apollo29.calendarwatch.model.DTOAlert
import com.apollo29.calendarwatch.model.DTOEvent
import com.apollo29.calendarwatch.model.DTOResponse
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences = Preferences(context)

    @SuppressLint("Recycle")
    fun loadCalendarEvents(): DTOResponse<List<DTOEvent>> {
        val response = DTOResponse<List<DTOEvent>>()
        val arrayList = mutableListOf<DTOEvent>()
        var selectionClauseEvents: String
        var selectionsArgsEvents: Array<String>
        val currentDay = Calendar.getInstance()
        currentDay[11] = 0
        currentDay[12] = 0
        currentDay[13] = 0
        currentDay[14] = 0
        val dtstart = currentDay.timeInMillis
        val dtend = dtstart + TIME_PERIOD
        val eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(eventsUriBuilder, Long.MIN_VALUE)
        ContentUris.appendId(eventsUriBuilder, Long.MAX_VALUE)
        val contentEvents = eventsUriBuilder.build()
        val contentReminders = CalendarContract.Reminders.CONTENT_URI
        val vec = arrayOf(
            "title",
            "begin",
            "end",
            "allDay",
            "event_id",
            "calendar_id",
            "selfAttendeeStatus"
        )
        if (preferences.allDayEventsInfo()) {
            selectionClauseEvents =
                "(end >= ? AND end <= ? OR begin >= ? AND begin <= ? OR begin < ? AND end > ? )"
            selectionsArgsEvents = arrayOf(
                "" + dtstart,
                "" + dtend,
                "" + dtstart,
                "" + dtend,
                "" + dtstart,
                "" + dtend
            )
        } else {
            selectionClauseEvents =
                "((end >= ? AND end <= ? OR begin >= ? AND begin <= ? OR begin < ? AND end > ? ) AND allDay = ? )"
            selectionsArgsEvents = arrayOf(
                "" + dtstart,
                "" + dtend,
                "" + dtstart,
                "" + dtend,
                "" + dtstart,
                "" + dtend,
                "0"
            )
        }

        val contentResolver: ContentResolver = context.contentResolver
        val cursorEvents = contentResolver.query(
            contentEvents,
            vec,
            selectionClauseEvents,
            selectionsArgsEvents,
            null
        )
        cursorEvents!!.moveToFirst()
        val eventsCount = cursorEvents.count
        val tz = TimeZone.getDefault()
        val cal = GregorianCalendar.getInstance(tz)
        val allDayOffset = tz.getOffset(cal.timeInMillis)

        for (i in 0 until eventsCount) {
            val id = cursorEvents.getString(4)
            val title = cursorEvents.getString(0)
            val startDate = cursorEvents.getString(1).toLong()
            val endDate = cursorEvents.getString(2).toLong()
            val allDay = cursorEvents.getString(3).toInt() == 1
            val calendar_id = cursorEvents.getString(5)
            val attend_status = cursorEvents.getInt(6)
            val event = DTOEvent(id, title, startDate, endDate, allDay, calendar_id, attend_status)
            if (event.allDay) {
                event.startDate -= allDayOffset
                event.endDate -= allDayOffset
            }
            Logger.d(
                "event: " + event.id + " title: " + event.title + " calendar id:" + event.calendar_id + " attend_status: " + event.attend_status
            )
            cursorEvents.moveToNext()
            if (preferences.calendarEnabled(event.calendar_id)) {
                if (event.attend_status != 2) {
                    arrayList.add(event)
                } else {
                    Logger.d(
                        "event: [" + event.id + "] " + event.title + " - ATTENDEE_STATUS_DECLINED"
                    )
                }
            } else {
                Logger.d(
                    "event: [" + event.id + "] " + event.title + " - calendar id: " + event.calendar_id + " is disabled in settings"
                )
            }
        }

        if (eventsCount > 0) {
            val vec2 = arrayOf("_id", "event_id", "minutes", "method")
            val it: Iterator<DTOEvent> = arrayList.iterator()
            while (it.hasNext()) {
                val event = it.next()
                val selectionsArgsReminders = arrayOf(event.id)
                val cursorReminders = contentResolver.query(
                    contentReminders,
                    vec2,
                    "(event_id = ? )",
                    selectionsArgsReminders,
                    null
                )
                cursorReminders!!.moveToFirst()
                if (cursorReminders.count > 0) {
                    val remindersCount = cursorReminders.count
                    for (j in 0 until remindersCount) {
                        Logger.d(
                            "Reminder: " + cursorReminders.getString(0) + " event: " + cursorReminders.getString(
                                1
                            ) + " minutes: " + cursorReminders.getString(2) + " method: " + cursorReminders.getString(
                                3
                            )
                        )
                        val alert_type = cursorReminders.getString(3).toInt()
                        if (alert_type < 2) {
                            val id = cursorReminders.getString(0)
                            val event_id = cursorReminders.getString(1)
                            val minutes = cursorReminders.getString(2).toInt()
                            val alert = DTOAlert(id, event_id, minutes)
                            event.alertsList.add(alert)
                        }
                        cursorReminders.moveToNext()
                    }
                }
            }
        }
        response.errorCode = 1
        response.data = arrayList

        return response
    }

    companion object {
        const val TIME_PERIOD: Long = 259200000
    }
}