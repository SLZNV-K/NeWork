package com.github.slznvk.domain.repository

import com.github.slznvk.domain.dto.Event

interface EventRepository {
    suspend fun getAllEvents(): List<Event>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(event: Event)
    suspend fun getEventById(id: Long): Event
}