package com.apollo29.calendarwatch.sync

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orhanobut.logger.Logger

/**
 * Runs when the calendar provider reports changes: triggers a sync and observes the
 * calendar again.
 */
class CalendarObserverWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Logger.d("CalendarObserverWorker: calendar changed ${triggeredContentUris}")
        WatchSync.syncNow(applicationContext)
        WatchSync.observeCalendar(applicationContext, ExistingWorkPolicy.APPEND_OR_REPLACE)
        return Result.success()
    }
}
