package com.example.familytracking.presentation.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.ClearSessionUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase
) : ScreenModel {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            _userState.value = UserState.Loading
            try {
                getUserUseCase().collect { user ->
                    if (user != null) {
                        // Preserve isEditing state if possible, or reset to false
                        val currentEditing = (_userState.value as? UserState.Success)?.isEditing ?: false
                        _userState.value = UserState.Success(user, currentEditing)
                    } else {
                        // If no user found via session (or session cleared), we might reach here
                         // If session is cleared, GetUserUseCase should return null.
                        _userState.value = UserState.Empty
                    }
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        screenModelScope.launch {
            clearSessionUseCase()
            _userState.value = UserState.LoggedOut
        }
    }

    fun createAccount(name: String, email: String) {
        // Feature moved to Register Screen
        _userState.value = UserState.Error("Please use the Register screen to create an account.")
    }

    fun startEditing() {
        val currentState = _userState.value
        if (currentState is UserState.Success) {
            _userState.value = currentState.copy(isEditing = true)
        }
    }

    fun cancelEditing() {
        val currentState = _userState.value
        if (currentState is UserState.Success) {
            _userState.value = currentState.copy(isEditing = false)
        }
    }

    fun updateUser(name: String, email: String) {
        val currentState = _userState.value
        if (currentState is UserState.Success) {
            screenModelScope.launch {
                try {
                    updateUserUseCase(currentState.user.id, name, email)
                    _userState.value = currentState.copy(isEditing = false)
                } catch (e: Exception) {
                    _userState.value = UserState.Error("Failed to update profile: ${e.message}")
                }
            }
        }
    }
}

sealed class UserState {
    data object Loading : UserState()
    data object Empty : UserState()
    data object LoggedOut : UserState()
    data class Success(val user: User, val isEditing: Boolean = false) : UserState()
    data class Error(val message: String) : UserState()
}