package com.github.slznvk.nework.di

import com.github.slznvk.data.EventRepositoryImpl
import com.github.slznvk.domain.repository.EventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface EventRepositoryModule {

    @Binds
    fun bindsEventRepository(impl: EventRepositoryImpl): EventRepository
}