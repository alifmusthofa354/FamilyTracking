package com.example.familytracking.presentation.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.CreateUserUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
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
                        _userState.value = UserState.Empty
                    }
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createAccount(name: String, email: String) {
        screenModelScope.launch {
            try {
                createUserUseCase(name, email)
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to create account: ${e.message}")
            }
        }
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
    data class Success(val user: User, val isEditing: Boolean = false) : UserState()
    data class Error(val message: String) : UserState()
}