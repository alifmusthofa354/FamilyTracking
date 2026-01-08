package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastLocationUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<Pair<Double, Double>?> {
        return sessionRepository.lastLocation
    }
}
