package com.example.familytracking.data.remote

import com.example.familytracking.core.common.Resource
import com.example.familytracking.data.remote.api.AuthApiService
import com.example.familytracking.data.remote.model.AuthResponse
import com.example.familytracking.data.remote.model.LoginRequest
import com.example.familytracking.data.remote.model.RegisterRequest
import com.example.familytracking.data.remote.model.UserDto
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val apiService: AuthApiService
) {
    suspend fun login(request: LoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.login(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.register(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun getProfile(): Resource<UserDto> {
        return try {
            val response = apiService.getProfile()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch profile")
        }
    }
}
