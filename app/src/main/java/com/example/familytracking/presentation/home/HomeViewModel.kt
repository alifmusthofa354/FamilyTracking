package com.example.familytracking.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ScreenModel {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        screenModelScope.launch {
            getUserUseCase().collect { user ->
                _currentUser.value = user
            }
        }
    }
}
