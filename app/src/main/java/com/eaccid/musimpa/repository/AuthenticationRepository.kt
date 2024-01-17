package com.eaccid.musimpa.repository

import com.eaccid.musimpa.entities.Authentication

interface AuthenticationRepository {
    suspend fun login(): Authentication
}