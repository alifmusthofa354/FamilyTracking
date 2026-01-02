package com.example.familytracking.data.repository

import com.example.familytracking.core.common.Resource
import com.example.familytracking.core.utils.SecurityUtils
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

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val hashedPassword = SecurityUtils.hashPassword(password)
            val entity = userDao.getUserByCredentials(email, hashedPassword)
            if (entity != null) {
                Resource.Success(entity.toDomain())
            } else {
                Resource.Error("Invalid email or password")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                return Resource.Error("Email already registered")
            }
            val id = UUID.randomUUID().toString()
            val hashedPassword = SecurityUtils.hashPassword(password)
            val entity = UserEntity(id, name, email, hashedPassword)
            userDao.insertUser(entity)
            Resource.Success(entity.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun updateProfile(user: User): Resource<Unit> {
        return try {
            val existing = userDao.getUserEntityById(user.id)
            if (existing == null) {
                return Resource.Error("User not found")
            }
            
            val updatedEntity = existing.copy(
                name = user.name,
                email = user.email
                // Password remains hashed from existing entity
            )
            userDao.insertUser(updatedEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Update failed")
        }
    }
}
