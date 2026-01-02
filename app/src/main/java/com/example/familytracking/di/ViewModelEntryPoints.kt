package com.example.familytracking.di

import com.example.familytracking.domain.usecase.ClearSessionUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.UpdateUserUseCase
import com.example.familytracking.presentation.auth.AuthViewModel
import com.example.familytracking.presentation.home.HomeViewModel
import com.example.familytracking.presentation.splash.SplashViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProfileEntryPoint {
    fun getUserUseCase(): GetUserUseCase
    fun updateUserUseCase(): UpdateUserUseCase
    fun clearSessionUseCase(): ClearSessionUseCase
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthEntryPoint {
    fun authViewModel(): AuthViewModel
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SplashEntryPoint {
    fun splashViewModel(): SplashViewModel
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HomeEntryPoint {
    fun homeViewModel(): HomeViewModel
}