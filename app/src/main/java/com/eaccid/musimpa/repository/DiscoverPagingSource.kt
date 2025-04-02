package com.eaccid.musimpa.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.domain.Movie
import com.eaccid.musimpa.utils.toMovie

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
            when (val response = moviesRepository.discoverAll(currentPage)) {
                is ApiResponse.Success -> {
                    LoadResult.Page(
                        data = response.data.movies.map { it.toMovie() }, // Convert to UI model
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (response.data.movies.isEmpty()) null else currentPage + 1
                    )
                }

                is ApiResponse.Error -> LoadResult.Error(Exception(response.message)) // Handle API errors
                ApiResponse.NetworkError -> LoadResult.Error(Exception("Network error occurred")) // Handle network failures
            }
        } catch (e: Exception) {
            LoadResult.Error(e) // Handle unknown errors
        }
    }
}