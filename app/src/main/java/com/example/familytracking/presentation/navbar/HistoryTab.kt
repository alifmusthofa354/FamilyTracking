package com.example.familytracking.presentation.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.familytracking.presentation.history.HistoryScreen
import com.example.familytracking.presentation.history.HistoryViewModel

object HistoryTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "History"
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.List)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { HistoryViewModel() }
        HistoryScreen(viewModel)
    }
}
