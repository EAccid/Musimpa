package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MovieApiService {

    @GET("discover/movie")
    suspend fun discoverAll(
        @QueryMap options: Map<String, String>
    ): DiscoverDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): VideosResult

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): MovieDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): MovieCredits
}