package com.eaccid.musimpa.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.LocalData
import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.network.TMDbServiceAPI
import com.eaccid.musimpa.network.safeApiRequest
import com.eaccid.musimpa.utils.API_VERSION

class MoviesRepositoryImpl(
    private val serviceAPI: TMDbServiceAPI,
    private val localData: LocalData
) : MoviesRepository {
    override suspend fun discoverAll(): ApiResponse<Discover> {
        val params = mapOf(
            "api_key" to BuildConfig.THE_MOVIE_DB_API_KEY,
            "language" to "en-US",
            "sort_by" to "popularity.desc",
            "include_adult" to "false",
            "include_video" to "false"
        )
        return safeApiRequest { serviceAPI.discoverAll(API_VERSION, params) }
    }
}
