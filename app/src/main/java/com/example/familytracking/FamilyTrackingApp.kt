package com.example.familytracking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class FamilyTrackingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Important for OSMDroid to load tiles
        Configuration.getInstance().userAgentValue = packageName
        // Optional: Cache path config if needed, but defaults usually work
        val osmConf = Configuration.getInstance()
        osmConf.osmdroidBasePath = File(cacheDir, "osmdroid")
        osmConf.osmdroidTileCache = File(osmConf.osmdroidBasePath, "tile")
    }
}