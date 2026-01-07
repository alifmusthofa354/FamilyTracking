package com.example.familytracking.data.remote.model

import com.example.familytracking.data.local.entity.UserEntity
import com.example.familytracking.domain.model.User

data class AuthResponse(
    val message: String,
    val token: String?,
    val user: UserDto?
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val profilePicturePath: String? = null
) {
    fun toDomain(): User {
        return User(id, name, email, profilePicturePath)
    }

    fun toEntity(): UserEntity {
        // Password is not returned by API mostly, or hashed.
        // For cache, we don't need password field anymore if we use Token based auth.
        // But our Entity requires password. We can put empty string or token.
        // Refactoring UserEntity to remove password requirement is better but requires migration.
        // For now, I will use "REMOTE_AUTH" as placeholder password.
        return UserEntity(id, name, email, "REMOTE_AUTH", profilePicturePath)
    }
}
