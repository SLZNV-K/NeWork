package com.github.slznvk.domain.dto

data class User(
    val avatar: String,
    override val id: Int,
    val login: String,
    val name: String
) : ListItem