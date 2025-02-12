package com.eaccid.musimpa.ui.moviedetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.entities.Movie
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.utils.toMovieItem
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
        getMovieDetails(movieId)
    }

    private fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            val movieResponse: ApiResponse<Movie> =
                moviesRepository.getMovie(movieId = Integer.parseInt(movieId))
            if (movieResponse is ApiResponse.Success) {
                val movie = movieResponse.data
                val movieItem = movie.toMovieItem()
                val videosResponse =
                    moviesRepository.getMovieVideos(movieId = Integer.parseInt(movieId))
                if (videosResponse is ApiResponse.Success && videosResponse.data.results != null) {
                    for (video in videosResponse.data.results) {
                        if (video.official && video.key.isNotEmpty()) {
                            movieItem.videoKey = video.key
                        }
                    }
                }
                _uiState.value =
                    MovieDetailsScreenViewState.Success(movie = movieItem)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MusimpaApp", "MovieDetailsScreenViewModel is cleared")
    }
}