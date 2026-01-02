package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.LocationModel
import com.example.familytracking.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUpdatesUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(intervalMs: Long = 5000L): Flow<LocationModel> {
        return locationRepository.getLocationUpdates(intervalMs)
    }
}
