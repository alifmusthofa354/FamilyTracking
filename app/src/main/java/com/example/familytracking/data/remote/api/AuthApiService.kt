package com.example.familytracking.data.remote.api

import com.example.familytracking.data.remote.model.AuthResponse
import com.example.familytracking.data.remote.model.LoginRequest
import com.example.familytracking.data.remote.model.RegisterRequest
import com.example.familytracking.data.remote.model.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/users/me")
    suspend fun getProfile(): UserDto

    @retrofit2.http.PUT("api/users/me")
    suspend fun updateProfile(@Body request: Map<String, String>): Map<String, Any> // Adjust return type based on API

    @Multipart
    @POST("api/users/upload-photo")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): Map<String, String> // Returns { message, imageUrl }
}
