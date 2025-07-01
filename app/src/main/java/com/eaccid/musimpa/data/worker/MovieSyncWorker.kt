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
        println("MovieSyncWorker: MovieSyncWorker started")
        return try {
            syncPopularMoviesUseCase()
            println("MovieSyncWorker: MovieSyncWorker finished")
            Result.success()
        } catch (e: Exception) {
            println("MovieSyncWorker: MovieSyncWorker failed")
            Result.retry()
        }
    }
}