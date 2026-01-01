package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.SessionRepository
import com.example.familytracking.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): User {
        val user = userRepository.login(email, password)
            ?: throw Exception("Invalid email or password")
        
        sessionRepository.saveSession(user.id)
        return user
    }
}
