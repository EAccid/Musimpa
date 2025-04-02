package com.eaccid.musimpa.data.remote.entities

import com.squareup.moshi.Json

data class ProductionCountry(
    @Json(name = "iso_3166_1")
    val code: String,
    val name: String
)