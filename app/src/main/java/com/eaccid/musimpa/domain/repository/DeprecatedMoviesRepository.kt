package com.eaccid.musimpa.domain.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.DiscoverDto
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.MovieApiService
import com.eaccid.musimpa.domain.model.MovieDiscoverAllQueryMap
import com.eaccid.musimpa.utils.toMovieEntity


interface DeprecatedMoviesRepository {
    suspend fun discoverAll(page: Int = 1): ApiResponse<DiscoverDto>
    suspend fun getMovie(movieId: Int): ApiResponse<MovieDto>
    suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult>
    suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits>
    suspend fun syncPopularMovies(): Boolean
    suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity>

    //try to think about this in a better way in separate interface
    suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): ApiResponse<DiscoverDto>
}

class DeprecatedMoviesRepositoryImpl(
    private val serviceAPI: MovieApiService,
    private val movieDatabase: MovieDatabase
) : DeprecatedMoviesRepository {

    override suspend fun discoverAll(page: Int): ApiResponse<DiscoverDto> {
        return discoverAll(MovieDiscoverAllQueryMap(page = page))
    }

    private suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto> {
        val params = query.toQueryMap()
        return safeApiRequest { serviceAPI.discoverAll(params) }
    }

    override suspend fun getMovie(movieId: Int): ApiResponse<MovieDto> {
        return safeApiRequest {
            serviceAPI.getMovie(movieId, emptyMap())
        }
    }

    override suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResult> {
        return safeApiRequest {
            serviceAPI.getMovieVideos(movieId, emptyMap())
        }
    }

    override suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCredits> {
        return safeApiRequest {
            serviceAPI.getMovieCredits(movieId, emptyMap())
        }
    }

    override suspend fun getLocalPagingSource(): PagingSource<Int, MovieEntity> {
        return movieDatabase.movieDao.pagingSource()
    }

    override suspend fun syncPopularMovies(): Boolean {
        return handleFetchAndCachePopularMovies(1, true) is ApiResponse.Success
    }

    override suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): ApiResponse<DiscoverDto> {
        return handleFetchAndCachePopularMovies(page, clearDataFirst)
    }

    private suspend fun handleFetchAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): ApiResponse<DiscoverDto> {
        val response = discoverAll(page)
        when (response) {
            is ApiResponse.Success -> {
                val movies = response.data.movies
                movieDatabase.withTransaction {
                    if (clearDataFirst) {
                        movieDatabase.movieDao.clearAll()
                    }
                    val movieEntities = movies.map { it.toMovieEntity(page = page) }
                    movieDatabase.movieDao.insertAll(movieEntities)
                }
                println("MovieSyncWorker: MoviesRepositoryImpl handleFetchAndCachePopularMovies Success")
            }

            is ApiResponse.Error -> Log.e("MoviesRepositoryImpl", "error: ${response.message}")
            is ApiResponse.NetworkError -> Log.e("MoviesRepositoryImpl", "Network error")
        }
        return response
    }
}