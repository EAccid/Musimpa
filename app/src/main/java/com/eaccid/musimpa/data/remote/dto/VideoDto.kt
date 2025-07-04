package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class VideoDto(
    val id: String,

    @Json(name = "iso_639_1")
    val iso639_1: String,

    @Json(name = "iso_3166_1")
    val iso3166_1: String,

    val name: String,
    val key: String,
    val site: String,
    val size: Long,
    val type: String,
    val official: Boolean,

    @Json(name = "published_at")
    val publishedAt: String

)