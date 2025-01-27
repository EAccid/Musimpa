package com.eaccid.musimpa.ui.moviedetails

import com.eaccid.musimpa.ui.uientities.MovieItem

sealed class MovieDetailsScreenViewState {
    data class Success(val movie: MovieItem) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable) : MovieDetailsScreenViewState()
    object NoData : MovieDetailsScreenViewState()
}