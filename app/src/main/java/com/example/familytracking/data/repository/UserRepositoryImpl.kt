package com.example.familytracking.data.repository

import com.example.familytracking.core.common.Resource
import com.example.familytracking.data.local.dao.UserDao
import com.example.familytracking.data.remote.RemoteAuthDataSource
import com.example.familytracking.data.remote.model.LoginRequest
import com.example.familytracking.data.remote.model.RegisterRequest
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: RemoteAuthDataSource
) : UserRepository {
    override fun getUserProfile(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    override fun getUserProfileById(userId: String): Flow<User?> {
        return userDao.getUserById(userId).map { it?.toDomain() }
    }

    override suspend fun login(email: String, password: String): Resource<Pair<User, String>> {
        val request = LoginRequest(email, password)
        return when (val result = remoteDataSource.login(request)) {
            is Resource.Success -> {
                val userDto = result.data.user
                val token = result.data.token
                
                if (userDto != null && token != null) {
                    // Save to Cache (Room)
                    userDao.insertUser(userDto.toEntity())
                    Resource.Success(Pair(userDto.toDomain(), token))
                } else {
                    Resource.Error("Invalid response from server")
                }
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        val request = RegisterRequest(name, email, password)
        return when (val result = remoteDataSource.register(request)) {
            is Resource.Success -> {
                val userDto = result.data.user
                if (userDto != null) {
                    Resource.Success(userDto.toDomain())
                } else {
                    Resource.Error("Registration successful but no user data returned")
                }
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateProfile(user: User): Resource<Unit> {
        // For now, update local only or implement remote update
        // Assuming local update for this step to focus on Auth
        return try {
            val existing = userDao.getUserEntityById(user.id)
            if (existing == null) return Resource.Error("User not found locally")
            
            val updatedEntity = existing.copy(
                name = user.name,
                email = user.email,
                profilePicturePath = user.profilePicturePath
            )
            userDao.insertUser(updatedEntity)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Update failed")
        }
    }
}
