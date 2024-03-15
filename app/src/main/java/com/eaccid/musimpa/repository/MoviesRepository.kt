package com.eaccid.musimpa.repository

import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.entities.Movie
import com.eaccid.musimpa.network.ApiResponse

interface MoviesRepository {
    suspend fun discoverAll(): ApiResponse<Discover>
    suspend fun getMovie(movieId: Int): ApiResponse<Movie>
}
