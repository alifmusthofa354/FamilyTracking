package com.example.familytracking.data.repository

import com.example.familytracking.data.local.dao.UserDao
import com.example.familytracking.data.local.entity.UserEntity
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun getUserProfile(): Flow<User?> {
        // Fallback for compatibility, though usually we should use ID
        return userDao.getUser()
            .map { it?.toDomain() }
    }

    override fun getUserProfileById(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
            .map { it?.toDomain() }
    }

    override suspend fun login(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)?.toDomain()
    }

    override suspend fun register(name: String, email: String, password: String): User {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            throw Exception("Email already registered")
        }
        val id = UUID.randomUUID().toString()
        val entity = UserEntity(id, name, email, password)
        userDao.insertUser(entity)
        return entity.toDomain()
    }

    override suspend fun updateProfile(user: User) {
        val existing = userDao.getUserEntityById(user.id)
            ?: throw Exception("User not found")
        
        val updatedEntity = existing.copy(
            name = user.name,
            email = user.email
            // Password remains from existing
        )
        userDao.insertUser(updatedEntity)
    }
}
