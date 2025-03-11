// File: app/src/main/java/com/example/coeating/PreferencesRepository.kt
package com.example.coeating

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val DIETARY_PREFERENCES = stringPreferencesKey("dietary_preferences")
        val COSMETIC_PREFERENCES = stringPreferencesKey("cosmetic_preferences")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME] ?: ""
    }

    val dietaryPreferencesFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DIETARY_PREFERENCES] ?: ""
    }

    val cosmeticPreferencesFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COSMETIC_PREFERENCES] ?: ""
    }

    suspend fun savePreferences(
        userName: String,
        dietaryPreferences: String,
        cosmeticPreferences: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = userName
            preferences[PreferencesKeys.DIETARY_PREFERENCES] = dietaryPreferences
            preferences[PreferencesKeys.COSMETIC_PREFERENCES] = cosmeticPreferences
        }
    }
}