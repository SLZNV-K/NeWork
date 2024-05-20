package com.github.slznvk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.slznvk.data.api.EventApiService
import com.github.slznvk.data.dao.EventDao
import com.github.slznvk.data.dao.EventRemoteKeyDao
import com.github.slznvk.data.db.AppDb
import com.github.slznvk.data.entity.EventEntity
import com.github.slznvk.data.entity.EventRemoteKeyEntity
import com.github.slznvk.data.entity.toEntity

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: EventApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = eventRemoteKeyDao.max()
                    if (id == null) {
                        apiService.getLatestEvents(state.config.initialLoadSize)
                    } else apiService.getAfterEvents(id, state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getBeforeEvents(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = response.body().orEmpty()
                .ifEmpty { return MediatorResult.Success(endOfPaginationReached = false) }

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (eventRemoteKeyDao.max() == null) {
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        type = EventRemoteKeyEntity.KeyType.AFTER,
                                        key = body.first().id,
                                    ),
                                    EventRemoteKeyEntity(
                                        type = EventRemoteKeyEntity.KeyType.BEFORE,
                                        key = body.last().id,
                                    ),
                                )
                            )
                        } else eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.AFTER,
                                key = body.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id,
                            )
                        )
                    }

                    else -> {}
                }

                eventDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}