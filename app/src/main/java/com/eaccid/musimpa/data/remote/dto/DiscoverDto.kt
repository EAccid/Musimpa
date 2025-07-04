package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class DiscoverDto(
    val page: Int = 0,
    @Json(name = "results")
    val movies: List<MovieDto>,
    @Json(name = "total_results")
    val totalResults: Int = 0,
    @Json(name = "total_pages")
    val totalPages: Int = 0
)