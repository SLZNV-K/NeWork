package com.github.slznvk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.slznvk.data.dao.EventDao
import com.github.slznvk.data.dao.EventRemoteKeyDao
import com.github.slznvk.data.dao.PostDao
import com.github.slznvk.data.dao.PostRemoteKeyDao
import com.github.slznvk.data.dao.UserDao
import com.github.slznvk.data.entity.Converter
import com.github.slznvk.data.entity.EventEntity
import com.github.slznvk.data.entity.EventRemoteKeyEntity
import com.github.slznvk.data.entity.PostEntity
import com.github.slznvk.data.entity.PostRemoteKeyEntity
import com.github.slznvk.data.entity.UserEntity

@TypeConverters(value = [Converter::class])
@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class, EventEntity::class, EventRemoteKeyEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao

    abstract fun eventDao(): EventDao

    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao

    abstract fun userDao(): UserDao
}