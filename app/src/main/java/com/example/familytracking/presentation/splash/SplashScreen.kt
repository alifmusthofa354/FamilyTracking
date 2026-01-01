package com.example.familytracking.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.familytracking.di.SplashEntryPoint
import com.example.familytracking.presentation.auth.LoginScreen
import com.example.familytracking.presentation.main.MainScreen
import dagger.hilt.android.EntryPointAccessors

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel = rememberScreenModel {
            EntryPointAccessors.fromApplication(context, SplashEntryPoint::class.java).splashViewModel()
        }
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state) {
            when (state) {
                is SplashState.Authenticated -> navigator.replaceAll(MainScreen())
                is SplashState.Unauthenticated -> navigator.replaceAll(LoginScreen())
                else -> {}
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
