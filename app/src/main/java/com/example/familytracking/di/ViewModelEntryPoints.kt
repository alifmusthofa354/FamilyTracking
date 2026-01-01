package com.example.familytracking.di

import com.example.familytracking.domain.usecase.CreateUserUseCase
import com.example.familytracking.domain.usecase.GetUserUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProfileEntryPoint {
    fun getUserUseCase(): GetUserUseCase
    fun createUserUseCase(): CreateUserUseCase
}
