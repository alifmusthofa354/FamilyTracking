package com.example.familytracking.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.familytracking.presentation.components.OSMMapView

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        OSMMapView(modifier = Modifier.fillMaxSize())
    }
}
