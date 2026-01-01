package com.example.familytracking.di

import com.example.familytracking.domain.usecase.CreateUserUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import com.example.familytracking.domain.usecase.UpdateUserUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProfileEntryPoint {
    fun getUserUseCase(): GetUserUseCase
    fun createUserUseCase(): CreateUserUseCase
    fun updateUserUseCase(): UpdateUserUseCase
}
