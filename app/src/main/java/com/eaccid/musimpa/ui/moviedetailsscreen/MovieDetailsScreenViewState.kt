package com.eaccid.musimpa.ui.moviedetailsscreen

import com.eaccid.musimpa.data.domain.Movie

sealed class MovieDetailsScreenViewState {
    data class Success(val movie: Movie) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable) : MovieDetailsScreenViewState()
    data object NoData : MovieDetailsScreenViewState()
}