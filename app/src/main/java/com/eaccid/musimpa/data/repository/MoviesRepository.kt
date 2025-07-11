package com.eaccid.musimpa.data.repository

import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.Genre
import com.eaccid.musimpa.domain.models.MovieSearchFilter

interface MoviesRepository {
    suspend fun discoverAll(page: Int = 1): DataResult<DiscoverDto>
    suspend fun getMovie(movieId: Int): DataResult<MovieDto>
    suspend fun getMovieVideos(movieId: Int): DataResult<VideosResultDto>
    suspend fun getMovieCredits(movieId: Int): DataResult<MovieCreditsDto>
    suspend fun getGenres(): DataResult<List<Genre>>

    suspend fun syncPopularMovies(): DataResult<DiscoverDto>
    suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto>

    suspend fun searchAndCacheMovies(
        searchQuery: String,
        filter: MovieSearchFilter,
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto>

    suspend fun discoverAndCacheMoviesWithFilter(
        filter: MovieSearchFilter,
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto>

}



