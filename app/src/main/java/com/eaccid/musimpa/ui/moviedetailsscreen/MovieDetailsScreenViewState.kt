package com.eaccid.musimpa.ui.moviedetailsscreen

import com.eaccid.musimpa.data.domain.Actor
import com.eaccid.musimpa.data.domain.Movie

//TODO rethink status. Its useless for now
sealed class MovieDetailsScreenViewState {
    data class Success(val movie: Movie, val cast: List<Actor>) : MovieDetailsScreenViewState()
    data class Error(val exception: Throwable? = null) : MovieDetailsScreenViewState()
    data object NoData : MovieDetailsScreenViewState()
    data object Loading : MovieDetailsScreenViewState()
}