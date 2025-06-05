package com.eaccid.musimpa.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil.network.HttpException
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.domain.repository.MoviesRepository
import com.eaccid.musimpa.utils.toMovieEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val moviesRepository: MoviesRepository,
    private val movieDatabase: MovieDatabase
) : RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType, // refresh, new items from new page
        state: PagingState<Int, MovieEntity> // current page info
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success( // not using, adding only to the end of the list
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    /** better practice in bigger projects would be to store
                     * the pagination information in a separate table.
                     *  But, for this small test-project this complexity is not necessary.*/
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        println("------------mediator test lastItem.id = $lastItem.id to get api page---------------------")
                        lastItem.page + 1
                    }
                }
            }
            println("$loadType mediator test loadKey = $loadKey")
            when (val response = moviesRepository.discoverAll(loadKey)) {
                is ApiResponse.Success -> {
                    val movies = response.data.movies
                    movieDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            movieDatabase.movieDao.clearAll()
                        }
                        val movieEntities =
                            movies.map { it.toMovieEntity(page = loadKey) }
                        movieDatabase.movieDao.insertAll(movieEntities)
                    }
                    MediatorResult.Success(
                        endOfPaginationReached = movies.isEmpty() // might work but not safe: loadKey == response.data.totalPages,
                    )
                }

                is ApiResponse.Error -> MediatorResult.Error(Exception(response.message))
                ApiResponse.NetworkError -> MediatorResult.Error(Exception("Network error occurred"))
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }

    }
}