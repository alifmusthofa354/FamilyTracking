package com.example.familytracking.data.remote.api

import com.example.familytracking.data.remote.model.AuthResponse
import com.example.familytracking.data.remote.model.LoginRequest
import com.example.familytracking.data.remote.model.RegisterRequest
import com.example.familytracking.data.remote.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/users/me")
    suspend fun getProfile(): UserDto
}
