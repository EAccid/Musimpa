package com.eaccid.musimpa.repository

import com.eaccid.musimpa.data.remote.entities.Authentication
import com.eaccid.musimpa.data.remote.ApiResponse

interface AuthenticationRepository {
    suspend fun getToken(): ApiResponse<Authentication>
    fun isUserLoggedIn(): Boolean
    suspend fun createSessionId(): ApiResponse<Authentication>
}