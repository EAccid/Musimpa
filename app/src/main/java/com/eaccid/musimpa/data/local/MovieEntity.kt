package com.eaccid.musimpa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    
    // delete this if pagination information in a separate table
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    // this is just to see how pager works
    // pagination would work better with separate table according to the documentation
    var page: Int = 0,

    val apiId: Int = 0,
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
