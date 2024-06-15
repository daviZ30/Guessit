package com.example.signup.data.userPreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.intellij.lang.annotations.Language

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    fun saveTheme(theme: Boolean) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[THEME] = theme.toString()
            }
        }
    }

    fun saveLanguage(languege: String) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[LANGUAGE] = languege ?: "none"
            }
        }
    }
    fun getLanguage(): String {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[LANGUAGE] ?: "none"
            }.first()
        }
    }

    fun getTheme(): String {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[THEME] ?: "none"
            }.first()
        }
    }

    companion object {
        private val THEME = stringPreferencesKey("theme")
        private val LANGUAGE = stringPreferencesKey("languege")
    }
}