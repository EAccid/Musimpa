package com.eaccid.musimpa.ui.models


data class MovieUi(
    val id: Int = 0,
    val originalTitle: String? = null,
    val releaseDate: String? = null,
    val posterPath: String? = null,
    val title: String? = null,
    val overview: String? = null,
    val voteAverage: Double = 0.0, //percentage
    val tagline: String? = null,
    val runtime: Int = 0
)
