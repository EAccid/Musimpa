package com.eaccid.musimpa.data.remote.entities

import com.squareup.moshi.Json

data class Actor(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "adult") val adult: Boolean = true,
    @Json(name = "gender") val gender: Int = 0,
    @Json(name = "known_for_department") val knownForDepartment: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "original_name") val originalName: String? = null,
    @Json(name = "popularity") val popularity: Number = 0,
    @Json(name = "profile_path") val profilePath: String? = null,
    @Json(name = "cast_id") val castId: Int = 0,
    @Json(name = "character") val character: String? = null,
    @Json(name = "credit_id") val creditId: String? = null,
    @Json(name = "order") val order: Int = 0
)
