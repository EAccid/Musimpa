package com.eaccid.musimpa.dikoin

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.eaccid.musimpa.data.worker.MovieSyncWorker
import com.eaccid.musimpa.domain.usecase.SyncPopularMoviesUseCase
import org.koin.core.Koin

class KoinWorkerFactory(
    private val koin: Koin
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            MovieSyncWorker::class.qualifiedName -> {
                MovieSyncWorker(
                    appContext,
                    workerParameters,
                    koin.get<SyncPopularMoviesUseCase>()
                )
            }

            else -> null
        }
    }
}