package com.example.familytracking.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.familytracking.presentation.auth.LoginScreen

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.userState.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(state) {
        if (state is UserState.LoggedOut) {
            // We are inside a TabNavigator, so 'navigator' refers to the TabNavigator.
            // We need to access the parent (Root Navigator) to replace the MainScreen.
            navigator.parent?.replaceAll(LoginScreen())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is UserState.Loading -> {
                CircularProgressIndicator()
            }
            is UserState.Empty -> {
                // Empty state logic (redirect to login or show simplified view)
                // For now keeping CreateAccountContent but it might be unreachable if auth is enforced
                CreateAccountContent(onCreateAccount = { _, _ -> }) 
                // Suggest redirecting to login instead
            }
            is UserState.Success -> {
                if (currentState.isEditing) {
                    EditProfileContent(
                        initialName = currentState.user.name,
                        initialEmail = currentState.user.email,
                        onSave = { name, email -> viewModel.updateUser(name, email) },
                        onCancel = { viewModel.cancelEditing() }
                    )
                } else {
                    ViewProfileContent(
                        name = currentState.user.name,
                        email = currentState.user.email,
                        onEdit = { viewModel.startEditing() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
            is UserState.LoggedOut -> {
                 // Handled by LaunchedEffect
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

@Composable
fun ViewProfileContent(name: String, email: String, onEdit: () -> Unit, onLogout: () -> Unit) {
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
            text = "Name: $name",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Email: $email",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onEdit) {
            Text("Edit Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onLogout) {
            Text("Logout")
        }
    }
}

@Composable
fun EditProfileContent(
    initialName: String,
    initialEmail: String,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onSave(name, email) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun CreateAccountContent(onCreateAccount: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "No Profile Found",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onCreateAccount(name, email) },
            enabled = name.isNotBlank() && email.isNotBlank()
        ) {
            Text("Create Account")
        }
    }
}