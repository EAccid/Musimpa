package com.eaccid.musimpa.ui.moviedetailsscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.entities.MovieCredits
import com.eaccid.musimpa.data.remote.entities.MovieDto
import com.eaccid.musimpa.data.remote.entities.VideosResult
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.utils.toActor
import com.eaccid.musimpa.utils.toMovie
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsScreenViewModel(
    state: SavedStateHandle,
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val movieId: String = checkNotNull(state["movieId"])

    // Backing property to avoid state updates from other classes
    private val _uiState: MutableStateFlow<MovieDetailsScreenViewState> =
        MutableStateFlow(MovieDetailsScreenViewState.NoData)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<MovieDetailsScreenViewState> = _uiState.asStateFlow()

    init {
        Log.i("MusimpaApp", "MovieDetailsScreenViewModel movie ${movieId} from state")
        Log.i("MovieDetailsScreenViewModel", " $this is created 3")
        getMovieDetails(movieId)
    }

    //TODO temp better to combine video to movie in repository?
    private fun getMovieDetails(movieId: String) {
        _uiState.value = MovieDetailsScreenViewState.Loading
        viewModelScope.launch {
            // coroutineScope used to await all responses before the main action in viewModelScope
            coroutineScope {
                val movieDtoDeferred = async {
                    moviesRepository.getMovie(movieId = Integer.parseInt(movieId))
                }
                val videosResponseDeferred = async {
                    moviesRepository.getMovieVideos(movieId = Integer.parseInt(movieId))
                }
                val creditResponseDeferred = async {
                    moviesRepository.getMovieCredits(movieId = Integer.parseInt(movieId))
                }
                val (movieDtoResponse, videosResponse, creditResponse) = awaitAll(
                    movieDtoDeferred,
                    videosResponseDeferred,
                    creditResponseDeferred
                )
                when {
                    movieDtoResponse is ApiResponse.Success && videosResponse is ApiResponse.Success && creditResponse is ApiResponse.Success -> {
                        val movie = (movieDtoResponse.data as MovieDto).toMovie()
                        val movieVideos = (videosResponse.data as VideosResult).results
                        if (movieVideos != null) {
                            for (video in movieVideos) {
                                if (video.official && video.key.isNotEmpty()) {
                                    movie.videoKey = video.key
                                    break // Assuming only one official video is needed
                                }
                            }
                        }
                        val cast =
                            (creditResponse.data as MovieCredits).cast.map { actorDto -> actorDto.toActor() }
                        _uiState.value =
                            MovieDetailsScreenViewState.Success(movie = movie, cast = cast)
                    }

                    movieDtoResponse is ApiResponse.NetworkError -> {
                        //handle network error
                        TODO()
                    }

                    movieDtoResponse is ApiResponse.Error -> {
                        _uiState.value = MovieDetailsScreenViewState.Error(movieDtoResponse.error)
                    }

                    videosResponse is ApiResponse.Error -> {
                        _uiState.value = MovieDetailsScreenViewState.Error(videosResponse.error)
                    }

                    creditResponse is ApiResponse.Error -> {
                        _uiState.value = MovieDetailsScreenViewState.Error(creditResponse.error)
                        Log.e(
                            "ApiResponseError",
                            creditResponse.error?.message ?: "creditResponse error"
                        )
                    }

                    else -> {
                        _uiState.value = MovieDetailsScreenViewState.NoData
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i(
            "MovieDetailsScreenViewModel", " $this is cleared 3"
        )
    }
}