package com.eaccid.musimpa.entities

import com.squareup.moshi.Json

data class Movie(
    val id: Int,
    @Json(name = "original_title")
    val originalTitle: String?,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String?,

    val adult: Boolean?,

    @Json(name = "backdrop_path")
    val backdropPath: String?,

    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,

    @Json(name = "imdb_id")
    val imdbID: String?,

    @Json(name = "original_language")
    val originalLanguage: String?,
    val overview: String?,
    val popularity: Double?,

    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompany>?,

    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountry>?,
    val revenue: Int?,
    val runtime: Int?,

    @Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?,

    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,

    @Json(name = "vote_average")
    val voteAverage: Double?,

    @Json(name = "vote_count")
    val voteCount: Int?

)

