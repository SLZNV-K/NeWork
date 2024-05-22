package com.github.slznvk.domain.dto

data class Event(
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String? = null,
    val authorId: Int,
    val authorJob: String? = null,
    val content: String,
    val coords: Coords? = null,
    val datetime: String = "",
    override val id: Int,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean = false,
    val link: String? = null,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean = false,
    val published: String,
    val speakerIds: List<Int>,
    val type: String = "",
    val users: Users? = null,
    val ownedByMe: Boolean = false,
    var songPlaying: Boolean = false
) : ListItem
