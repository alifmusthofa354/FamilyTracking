package com.example.familytracking.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.core.common.Resource
import com.example.familytracking.domain.usecase.LoginUseCase
import com.example.familytracking.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        screenModelScope.launch {
            _state.value = AuthState.Loading
            when (val result = loginUseCase(email, password)) {
                is Resource.Success -> _state.value = AuthState.Success
                is Resource.Error -> _state.value = AuthState.Error(result.message)
                else -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        screenModelScope.launch {
            _state.value = AuthState.Loading
            when (val result = registerUseCase(name, email, password)) {
                is Resource.Success -> {
                    // Registration success, now Auto-Login
                    login(email, password)
                }
                is Resource.Error -> _state.value = AuthState.Error(result.message)
                else -> {}
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
