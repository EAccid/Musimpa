package com.eaccid.musimpa.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.eaccid.musimpa.data.repository.MoviesLocalDataSource
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.mappers.toMovie
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.domain.models.MovieSearchFilter
import com.eaccid.musimpa.domain.paging.MovieSearchRemoteMediator
import com.eaccid.musimpa.domain.paging.SearchType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GetDiscoverMoviesWithFilterUseCase {
    suspend operator fun invoke(filter: MovieSearchFilter): Flow<PagingData<Movie>>
}

//for now filter is only by genre
class GetDiscoverMoviesWithFilterUseCaseImpl(
    private val repository: MoviesRepository,
    private val local: MoviesLocalDataSource
) : GetDiscoverMoviesWithFilterUseCase {


    @OptIn(ExperimentalPagingApi::class)
    override suspend operator fun invoke(filter: MovieSearchFilter): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = MovieSearchRemoteMediator(
                repository = repository,
                searchType = SearchType.FILTER,
                filter = filter
            ),
            pagingSourceFactory = { local.getDiscoverMoviesPagingSource() }
        ).flow
            .map { pagingData -> pagingData.map { it.toMovie() } }
    }
}
