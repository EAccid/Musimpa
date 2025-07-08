package com.eaccid.musimpa.domain.usecase

import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieDetails
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.mappers.toActor
import com.eaccid.musimpa.domain.mappers.toMovie
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

interface GetMovieDetailsUseCase {
    suspend operator fun invoke(movieId: Int): DataResult<MovieDetails>
}

class GetMovieDetailsUseCaseImpl(
    private val moviesRepository: MoviesRepository
) : GetMovieDetailsUseCase {

    override suspend operator fun invoke(movieId: Int): DataResult<MovieDetails> {
        return try {
            coroutineScope {
                val movieDeferred = async { moviesRepository.getMovie(movieId) }
                val videosDeferred = async { moviesRepository.getMovieVideos(movieId) }
                val creditsDeferred = async { moviesRepository.getMovieCredits(movieId) }

                val (movieResult, videosResult, creditsResult) = awaitAll(
                    movieDeferred,
                    videosDeferred,
                    creditsDeferred
                )

                when {
                    movieResult is DataResult.Success &&
                            videosResult is DataResult.Success &&
                            creditsResult is DataResult.Success -> {

                        val movie = (movieResult.data as MovieDto).toMovie()
                        val videos = (videosResult.data as VideosResultDto).results ?: emptyList()
                        val cast = (creditsResult.data as MovieCreditsDto).cast.map { it.toActor() }

                        // Find official video key
                        val officialVideoKey = videos.firstOrNull { video ->
                            video.official && video.key.isNotEmpty()
                        }?.key.orEmpty()

                        val movieDetails = MovieDetails(
                            movie = movie,
                            cast = cast,
                            videoKey = officialVideoKey
                        )

                        DataResult.Success(movieDetails)
                    }

                    movieResult is DataResult.NetworkError ||
                            videosResult is DataResult.NetworkError ||
                            creditsResult is DataResult.NetworkError -> {
                        DataResult.NetworkError
                    }

                    movieResult is DataResult.Failure -> {
                        DataResult.Failure(
                            movieResult.error,
                            "Failed to get movie: ${movieResult.message}"
                        )
                    }

                    videosResult is DataResult.Failure -> {
                        DataResult.Failure(
                            videosResult.error,
                            "Failed to get videos: ${videosResult.message}"
                        )
                    }

                    creditsResult is DataResult.Failure -> {
                        DataResult.Failure(
                            creditsResult.error,
                            "Failed to get credits: ${creditsResult.message}"
                        )
                    }

                    else -> {
                        DataResult.Failure(
                            Exception("Unknown error"),
                            "Failed to get movie details"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            DataResult.Failure(e, "Failed to get movie details: ${e.message}")
        }
    }
}
