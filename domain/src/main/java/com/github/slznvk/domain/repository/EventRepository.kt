package com.github.slznvk.domain.repository

import androidx.paging.PagingData
import com.github.slznvk.domain.dto.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(event: Event)
    suspend fun getEventById(id: Long): Event
}