package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class ProductionCountryDto(
    @Json(name = "iso_3166_1")
    val code: String,
    val name: String
)