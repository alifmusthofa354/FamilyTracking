package com.example.familytracking.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.familytracking.R
import com.example.familytracking.core.utils.BitmapUtils
import com.example.familytracking.presentation.components.OSMMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val remoteUsers by viewModel.remoteUsers.collectAsState()
    val isFollowingUser by viewModel.isFollowingUser.collectAsState()
    
    // Load Profile Bitmap (Handle both Local File and Remote URL)
    val userMarkerBitmap by produceState<Bitmap?>(initialValue = null, currentUser?.profilePicturePath) {
        val path = currentUser?.profilePicturePath
        if (path != null) {
            value = withContext(Dispatchers.IO) {
                // Use Coil to load image (handles File, Uri, and Url)
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(path)
                    .allowHardware(false) // Crucial for Bitmap manipulation
                    .build()
                
                val result = (loader.execute(request) as? SuccessResult)?.drawable
                val bitmap = (result as? android.graphics.drawable.BitmapDrawable)?.bitmap
                
                if (bitmap != null) {
                    BitmapUtils.createMarkerWithBitmap(context, bitmap, currentUser?.name ?: "Me", R.drawable.ic_profile)
                } else {
                    // Fallback if Coil fails
                    BitmapUtils.createMarkerWithText(context, null, currentUser?.name ?: "Me", R.drawable.ic_profile)
                }
            }
        } else {
            value = withContext(Dispatchers.IO) {
                BitmapUtils.createMarkerWithText(context, null, currentUser?.name ?: "Me", R.drawable.ic_profile)
            }
        }
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                viewModel.startLocationUpdates()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.startLocationUpdates()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        OSMMapView(
            modifier = Modifier.fillMaxSize(),
            userLocation = currentLocation,
            userIcon = userMarkerBitmap,
            remoteUsers = remoteUsers,
            isFollowingUser = isFollowingUser,
            onMapInteraction = { viewModel.stopFollowingUser() }
        )
        
        if (!hasLocationPermission) {
             Box(modifier = Modifier.align(Alignment.Center)) {
                 Text("Location permission required to show your position.")
             }
        }

        // Floating Action Button to Recenter
        if (hasLocationPermission && !isFollowingUser) {
            FloatingActionButton(
                onClick = { viewModel.startFollowingUser() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Recenter Map"
                )
            }
        }
    }
}