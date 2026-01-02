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
    val isFollowingUser by viewModel.isFollowingUser.collectAsState()
    
    // Load Profile Bitmap
    val userMarkerBitmap by produceState<Bitmap?>(initialValue = null, currentUser) {
        value = withContext(Dispatchers.IO) {
            BitmapUtils.getCircularBitmapFromPath(
                context = context,
                path = currentUser?.profilePicturePath,
                placeholderResId = R.drawable.ic_profile,
                sizeDp = 48 // Size of the marker
            )
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