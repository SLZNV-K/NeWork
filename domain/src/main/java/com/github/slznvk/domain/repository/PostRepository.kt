package com.github.slznvk.domain.repository

import androidx.paging.PagingData
import com.github.slznvk.domain.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getPostById(id: Long): Post
    suspend fun loadUserWall(authorId: Long): Flow<PagingData<Post>>
}