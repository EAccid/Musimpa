package com.eaccid.musimpa.entities

import com.squareup.moshi.Json

data class Discover(
    val page: Int,
    @Json(name = "results")
    val movies: List<Movie>,
    @Json(name = "total_results")
    val totalResults: Int,
    @Json(name = "total_pages")
    val totalPages: Int
)