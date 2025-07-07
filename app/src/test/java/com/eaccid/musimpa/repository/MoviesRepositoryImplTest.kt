package com.eaccid.musimpa.repository

import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.common.toDataResult
import com.eaccid.musimpa.data.repository.MoviesLocalDataSource
import com.eaccid.musimpa.data.repository.MoviesRemoteDataSource
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.repository.MoviesRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@ExperimentalCoroutinesApi
class MoviesRepositoryImplTest {

    private val remote = mockk<MoviesRemoteDataSource>()
    private val local = mockk<MoviesLocalDataSource>(relaxed = true)

    private lateinit var repository: MoviesRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = MoviesRepositoryImpl(remote, local)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `discoverAll returns success when remote call succeeds`() = runTest {
        val fakeDto = DiscoverDto(movies = listOf(MovieDto()))
        val fakeQuery = MovieDiscoverAllQueryMap(page = 1)
        coEvery { remote.discoverAll(fakeQuery) } returns ApiResponse.Success(fakeDto)

        val result = repository.discoverAll(1)

        assertTrue(result is DataResult.Success)
        assertEquals(fakeDto, (result as DataResult.Success).data)
    }

    @Test
    fun `getMovie returns failure when remote call fails`() = runTest {
        coEvery { remote.getMovie(any()) } returns ApiResponse.NetworkError

        val result = repository.getMovie(123)

        assertTrue(result is DataResult.NetworkError)
    }

    @Test
    fun `syncPopularMovies caches data and returns success`() = runTest {
        val fakeDto = DiscoverDto(movies = listOf(MovieDto(id = 1, title = "Test")))

        coEvery { remote.discoverAll(1) } returns ApiResponse.Success(fakeDto)
        coEvery { local.cachePopularMovies(any(), any()) } just Runs

        val result = repository.syncPopularMovies()

        assertTrue(result is DataResult.Success)
        coVerify { local.cachePopularMovies(match { it.first().apiId == 1 }, true) }
    }

    @Test
    fun `discoverAndCachePopularMovies returns failure when remote error occurs`() = runTest {
        coEvery { remote.discoverAll(1) } returns ApiResponse.Error(500, "Server error", null)

        val result = repository.discoverAndCachePopularMovies(1, false)

        assertTrue(result is DataResult.Failure)
        assertEquals("Server error", (result as DataResult.Failure).message)
    }

    @Test
    fun `handleFetchAndCachePopularMovies returns failure if caching throws`() = runTest {
        val fakeDto = DiscoverDto(movies = listOf(MovieDto(id = 1, title = "Test")))
        coEvery { remote.discoverAll(1) } returns ApiResponse.Success(fakeDto)
        coEvery { local.cachePopularMovies(any(), any()) } throws RuntimeException("DB write error")

        val result = repository.discoverAndCachePopularMovies(1, false)

        assertTrue(result is DataResult.Failure)
        assertTrue((result as DataResult.Failure).message?.contains("Failed to cache data") == true)
    }

    @Test
    fun `toDataResult converts network error correctly`() {
        val apiResponse: ApiResponse<String> = ApiResponse.NetworkError
        val result = apiResponse.toDataResult()
        assertTrue(result is DataResult.NetworkError)
    }

}