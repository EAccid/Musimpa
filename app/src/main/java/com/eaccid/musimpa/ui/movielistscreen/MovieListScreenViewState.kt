package com.eaccid.musimpa.ui.movielistscreen

import com.eaccid.musimpa.ui.uientities.MovieItem

//todo add Loading
sealed class MovieListScreenViewState {
    data class Success(val movies: List<MovieItem>) : MovieListScreenViewState()
    data class Error(val exception: Throwable) : MovieListScreenViewState()
}