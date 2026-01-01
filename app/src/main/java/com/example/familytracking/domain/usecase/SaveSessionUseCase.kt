package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.repository.SessionRepository
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(userId: String) {
        sessionRepository.saveSession(userId)
    }
}
