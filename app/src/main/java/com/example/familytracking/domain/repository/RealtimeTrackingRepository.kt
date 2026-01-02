package com.example.familytracking.domain.repository

interface RealtimeTrackingRepository {
    fun connect()
    fun disconnect()
    fun sendLocation(id: String, name: String, lat: Double, lng: Double)
}
