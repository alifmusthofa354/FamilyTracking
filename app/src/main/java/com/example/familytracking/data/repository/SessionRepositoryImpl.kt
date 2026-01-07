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
    }

    override val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    override val token: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TOKEN]
        }

    override suspend fun saveSession(userId: String, token: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.TOKEN)
        }
    }
}
