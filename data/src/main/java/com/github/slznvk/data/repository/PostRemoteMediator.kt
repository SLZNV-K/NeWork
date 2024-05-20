package com.github.slznvk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.slznvk.data.api.PostApiService
import com.github.slznvk.data.dao.PostDao
import com.github.slznvk.data.dao.PostRemoteKeyDao
import com.github.slznvk.data.db.AppDb
import com.github.slznvk.data.entity.PostEntity
import com.github.slznvk.data.entity.PostRemoteKeyEntity
import com.github.slznvk.data.entity.toEntity

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = postRemoteKeyDao.max()
                    if (id == null) {
                        postApiService.getLatestPosts(state.config.initialLoadSize)
                    } else postApiService.getAfterPosts(id, state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    postApiService.getBeforePosts(id, state.config.pageSize)
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
                        if (postRemoteKeyDao.max() == null) {
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        key = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.BEFORE,
                                        key = body.last().id,
                                    ),
                                )
                            )
                        } else postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                key = body.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id,
                            )
                        )
                    }

                    else -> {}
                }

                postDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

}
