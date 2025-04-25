package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MovieApi {
    
    @GET("{version}/discover/movie")
    suspend fun discoverAll(
        @Path("version") version: Int,
        @QueryMap options: Map<String, String>
    ): DiscoverDto

    @GET("{version}/movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("version") version: Int,
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): VideosResult

    @GET("{version}/movie/{movie_id}")
    suspend fun getMovie(
        @Path("version") version: Int,
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): MovieDto
}