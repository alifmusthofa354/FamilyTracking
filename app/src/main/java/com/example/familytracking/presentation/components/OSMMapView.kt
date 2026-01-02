package com.example.familytracking.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
    
    // 1. Create MapView Instance
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
            controller.setCenter(GeoPoint(-6.2088, 106.8456)) 
        }
    }

    // 2. Create Location Overlay Instance (Persist across recompositions)
    val locationOverlay = remember(mapView) {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableFollowLocation() // Auto-center map on user movement
            runOnFirstFix {
                // Optional: Code to run when first location is fixed
            }
        }
    }

    // 3. Update Overlay Icons when Bitmap changes
    LaunchedEffect(userIcon) {
        if (userIcon != null) {
            locationOverlay.setPersonIcon(userIcon)
            locationOverlay.setDirectionIcon(userIcon)
            
            // Set Hotspot to Bottom Center for Pin accuracy
            locationOverlay.setPersonHotspot(userIcon.width / 2f, userIcon.height.toFloat())
            
            mapView.invalidate()
        }
    }

    // 4. Manage Lifecycle & Permission Logic
    // This effect runs whenever 'enableUserLocation' changes OR lifecycle events happen
    DisposableEffect(lifecycleOwner, enableUserLocation) {
        // Add overlay if not present
        if (!mapView.overlays.contains(locationOverlay)) {
            mapView.overlays.add(locationOverlay)
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    if (enableUserLocation) {
                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                    // Stop GPS tracking to save battery
                    locationOverlay.disableMyLocation()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // Trigger initial state immediately (for first composition)
        if (enableUserLocation) {
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            locationOverlay.disableMyLocation()
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
