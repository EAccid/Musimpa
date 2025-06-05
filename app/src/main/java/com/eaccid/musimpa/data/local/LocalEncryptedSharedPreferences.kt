package com.eaccid.musimpa.data.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val APP_PREF = "musimpaPrefs"

class LocalEncryptedSharedPreferences(context: Context) : LocalData, LocalPreferences {
    private val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(
            KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256).build()).build()

    private val preferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            APP_PREF,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

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