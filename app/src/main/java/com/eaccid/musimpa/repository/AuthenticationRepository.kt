package com.eaccid.musimpa.repository

import com.eaccid.musimpa.entities.Authentication
import com.eaccid.musimpa.network.ApiResponse

interface AuthenticationRepository {
    suspend fun login(): ApiResponse<Authentication>
}