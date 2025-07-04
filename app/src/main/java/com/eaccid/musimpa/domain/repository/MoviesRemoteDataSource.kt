package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.MovieApiService
import com.eaccid.musimpa.domain.model.MovieDiscoverAllQueryMap

interface MoviesRemoteDataSource {
    suspend fun discoverAll(page: Int = 1): ApiResponse<DiscoverDto>
    suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto>
    suspend fun getMovie(movieId: Int): ApiResponse<MovieDto>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult>
    suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits>
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

    override suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult> =
        safeApiRequest {
            apiService.getMovieVideos(movieId, emptyMap())
        }

    override suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits> =
        safeApiRequest {
            apiService.getMovieCredits(movieId, emptyMap())
        }
}