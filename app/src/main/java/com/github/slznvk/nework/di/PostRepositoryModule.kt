package com.github.slznvk.nework.di

import com.github.slznvk.data.repository.PostRepositoryImpl
import com.github.slznvk.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
interface PostRepositoryModule {

    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository
}