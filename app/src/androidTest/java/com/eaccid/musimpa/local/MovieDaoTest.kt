package com.eaccid.musimpa.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eaccid.musimpa.data.local.room.MovieDao
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.local.room.MovieEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    private lateinit var database: MovieDatabase
    private lateinit var dao: MovieDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MovieDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.movieDao
    }

    @Test
    fun insertAllAndQueryReturnsCorrectData() = runTest {
        val movie = MovieEntity(apiId = 1, originalTitle = "Title", title = "Title")
        dao.insertAll(listOf(movie))
        val result = dao.getAllMovies().first()
        assertEquals(true, result.contains(movie))
    }

    @Test
    fun insertAllAndQueryReturnsCorrectDataWithPagingSource() = runTest {
        val movie = MovieEntity(apiId = 1, originalTitle = "Title", title = "Title")
        dao.insertAll(listOf(movie))
        val pagingSource = dao.pagingSource()
        pagingSource.invalidate()
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = 0, loadSize = 1, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page
        assertEquals(true, result.data.contains(movie))
    }

    @After
    fun teardown() {
        database.close()
    }

}