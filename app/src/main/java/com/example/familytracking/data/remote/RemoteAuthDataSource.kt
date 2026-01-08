package com.example.familytracking.data.remote

import com.example.familytracking.core.common.Resource
import com.example.familytracking.data.remote.api.AuthApiService
import com.example.familytracking.data.remote.model.AuthResponse
import com.example.familytracking.data.remote.model.LoginRequest
import com.example.familytracking.data.remote.model.RegisterRequest
import com.example.familytracking.data.remote.model.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val apiService: AuthApiService
) {
    // ... login & register existing ...

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

    suspend fun uploadPhoto(file: File): Resource<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val response = apiService.uploadPhoto(body)
            val imageUrl = response["imageUrl"]
            if (imageUrl != null) {
                Resource.Success(imageUrl)
            } else {
                Resource.Error("No image URL returned")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Upload failed")
        }
    }
}
