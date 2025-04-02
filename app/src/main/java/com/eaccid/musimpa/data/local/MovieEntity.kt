package com.eaccid.musimpa.data.local

import androidx.room.Entity

@Entity
data class MovieEntity(
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
)
