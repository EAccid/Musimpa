package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.BuildConfig
import com.eaccid.musimpa.data.local.LocalData
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.dto.AuthenticationDto
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.AuthenticationApi
import com.eaccid.musimpa.data.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val serviceAPI: AuthenticationApi,
    private val localData: LocalData
) :
    AuthenticationRepository {
    override suspend fun getToken(): ApiResponse<AuthenticationDto> {
        val result = safeApiRequest {
            serviceAPI.requestToken(emptyMap())
        }
        updateLocalDataToken(result)
        return result
    }

    override fun isUserLoggedIn(): Boolean {
        return getLocalDataUserSessionId().isNotEmpty()
    }

    override suspend fun createSessionId(): ApiResponse<AuthenticationDto> {
        val result = safeApiRequest {
            serviceAPI.createSession(
                BuildConfig.THE_MOVIE_DB_API_READ_ACCESS_TOKEN,
                AuthenticationDto(request_token = getLocalDataToken())
            )
        }
        updateSessionID(result)
        return result
    }

    private fun getLocalDataToken(): String {
        return localData.getToken()
    }

    private fun updateLocalDataToken(result: ApiResponse<AuthenticationDto>) {
        if (result is ApiResponse.Success) {
            localData.saveToken(result.data.request_token ?: "")
        }
    }

    private fun getLocalDataUserSessionId(): String {
        return localData.getSessionId()
    }

    private fun updateSessionID(result: ApiResponse<AuthenticationDto>) {
        if (result is ApiResponse.Success) {
            localData.saveSessionId(result.data.session_id ?: "")
        }
    }
}