package com.eaccid.musimpa.data.remote.entities

import com.squareup.moshi.Json

data class SpokenLanguage(
    @Json(name = "iso_639_1")
    val code: String,
    val name: String
)