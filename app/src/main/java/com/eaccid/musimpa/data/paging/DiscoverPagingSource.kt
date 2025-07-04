package com.eaccid.musimpa.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eaccid.musimpa.domain.common.handleReturn
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.domain.repository.MoviesRepository
import com.eaccid.musimpa.utils.toMovie

//Not in use but kept to see how PagingSource works
class DiscoverPagingSource(
    private val moviesRepository: MoviesRepository
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentPage = params.key ?: 1
            println("temptest DiscoverPagingSource load page: currentPage $currentPage")
            moviesRepository.discoverAll(currentPage)
                .handleReturn(
                    onSuccess = { data ->
                        LoadResult.Page(
                            data = data.movies.map { it.toMovie() },
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = if (data.movies.isEmpty()) null else currentPage + 1
                        )
                    },
                    onFailure = { error, message ->
                        LoadResult.Error(Exception(message, error))
                    },
                    onNetworkError = {
                        LoadResult.Error(Exception("Network error occurred"))
                    }
                )
        } catch (e: Exception) {
            LoadResult.Error(e) // Handle unknown errors
        }
    }
}