package com.github.slznvk.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.github.slznvk.domain.dto.AdditionalProp
import com.github.slznvk.domain.dto.Attachment
import com.github.slznvk.domain.dto.Coords
import com.github.slznvk.domain.dto.Post

@TypeConverters(Converter::class)
@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String?,
    val authorId: Long,
    val authorJob: String?,
    val content: String,
    @Embedded
    val coords: Coords? = null,
    val likeOwnerIds: List<Long>,
    val likedByMe: Boolean,
    val link: String?,
    val mentionIds: List<Long>,
    val mentionedMe: Boolean,
    val published: String,
    val users: Map<Long, AdditionalProp>,
    val ownedByMe: Boolean
) {

    fun toDto() = Post(
        id = id,
        attachment = attachment,
        author = author,
        authorAvatar = authorAvatar,
        authorId = authorId,
        authorJob = authorJob,
        content = content,
        coords = coords,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
        link = link,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        published = published,
        users = users,
        ownedByMe = ownedByMe
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                attachment = dto.attachment,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorId = dto.authorId,
                authorJob = dto.authorJob,
                content = dto.content,
                coords = dto.coords,
                likeOwnerIds = dto.likeOwnerIds,
                likedByMe = dto.likedByMe,
                link = dto.link,
                mentionIds = dto.mentionIds,
                mentionedMe = dto.mentionedMe,
                published = dto.published,
                users = dto.users,
                ownedByMe = dto.ownedByMe
            )
    }
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)