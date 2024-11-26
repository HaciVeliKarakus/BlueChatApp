package io.hvk.bluechatapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeDataStore(private val context: Context) {
    private val themeKey = booleanPreferencesKey("theme_key")
    
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[themeKey] ?: false
        }
    
    suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            val current = preferences[themeKey] ?: false
            preferences[themeKey] = !current
        }
    }
} 