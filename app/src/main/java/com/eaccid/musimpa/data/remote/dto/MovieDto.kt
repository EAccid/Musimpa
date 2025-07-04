package com.eaccid.musimpa.data.remote.dto

import com.squareup.moshi.Json

data class MovieDto(
    val id: Int = 0,
    @Json(name = "original_title")
    val originalTitle: String? = null,
    @Json(name = "poster_path")
    val posterPath: String? = null,
    @Json(name = "release_date")
    val releaseDate: String? = null,

    val adult: Boolean? = null,

    @Json(name = "backdrop_path")
    val backdropPath: String? = null,

    val budget: Int? = null,
    val genre: List<GenreDto>? = null,
    val homepage: String? = null,

    @Json(name = "imdb_id")
    val imdbID: String? = null,

    @Json(name = "original_language")
    val originalLanguage: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,

    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompanyDto>? = null,

    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountryDto>? = null,
    val revenue: Int? = null,
    val runtime: Int? = null,

    @Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguageDto>? = null,

    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,

    @Json(name = "vote_average")
    val voteAverage: Double? = null,

    @Json(name = "vote_count")
    val voteCount: Int? = null

)

