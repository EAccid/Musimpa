package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.entities.Authentication
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface AuthenticationApi {
    @GET("authentication/token/new")
    suspend fun requestToken(
        @QueryMap options: Map<String, String>
    ): Authentication

    @POST("authentication/session/new")
    suspend fun createSession(
        @Header("Authorization") bearer: String,
        @Body authentication: Authentication
    ): Authentication
}