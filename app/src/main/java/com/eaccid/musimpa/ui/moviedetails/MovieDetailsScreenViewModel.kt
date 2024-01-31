package com.eaccid.musimpa.ui.moviedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaccid.musimpa.repository.MoviesRepository
import kotlinx.coroutines.launch

class MovieDetailsScreenViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    init {

    }

    private fun getMovieDetails() {
        viewModelScope.launch {

        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MusimpaApp", "MovieDetailsScreenViewModel is cleared")
    }
}