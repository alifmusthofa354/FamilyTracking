package com.example.familytracking.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val userId: Flow<String?>
    val token: Flow<String?>
    suspend fun saveSession(userId: String, token: String)
    suspend fun clearSession()
}
