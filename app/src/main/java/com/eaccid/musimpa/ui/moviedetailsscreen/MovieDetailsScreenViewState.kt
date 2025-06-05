package com.eaccid.musimpa.ui.moviedetailsscreen

import com.eaccid.musimpa.domain.model.Actor
import com.eaccid.musimpa.domain.model.Movie

//TODO rethink status
sealed class MovieDetailsScreenViewState {
    data class Success(val movie: Movie, val cast: List<Actor>) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable? = null) : MovieDetailsScreenViewState()
    data object NoData : MovieDetailsScreenViewState()
    data object Loading : MovieDetailsScreenViewState()
}