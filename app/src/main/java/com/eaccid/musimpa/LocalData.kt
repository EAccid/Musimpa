package com.eaccid.musimpa

interface LocalData {
    fun saveToken(value: String)
    fun getToken(): String
    fun saveSessionId(value: String)
    fun getSessionId(): String
}
