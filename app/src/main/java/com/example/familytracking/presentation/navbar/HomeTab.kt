package com.example.familytracking.presentation.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.familytracking.di.HomeEntryPoint
import com.example.familytracking.presentation.home.HomeScreen
import com.example.familytracking.presentation.home.HomeViewModel
import dagger.hilt.android.EntryPointAccessors

object HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel = rememberScreenModel {
            EntryPointAccessors.fromApplication(context, HomeEntryPoint::class.java).homeViewModel()
        }
        HomeScreen(viewModel)
    }
}
