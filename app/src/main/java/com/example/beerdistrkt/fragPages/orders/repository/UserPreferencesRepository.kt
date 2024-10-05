package com.example.beerdistrkt.fragPages.orders.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val tag: String = "UserPreferencesRepo"

    private object PreferencesKeys {
        val FOLDS_STATE = stringPreferencesKey("my_folds")
    }

    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<String> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(preferences: Preferences): String {
        return preferences[PreferencesKeys.FOLDS_STATE] ?: ""
    }

    suspend fun saveFoldsState(state: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOLDS_STATE] = state
        }
    }
}