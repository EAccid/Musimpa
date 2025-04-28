package com.eaccid.musimpa.data.remote.entities

data class MovieCredits(
    val id: Int,
    val cast: List<Actor>,
    val crew: List<CrewMember>
)
