package com.example.beerdistrkt.fragPages.orders.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.utils.UserInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi,
) {

    private val tag: String = "UserPreferencesRepo"

    private object PreferencesKeys {
        val FOLDS_STATE = stringPreferencesKey("my_folds")
    }

    private val moshiSessionAdapter: JsonAdapter<UserInfo> = moshi.adapter(UserInfo::class.java)
    private val moshiRegionAdapter: JsonAdapter<WorkRegion> = moshi.adapter(WorkRegion::class.java)

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

    suspend fun saveUserSession(info: UserInfo) {
        val dataStoreKey = stringPreferencesKey(SESSION_KEY)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = moshiSessionAdapter.toJson(info)
        }
    }

    suspend fun readUserSession(): UserInfo? {
        val dataStoreKey = stringPreferencesKey(SESSION_KEY)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]?.let { sessionJsonString ->
            moshiSessionAdapter.fromJson(sessionJsonString)
        }
    }

    suspend fun saveRegion(info: WorkRegion?) {
        val dataStoreKey = stringPreferencesKey(REGION_KEY)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = moshiRegionAdapter.toJson(info)
        }
    }

    suspend fun readRegion(): WorkRegion? {
        val dataStoreKey = stringPreferencesKey(REGION_KEY)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]?.let { sessionJsonString ->
            moshiRegionAdapter.fromJson(sessionJsonString)
        }
    }

    companion object {
        private const val SESSION_KEY = "SESSION_KEY"
        private const val REGION_KEY = "REGION_KEY"
    }
}