package com.example.familytracking.domain.repository

import com.example.familytracking.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(intervalMs: Long): Flow<LocationModel>
}
