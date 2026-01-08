package com.example.familytracking.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val userId: Flow<String?>
    val token: Flow<String?>
    val lastLocation: Flow<Pair<Double, Double>?>
    suspend fun saveSession(userId: String, token: String)
    suspend fun saveLastLocation(lat: Double, lng: Double)
    suspend fun clearSession()
}
