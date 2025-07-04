package com.eaccid.musimpa.ui.mappers

import com.eaccid.musimpa.domain.models.Actor
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.domain.models.MovieDetails
import com.eaccid.musimpa.ui.models.ActorUi
import com.eaccid.musimpa.ui.models.MovieDetailsUi
import com.eaccid.musimpa.ui.models.MovieUi

fun Movie.toMovieUi(): MovieUi {
    return MovieUi(
        id = id,
        title = title,
        originalTitle = originalTitle,
        posterPath = posterPath,
        releaseDate = releaseDate,
        overview = overview,
        voteAverage = voteAverage,
        tagline = tagline,
        runtime = runtime
    )
}

fun Actor.toActorUi(): ActorUi {
    return ActorUi(
        id = id,
        name = name,
        originalName = originalName,
        profilePath = profilePath,
        character = character,
    )
}

fun MovieDetails.toMovieDetailsUi(): MovieDetailsUi {
    return MovieDetailsUi(
        movie = movie.toMovieUi(),
        cast = cast.map { it.toActorUi() },
        videoKey = videoKey
    )
}