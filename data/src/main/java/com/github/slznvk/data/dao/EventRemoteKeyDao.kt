package com.github.slznvk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.slznvk.data.entity.EventRemoteKeyEntity

@Dao
interface EventRemoteKeyDao {

    @Query("SELECT max(`key`) FROM EventRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM EventRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: EventRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: List<EventRemoteKeyEntity>)

    @Query("DELETE FROM EventRemoteKeyEntity")
    suspend fun clear()
}