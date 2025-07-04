package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.MovieApiService
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap

interface MoviesRemoteDataSource {
    suspend fun discoverAll(page: Int = 1): ApiResponse<DiscoverDto>
    suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto>
    suspend fun getMovie(movieId: Int): ApiResponse<MovieDto>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResultDto>
    suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCreditsDto>
}

class MoviesRemoteDataSourceImpl(
    private val apiService: MovieApiService
) : MoviesRemoteDataSource {

    override suspend fun discoverAll(page: Int): ApiResponse<DiscoverDto> =
        discoverAll(MovieDiscoverAllQueryMap(page = page))

    override suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto> {
        val params = query.toQueryMap()
        return safeApiRequest { apiService.discoverAll(params) }
    }

    override suspend fun getMovie(movieId: Int): ApiResponse<MovieDto> =
        safeApiRequest {
            apiService.getMovie(movieId, emptyMap())
        }

    override suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResultDto> =
        safeApiRequest {
            apiService.getMovieVideos(movieId, emptyMap())
        }

    override suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCreditsDto> =
        safeApiRequest {
            apiService.getMovieCredits(movieId, emptyMap())
        }
}