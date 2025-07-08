package com.eaccid.musimpa.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieSearchFilter
import com.eaccid.musimpa.domain.repository.MoviesRepositoryImpl

enum class SearchType {
    SEARCH,
    DISCOVER,
    POPULAR,
    FILTER
}

@OptIn(ExperimentalPagingApi::class)
class MovieSearchRemoteMediator(
    private val repository: MoviesRepositoryImpl,
    private val searchType: SearchType,
    private val searchQuery: String = "",
    private val filter: MovieSearchFilter = MovieSearchFilter()
) : RemoteMediator<Int, MovieEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> state.lastItemOrNull()?.page?.plus(1) ?: STARTING_PAGE_INDEX
        }

        //Is not allowed combining search and filter due to tmdb api restrictions
        return when (searchType) {
            SearchType.SEARCH -> {
                repository.searchAndCacheMovies(
                    searchQuery = searchQuery,
                    filter = filter,
                    page = loadKey,
                    clearDataFirst = loadType == LoadType.REFRESH
                ).toMediatorResult()
            }

            SearchType.FILTER -> {
                repository.discoverAndCacheMoviesWithFilter(
                    filter = filter,
                    page = loadKey,
                    clearDataFirst = loadType == LoadType.REFRESH
                ).toMediatorResult()
            }
            //SearchType. Combined???
            else -> {
                repository.discoverAndCachePopularMovies(
                    page = loadKey,
                    clearDataFirst = loadType == LoadType.REFRESH
                ).toMediatorResult()
            }
        }
    }

    private fun DataResult<DiscoverDto>.toMediatorResult(): MediatorResult {
        return when (this) {
            is DataResult.Success -> MediatorResult.Success(
                endOfPaginationReached = data.movies.isEmpty()
            )

            is DataResult.Failure -> MediatorResult.Error(error)
            is DataResult.NetworkError -> MediatorResult.Error(Exception("Network error"))
        }
    }
}