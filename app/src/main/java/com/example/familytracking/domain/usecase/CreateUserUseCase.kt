package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String) {
        // ID is hardcoded to "1" for single user profile for now
        val user = User(id = "1", name = name, email = email)
        userRepository.saveUser(user)
    }
}
