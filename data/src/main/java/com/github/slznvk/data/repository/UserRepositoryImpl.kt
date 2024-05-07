package com.github.slznvk.data.repository

import com.github.slznvk.data.api.ApiService
import com.github.slznvk.domain.dto.User
import com.github.slznvk.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override suspend fun getAllUsers(): List<User> {
        return try {
            val response = apiService.getAllUsers()
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw e
        }
    }
}