package com.eaccid.musimpa.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.LocalPreferences
import com.eaccid.musimpa.entities.Authentication
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.network.TMDbServiceAPI
import com.eaccid.musimpa.network.safeApiRequest
import com.eaccid.musimpa.utils.API_VERSION

class AuthenticationRepositoryImpl(
    private val serviceAPI: TMDbServiceAPI, private val preferences: LocalPreferences
) :
    AuthenticationRepository {
    override suspend fun getToken(): ApiResponse<Authentication> {
        val params = mapOf("api_key" to BuildConfig.THE_MOVIE_DB_API_KEY)
        val result = safeApiRequest {
            serviceAPI.requestToken(API_VERSION, params)
        }
        updateLocalDataToken(result)
        return result
    }

    override fun isUserLoggedIn(): Boolean {
        return getLocalDataUserSessionId().isNotEmpty()
    }

    private fun getUserSessionId(sessionId: String) {

    }

    private fun getLocalDataUserSessionId(): String {
        return preferences.getString(LocalPreferences.SharedKeys.SESSION_ID.key)
    }

    private fun updateLocalDataToken(result: ApiResponse<Authentication>) {
        if (result is ApiResponse.Success) {
            preferences.saveString(
                LocalPreferences.SharedKeys.TOKEN.key,
                result.data.request_token ?: ""
            )
        }
    }
}