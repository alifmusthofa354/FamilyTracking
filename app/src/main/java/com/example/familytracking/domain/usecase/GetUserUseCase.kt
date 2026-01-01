package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User> {
        return userRepository.getUserProfile()
    }
}
