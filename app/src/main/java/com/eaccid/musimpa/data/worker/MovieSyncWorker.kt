package com.eaccid.musimpa.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eaccid.musimpa.domain.usecase.SyncPopularMoviesUseCase

class MovieSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncPopularMoviesUseCase: SyncPopularMoviesUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncPopularMoviesUseCase()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}