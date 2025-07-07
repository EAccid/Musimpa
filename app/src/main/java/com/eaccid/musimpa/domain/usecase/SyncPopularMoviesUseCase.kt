package com.eaccid.musimpa.domain.usecase

import com.eaccid.musimpa.data.repository.MoviesRepository

interface SyncPopularMoviesUseCase {
    suspend operator fun invoke()
}

class SyncPopularMoviesUseCaseImpl(
    private val repository: MoviesRepository
) : SyncPopularMoviesUseCase {
    override suspend operator fun invoke() {
        println("MovieSyncWorker: SyncPopularMoviesUseCase invoked")
        repository.syncPopularMovies()
    }
}