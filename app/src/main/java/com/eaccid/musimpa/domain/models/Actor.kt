package com.eaccid.musimpa.domain.models

data class Actor(
    val id: Int = 0,
    val name: String? = null,
    val originalName: String? = null,
    val profilePath: String? = null,
    val character: String? = null,
    val order: Int = 0
)
