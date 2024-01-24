package com.eaccid.musimpa.ui.movieslist.ui.theme

//todo add Loading
sealed class MoviesScreenViewState {
    data class Success(val movies: List<MovieItem>) : MoviesScreenViewState()
    data class Error(val exception: Throwable) : MoviesScreenViewState()
}