package com.eaccid.musimpa.data.repository

import androidx.paging.PagingSource
import com.eaccid.musimpa.data.local.room.MovieEntity

interface MoviesLocalDataSource {
    suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity>
    suspend fun cachePopularMovies(moviesEntity: List<MovieEntity>, clearDataFirst: Boolean)
    fun getDiscoverMoviesPagingSource(): PagingSource<Int, MovieEntity>
    fun getSearchMoviesPagingSource(query: String): PagingSource<Int, MovieEntity>
    fun getGenreMoviesPagingSource(genreId: String): PagingSource<Int, MovieEntity>
    fun getCombinedFilterPagingSource(
        query: String,
        genreId: String
    ): PagingSource<Int, MovieEntity>

    suspend fun clearBySearchType(searchType: String)
    suspend fun clearBySearchTypeAndQuery(searchType: String, query: String)
    suspend fun getMovieCount(searchType: String, query: String): Int
    suspend fun cacheMovies(movieEntities: List<MovieEntity>)
}

