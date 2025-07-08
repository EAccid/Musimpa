package com.eaccid.musimpa.domain.mappers

import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.dto.ActorDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.domain.models.Actor
import com.eaccid.musimpa.domain.models.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun MovieDto.toMovie(): Movie { //todo do we need this?
    return Movie(
        id = id,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage?.times(10) ?: 0.0,
        tagline = tagline,
        runtime = runtime ?: 0
    )
}

fun MovieDto.toMovieEntity(page: Int = 0): MovieEntity {
    return MovieEntity(
        apiId = id,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage?.times(10) ?: 0.0,
        tagline = tagline,
        runtime = runtime ?: 0,
        page = page
    )
}

fun MovieDto.toMovieEntity(
    page: Int,
    searchQuery: String = "",
    searchType: String = "discover",
    genreIds: List<Int> = emptyList()
): MovieEntity {
    return MovieEntity(
        page = page,
        apiId = id,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage ?: 0.0,
        tagline = tagline,
        runtime = runtime ?: 0,
        genreIds = if (genreIds.isNotEmpty()) Gson().toJson(genreIds) else null
//        searchQuery = searchQuery,
//        searchType = searchType
    )
}

fun MovieEntity.toMovie(): Movie {
    val genreIdsList: List<Int> = try {
        genreIds?.let {
            Gson().fromJson<List<Int>>(it, object : TypeToken<List<Int>>() {}.type)
        } ?: emptyList()
    } catch (e: Exception) {
        emptyList<Int>()
    }

    return Movie(
        id = apiId,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        tagline = tagline,
        runtime = runtime,
        genreIds = genreIdsList
    )
}

fun ActorDto.toActor(): Actor {
    return Actor(
        id = id,
        name = name,
        originalName = originalName,
        profilePath = profilePath,
        character = character,
        order = order
    )
}