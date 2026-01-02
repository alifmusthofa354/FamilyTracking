package com.example.familytracking.presentation.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
            // Default initial center (e.g. Jakarta) before first GPS fix
            controller.setCenter(GeoPoint(-6.2088, 106.8456)) 
        }
    }

    // 2. Create Marker Instance
    val userMarker = remember(mapView) {
        Marker(mapView).apply {
            title = "Me"
            // Set Anchor to Bottom Center for Pin accuracy
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // 3. Update Marker Position & Icon with Smooth Camera Animation
    LaunchedEffect(userLocation, userIcon) {
        // Update Icon if changed
        if (userIcon != null) {
            userMarker.icon = BitmapDrawable(context.resources, userIcon)
        }

        // Update Position and Camera
        if (userLocation != null) {
            val point = GeoPoint(userLocation.latitude, userLocation.longitude)
            userMarker.position = point
            
            if (!mapView.overlays.contains(userMarker)) {
                mapView.overlays.add(userMarker)
            }
            
            // UX FIX: Use animateTo for smooth camera movement instead of setCenter
            // This prevents "teleporting" and gives better orientation
            mapView.controller.animateTo(point)
        } else {
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