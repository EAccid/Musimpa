package com.eaccid.musimpa.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.paging.MovieSearchRemoteMediator
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
import kotlinx.coroutines.flow.Flow

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

    @OptIn(ExperimentalPagingApi::class)
    override fun getDiscoverMoviesPager(): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "discover"
            ),
            pagingSourceFactory = { local.getDiscoverMoviesPagingSource() }
        )
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun getGenreMoviesPager(genreId: Int): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "genre",
                genreId = genreId
            ),
            pagingSourceFactory = { local.getGenreMoviesPagingSource(genreId.toString()) }
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
                try {
                    val movies = discoverDto.movies
                    val movieEntities = movies.map { dto ->
                        dto.toMovieEntity(
                            page = page,
                            searchQuery = searchQuery,
                            searchType = if (searchQuery.isBlank()) "discover" else "search",
                            genreIds = filter.selectedGenreIds
                        )
                    }

                    if (clearDataFirst) {
                        local.clearBySearchTypeAndQuery(
                            if (searchQuery.isBlank()) "discover" else "search",
                            searchQuery
                        )
                    }

                    local.cacheMovies(movieEntities)
                    DataResult.Success(discoverDto)
                } catch (e: Exception) {
                    DataResult.Failure(e, "Failed to cache search results")
                }
            }
        )
    }

    override suspend fun getGenres(): DataResult<List<Genre>> {
        return remote.getGenres().handleReturn(
            onSuccess = { genreListDto ->
                val genres: List<Genre> = genreListDto.genres.map { Genre(it.id, it.name) }
                DataResult.Success(genres)
            }
        )
    }

    override fun getMoviesWithFilter(filter: Flow<MovieSearchFilter>): Flow<PagingData<MovieEntity>> {
        TODO("Not yet implemented")
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchMoviesPager(
        searchQuery: String,
        filter: MovieSearchFilter
    ): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "search",
                searchQuery = searchQuery,
                filter = filter
            ),
            pagingSourceFactory = { local.getSearchMoviesPagingSource(searchQuery) }
        )
    }


//---------------


//    @OptIn(ExperimentalPagingApi::class)
//    override fun getSearchMoviesPager(
//        searchQuery: String,
//        filter: MovieSearchFilter
//    ): Pager<Int, MovieEntity> {
//        return Pager(
//            config = PagingConfig(pageSize = 20),
//            remoteMediator = MovieSearchRemoteMediator(
//                repository = this,
//                searchType = "search",
//                searchQuery = searchQuery,
//                filter = filter
//            ),
//            pagingSourceFactory = { movieDao.getAllMovies() }
//        )
//    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getDiscoverMoviesPager(
        filter: MovieSearchFilter?
    ): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "discover",
                filter = filter ?: MovieSearchFilter()
            ),
            pagingSourceFactory = { local.getDiscoverMoviesPagingSource() }
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getGenreMoviesPager(genreIds: List<Int>): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "genre",
                filter = MovieSearchFilter(selectedGenreIds = genreIds)
            ),
            pagingSourceFactory = { local.getDiscoverMoviesPagingSource() } //????
        )
    }

    // Add this method to handle filtered discover
    @OptIn(ExperimentalPagingApi::class)
    override fun getDiscoverMoviesWithFilter(filter: MovieSearchFilter): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieSearchRemoteMediator(
                repository = this,
                searchType = "discover",
                filter = filter
            ),
            pagingSourceFactory = { local.getDiscoverMoviesPagingSource() } //????
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
                    local.cachePopularMovies(movieEntities, clearDataFirst)
                    DataResult.Success(discoverDto)
                },
                onError = { error, message -> DataResult.Failure(Exception("dsds")) },
                onNetworkError = { DataResult.NetworkError }
            )
        } catch (e: Exception) {
            DataResult.Failure(e)
        }


    }

}