package com.demo.recipeapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

data class FilterPreferences(val type: String)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val type = preferences[PreferencesKeys.SELECTION_TYPE] ?: "Breakfast"
            FilterPreferences(type)
        }

    suspend fun updateTypeSelection(type: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTION_TYPE] = type
        }
    }

    private object PreferencesKeys {
        val SELECTION_TYPE = preferencesKey<String>("selection_type")
    }
}