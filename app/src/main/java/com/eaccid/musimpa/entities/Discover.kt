package com.eaccid.musimpa.entities

import com.squareup.moshi.Json

data class Discover(
    val page: Long,
    @Json(name = "results")
    val movies: List<Movie>,
    @Json(name = "total_results")
    val totalResults: Long,
    @Json(name = "total_pages")
    val totalPages: Long
)