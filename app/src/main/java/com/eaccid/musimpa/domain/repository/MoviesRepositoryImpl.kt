package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.MovieApi
import com.eaccid.musimpa.utils.API_VERSION

class MoviesRepositoryImpl(
    private val serviceAPI: MovieApi
) : MoviesRepository {

    override suspend fun discoverAll(page: Int): ApiResponse<DiscoverDto> {
        val params = mapOf(
            "api_key" to BuildConfig.THE_MOVIE_DB_API_KEY,
            "page" to page.toString(),
            "language" to "en-US",
            "sort_by" to "popularity.desc",
            "include_adult" to "false",
            "include_video" to "false"
        )
        return safeApiRequest { serviceAPI.discoverAll(API_VERSION, params) }
    }

    override suspend fun getMovie(movieId: Int): ApiResponse<MovieDto> {
        val params = mapOf(
            "api_key" to BuildConfig.THE_MOVIE_DB_API_KEY,
            "language" to "en-US"
        )
        return safeApiRequest {
            serviceAPI.getMovie(API_VERSION, movieId, params)
        }
    }

    override suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult> {
        val params = mapOf(
            "api_key" to BuildConfig.THE_MOVIE_DB_API_KEY,
            "language" to "en-US"
        )
        return safeApiRequest {
            serviceAPI.getMovieVideos(API_VERSION, movieId, params)
        }
    }

    override suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits> {
        val params = mapOf(
            "api_key" to BuildConfig.THE_MOVIE_DB_API_KEY,
            "language" to "en-US"
        )
        return safeApiRequest {
            serviceAPI.getMovieCredits(API_VERSION, movieId, params)
        }
    }

}
