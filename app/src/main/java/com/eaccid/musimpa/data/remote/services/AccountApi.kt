package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.dto.MovieListDto
import com.eaccid.musimpa.data.remote.dto.UserAccountDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AccountApi {
    @GET("account/")
    fun getAccount(
        @Path("version") version: Int,
        @QueryMap options: Map<String, String>
    ): UserAccountDto

    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteMovies(
        @Path("version") version: Int,
        @Path("account_id") listId: Int,
        @Query("account_id") username: String,
        @QueryMap options: Map<String, String>
    ): List<MovieListDto>
}