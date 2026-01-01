package com.example.familytracking.data.repository

import com.example.familytracking.data.local.dao.UserDao
import com.example.familytracking.data.local.entity.UserEntity
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun getUserProfile(): Flow<User?> {
        return userDao.getUser()
            .map { it?.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                name = user.name,
                email = user.email
            )
        )
    }
}
