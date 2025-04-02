package com.eaccid.musimpa.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.eaccid.musimpa.data.domain.Movie
import com.eaccid.musimpa.data.local.MovieEntity
import com.eaccid.musimpa.data.remote.entities.MovieDto

const val BASE_URL = "https://api.themoviedb.org/"
const val MOVIE_IMAGE_URL_PATH = "image.tmdb.org/t/p/"
const val AUTHENTICATE_REQUEST_TOKEN_URL = "https://www.themoviedb.org/authenticate/"
const val API_VERSION = 3
const val EMPTY_STRING_VALUE = ""

//try extensions
fun Context.showToast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.show()
}

//extensions: work with API

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
        voteAverage = voteAverage?.times(10)?.toInt() ?: 0,
        tagline = tagline,
        runtime = runtime
    )
}

fun MovieDto.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage?.times(10)?.toInt() ?: 0,
        tagline = tagline,
        runtime = runtime
    )
}

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
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