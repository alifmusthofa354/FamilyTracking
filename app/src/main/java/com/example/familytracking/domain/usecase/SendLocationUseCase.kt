package com.example.familytracking.domain.usecase

import com.example.familytracking.domain.repository.RealtimeTrackingRepository
import javax.inject.Inject

class SendLocationUseCase @Inject constructor(
    private val repository: RealtimeTrackingRepository
) {
    fun connect() {
        repository.connect()
    }

    fun disconnect() {
        repository.disconnect()
    }

    operator fun invoke(id: String, name: String, lat: Double, lng: Double) {
        repository.sendLocation(id, name, lat, lng)
    }
}
