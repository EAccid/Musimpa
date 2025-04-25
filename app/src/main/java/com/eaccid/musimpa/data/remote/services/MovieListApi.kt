package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.entities.MovieListDto
import com.eaccid.musimpa.data.remote.entities.RequestMediaObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MovieListApi {

    @POST("{version}/list")
    fun createList(
        @Path("version") version: Int,
        @Body list: MovieListDto,
        @QueryMap options: Map<String, String>
    ): Response<String>

    @POST("{version}/list/{list_id}/clear")
    fun clearList(
        @Path("version") version: Int,
        @Path("list_id") listId: Int,
        @QueryMap options: Map<String, String>
    ): Response<String>

    @GET("{version}/list/{list_id}")
    fun getListDetails(
        @Path("version") version: Int,
        @Path("list_id") listId: Int,
        @QueryMap options: Map<String, String>
    ): MovieListDto

    @POST("{version}/list/{list_id}/add_item")
    fun addMovieToList(
        @Path("version") version: Int,
        @Path("list_id") listId: Int,
        @Body mediaObject: RequestMediaObject,
        @QueryMap options: Map<String, String>
    ): Response<String>

    @POST("{version}/list/{list_id}/remove_item")
    fun removeMovieFromList(
        @Path("version") version: Int,
        @Path("list_id") listId: Int,
        @Body mediaObject: RequestMediaObject,
        @QueryMap options: Map<String, String>
    ): Response<String>
}