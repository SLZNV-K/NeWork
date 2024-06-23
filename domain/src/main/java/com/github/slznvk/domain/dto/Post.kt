package com.github.slznvk.domain.dto

data class Post(
    override val id: Long,
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String? = null,
    val authorId: Long,
    val authorJob: String? = null,
    val content: String,
    val coords: Coords? = null,
    val likeOwnerIds: List<Long>,
    val likedByMe: Boolean = false,
    val link: String? = null,
    val mentionIds: List<Long>,
    val mentionedMe: Boolean = false,
    val published: String,
    val users: Map<Long, AdditionalProp> = emptyMap(),
    val ownedByMe: Boolean = false,
    var songPlaying: Boolean = false
) : ListItem