package com.eaccid.musimpa.repository

import com.eaccid.musimpa.entities.Authentication
import com.eaccid.musimpa.network.ApiResponse

interface AuthenticationRepository {
    suspend fun getToken(): ApiResponse<Authentication>
    fun isUserLoggedIn(): Boolean
    suspend fun createSessionId(): ApiResponse<Authentication>
}