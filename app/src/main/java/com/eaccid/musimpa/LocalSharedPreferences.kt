package com.eaccid.musimpa

import android.content.Context
import android.content.SharedPreferences
import com.eaccid.musimpa.utils.showToast

private const val APP_PREF = "musimpaPrefs"

class LocalSharedPreferences(private val context: Context) : LocalPreferences {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String) {
        //todo delete temp
        context.showToast("set = $value")
        preferences.edit().putString(APP_PREF, value).apply()
    }

    override fun getString(key: String): String {
//        todo delete temp
        val value = preferences.getString(key, "") ?: ""
        context.showToast("get = $value")
        return value
    }


}