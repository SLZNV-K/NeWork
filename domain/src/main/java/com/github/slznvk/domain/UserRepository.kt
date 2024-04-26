package com.github.slznvk.domain

import com.github.slznvk.domain.dto.User

interface UserRepository {
    val data: List<User>
}