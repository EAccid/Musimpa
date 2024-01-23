package com.eaccid.musimpa

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val APP_PREF = "musimpaPrefs"

class LocalSharedPreferences(private val context: Context) : LocalData, LocalPreferences {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String) {
        Log.i("MusimpaApp", "set key = $key, value = $value")
        preferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String {
        val value = preferences.getString(key, "") ?: ""
        Log.i("MusimpaApp", "get key = $key, value = $value")
        return value
    }

    override fun saveToken(value: String) {
        saveString(LocalPreferences.SharedKeys.TOKEN.key, value)
    }

    override fun getToken(): String {
        return getString(LocalPreferences.SharedKeys.TOKEN.key)
    }

    override fun saveSessionId(value: String) {
        saveString(LocalPreferences.SharedKeys.SESSION_ID.key, value)
    }

    override fun getSessionId(): String {
        return getString(LocalPreferences.SharedKeys.SESSION_ID.key)
    }


}