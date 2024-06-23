package com.github.slznvk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.github.slznvk.data.api.EventApiService
import com.github.slznvk.data.dao.EventDao
import com.github.slznvk.data.dao.EventRemoteKeyDao
import com.github.slznvk.data.db.AppDb
import com.github.slznvk.data.entity.EventEntity
import com.github.slznvk.data.entity.EventEntity.Companion.fromDto
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val apiService: EventApiService,
    private val eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = eventDao::getPagingSource,
        remoteMediator = EventRemoteMediator(
            apiService = apiService,
            eventDao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao,
            appDb = appDb
        )
    ).flow.map {
        it.map(EventEntity::toDto)
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = apiService.likeEvent(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            val updateLikesEvent = response.body() ?: throw Error()
            eventDao.insert(fromDto(updateLikesEvent))
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = apiService.dislikeEvent(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            val updateLikesEvent = response.body() ?: throw Error()
            eventDao.insert(fromDto(updateLikesEvent))
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            apiService.removeEventById(id)
            eventDao.removeById(id)
        } catch (e: IOException) {
            throw Error()
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun save(event: Event) {
        try {
            val response = apiService.saveEvent(event)

            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = response.body() ?: throw Exception()
            eventDao.insert(fromDto(body))
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getEventById(id: Long): Event = eventDao.getEventById(id).toDto()
}