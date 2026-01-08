package com.example.familytracking.presentation.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.familytracking.R
import com.example.familytracking.core.utils.BitmapUtils
import com.example.familytracking.domain.model.LocationModel
import com.example.familytracking.domain.model.RemoteUser
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    userLocation: LocationModel? = null,
    userIcon: Bitmap? = null,
    userName: String? = null, // Add userName param
    currentUserId: String? = null,
    remoteUsers: List<RemoteUser> = emptyList(),
    isFollowingUser: Boolean = true,
    onMapInteraction: () -> Unit = {},
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    // 1. Create MapView Instance
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
            controller.setCenter(GeoPoint(-6.2088, 106.8456)) 
            
            // Detect user interaction to disable auto-follow AND close info windows
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        // Close any open info windows when map is touched
                        org.osmdroid.views.overlay.infowindow.InfoWindow.closeAllInfoWindowsOn(this)
                        onMapInteraction()
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        onMapInteraction()
                    }
                }
                false // Let the map handle the touch event (pan/zoom)
            }
        }
    }

    // 2. Create Marker Instance for Self
    val userMarker = remember(mapView) {
        Marker(mapView).apply {
            title = "Me"
            snippet = "Last update: Now" // Initial snippet
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // 3. Update Markers
    LaunchedEffect(userLocation, userIcon, isFollowingUser, remoteUsers, currentUserId, userName) {
        // --- Update Self Marker ---
        if (userIcon != null) {
            userMarker.icon = BitmapDrawable(context.resources, userIcon)
        }
        
        // Update Title and Snippet
        if (userName != null) {
            userMarker.title = userName
            userMarker.snippet = "Last update: ${java.text.DateFormat.getTimeInstance().format(java.util.Date())}"
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
        mapView.overlays.removeAll { it is Marker && it != userMarker }
        
        if (userLocation != null && !mapView.overlays.contains(userMarker)) {
            mapView.overlays.add(userMarker)
        }

        remoteUsers.filter { it.id != currentUserId }.forEach { remoteUser ->
            val remoteMarker = Marker(mapView).apply {
                position = GeoPoint(remoteUser.latitude, remoteUser.longitude)
                title = remoteUser.name
                snippet = "Last update: ${java.text.DateFormat.getTimeInstance().format(java.util.Date())}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            
            // Default icon
            val defaultIcon = BitmapUtils.createMarkerWithText(
                context = context,
                path = null,
                name = remoteUser.name,
                placeholderResId = R.drawable.ic_profile
            )
            remoteMarker.icon = BitmapDrawable(context.resources, defaultIcon)
            mapView.overlays.add(remoteMarker)

            // Load remote image
            if (remoteUser.profilePictureUrl != null) {
                scope.launch {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(remoteUser.profilePictureUrl)
                        .allowHardware(false) 
                        .build()
                    
                    val result = (loader.execute(request) as? SuccessResult)?.drawable
                    val bitmap = (result as? BitmapDrawable)?.bitmap
                    
                    if (bitmap != null) {
                        val customIcon = BitmapUtils.createMarkerWithBitmap(
                            context = context,
                            bitmap = bitmap,
                            name = remoteUser.name,
                            placeholderResId = R.drawable.ic_profile
                        )
                        remoteMarker.icon = BitmapDrawable(context.resources, customIcon)
                        mapView.invalidate()
                    }
                }
            }
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
