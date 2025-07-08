package com.eaccid.musimpa.data.remote.services

import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.GenreListDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MovieApiService {

    @GET("discover/movie")
    suspend fun discoverAll(
        @QueryMap options: Map<String, String>
    ): DiscoverDto

    @GET("search/movie")
    suspend fun searchMovies(
        @QueryMap options: Map<String, String>
    ): DiscoverDto

    @GET("genre/movie/list")
    suspend fun getGenres(
        @QueryMap options: Map<String, String>
    ): GenreListDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): VideosResultDto

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): MovieDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieItemId: Int,
        @QueryMap options: Map<String, String>
    ): MovieCreditsDto

}