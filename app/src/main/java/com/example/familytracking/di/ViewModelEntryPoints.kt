package com.example.familytracking.di

import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.UpdateUserUseCase
import com.example.familytracking.presentation.auth.AuthViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProfileEntryPoint {
    fun getUserUseCase(): GetUserUseCase
    fun updateUserUseCase(): UpdateUserUseCase
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthEntryPoint {
    fun authViewModel(): AuthViewModel
}