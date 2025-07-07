package com.eaccid.musimpa.data.repository

import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap

interface MoviesRemoteDataSource {
    suspend fun discoverAll(page: Int = 1): ApiResponse<DiscoverDto>
    suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto>
    suspend fun getMovie(movieId: Int): ApiResponse<MovieDto>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResultDto>
    suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCreditsDto>
}

