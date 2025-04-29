package com.eaccid.musimpa.repository

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
}
