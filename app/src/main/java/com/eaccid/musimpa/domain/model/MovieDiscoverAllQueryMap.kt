package com.eaccid.musimpa.domain.model

data class MovieDiscoverAllQueryMap(
    val page: Int = 1,
    val sortBy: String = "popularity.desc",
    val includeAdult: Boolean = false,
    val includeVideo: Boolean = false
) {
    fun toQueryMap(): Map<String, String> = mapOf(
        "page" to page.toString(),
        "sort_by" to sortBy,
        "include_adult" to includeAdult.toString(),
        "include_video" to includeVideo.toString()
    )
}
