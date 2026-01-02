package com.example.familytracking.di

import com.example.familytracking.data.repository.LocationRepositoryImpl
import com.example.familytracking.data.repository.SessionRepositoryImpl
import com.example.familytracking.data.repository.UserRepositoryImpl
import com.example.familytracking.domain.repository.LocationRepository
import com.example.familytracking.domain.repository.SessionRepository
import com.example.familytracking.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository
}
