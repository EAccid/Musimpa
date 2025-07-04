package com.eaccid.musimpa.data.remote.dto

data class MovieCreditsDto(
    val id: Int = 0,
    val cast: List<ActorDto> = emptyList(),
    val crew: List<CrewMemberDto> = emptyList()
)
