package com.example.familytracking.data.remote

import android.util.Log
import com.example.familytracking.core.common.Config
import com.example.familytracking.domain.model.RemoteUser
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketService @Inject constructor() {

    private var socket: Socket? = null
    
    private val _remoteUsers = MutableStateFlow<List<RemoteUser>>(emptyList())
    val remoteUsers: StateFlow<List<RemoteUser>> = _remoteUsers.asStateFlow()

    fun connect() {
        try {
            if (socket == null) {
                val options = IO.Options().apply {
                    reconnection = true
                    forceNew = true
                    transports = arrayOf("websocket") 
                }
                socket = IO.socket(Config.SOCKET_URL, options)
            }

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("SocketService", "Successfully connected to server")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketService", "Disconnected from server")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = if (args.isNotEmpty()) args[0] else "Unknown error"
                Log.e("SocketService", "Connection error: $error")
            }

            // Handle Initial Users List
            socket?.on("current-users") { args ->
                val data = if (args.isNotEmpty()) args[0] as? JSONObject else null
                if (data != null) {
                    val newList = mutableListOf<RemoteUser>()
                    val keys = data.keys()
                    while (keys.hasNext()) {
                        val key = keys.next() as String
                        val userObj = data.getJSONObject(key)
                        newList.add(parseRemoteUser(userObj))
                    }
                    _remoteUsers.value = newList
                }
            }

            // Handle Single User Update
            socket?.on("receive-location") { args ->
                val data = if (args.isNotEmpty()) args[0] as? JSONObject else null
                if (data != null) {
                    val updatedUser = parseRemoteUser(data)
                    _remoteUsers.update { currentList ->
                        val newList = currentList.toMutableList()
                        val index = newList.indexOfFirst { it.id == updatedUser.id }
                        if (index != -1) {
                            newList[index] = updatedUser
                        } else {
                            newList.add(updatedUser)
                        }
                        newList
                    }
                }
            }

            // Handle User Disconnect
            socket?.on("user-disconnected") { args ->
                val userId = if (args.isNotEmpty()) args[0] as? String else null
                if (userId != null) {
                    _remoteUsers.update { currentList ->
                        currentList.filter { it.id != userId }
                    }
                }
            }

            socket?.connect()
        } catch (e: Exception) {
            Log.e("SocketService", "Error in socket connection: ${e.message}")
        }
    }

    private fun parseRemoteUser(json: JSONObject): RemoteUser {
        return RemoteUser(
            id = json.optString("id"),
            name = json.optString("name"),
            latitude = json.optDouble("lat"),
            longitude = json.optDouble("lng"),
            profilePictureUrl = json.optString("profilePicturePath").ifEmpty { null }
        )
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
        _remoteUsers.value = emptyList()
    }

    fun sendLocation(id: String, name: String, lat: Double, lng: Double, photoUrl: String?) {
        val currentSocket = socket
        if (currentSocket != null && currentSocket.connected()) {
            val data = JSONObject().apply {
                put("id", id)
                put("name", name)
                put("lat", lat)
                put("lng", lng)
                put("profilePicturePath", photoUrl) // Server uses profilePicturePath? or photoUrl?
                // Server code uses: users[id] = { ... } from data directly.
                // Mobile expects to read this later. Let's check RemoteUser model.
            }
            currentSocket.emit("send-location", data)
            Log.d("SocketService", "Location sent: $name ($lat, $lng)")
        } else {
            Log.w("SocketService", "Cannot send location: Socket not connected")
            if (currentSocket == null || !currentSocket.connected()) {
                connect()
            }
        }
    }
}