package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class ProductionCompanyDto(
    val id: Int,

    @Json(name = "logo_path")
    val logoPath: String?,

    val name: String,

    @Json(name = "origin_country")
    val originCountry: String
)