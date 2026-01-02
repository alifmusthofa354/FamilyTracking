package com.example.familytracking.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.LocationModel
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.GetLocationUpdatesUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase
) : ScreenModel {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationModel?>(null)
    val currentLocation: StateFlow<LocationModel?> = _currentLocation.asStateFlow()

    private val _isFollowingUser = MutableStateFlow(true)
    val isFollowingUser: StateFlow<Boolean> = _isFollowingUser.asStateFlow()

    private var locationJob: Job? = null

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

    fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        
        locationJob = screenModelScope.launch {
            getLocationUpdatesUseCase().collect { location ->
                _currentLocation.value = location
            }
        }
    }

    fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

    fun startFollowingUser() {
        _isFollowingUser.value = true
    }

    fun stopFollowingUser() {
        _isFollowingUser.value = false
    }
}