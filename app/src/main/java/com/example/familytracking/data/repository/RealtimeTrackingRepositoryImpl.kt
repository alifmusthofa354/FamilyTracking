package com.example.familytracking.data.repository

import com.example.familytracking.data.remote.SocketService
import com.example.familytracking.domain.model.RemoteUser
import com.example.familytracking.domain.repository.RealtimeTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealtimeTrackingRepositoryImpl @Inject constructor(
    private val socketService: SocketService
) : RealtimeTrackingRepository {

    override fun connect() {
        socketService.connect()
    }

    override fun disconnect() {
        socketService.disconnect()
    }

    override fun sendLocation(id: String, name: String, lat: Double, lng: Double, photoUrl: String?) {
        socketService.sendLocation(id, name, lat, lng, photoUrl)
    }

    override fun observeRemoteUsers(): Flow<List<RemoteUser>> {
        return socketService.remoteUsers
    }
}