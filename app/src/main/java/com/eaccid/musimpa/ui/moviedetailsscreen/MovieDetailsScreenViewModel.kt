package com.eaccid.musimpa.ui.moviedetailsscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.usecase.GetMovieDetailsUseCase
import com.eaccid.musimpa.ui.mappers.toMovieDetailsUi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsScreenViewModel(
    state: SavedStateHandle,
    private val movieDetailsUseCase: GetMovieDetailsUseCase
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

    private fun getMovieDetails(movieId: String) {
        _uiState.value = MovieDetailsScreenViewState.Loading
        viewModelScope.launch {
            when (val result = movieDetailsUseCase(movieId.toInt())) {
                is DataResult.Success -> {
                    val movieDetails = result.data
                    _uiState.value = MovieDetailsScreenViewState.Success(
                        movieDetails = movieDetails.toMovieDetailsUi()
                    )
                }

                is DataResult.Failure -> {
                    _uiState.value = MovieDetailsScreenViewState.Error(result.error)
                }

                is DataResult.NetworkError -> {
                    _uiState.value = MovieDetailsScreenViewState.Error(Exception("Network error"))
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