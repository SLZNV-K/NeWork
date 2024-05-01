package com.github.slznvk.nework.di

import com.github.slznvk.data.UserRepositoryImpl
import com.github.slznvk.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface UserRepositoryModule {

    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}