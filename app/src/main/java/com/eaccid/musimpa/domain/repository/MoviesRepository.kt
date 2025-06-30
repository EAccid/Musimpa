package com.eaccid.musimpa.domain.repository

import androidx.paging.PagingSource
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult

interface MoviesRepository {
    suspend fun discoverAll(page: Int = 1): ApiResponse<DiscoverDto>
    suspend fun getMovie(movieId: Int): ApiResponse<MovieDto>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult>
    suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits>
    suspend fun syncPopularMovies(): Boolean
    fun getLocalPagingSource(): PagingSource<Int, MovieEntity>

    //try to think about this in a better way in separate interface
    suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): ApiResponse<DiscoverDto>
}
