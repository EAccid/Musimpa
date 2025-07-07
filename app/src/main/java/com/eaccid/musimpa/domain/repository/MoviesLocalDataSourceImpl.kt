package com.eaccid.musimpa.domain.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.repository.MoviesLocalDataSource

class MoviesLocalDataSourceImpl(
    private val moviesDatabase: MovieDatabase
) : MoviesLocalDataSource {

    override suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity> =
        moviesDatabase.movieDao.pagingSource()

    override suspend fun cachePopularMovies(
        moviesEntity: List<MovieEntity>,
        clearDataFirst: Boolean
    ) {
        moviesDatabase.withTransaction {
            if (clearDataFirst) {
                moviesDatabase.movieDao.clearAll()
            }
            moviesDatabase.movieDao.insertAll(moviesEntity)
        }
    }
}