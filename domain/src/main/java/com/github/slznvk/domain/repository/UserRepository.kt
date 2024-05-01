package com.github.slznvk.domain.repository

import com.github.slznvk.domain.dto.User

interface UserRepository {
    suspend fun getAllUsers(): List<User>
}