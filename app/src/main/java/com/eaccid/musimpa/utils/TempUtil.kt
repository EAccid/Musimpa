package com.eaccid.musimpa.utils

import android.net.Uri
import androidx.core.net.toUri
import com.eaccid.musimpa.domain.model.Actor
import com.eaccid.musimpa.domain.model.Movie
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.data.remote.entities.ActorDto
import com.eaccid.musimpa.data.remote.entities.MovieDto

const val BASE_URL = "https://api.themoviedb.org/3/"
const val MOVIE_IMAGE_URL_PATH = "image.tmdb.org/t/p/"
const val AUTHENTICATE_REQUEST_TOKEN_URL = "https://www.themoviedb.org/authenticate/"
const val EMPTY_STRING_VALUE = ""

fun String.toImageUri(posterSize: PosterSize): Uri {
    return (posterSize.imagePath + this).toUri().buildUpon().scheme("https").build()
}

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

enum class PosterSize(imageSize: String) {
    W92("w92"),
    W154("w154"),
    W185("w185"),
    W342("w342"),
    W500("w500"),
    W780("w780"),
    ORIGINAL("original");

    val imagePath: String = MOVIE_IMAGE_URL_PATH + imageSize
}