package com.github.slznvk.domain.repository

import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.User

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User

    suspend fun getUserJobs(userId: Long): List<Job>

    suspend fun saveJob(job: Job)

    suspend fun deleteJodById(id: Long)

    suspend fun saveUser(user: User)
}