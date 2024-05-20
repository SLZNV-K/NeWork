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

    override suspend fun likeById(id: Int) {
        eventDao.likeById(id)
        try {
            val response = apiService.likeEvent(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            response.body() ?: throw Error()
        } catch (e: Exception) {
            eventDao.likeById(id)
            throw Error()
        }
    }

    override suspend fun dislikeById(id: Int) {
        eventDao.likeById(id)
        try {
            val response = apiService.dislikeEvent(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            response.body() ?: throw Error()
        } catch (e: Exception) {
            eventDao.likeById(id)
            throw Error()
        }
    }

    override suspend fun shareById(id: Int) {}

    override suspend fun removeById(id: Int) {
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

    override suspend fun getEventById(id: Int): Event = eventDao.getEventById(id).toDto()
}