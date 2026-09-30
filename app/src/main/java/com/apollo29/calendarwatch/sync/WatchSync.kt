package com.apollo29.calendarwatch.sync

import android.content.Context
import android.provider.CalendarContract
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.apollo29.calendarwatch.repository.Preferences
import java.util.concurrent.TimeUnit

/**
 * Keeps the watch up to date while the app is not open, using WorkManager:
 * - periodically, so the three days on the watch move on even without calendar changes
 * - when the calendar changes (content URI trigger)
 * - when the time or time zone changes (see TimeSettingsReceiver)
 */
object WatchSync {

    private const val WORK_PERIODIC = "watch-sync-periodic"
    private const val WORK_NOW = "watch-sync-now"
    private const val WORK_CALENDAR_OBSERVER = "watch-sync-calendar-observer"

    private const val PERIODIC_INTERVAL_HOURS = 6L

    /**
     * Schedules the periodic sync and the calendar observer, if a watch has been paired.
     * Safe to call repeatedly (e.g. on every app start).
     */
    fun schedule(context: Context) {
        if (Preferences(context).watchAddress() == null) return
        val workManager = WorkManager.getInstance(context)

        val periodic = PeriodicWorkRequestBuilder<WatchSyncWorker>(
            PERIODIC_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_PERIODIC,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodic
        )

        observeCalendar(context, ExistingWorkPolicy.KEEP)
    }

    /**
     * Syncs as soon as possible, e.g. after the calendar or the time changed.
     */
    fun syncNow(context: Context, timeChanged: Boolean = false) {
        if (Preferences(context).watchAddress() == null) return
        val request = OneTimeWorkRequestBuilder<WatchSyncWorker>()
            .setInputData(workDataOf(WatchSyncWorker.KEY_TIME_CHANGED to timeChanged))
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()
        // don't cancel a sync that is already sending, run this one after it instead
        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NOW, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    /**
     * Content URI triggers fire only once, so [CalendarObserverWorker] re-registers itself
     * (appended to the running one) every time it runs.
     */
    internal fun observeCalendar(context: Context, policy: ExistingWorkPolicy) {
        val constraints = Constraints.Builder()
            .addContentUriTrigger(CalendarContract.Events.CONTENT_URI, true)
            .addContentUriTrigger(CalendarContract.Reminders.CONTENT_URI, true)
            // wait for more changes (e.g. a calendar sync) before syncing the watch
            .setTriggerContentUpdateDelay(30, TimeUnit.SECONDS)
            .setTriggerContentMaxDelay(2, TimeUnit.MINUTES)
            .build()
        val request = OneTimeWorkRequestBuilder<CalendarObserverWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_CALENDAR_OBSERVER, policy, request)
    }

    fun cancel(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_PERIODIC)
        workManager.cancelUniqueWork(WORK_NOW)
        workManager.cancelUniqueWork(WORK_CALENDAR_OBSERVER)
    }
}
