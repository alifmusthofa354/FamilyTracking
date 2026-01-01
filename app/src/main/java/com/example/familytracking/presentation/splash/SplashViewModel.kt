package com.example.familytracking.presentation.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.usecase.GetSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        screenModelScope.launch {
            // Check session status once
            val userId = getSessionUseCase().first()
            if (userId != null) {
                _state.value = SplashState.Authenticated
            } else {
                _state.value = SplashState.Unauthenticated
            }
        }
    }
}

sealed class SplashState {
    data object Loading : SplashState()
    data object Authenticated : SplashState()
    data object Unauthenticated : SplashState()
}
