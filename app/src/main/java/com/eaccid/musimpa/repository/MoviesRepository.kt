package com.eaccid.musimpa.repository

import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.entities.Movie
import com.eaccid.musimpa.entities.VideosResult
import com.eaccid.musimpa.network.ApiResponse

interface MoviesRepository {
    suspend fun discoverAll(page: Int = 1): ApiResponse<Discover>
    suspend fun getMovie(movieId: Int): ApiResponse<Movie>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult>
}
