package com.example.familytracking.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    enableUserLocation: Boolean = false,
    userIcon: Bitmap? = null,
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Remember the MapView instance so it's not recreated on recomposition
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            
            // Default View Point (will be overridden if location enabled)
            controller.setZoom(18.0)
            controller.setCenter(GeoPoint(-6.2088, 106.8456)) 
        }
    }

    // Handle User Location Overlay
    // Use LaunchedEffect to update overlay when permission state or userIcon changes
    androidx.compose.runtime.LaunchedEffect(enableUserLocation, userIcon) {
        if (enableUserLocation) {
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation() // Auto-center map on user
            
            if (userIcon != null) {
                locationOverlay.setPersonIcon(userIcon)
                locationOverlay.setDirectionIcon(userIcon)
                
                // Set Anchor to Bottom Center because we added a Pin/Triangle pointer
                // The Hotspot coordinates are in pixels relative to the bitmap
                // X = Center, Y = Bottom
                locationOverlay.setPersonHotspot(userIcon.width / 2f, userIcon.height.toFloat())
            }
            
            // Remove existing location overlays to prevent duplicates
            mapView.overlays.removeAll { it is MyLocationNewOverlay }
            mapView.overlays.add(locationOverlay)
            mapView.invalidate()
        }
    }

    // Handle Lifecycle (Resume/Pause)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { map ->
            onMapReady(map)
        }
    )
}