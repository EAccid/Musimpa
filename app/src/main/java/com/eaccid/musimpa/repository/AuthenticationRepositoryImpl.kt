package com.eaccid.musimpa.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.LocalPreferences
import com.eaccid.musimpa.entities.Authentication
import com.eaccid.musimpa.utils.API_VERSION

class AuthenticationRepositoryImpl(
    private val serviceAPI: TMDbServiceAPI, private val preferences: LocalPreferences
) :
    AuthenticationRepository {
    override suspend fun login(): Authentication {
        val params = mapOf("api_key" to BuildConfig.THE_MOVIE_DB_API_KEY)
        val authentication: Authentication = serviceAPI.requestToken(API_VERSION, params)
        return authentication
    }
}