package com.example.familytracking.domain.repository

import com.example.familytracking.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<User?>
    fun getUserProfileById(userId: String): Flow<User?>
    suspend fun login(email: String, password: String): User?
    suspend fun register(name: String, email: String, password: String): User
    suspend fun updateProfile(user: User)
}
