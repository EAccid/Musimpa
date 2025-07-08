package com.eaccid.musimpa.data.remote.services

data class MovieDiscoverAllQueryMap(
    val page: Int = 1,
    val sortBy: String = "popularity.desc",
    val includeAdult: Boolean = false,
    val includeVideo: Boolean = false,
    val searchQuery: String? = null,
    val genreIds: List<Int>? = null,
    val primaryReleaseYear: Int? = null,
    val voteAverageGte: Double? = null,
    val voteAverageLte: Double? = null
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        put("page", page.toString())
        put("sort_by", sortBy)
        put("include_adult", includeAdult.toString())
        put("include_video", includeVideo.toString())

        searchQuery?.takeIf { it.isNotBlank() }?.let { put("query", it) }
        genreIds?.takeIf { it.isNotEmpty() }?.let {
            put("with_genres", it.joinToString(","))
        }
        primaryReleaseYear?.let { put("primary_release_year", it.toString()) }
        voteAverageGte?.let { put("vote_average.gte", it.toString()) }
        voteAverageLte?.let { put("vote_average.lte", it.toString()) }
    }
}