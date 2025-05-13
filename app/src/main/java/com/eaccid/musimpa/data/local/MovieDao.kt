package com.eaccid.musimpa.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Upsert
    suspend fun insertAll(movies: List<MovieEntity>)

    //int - page number
    // delete this if pagination information in a separate table
    @Query("SELECT * FROM movieentity")
    fun pagingSource(): PagingSource<Int, MovieEntity>

    @Query("DELETE FROM movieentity")
    suspend fun clearAll()

    @Query("SELECT * FROM movieentity ORDER BY localId ASC")
    fun getAllMovies(): Flow<List<MovieEntity>>

}