package com.eaccid.musimpa.domain.usecase

import com.eaccid.musimpa.data.remote.dto.ActorDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideoDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieDetails
import com.eaccid.musimpa.domain.repository.MoviesRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieDetailsUseCaseImplTest {

    private val moviesRepository = mockk<MoviesRepository>()
    private lateinit var useCase: GetMovieDetailsUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetMovieDetailsUseCaseImpl(moviesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return success when all repository calls succeed`() = runTest {
        val movieId = 1
        val movieDto = MovieDto(id = movieId, title = "Movie")
        val videosDto = VideosResultDto(results = listOf(VideoDto(key = "abc123", official = true)))
        val creditsDto = MovieCreditsDto(cast = listOf(ActorDto(id = 1, name = "Actor")))

        coEvery { moviesRepository.getMovie(movieId) } returns DataResult.Success(movieDto)
        coEvery { moviesRepository.getMovieVideos(movieId) } returns DataResult.Success(videosDto)
        coEvery { moviesRepository.getMovieCredits(movieId) } returns DataResult.Success(creditsDto)

        // When
        val result = useCase(movieId)

        // Then
        result.shouldBeInstanceOf<DataResult.Success<MovieDetails>>()
        val details = (result as DataResult.Success).data
        details.movie.id shouldBe movieId
        details.cast shouldHaveSize 1
        details.videoKey shouldBe "abc123"
    }

    @Test
    fun `should return network error if any call fails with network error`() = runTest {
        coEvery { moviesRepository.getMovie(any()) } returns DataResult.NetworkError
        coEvery { moviesRepository.getMovieVideos(any()) } returns DataResult.Success(
            VideosResultDto()
        )
        coEvery { moviesRepository.getMovieCredits(any()) } returns DataResult.Success(
            MovieCreditsDto()
        )

        val result = useCase.invoke(1)

        result shouldBe DataResult.NetworkError
    }

    @Test
    fun `should return failure if getMovie fails`() = runTest {
        val error = Exception("404")
        coEvery { moviesRepository.getMovie(any()) } returns DataResult.Failure(error, "Not found")
        coEvery { moviesRepository.getMovieVideos(any()) } returns DataResult.Success(
            VideosResultDto()
        )
        coEvery { moviesRepository.getMovieCredits(any()) } returns DataResult.Success(
            MovieCreditsDto()
        )

        val result = useCase.invoke(1)

        result.shouldBeInstanceOf<DataResult.Failure>()
        (result as DataResult.Failure).message shouldContain "Failed to get movie"
    }

    @Test
    fun `should return failure if getMovieVideos fails`() = runTest {
        val error = Exception("Video error")
        coEvery { moviesRepository.getMovie(any()) } returns DataResult.Success(MovieDto())
        coEvery { moviesRepository.getMovieVideos(any()) } returns DataResult.Failure(
            error,
            "Video failed"
        )
        coEvery { moviesRepository.getMovieCredits(any()) } returns DataResult.Success(
            MovieCreditsDto()
        )

        val result = useCase.invoke(1)

        result.shouldBeInstanceOf<DataResult.Failure>()
        (result as DataResult.Failure).message shouldContain "Failed to get videos"
    }

    @Test
    fun `should return failure if exception is thrown`() = runTest {
        coEvery { moviesRepository.getMovie(any()) } throws RuntimeException("Test Exception")

        val result = useCase.invoke(1)

        result.shouldBeInstanceOf<DataResult.Failure>()
        (result as DataResult.Failure).message shouldContain "Failed to get movie details"
    }
}