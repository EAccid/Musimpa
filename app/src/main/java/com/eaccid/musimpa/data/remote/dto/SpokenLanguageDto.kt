package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class SpokenLanguageDto(
    @Json(name = "iso_639_1")
    val code: String,
    val name: String
)