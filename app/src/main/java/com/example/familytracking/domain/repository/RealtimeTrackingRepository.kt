package com.example.familytracking.domain.repository

import com.example.familytracking.domain.model.RemoteUser
import kotlinx.coroutines.flow.Flow

interface RealtimeTrackingRepository {
    fun connect()
    fun disconnect()
    fun sendLocation(id: String, name: String, lat: Double, lng: Double, photoUrl: String?)
    fun observeRemoteUsers(): Flow<List<RemoteUser>>
}
