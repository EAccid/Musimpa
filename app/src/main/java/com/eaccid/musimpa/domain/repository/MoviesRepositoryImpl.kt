package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap
import com.eaccid.musimpa.data.repository.MoviesLocalDataSource
import com.eaccid.musimpa.data.repository.MoviesRemoteDataSource
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.common.handle
import com.eaccid.musimpa.domain.common.handleReturn
import com.eaccid.musimpa.domain.common.toDataResult
import com.eaccid.musimpa.domain.mappers.toMovieEntity
import com.eaccid.musimpa.domain.models.Genre
import com.eaccid.musimpa.domain.models.MovieSearchFilter

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

    override suspend fun getGenres(): DataResult<List<Genre>> {
        return remote.getGenres().handleReturn(
            onSuccess = { genreListDto ->
                val genres: List<Genre> = genreListDto.genres.map { Genre(it.id, it.name) }
                DataResult.Success(genres)
            },
            onError = { error, message ->
                DataResult.Failure(error ?: Exception(message ?: "Unknown"), message)
            },
            onNetworkError = {
                DataResult.NetworkError
            }
        )
    }

    override suspend fun searchAndCacheMovies(
        searchQuery: String,
        filter: MovieSearchFilter,
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto> {
        return remote.searchMovies(searchQuery, page, filter).handle(
            onSuccess = { discoverDto ->
                val movies = discoverDto.movies
                val movieEntities = movies.map { dto ->
                    dto.toMovieEntity(
                        page = page
                    )
                }
                // TODO cache by search! clearBySearchTypeAndQuery
                local.cachePopularMovies(movieEntities, clearDataFirst) //temp
                DataResult.Success(discoverDto)
            }
        )
    }


    override suspend fun discoverAndCacheMoviesWithFilter(
        filter: MovieSearchFilter,
        page: Int,
        clearDataFirst: Boolean
    ): DataResult<DiscoverDto> {
        return try {
            return remote.searchMovies(filter = filter, page = page).handle(
                onSuccess = { discoverDto ->
                    val movieEntities = discoverDto.movies.map { movieDto ->
                        movieDto.toMovieEntity().copy(page = page)
                    }
                    // TODO cache with filter query
                    local.cachePopularMovies(movieEntities, clearDataFirst)  //temp
                    DataResult.Success(discoverDto)
                },
                onError = { error, message ->
                    DataResult.Failure(
                        error ?: Exception(
                            message ?: "Unknown"
                        ), message
                    )
                },
                onNetworkError = { DataResult.NetworkError }
            )
        } catch (e: Exception) {
            DataResult.Failure(e)
        }

    }

}