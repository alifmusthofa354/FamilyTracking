package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.repository.SessionRepository
import javax.inject.Inject

class SaveLastLocationUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double) {
        sessionRepository.saveLastLocation(lat, lng)
    }
}
