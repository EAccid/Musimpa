package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.common.handle
import com.eaccid.musimpa.domain.common.toDataResult
import com.eaccid.musimpa.utils.toMovieEntity

interface MoviesRepository {
    suspend fun discoverAll(page: Int = 1): DataResult<DiscoverDto>
    suspend fun getMovie(movieId: Int): DataResult<MovieDto>
    suspend fun getMovieVideos(movieId: Int): DataResult<VideosResultDto>
    suspend fun getMovieCredits(movieId: Int): DataResult<MovieCreditsDto>

    suspend fun syncPopularMovies(): DataResult<DiscoverDto>
    suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto>
}

class MoviesRepositoryImpl(
    private val remote: MoviesRemoteDataSource,
    private val local: MoviesLocalDataSource
) : MoviesRepository {
    override suspend fun discoverAll(page: Int): DataResult<DiscoverDto> {
        val params = MovieDiscoverAllQueryMap(page = page)
        return remote.discoverAll(params).toDataResult()
    }

    override suspend fun getMovie(movieId: Int): DataResult<MovieDto> {
        return remote.getMovie(movieId).toDataResult()
    }

    override suspend fun getMovieVideos(movieId: Int): DataResult<VideosResultDto> {
        return remote.getMovieVideos(movieId).toDataResult()
    }

    override suspend fun getMovieCredits(movieId: Int): DataResult<MovieCreditsDto> {
        return remote.getMovieCredits(movieId).toDataResult()
    }

    override suspend fun syncPopularMovies(): DataResult<DiscoverDto> {
        return handleFetchAndCachePopularMovies(1, true)
    }

    override suspend fun discoverAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto> {
        return handleFetchAndCachePopularMovies(page, clearDataFirst)
    }

    private suspend fun handleFetchAndCachePopularMovies(
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto> {
        return remote.discoverAll(page).handle(
            onSuccess = { discoverDto ->
                try {
                    val movies = discoverDto.movies
                    val movieEntities = movies.map { it.toMovieEntity(page) }
                    local.cachePopularMovies(movieEntities, clearDataFirst)
                    println("MovieSyncWorker: MoviesRepositoryImpl handleFetchAndCachePopularMovies Success")
                    DataResult.Success(discoverDto)
                } catch (e: Exception) {
                    println("MoviesRepositoryImpl Local caching error: ${e.message}")
                    DataResult.Failure(e, "Failed to cache data")
                }
            },
            onError = { error, message ->
                println("MoviesRepositoryImpl API Error: $message")
                DataResult.Failure(error ?: Exception(message ?: "Unknown"), message)
            },
            onNetworkError = {
                println("MoviesRepositoryImpl Network error")
                DataResult.NetworkError
            }
        )
    }

}