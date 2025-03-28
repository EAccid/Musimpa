package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.eaccid.musimpa.utils.toMovieItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// todo delete as deprecated, changed to paging
class MovieListScreenViewModelDELETE(private val moviesRepository: MoviesRepository) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(MovieListScreenViewState.Success)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<MovieListScreenViewState> = _uiState.asStateFlow()

    private val _movies = MutableStateFlow(listOf<MovieItem>())
    val movies: StateFlow<List<MovieItem>>
        get() = _movies.asStateFlow()

    init {
        Log.i("MoviesViewModel temptest ----------------- ", "$this is created 2")
        getDiscoverMovies()
    }

    private fun getDiscoverMovies() {
        viewModelScope.launch {
            when (val discover: ApiResponse<Discover> = moviesRepository.discoverAll()) {
                is ApiResponse.Success -> {
                    _uiState.value = MovieListScreenViewState.Success
                    _movies.value = discover.data.movies.map {
                        it.toMovieItem()
                    }
                }

                is ApiResponse.Error -> TODO()
                ApiResponse.NetworkError -> TODO()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}