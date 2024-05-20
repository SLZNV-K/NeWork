package com.github.slznvk.nework.di

import android.content.Context
import androidx.room.Room
import com.github.slznvk.data.dao.EventDao
import com.github.slznvk.data.dao.EventRemoteKeyDao
import com.github.slznvk.data.dao.PostDao
import com.github.slznvk.data.dao.PostRemoteKeyDao
import com.github.slznvk.data.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()


    @Provides
    fun providePostDao(appDb: AppDb): PostDao = appDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(appDb: AppDb): PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Provides
    fun provideEventDao(appDb: AppDb): EventDao = appDb.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(appDb: AppDb): EventRemoteKeyDao = appDb.eventRemoteKeyDao()
}