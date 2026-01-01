package com.example.familytracking.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.userState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is UserState.Loading -> {
                CircularProgressIndicator()
            }
            is UserState.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Text(
                        text = "Name: ${currentState.user.name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Email: ${currentState.user.email}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is UserState.Error -> {
                Text(
                    text = "Error: ${currentState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}