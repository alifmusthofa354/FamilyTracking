package com.example.familytracking.core.common

object Config {
    // Ganti URL ini sekali saja jika Ngrok berubah
    const val SERVER_URL = "https://ababa881c95e.ngrok-free.app"
    
    // Socket.io biasanya butuh URL tanpa trailing slash, atau handle sendiri
    // Kita sediakan helper jika perlu, tapi library biasanya pintar.
    // Tapi untuk konsistensi string:
    val SOCKET_URL = SERVER_URL.removeSuffix("/")
}
