package com.eaccid.musimpa.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.LocalData
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.Authentication
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.AuthenticationApi
import com.eaccid.musimpa.utils.API_VERSION

class AuthenticationRepositoryImpl(
    private val serviceAPI: AuthenticationApi,
    private val localData: LocalData
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

    override suspend fun createSessionId(): ApiResponse<Authentication> {
        val result = safeApiRequest {
            serviceAPI.createSession(
                BuildConfig.THE_MOVIE_DB_API_READ_ACCESS_TOKEN,
                API_VERSION,
                Authentication(request_token = getLocalDataToken())
            )
        }
        updateSessionID(result)
        return result
    }

    private fun getLocalDataToken(): String {
        return localData.getToken()
    }

    private fun updateLocalDataToken(result: ApiResponse<Authentication>) {
        if (result is ApiResponse.Success) {
            localData.saveToken(result.data.request_token ?: "")
        }
    }

    private fun getLocalDataUserSessionId(): String {
        return localData.getSessionId()
    }

    private fun updateSessionID(result: ApiResponse<Authentication>) {
        if (result is ApiResponse.Success) {
            localData.saveSessionId(result.data.session_id ?: "")
        }
    }
}