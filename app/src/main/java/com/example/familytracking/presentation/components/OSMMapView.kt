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
import com.example.familytracking.R
import com.example.familytracking.core.utils.BitmapUtils
import com.example.familytracking.domain.model.LocationModel
import com.example.familytracking.domain.model.RemoteUser
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    userLocation: LocationModel? = null,
    userIcon: Bitmap? = null,
    remoteUsers: List<RemoteUser> = emptyList(),
    isFollowingUser: Boolean = true,
    onMapInteraction: () -> Unit = {},
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
            
            // Detect user interaction to disable auto-follow
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN, 
                    android.view.MotionEvent.ACTION_MOVE -> {
                        onMapInteraction()
                    }
                }
                false // Let the map handle the touch event
            }
        }
    }

    // 2. Create Marker Instance for Self
    val userMarker = remember(mapView) {
        Marker(mapView).apply {
            title = "Me"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // 3. Update Markers
    LaunchedEffect(userLocation, userIcon, isFollowingUser, remoteUsers) {
        // --- Update Self Marker ---
        if (userIcon != null) {
            userMarker.icon = BitmapDrawable(context.resources, userIcon)
        }

        if (userLocation != null) {
            val point = GeoPoint(userLocation.latitude, userLocation.longitude)
            userMarker.position = point
            
            if (!mapView.overlays.contains(userMarker)) {
                mapView.overlays.add(userMarker)
            }
            
            // Only animate/follow if enabled
            if (isFollowingUser) {
                mapView.controller.animateTo(point)
            }
        } else {
            mapView.overlays.remove(userMarker)
        }

        // --- Update Remote Users Markers ---
        // Clean up old remote markers first
        mapView.overlays.removeAll { it is Marker && it != userMarker }
        
        // Re-add self marker if it was removed
        if (userLocation != null && !mapView.overlays.contains(userMarker)) {
            mapView.overlays.add(userMarker)
        }

        remoteUsers.forEach { remoteUser ->
            val remoteMarker = Marker(mapView).apply {
                position = GeoPoint(remoteUser.latitude, remoteUser.longitude)
                title = remoteUser.name
                snippet = "Last update: ${java.text.DateFormat.getTimeInstance().format(java.util.Date())}"
                
                // Create custom icon with name label
                val iconWithText = BitmapUtils.createMarkerWithText(
                    context = context,
                    path = null, // Future: add remote profile photo path
                    name = remoteUser.name,
                    placeholderResId = R.drawable.ic_profile
                )
                
                icon = BitmapDrawable(context.resources, iconWithText)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(remoteMarker)
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