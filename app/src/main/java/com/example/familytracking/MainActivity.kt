package com.example.familytracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import com.example.familytracking.ui.screens.HomeScreen
import com.example.familytracking.ui.theme.FamilyTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyTrackingTheme {
                Navigator(HomeScreen())
            }
        }
    }
}