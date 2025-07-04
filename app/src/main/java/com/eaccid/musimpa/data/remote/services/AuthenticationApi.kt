package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.dto.AuthenticationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface AuthenticationApi {
    @GET("authentication/token/new")
    suspend fun requestToken(
        @QueryMap options: Map<String, String>
    ): AuthenticationDto

    @POST("authentication/session/new")
    suspend fun createSession(
        @Header("Authorization") bearer: String,
        @Body authentication: AuthenticationDto
    ): AuthenticationDto
}