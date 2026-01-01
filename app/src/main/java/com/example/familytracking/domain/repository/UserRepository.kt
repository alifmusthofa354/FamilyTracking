package com.example.familytracking.domain.repository

import com.example.familytracking.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<User>
}
