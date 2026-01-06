package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.model.RemoteUser
import com.example.familytracking.domain.repository.RealtimeTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRemoteUsersUseCase @Inject constructor(
    private val repository: RealtimeTrackingRepository
) {
    operator fun invoke(): Flow<List<RemoteUser>> {
        return repository.observeRemoteUsers()
    }
}
