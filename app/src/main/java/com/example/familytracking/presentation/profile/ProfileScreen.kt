package com.example.familytracking.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.familytracking.R
import com.example.familytracking.core.utils.FileStorageUtils
import com.example.familytracking.presentation.auth.LoginScreen
import java.io.File

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.userState.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(state) {
        if (state is UserState.LoggedOut) {
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
                CreateAccountContent(onCreateAccount = { _, _ -> })
            }
            is UserState.Success -> {
                if (currentState.isEditing) {
                    EditProfileContent(
                        initialName = currentState.user.name,
                        initialEmail = currentState.user.email,
                        initialProfilePath = currentState.user.profilePicturePath,
                        onSave = { name, email, newPath -> 
                             viewModel.updateUser(name, email, newPath)
                        },
                        onCancel = { viewModel.cancelEditing() }
                    )
                } else {
                    ViewProfileContent(
                        name = currentState.user.name,
                        email = currentState.user.email,
                        profilePath = currentState.user.profilePicturePath,
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
fun ViewProfileContent(name: String, email: String, profilePath: String?, onEdit: () -> Unit, onLogout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ProfileImage(path = profilePath, name = name, size = 120.dp)

        Spacer(modifier = Modifier.height(24.dp))

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
    initialProfilePath: String?,
    onSave: (String, String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var profilePath by remember { mutableStateOf(initialProfilePath) }
    
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val savedPath = FileStorageUtils.saveImageToInternalStorage(context, uri)
                if (savedPath != null) {
                    profilePath = savedPath
                }
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.clickable { 
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            ProfileImage(path = profilePath, name = initialName, size = 100.dp, isEditable = true)
        }
        Text(text = "Tap to change photo", style = MaterialTheme.typography.labelSmall)

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
                onClick = { onSave(name, email, profilePath) },
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

        ProfileImage(path = null, name = "New", size = 100.dp)

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

@Composable
fun ProfileImage(path: String?, name: String, size: androidx.compose.ui.unit.Dp, isEditable: Boolean = false) {
    if (path != null) {
        val model = if (path.startsWith("http")) path else File(path)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_profile),
            error = painterResource(R.drawable.ic_profile),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    width = if (isEditable) 3.dp else 2.dp, 
                    color = if (isEditable) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary, 
                    shape = CircleShape
                )
        )
    } else {
        val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
        val colors = listOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A6F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722")
        val colorIndex = Math.abs(name.hashCode()) % colors.size
        val bgColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(colors[colorIndex]))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor)
                .border(
                    width = if (isEditable) 3.dp else 2.dp, 
                    color = if (isEditable) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary, 
                    shape = CircleShape
                )
        ) {
            Text(
                text = initial,
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = (size.value * 0.4).sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}