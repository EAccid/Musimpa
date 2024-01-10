package com.eaccid.musimpa.entities

data class Authentication(
    val success: Boolean?,
    val expires_at: String?,
    val request_token: String?,
    val session_id: String?
) {
    constructor(request_token: String) : this(null, null, request_token, null)
}
