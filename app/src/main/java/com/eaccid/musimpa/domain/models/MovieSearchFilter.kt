package com.eaccid.musimpa.domain.models

data class MovieSearchFilter(
    val searchQuery: String = "",
    val selectedGenreIds: List<Int> = emptyList(),
    val sortBy: SortOption = SortOption.POPULARITY_DESC,
    val releaseYear: Int? = null,
    val minRating: Double? = null,
    val maxRating: Double? = null
)