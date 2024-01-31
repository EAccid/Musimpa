package com.eaccid.musimpa.ui.movieslist

import com.eaccid.musimpa.ui.uientities.MovieItem

//todo add Loading
sealed class MoviesScreenViewState {
    data class Success(val movies: List<MovieItem>) : MoviesScreenViewState()
    data class Error(val exception: Throwable) : MoviesScreenViewState()
}