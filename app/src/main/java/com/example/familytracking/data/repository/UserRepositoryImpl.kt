package com.example.familytracking.data.repository

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {
    override fun getUserProfile(): Flow<User> = flow {
        // Simulate network delay
        delay(1000)
        emit(
            User(
                id = "1",
                name = "Muhammad",
                email = "muhammad@example.com"
            )
        )
    }
}
