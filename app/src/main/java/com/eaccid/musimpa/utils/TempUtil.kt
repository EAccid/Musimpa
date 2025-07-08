package com.eaccid.musimpa.utils

import android.net.Uri
import androidx.core.net.toUri

const val BASE_URL = "https://api.themoviedb.org/3/"
const val MOVIE_IMAGE_URL_PATH = "image.tmdb.org/t/p/"
const val AUTHENTICATE_REQUEST_TOKEN_URL = "https://www.themoviedb.org/authenticate/"
const val EMPTY_STRING_VALUE = ""

fun String.toImageUri(posterSize: PosterSize): Uri {
    return (posterSize.imagePath + this).toUri().buildUpon().scheme("https").build()
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