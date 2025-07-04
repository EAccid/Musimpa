package com.eaccid.musimpa.data.remote.dto

data class AuthenticationDto(
    val success: Boolean?,
    val expires_at: String?,
    val request_token: String?,
    val session_id: String?,
    val status_code: Int?,
    val status_message: String?
) {
    constructor(request_token: String) : this(null, null, request_token, null, null, null)
}
