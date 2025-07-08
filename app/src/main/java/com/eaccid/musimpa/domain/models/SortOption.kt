package com.eaccid.musimpa.domain.models

enum class SortOption(val value: String) {
    POPULARITY_DESC("popularity.desc"),
    POPULARITY_ASC("popularity.asc"),
    RELEASE_DATE_DESC("release_date.desc"),
    RELEASE_DATE_ASC("release_date.asc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
    VOTE_AVERAGE_ASC("vote_average.asc"),
    TITLE_ASC("title.asc"),
    TITLE_DESC("title.desc")
}