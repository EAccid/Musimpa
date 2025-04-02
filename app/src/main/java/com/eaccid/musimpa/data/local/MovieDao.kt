package com.eaccid.musimpa.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MovieDao {

    @Upsert
    suspend fun insertAll(movies: List<MovieEntity>)

    //int - page number
    @Query("SELECT * FROM movieentity")
    fun pagingSource(): PagingSource<Int, MovieEntity>

    @Query("DELETE FROM movieentity")
    suspend fun clearAll()

}