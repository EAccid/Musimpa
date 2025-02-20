package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.entities.Discover
import com.eaccid.musimpa.network.ApiResponse
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.ui.uientities.MovieItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieListScreenViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(MovieListScreenViewState.Success(emptyList()))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<MovieListScreenViewState> = _uiState.asStateFlow()

    init {
        Log.i("MoviesViewModel twicetest ----------------- ", "$this is created 2")
        getDiscoverMovies()
    }

    private fun getDiscoverMovies() {
        viewModelScope.launch {
            val discover: ApiResponse<Discover> = moviesRepository.discoverAll()
            if (discover is ApiResponse.Success)
                _uiState.value = MovieListScreenViewState.Success(movies = discover.data.movies.map {
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
        Log.i("MoviesViewModel twicetest ----------------- ", "$this is cleared 2")
    }
}

class MovieListScreenViewModelFactory(
    private val moviesRepository: MoviesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieListScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieListScreenViewModel(moviesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
