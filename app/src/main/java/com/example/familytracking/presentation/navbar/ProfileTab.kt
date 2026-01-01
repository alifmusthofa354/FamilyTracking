package com.example.familytracking.presentation.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.familytracking.di.ProfileEntryPoint
import com.example.familytracking.presentation.profile.ProfileScreen
import com.example.familytracking.presentation.profile.ProfileViewModel
import dagger.hilt.android.EntryPointAccessors

object ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Profile"
            val icon = rememberVectorPainter(Icons.Default.Person)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val screenModel = rememberScreenModel {
            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                ProfileEntryPoint::class.java
            )
            ProfileViewModel(
                getUserUseCase = entryPoint.getUserUseCase(),
                updateUserUseCase = entryPoint.updateUserUseCase(),
                clearSessionUseCase = entryPoint.clearSessionUseCase()
            )
        }
        ProfileScreen(screenModel)
    }
}
