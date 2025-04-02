package com.eaccid.musimpa.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil.network.HttpException
import com.eaccid.musimpa.data.local.MovieDatabase
import com.eaccid.musimpa.data.local.MovieEntity
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.utils.toMovieEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val moviesRepository: MoviesRepository, //TmdbServiceAPI
    private val movieDatabase: MovieDatabase
) : RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType,//refresh, new items from new page
        state: PagingState<Int, MovieEntity>//current page info
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(//not using, adding only to the end of the list
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1 //rethink TODO!!!!
                    }
                }
            }
            //TODO refator, its just temp to try new room + paging
            when (val response = moviesRepository.discoverAll(loadKey)) {
                is ApiResponse.Success -> {
                    val movies = response.data.movies
                    movieDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            movieDatabase.movieDao.clearAll()
                        }
                        val movieEntities = movies.map { it.toMovieEntity() }
                        movieDatabase.movieDao.insertAll(movieEntities)
                    }
                    MediatorResult.Success(
                        endOfPaginationReached = movies.isEmpty()
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