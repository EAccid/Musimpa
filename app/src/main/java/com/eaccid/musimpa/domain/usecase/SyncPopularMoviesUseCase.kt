package com.eaccid.musimpa.domain.usecase

import android.util.Log
import com.eaccid.musimpa.domain.repository.MoviesRepository

class SyncPopularMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke() {
        Log.i("MovieSyncWorker", "SyncPopularMoviesUseCase invoked")
        repository.syncPopularMovies()
    }
}