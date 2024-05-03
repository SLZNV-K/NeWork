package com.github.slznvk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.github.slznvk.data.api.ApiService
import com.github.slznvk.data.dao.PostDao
import com.github.slznvk.data.dao.PostRemoteKeyDao
import com.github.slznvk.data.db.AppDb
import com.github.slznvk.data.entity.PostEntity
import com.github.slznvk.data.entity.PostEntity.Companion.fromDto
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.repository.PostRepository
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = dao::getPagingSource,
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAllPosts(): List<Post> {
        return try {
            val response = apiService.getAllPosts()
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw Exception()
        }
    }

    override suspend fun likeById(id: Int) {
        dao.likeById(id)
        try {
            val response = apiService.like(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            response.body() ?: throw Error()
        } catch (e: Exception) {
            dao.likeById(id)
            throw Error()
        }
    }

    override suspend fun dislikeById(id: Int) {
        dao.likeById(id)
        try {
            val response = apiService.dislike(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            response.body() ?: throw Error()
        } catch (e: Exception) {
            dao.likeById(id)
            throw Error()
        }
    }

    override suspend fun removeById(id: Int) {
        dao.removeById(id)
        try {
            apiService.delete(id)
        } catch (e: IOException) {
            throw Error()
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun save(post: Post) {
//        dao.insert(fromDto(post))
        try {
            val response = apiService.savePost(post)

            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = response.body() ?: throw Exception()
            dao.insert(fromDto(body))

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getPostById(id: Int) = dao.getPostById(id).toDto()
}