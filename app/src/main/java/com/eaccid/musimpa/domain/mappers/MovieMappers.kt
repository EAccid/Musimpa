package com.eaccid.musimpa.domain.mappers

import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.dto.ActorDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.domain.models.Actor
import com.eaccid.musimpa.domain.models.Movie

fun MovieDto.toMovie(): Movie {
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

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = apiId,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        tagline = tagline,
        runtime = runtime
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