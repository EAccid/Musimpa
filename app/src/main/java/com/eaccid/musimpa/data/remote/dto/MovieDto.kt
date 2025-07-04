package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class MovieDto(
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
    val genre: List<GenreDto>?,
    val homepage: String?,

    @Json(name = "imdb_id")
    val imdbID: String?,

    @Json(name = "original_language")
    val originalLanguage: String?,
    val overview: String?,
    val popularity: Double?,

    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompanyDto>?,

    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountryDto>?,
    val revenue: Int?,
    val runtime: Int?,

    @Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguageDto>?,

    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,

    @Json(name = "vote_average")
    val voteAverage: Double?,

    @Json(name = "vote_count")
    val voteCount: Int?

)

