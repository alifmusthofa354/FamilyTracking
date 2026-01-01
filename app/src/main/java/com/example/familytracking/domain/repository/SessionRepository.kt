package com.example.familytracking.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val userId: Flow<String?>
    suspend fun saveSession(userId: String)
    suspend fun clearSession()
}
