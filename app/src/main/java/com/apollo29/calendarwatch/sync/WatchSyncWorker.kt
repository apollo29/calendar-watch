package com.apollo29.calendarwatch.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import com.orhanobut.logger.Logger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Connects to the paired watch, sends the calendar and disconnects again.
 * Retries with backoff if the watch is not in range.
 */
class WatchSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WatchSyncEntryPoint {
        fun watchManager(): WhatCalendarWatchManager
    }

    override suspend fun doWork(): Result {
        val manager = EntryPointAccessors
            .fromApplication(applicationContext, WatchSyncEntryPoint::class.java)
            .watchManager()
        val timeChanged = inputData.getBoolean(KEY_TIME_CHANGED, false)
        Logger.d("WatchSyncWorker: start (attempt $runAttemptCount, time changed: $timeChanged)")
        return when {
            manager.syncInBackground(timeChanged) -> Result.success()
            runAttemptCount < MAX_ATTEMPTS -> Result.retry()
            else -> Result.failure()
        }
    }

    companion object {
        const val KEY_TIME_CHANGED = "time_changed"
        private const val MAX_ATTEMPTS = 5
    }
}
