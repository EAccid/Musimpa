package com.eaccid.musimpa.ui.movieslist.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesScreenViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(MoviesScreenViewState.Success(emptyList()))
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<MoviesScreenViewState> = _uiState.asStateFlow()

    init {
        getDiscoverMovies()
    }

    private fun getDiscoverMovies() {
        viewModelScope.launch {
            val discover: ApiResponse<Discover> = moviesRepository.discoverAll()
            if (discover is ApiResponse.Success)
                _uiState.value = MoviesScreenViewState.Success(movies = discover.data.movies.map {
                    MovieItem(
                        it.id,
                        it.originalTitle,
                        it.releaseDate,
                        it.posterPath,
                        it.title,
                        it.overview,
                        it.voteAverage,
                        it.tagline,
                        it.runtime
                    )
                })
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MusimpaApp", "MoviesViewModel is cleared")
    }
}