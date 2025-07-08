package com.eaccid.musimpa.data.repository

import androidx.paging.PagingSource
import com.eaccid.musimpa.data.local.room.MovieEntity

interface MoviesLocalDataSource {
    suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity>
    suspend fun cachePopularMovies(moviesEntity: List<MovieEntity>, clearDataFirst: Boolean)
    fun getSearchMoviesPagingSource(query: String): PagingSource<Int, MovieEntity>
    fun getDiscoverMoviesPagingSource(): PagingSource<Int, MovieEntity>
}

