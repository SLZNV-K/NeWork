package com.github.slznvk.domain.dto

data class Post(
    val attachment: Attachment,
    val author: String,
    val authorAvatar: String,
    val authorId: Int,
    val authorJob: String,
    val content: String,
    val coords: Coords,
    val id: Int,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val link: String,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val published: String,
    val users: Users
)