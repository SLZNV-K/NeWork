package com.github.slznvk.domain.dto

data class Job(
    override val id: Long,
    val finish: String? = null,
    val link: String? = null,
    val name: String,
    val position: String,
    val start: String
): ListItem