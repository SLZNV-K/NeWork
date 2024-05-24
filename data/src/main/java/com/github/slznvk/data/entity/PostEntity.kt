package com.github.slznvk.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.slznvk.domain.dto.AdditionalProp
import com.github.slznvk.domain.dto.Attachment
import com.github.slznvk.domain.dto.Coords
import com.github.slznvk.domain.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    val likeOwnerIds: Long,
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
        likeOwnerIds = listOf(likeOwnerIds),
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
                likeOwnerIds = dto.likeOwnerIds.firstOrNull() ?: 0L,
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


class Converter {
    private val gson = Gson()

    @TypeConverter
    fun fromListLong(list: List<Long>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListLong(data: String): List<Long> {
        return data.split(",").mapNotNull { it.toLongOrNull() }
    }

    @TypeConverter
    fun fromMap(map: Map<Long, AdditionalProp>): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(data: String): Map<Long, AdditionalProp> {
        val mapType = object : TypeToken<Map<Long, AdditionalProp>>() {}.type
        return gson.fromJson(data, mapType)
    }
}