package com.example.familytracking.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.familytracking.domain.model.LocationModel
import com.example.familytracking.domain.model.RemoteUser
import com.example.familytracking.domain.model.User
import com.example.familytracking.domain.usecase.GetLastLocationUseCase
import com.example.familytracking.domain.usecase.GetLocationUpdatesUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.ObserveRemoteUsersUseCase
import com.example.familytracking.domain.usecase.SaveLastLocationUseCase
import com.example.familytracking.domain.usecase.SendLocationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val sendLocationUseCase: SendLocationUseCase,
    private val observeRemoteUsersUseCase: ObserveRemoteUsersUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val saveLastLocationUseCase: SaveLastLocationUseCase
) : ScreenModel {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _remoteUsers = MutableStateFlow<List<RemoteUser>>(emptyList())
    val remoteUsers: StateFlow<List<RemoteUser>> = _remoteUsers.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationModel?>(null)
    val currentLocation: StateFlow<LocationModel?> = _currentLocation.asStateFlow()

    private val _isFollowingUser = MutableStateFlow(true)
    val isFollowingUser: StateFlow<Boolean> = _isFollowingUser.asStateFlow()

    private var locationJob: Job? = null

    init {
        loadInitialLocation()
        fetchUser()
        sendLocationUseCase.connect()
        observeRemoteUsers()
    }

    private fun loadInitialLocation() {
        screenModelScope.launch {
            // Load cache first
            val cached = getLastLocationUseCase().first()
            if (cached != null) {
                _currentLocation.value = LocationModel(cached.first, cached.second)
            }
        }
    }

    private fun fetchUser() {
        screenModelScope.launch {
            getUserUseCase().collect { user ->
                _currentUser.value = user
            }
        }
    }

    private fun observeRemoteUsers() {
        screenModelScope.launch {
            observeRemoteUsersUseCase().collect { users ->
                _remoteUsers.value = users
            }
        }
    }

    fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        
        locationJob = screenModelScope.launch {
            getLocationUpdatesUseCase().collect { location ->
                _currentLocation.value = location
                
                // Save to cache
                saveLastLocationUseCase(location.latitude, location.longitude)
                
                // Send location to server
                val user = _currentUser.value
                if (user != null) {
                    sendLocationUseCase(
                        id = user.id,
                        name = user.name,
                        lat = location.latitude,
                        lng = location.longitude,
                        photoUrl = user.profilePicturePath
                    )
                }
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

    override fun onDispose() {
        super.onDispose()
        sendLocationUseCase.disconnect()
    }
}
