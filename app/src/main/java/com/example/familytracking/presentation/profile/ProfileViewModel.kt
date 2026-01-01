package com.example.familytracking.presentation.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.CreateUserUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val createUserUseCase: CreateUserUseCase
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
                        _userState.value = UserState.Success(user)
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
                // Flow collection in loadUserProfile will automatically update the state
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to create account: ${e.message}")
            }
        }
    }
}

sealed class UserState {
    data object Loading : UserState()
    data object Empty : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}