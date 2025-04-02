package com.eaccid.musimpa.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int = 0,
    val originalTitle: String?,
    val releaseDate: String?,
    val posterPath: String?,
    val title: String?,
    val overview: String?,
    val voteAverage: Int = 0, //percentage
    val tagline: String?,
    val runtime: Int? = 0,
    var videoKey: String = ""
) : Parcelable
