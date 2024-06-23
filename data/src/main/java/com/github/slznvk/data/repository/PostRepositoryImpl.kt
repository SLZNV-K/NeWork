package com.github.slznvk.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.filter
import androidx.paging.map
import com.github.slznvk.data.api.PostApiService
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
    private val postApiService: PostApiService,
    private val dao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = dao::getPagingSource,
        remoteMediator = PostRemoteMediator(
            postApiService = postApiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun likeById(id: Long) {

        try {
            val response = postApiService.likePost(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            val updateLikesPost = response.body() ?: throw Error()
            dao.insert(fromDto(updateLikesPost))
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = postApiService.dislikePost(id)
            if (!response.isSuccessful) {
                throw Error()
            }
            response.body() ?: throw Error()
            val updateLikesPost = response.body() ?: throw Error()
            dao.insert(fromDto(updateLikesPost))
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postApiService.removePostById(id)
            dao.removeById(id)
        } catch (e: IOException) {
            throw Error()
        } catch (e: Exception) {
            throw Error()
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = postApiService.savePost(post)

            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = response.body() ?: throw Exception()
            dao.insert(fromDto(body))
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getPostById(id: Long) = dao.getPostById(id).toDto()
    override suspend fun loadUserWall(authorId: Long) =
        data.map { pagingData -> pagingData.filter { it.authorId == authorId } }


//    override suspend fun loadUserWall(id: Int): List<Post> {
//        return try {
//            val response = postApiService.getUserWall(id)
//
//            if (!response.isSuccessful) {
//                throw Exception()
//            }
//            response.body() ?: throw Exception()
//        } catch (e: Exception) {
//            throw e
//        }
//    }

//    override suspend fun loadWall(authorId: Int) {
//        wall = data.map { pagingData -> pagingData.filter { it.authorId == authorId } }
//    }
}