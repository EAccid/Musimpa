package com.eaccid.musimpa.domain.usecase

import com.eaccid.musimpa.domain.repository.MoviesRepository

class SyncPopularMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke() {
        println("MovieSyncWorker: SyncPopularMoviesUseCase invoked")
        repository.syncPopularMovies()
    }
}