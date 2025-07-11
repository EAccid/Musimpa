package com.eaccid.musimpa.domain.usecase

import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.Genre

interface GetGenresUseCase {
    suspend operator fun invoke(): DataResult<List<Genre>>
}

class GetGenresUseCaseImpl(
    private val repository: MoviesRepository
) : GetGenresUseCase {
    override suspend operator fun invoke(): DataResult<List<Genre>> {
        return repository.getGenres()
    }
}