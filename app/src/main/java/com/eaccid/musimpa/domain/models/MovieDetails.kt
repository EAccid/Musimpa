package com.eaccid.musimpa.domain.models

data class MovieDetails(
    val movie: Movie,
    val cast: List<Actor>,
    val videoKey: String = ""
)