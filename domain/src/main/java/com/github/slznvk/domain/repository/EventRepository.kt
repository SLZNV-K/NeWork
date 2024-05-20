package com.github.slznvk.domain.repository

import androidx.paging.PagingData
import com.github.slznvk.domain.dto.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun shareById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(event: Event)
    suspend fun getEventById(id: Int): Event
}