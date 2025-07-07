package com.eaccid.musimpa.data.repository

import com.eaccid.musimpa.data.remote.dto.AuthenticationDto
import com.eaccid.musimpa.data.remote.ApiResponse

interface AuthenticationRepository {
    suspend fun getToken(): ApiResponse<AuthenticationDto>
    fun isUserLoggedIn(): Boolean
    suspend fun createSessionId(): ApiResponse<AuthenticationDto>
}