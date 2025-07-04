package com.eaccid.musimpa.data.remote.dto

data class MovieCreditsDto(
    val id: Int,
    val cast: List<ActorDto>,
    val crew: List<CrewMemberDto>
)
