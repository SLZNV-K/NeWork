package com.github.slznvk.data

import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PostRepository {

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

    override suspend fun save(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun getPostById(id: Int): Post {
        return try {
            val response = apiService.getPostById(id)
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw Exception()
        }
    }
}