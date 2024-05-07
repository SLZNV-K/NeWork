package com.github.slznvk.domain.dto

data class Post(
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String? = null,
    val authorId: Int,
    val authorJob: String? = null,
    val content: String,
    val coords: Coords? = null,
    override val id: Int,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean = false,
    val link: String? = null,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean = false,
    val published: String,
    val users: Users? = null,
    val ownedByMe: Boolean = false
) : ListItem