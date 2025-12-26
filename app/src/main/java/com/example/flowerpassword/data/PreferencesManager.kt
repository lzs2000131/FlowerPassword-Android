package com.example.flowerpassword.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages user preferences including the remembered memory password.
 */
class PreferencesManager(private val context: Context) {

    companion object {
        private val REMEMBER_KEYWORD_KEY = booleanPreferencesKey("remember_keyword")
        private val SAVED_KEYWORD_KEY = stringPreferencesKey("saved_keyword")
    }

    val rememberKeyword: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_KEYWORD_KEY] ?: false
    }

    val savedKeyword: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SAVED_KEYWORD_KEY] ?: ""
    }

    suspend fun setRememberKeyword(remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_KEYWORD_KEY] = remember
        }
    }

    suspend fun setSavedKeyword(keyword: String) {
        context.dataStore.edit { preferences ->
            preferences[SAVED_KEYWORD_KEY] = keyword
        }
    }

    suspend fun clearSavedKeyword() {
        context.dataStore.edit { preferences ->
            preferences.remove(SAVED_KEYWORD_KEY)
        }
    }
}
