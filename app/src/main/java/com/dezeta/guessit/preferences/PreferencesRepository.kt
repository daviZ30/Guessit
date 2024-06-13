package com.example.signup.data.userPreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    fun saveTheme(theme: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[THEME] = theme ?: "none"
            }
        }
    }

    fun saveUsername(username: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[THEME] = username ?: "none"
            }
        }
    }

    fun getTheme(): String {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[USERNAME] ?: "none"
            }.first()
        }
    }

    companion object {
        private val THEME = stringPreferencesKey("theme")
        private val USERNAME = stringPreferencesKey("username")
    }
}