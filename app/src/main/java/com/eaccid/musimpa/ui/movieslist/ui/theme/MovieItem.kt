package com.eaccid.musimpa.ui.movieslist.ui.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieItem(
    val id: Int,
    val originalTitle: String?,
    val releaseDate: String?,
    val posterPath: String?,
    val title: String?,
    val overview: String?,
    val voteAverage: Double?,
    val tagline: String?,
//    val genres: List<Genre>?,
    val runtime: Int?
) : Parcelable
