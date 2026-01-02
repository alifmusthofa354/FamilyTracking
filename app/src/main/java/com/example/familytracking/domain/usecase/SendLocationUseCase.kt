package com.example.familytracking.domain.usecase

import com.example.familytracking.data.remote.SocketService
import javax.inject.Inject

class SendLocationUseCase @Inject constructor(
    private val socketService: SocketService
) {
    // We can also add connect/disconnect methods here or separate use cases
    
    fun connect() {
        socketService.connect()
    }

    fun disconnect() {
        socketService.disconnect()
    }

    operator fun invoke(id: String, name: String, lat: Double, lng: Double) {
        socketService.sendLocation(id, name, lat, lng)
    }
}
