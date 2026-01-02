package com.example.familytracking.presentation.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.example.familytracking.domain.model.LocationModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    userLocation: LocationModel? = null,
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

    // 2. Create Marker Instance (Persist across recompositions)
    val userMarker = remember(mapView) {
        Marker(mapView).apply {
            title = "Me"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // 3. Update Marker Position & Icon
    LaunchedEffect(userLocation, userIcon) {
        // Update Icon
        if (userIcon != null) {
            userMarker.icon = BitmapDrawable(context.resources, userIcon)
        }

        // Update Position
        if (userLocation != null) {
            val point = GeoPoint(userLocation.latitude, userLocation.longitude)
            userMarker.position = point
            
            // Add marker if not already present
            if (!mapView.overlays.contains(userMarker)) {
                mapView.overlays.add(userMarker)
            }
            
            // Auto-center map (Follow Mode) - Optional, can be made toggleable
            mapView.controller.animateTo(point)
        } else {
            // Remove marker if location unavailable
            mapView.overlays.remove(userMarker)
        }
        
        mapView.invalidate()
    }

    // 4. Manage Lifecycle
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
