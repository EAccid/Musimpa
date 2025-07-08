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
        return movieDao.getDiscoverMoviesPagingSource() //temporary
    }

    override fun getSearchMoviesPagingSource(query: String): PagingSource<Int, MovieEntity> {
        return movieDao.getDiscoverMoviesPagingSource() //temporary
    }

}