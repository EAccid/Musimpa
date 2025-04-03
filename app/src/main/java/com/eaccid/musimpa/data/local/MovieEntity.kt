package com.eaccid.musimpa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0, // delete when store the pagination information in a separate table
    val apiId: Int = 0,
    val originalTitle: String?,
    val releaseDate: String?,
    val posterPath: String?,
    val title: String?,
    val overview: String?,
    val voteAverage: Int = 0, //percentage
    val tagline: String?,
    val runtime: Int? = 0,
    var videoKey: String = "",
    var page: Int = 0 //just temporary to see how pager works
)
