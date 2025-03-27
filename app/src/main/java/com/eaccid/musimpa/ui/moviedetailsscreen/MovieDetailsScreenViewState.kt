package com.eaccid.musimpa.ui.moviedetailsscreen

import com.eaccid.musimpa.ui.uientities.MovieItem

sealed class MovieDetailsScreenViewState {
    data class Success(val movie: MovieItem) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable) : MovieDetailsScreenViewState()
    data object NoData : MovieDetailsScreenViewState()
}