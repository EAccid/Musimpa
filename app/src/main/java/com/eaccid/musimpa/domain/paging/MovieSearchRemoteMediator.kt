package com.eaccid.musimpa.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieSearchFilter

enum class SearchType {
    SEARCH,
    DISCOVER,
    POPULAR,
    FILTER
}

@OptIn(ExperimentalPagingApi::class)
class MovieSearchRemoteMediator(
    private val repository: MoviesRepository,
    private val searchType: SearchType,
    private val searchQuery: String = "",
    private val filter: MovieSearchFilter = MovieSearchFilter()
) : RemoteMediator<Int, MovieEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType, // refresh, new items from new page
        state: PagingState<Int, MovieEntity> // current page info
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)  // not using, adding only to the end of the list
            LoadType.APPEND -> state.lastItemOrNull()?.page?.plus(1) ?: STARTING_PAGE_INDEX
        }
        println("$loadType mediator test loadKey = $loadKey")

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
            //SearchType. Combined - not possible, api restriction
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
            is DataResult.NetworkError -> MediatorResult.Error(Exception("Failed to load movies: Network error"))
        }
    }
}