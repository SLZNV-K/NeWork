package com.github.slznvk.data.repository

import com.github.slznvk.data.api.UserApiService
import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.User
import com.github.slznvk.domain.repository.UserRepository
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
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

    override suspend fun getUserById(id: Int): User {
        return try {
            val response = apiService.getUserById(id)
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserJobs(userId: Int): List<Job> {
        return try {
            val response = apiService.getUserJobs(userId)
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveJob(job: Job) {
        try {
            val response = apiService.saveJob(job)

            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw Exception()

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteJodById(id: Int) {
        try {
            apiService.deleteJodById(id)
        } catch (e: IOException) {
            throw Error()
        } catch (e: Exception) {
            throw Error()
        }
    }
}