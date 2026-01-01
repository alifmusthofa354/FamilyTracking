package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.SessionRepository
import com.example.familytracking.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): User {
        val user = userRepository.register(name, email, password)
        sessionRepository.saveSession(user.id)
        return user
    }
}
