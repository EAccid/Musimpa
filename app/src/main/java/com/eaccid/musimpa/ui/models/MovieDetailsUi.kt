package com.eaccid.musimpa.ui.models

data class MovieDetailsUi(
    val movie: MovieUi,
    val cast: List<ActorUi>,
    val videoKey: String = ""
)