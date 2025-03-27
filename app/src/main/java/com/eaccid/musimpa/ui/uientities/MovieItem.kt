package com.eaccid.musimpa.ui.uientities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//todo delete default values
@Parcelize
data class MovieItem(
    val id: Int = 0,
    val originalTitle: String? = "originalTitle",
    val releaseDate: String? = "releaseDate",
    val posterPath: String? = "posterPath",
    val title: String? = "title",
    val overview: String? = "overview",
    val voteAverage: Int = 0, //percentage
    val tagline: String? = "tagline",
//    val genres: List<Genre>?,
    val runtime: Int? = 0,
    var videoKey: String = ""
) : Parcelable
