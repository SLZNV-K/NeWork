package com.github.slznvk.domain.repository

import androidx.paging.PagingData
import com.github.slznvk.domain.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun save(post: Post)
    suspend fun getPostById(id: Int): Post
    suspend fun loadUserWall(authorId: Int): Flow<PagingData<Post>>
}