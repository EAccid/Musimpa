package com.eaccid.musimpa.data.remote.entities

import com.squareup.moshi.Json

data class MovieListDto(
    val id: Int?,
    val name: String?,
    val description: String?,
    val language: SpokenLanguage?,

    @Json(name = "created_by")
    val createdBy: String?,

    @Json(name = "favorite_count")
    val favoriteCount: Int?,

    @Json(name = "item_count")
    val itemCount: Int?,

    @Json(name = "poster_path")
    val posterPath: String?,

    @Json(name = "iso_639_1")
    val iso_639_1: String?,

    val items: List<MovieDto>
)
