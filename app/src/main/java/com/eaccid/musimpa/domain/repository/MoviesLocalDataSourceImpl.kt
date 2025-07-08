package com.eaccid.musimpa.domain.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.repository.MoviesLocalDataSource

class MoviesLocalDataSourceImpl(
    private val moviesDatabase: MovieDatabase
) : MoviesLocalDataSource {

    private val movieDao = moviesDatabase.movieDao

    override suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity> =
        movieDao.pagingSource()

    override suspend fun cachePopularMovies(
        moviesEntity: List<MovieEntity>,
        clearDataFirst: Boolean
    ) {
        moviesDatabase.withTransaction {
            if (clearDataFirst) {
                movieDao.clearAll()
            }
            movieDao.insertAll(moviesEntity)
        }
    }

    override fun getDiscoverMoviesPagingSource(): PagingSource<Int, MovieEntity> {
        return movieDao.getDiscoverMoviesPagingSource()
    }

    override fun getSearchMoviesPagingSource(query: String): PagingSource<Int, MovieEntity> {
        return movieDao.getSearchMoviesPagingSource(query)
    }

    override fun getGenreMoviesPagingSource(genreId: String): PagingSource<Int, MovieEntity> {
        return movieDao.getGenreMoviesPagingSource(genreId)
    }

    override fun getCombinedFilterPagingSource(
        query: String,
        genreId: String
    ): PagingSource<Int, MovieEntity> {
        return movieDao.getCombinedFilterPagingSource(query, genreId)
    }

    override suspend fun clearBySearchType(searchType: String) {
        movieDao.clearBySearchType(searchType)
    }

    override suspend fun clearBySearchTypeAndQuery(searchType: String, query: String) {
        movieDao.clearBySearchTypeAndQuery(searchType, query)
    }

    override suspend fun getMovieCount(searchType: String, query: String): Int {
        return movieDao.getMovieCount(searchType, query)
    }

    override suspend fun cacheMovies(movieEntities: List<MovieEntity>) {
        movieDao.insertAll(movieEntities)
    }

}