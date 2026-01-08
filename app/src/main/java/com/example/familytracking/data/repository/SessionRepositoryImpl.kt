package com.example.familytracking.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.familytracking.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SessionRepository {

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val TOKEN = stringPreferencesKey("auth_token")
        val LAST_LAT = androidx.datastore.preferences.core.doublePreferencesKey("last_lat")
        val LAST_LNG = androidx.datastore.preferences.core.doublePreferencesKey("last_lng")
    }

    override val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    override val token: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TOKEN]
        }

    override val lastLocation: Flow<Pair<Double, Double>?> = dataStore.data
        .map { preferences ->
            val lat = preferences[PreferencesKeys.LAST_LAT]
            val lng = preferences[PreferencesKeys.LAST_LNG]
            if (lat != null && lng != null) Pair(lat, lng) else null
        }

    override suspend fun saveSession(userId: String, token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    override suspend fun saveLastLocation(lat: Double, lng: Double) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_LAT] = lat
            preferences[PreferencesKeys.LAST_LNG] = lng
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.TOKEN)
            // Optional: Clear location on logout? Usually better to keep it for UX
            preferences.remove(PreferencesKeys.LAST_LAT)
            preferences.remove(PreferencesKeys.LAST_LNG)
        }
    }
}
