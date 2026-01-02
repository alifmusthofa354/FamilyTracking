package com.example.familytracking.domain.usecase

import com.example.familytracking.core.common.Resource
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, name: String, email: String, profilePicturePath: String?): Resource<Unit> {
        val user = User(id = id, name = name, email = email, profilePicturePath = profilePicturePath)
        return userRepository.updateProfile(user)
    }
}
