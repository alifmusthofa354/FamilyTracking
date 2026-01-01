package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.repository.SessionRepository
import com.example.familytracking.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<User?> {
        return sessionRepository.userId.flatMapLatest { userId ->
            if (userId != null) {
                userRepository.getUserProfileById(userId)
            } else {
                flowOf(null)
            }
        }
    }
}
