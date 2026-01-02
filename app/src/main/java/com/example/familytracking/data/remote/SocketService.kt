package com.example.familytracking.data.remote

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketService @Inject constructor() {

    private var socket: Socket? = null
    // Alamat Ngrok terbaru
    private val serverUrl = "https://9704a79486e4.ngrok-free.app" 

    fun connect() {
        try {
            if (socket == null) {
                val options = IO.Options().apply {
                    reconnection = true
                    forceNew = true
                    // Menggunakan websocket secara eksklusif untuk menghindari xhr poll error di Android
                    transports = arrayOf("websocket") 
                }
                socket = IO.socket(serverUrl, options)
            }

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("SocketService", "Successfully connected to server")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketService", "Disconnected from server")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = args.getOrNull(0)
                Log.e("SocketService", "Connection error: $error")
            }

            socket?.connect()
        } catch (e: Exception) {
            Log.e("SocketService", "Error in socket connection: ${e.message}")
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
    }

    fun sendLocation(id: String, name: String, lat: Double, lng: Double) {
        val currentSocket = socket
        if (currentSocket != null && currentSocket.connected()) {
            val data = JSONObject().apply {
                put("id", id)
                put("name", name)
                put("lat", lat)
                put("lng", lng)
            }
            currentSocket.emit("send-location", data)
            Log.d("SocketService", "Location sent: $name ($lat, $lng)")
        } else {
            Log.w("SocketService", "Cannot send location: Socket not connected")
            // Coba reconnect jika terputus
            if (currentSocket == null || !currentSocket.connected()) {
                connect()
            }
        }
    }
}