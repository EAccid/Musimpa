package com.eaccid.musimpa.ui.navigation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val USER_SETTINGS = "user_settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_SETTINGS
)

private object PreferencesKeys {
    val LAST_VISITED_SCREEN: Preferences.Key<String> = stringPreferencesKey("last_screen")
}

// check if the last screen restored after app process is killed
class PreferencesDataStoreManager(private val context: Context) {
    suspend fun getLastScreen(): String {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.LAST_VISITED_SCREEN] ?: Screen.Main.route
    }

    suspend fun saveLastScreen(screen: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_VISITED_SCREEN] = screen
        }
    }
}