package com.eaccid.musimpa.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

const val BASE_URL = "https://api.themoviedb.org/"
const val MOVIE_IMAGE_URL_PATH = "image.tmdb.org/t/p/"
const val AUTHENTICATE_REQUEST_TOKEN_URL = "https://www.themoviedb.org/authenticate/"
const val API_VERSION = 3
const val EMPTY_STRING_VALUE = ""
const val EXTRA_MESSAGE_SESSION_ID = "com.eaccid.kinogotmdb.EXTRA_MESSAGE_SESSION_ID"
const val EXTRA_MESSAGE_MOVIE_ITEM = "com.eaccid.kinogotmdb.EXTRA_MESSAGE_MOVIE_ITEM"


//todo delete from sh-pr
fun saveSessionId(context: Activity?, sessionId: String) {
    val sharedPref = context?.getSharedPreferences("UserDetails", Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putString("com.eaccid.kinogotmdb.SESSION_ID", sessionId)
        apply()
    }
}

fun getSavedSessionId(context: Context?): String {
    val sharedPref = context?.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        ?: return EMPTY_STRING_VALUE
    val defaultValue = EMPTY_STRING_VALUE
    return sharedPref.getString("com.eaccid.kinogotmdb.SESSION_ID", defaultValue)
        ?: EMPTY_STRING_VALUE
}

fun saveRequestToken(context: Activity?, sessionId: String) {
    val sharedPref = context?.getSharedPreferences("UserDetails", Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putString("com.eaccid.kinogotmdb.REQUEST_TOKEN", sessionId)
        apply()
    }
}

fun getRequestToken(context: Activity?): String {
    val sharedPref = context?.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        ?: return EMPTY_STRING_VALUE
    val defaultValue = EMPTY_STRING_VALUE
    return sharedPref.getString("com.eaccid.kinogotmdb.REQUEST_TOKEN", defaultValue)
        ?: EMPTY_STRING_VALUE
}

//try extensions
fun Context.showToast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.show()
}

//extensions: work with API

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