package com.eaccid.musimpa.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eaccid.musimpa.domain.usecase.SyncPopularMoviesUseCase

class MovieSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncPopularMoviesUseCase: SyncPopularMoviesUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.i("MovieSyncWorker", "MovieSyncWorker started")
        return try {
            syncPopularMoviesUseCase()
            Log.i("MovieSyncWorker", "MovieSyncWorker finished")
            Result.success()
        } catch (e: Exception) {
            Log.i("MovieSyncWorker", "MovieSyncWorker failed")
            Result.retry()
        }
    }
}