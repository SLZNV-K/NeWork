package com.github.slznvk.domain

import com.github.slznvk.domain.dto.Post

interface PostRepository {
    val data: List<Post>
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun getPostById(id: Long): Post
}