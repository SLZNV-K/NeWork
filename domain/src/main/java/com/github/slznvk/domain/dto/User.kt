package com.github.slznvk.domain.dto

data class User(
    override val id: Long,
    val avatar: String,
    val login: String,
    val name: String
) : ListItem