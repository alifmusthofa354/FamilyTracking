package com.example.familytracking.domain.usecase

import com.example.familytracking.core.common.Resource
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.SessionRepository
import com.example.familytracking.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        val result = userRepository.login(email, password)
        if (result is Resource.Success) {
            sessionRepository.saveSession(result.data.id)
        }
        return result
    }
}
