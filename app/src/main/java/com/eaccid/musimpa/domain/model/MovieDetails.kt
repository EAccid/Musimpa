package com.eaccid.musimpa.domain.model

data class MovieDetails(
    val movie: Movie,
    val cast: List<Actor>,
    val videoKey: String? = null
)