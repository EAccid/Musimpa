package com.eaccid.musimpa.data.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleMovieSync(context: Context) {
    periodicWorkRequest(context)
    //oneTimeWorkRequest(context)
}

fun oneTimeWorkRequest(context: Context) {
    val oneTime = OneTimeWorkRequestBuilder<MovieSyncWorker>()
        .setInitialDelay(5, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(oneTime)
}

fun periodicWorkRequest(context: Context) {
    val workRequest =
        PeriodicWorkRequestBuilder<MovieSyncWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "movie_sync",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
