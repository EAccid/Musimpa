package com.eaccid.musimpa.network

import com.eaccid.musimpa.entities.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface TMDbServiceAPI {

    @GET("{version}/authentication/token/new")
    suspend fun requestToken(
        @Path(value = "version") version: Int,
        @QueryMap options: Map<String, String>
    ): Authentication

    @POST("{version}/authentication/session/new")
    fun createSession(
        @Path(value = "version") version: Int,
        @Body authentication: Authentication,
        @QueryMap options: Map<String, String>
    ): Call<Authentication>

    /*discover*/

    @GET("{version}/discover/movie")
    suspend fun discoverAll(
        @Path(value = "version") version: Int,
        @QueryMap options: Map<String, String>
    ): Discover

    /*movies*/

    @GET("{version}/movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path(value = "version") version: Int,
        @Path(value = "movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): VideosResult

    @GET("{version}/movie/{movie_id}")
    suspend fun getMovie(
        @Path(value = "version") version: Int,
        @Path(value = "movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): Movie

    /*movie lists*/

    @POST("{version}/list")
    fun createList(
        @Path(value = "version") version: Int,
        @Body list: MovieList,
        @QueryMap options: Map<String, String>
    ): Response<String>

    @POST("{version}/list/{list_id}/clear")
    fun clearList(
        @Path(value = "version") version: Int,
        @Path(value = "list_id") listId: Int,
        @QueryMap options: Map<String, String>
    ): Response<String>

    @GET("{version}/list/{list_id}")
    fun getListDetails(
        @Path(value = "version") version: Int,
        @Path(value = "list_id") listId: Int,
        @QueryMap options: Map<String, String>
    ): MovieList

    @POST("{version}/list/{list_id}/add_item")
    fun addMovieToList(
        @Path(value = "version") version: Int,
        @Path(value = "list_id") listId: Int,
        @Body mediaObject: RequestMediaObject,
        @QueryMap options: Map<String, String>
    ): Response<String>


    @POST("{version}/list/{list_id}/remove_item")
    fun removeMovieFromList(
        @Path(value = "version") version: Int,
        @Path(value = "list_id") listId: Int,
        @Body mediaObject: RequestMediaObject,
        @QueryMap options: Map<String, String>
    ): Response<String>

    /*account*/

    @GET("{version}/account/")
    fun getAccount(
        @Path(value = "version") version: Int,
        @QueryMap options: Map<String, String>
    ): UserAccount


    @GET("{version}/account/{account_id}/favorite/movies")
    fun getFavoriteMovies(
        @Path(value = "version") version: Int,
        @Path(value = "account_id") listId: Int,
        @Query("account_id") username: String,
        @QueryMap options: Map<String, String>
    ): List<MovieList>


}