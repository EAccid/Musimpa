package com.eaccid.musimpa.ui.moviedetailsscreen

import com.eaccid.musimpa.ui.models.MovieDetailsUi

sealed class MovieDetailsScreenViewState {
    data class Success(val movieDetails: MovieDetailsUi) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable? = null) : MovieDetailsScreenViewState()
    data object NoData : MovieDetailsScreenViewState()
    data object Loading : MovieDetailsScreenViewState()
}