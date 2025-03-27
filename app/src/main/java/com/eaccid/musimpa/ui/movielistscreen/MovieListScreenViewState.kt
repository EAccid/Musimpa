package com.eaccid.musimpa.ui.movielistscreen

//todo add Loading
sealed class MovieListScreenViewState {
    data object Success : MovieListScreenViewState()
    data class Error(val exception: Throwable) : MovieListScreenViewState()
}