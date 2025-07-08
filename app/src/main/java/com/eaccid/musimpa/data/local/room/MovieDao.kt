package com.eaccid.musimpa.data.local.room

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

    //TODO alter MovieEntity or create MovieCacheJoin
    @Query("SELECT * FROM movieentity")
    fun getDiscoverMoviesPagingSource(): PagingSource<Int, MovieEntity>
//
//    @Query("SELECT * FROM movieentity WHERE searchType = 'search' AND searchQuery = :query ORDER BY page ASC, localId ASC")
//    fun getSearchMoviesPagingSource(query: String): PagingSource<Int, MovieEntity>
//
//    @Query("SELECT * FROM movieentity WHERE searchType = 'genre' AND genreIds LIKE '%' || :genreId || '%' ORDER BY page ASC, localId ASC")
//    fun getGenreMoviesPagingSource(genreId: String): PagingSource<Int, MovieEntity>
//
//    @Query("SELECT * FROM movieentity WHERE searchType = 'combined' AND searchQuery = :query AND genreIds LIKE '%' || :genreId || '%' ORDER BY page ASC, localId ASC")
//    fun getCombinedFilterPagingSource(
//        query: String,
//        genreId: String
//    ): PagingSource<Int, MovieEntity>
//
//    @Query("DELETE FROM movieentity WHERE searchType = :searchType")
//    suspend fun clearBySearchType(searchType: String)
//
//    @Query("DELETE FROM movieentity WHERE searchType = :searchType AND searchQuery = :query")
//    suspend fun clearBySearchTypeAndQuery(searchType: String, query: String)


}