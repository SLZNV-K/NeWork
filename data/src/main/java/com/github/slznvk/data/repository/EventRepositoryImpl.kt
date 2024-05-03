package com.github.slznvk.data.repository

import com.github.slznvk.data.api.ApiService
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val service: ApiService
) : EventRepository {
    override suspend fun getAllEvents(): List<Event> {
        return try {
            val response = service.getAllEvents()
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun likeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun dislikeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun save(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun getEventById(id: Long): Event {
        TODO("Not yet implemented")
    }
}