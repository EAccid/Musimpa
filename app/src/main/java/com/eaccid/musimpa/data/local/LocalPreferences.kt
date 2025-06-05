package com.eaccid.musimpa.data.local

interface LocalPreferences {
    fun saveString(key: String, value: String)
    fun getString(key: String): String

    enum class SharedKeys(val key: String) {
        TOKEN("com.eaccid.musimpa.REQUEST_TOKEN"),
        SESSION_ID("com.eaccid.musimpa.SESSION_ID")
    }
}