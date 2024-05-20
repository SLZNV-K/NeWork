package com.github.slznvk.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.slznvk.domain.dto.Attachment
import com.github.slznvk.domain.dto.Coords
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.dto.Users
import com.google.gson.Gson

@TypeConverters(Converter::class)
@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String?,
    val authorId: Int,
    val authorJob: String?,
    val content: String,
    @Embedded
    val coords: Coords? = null,
    val likeOwnerIds: Int,
    val likedByMe: Boolean,
    val link: String?,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val published: String,
    val users: Users? = null,
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
                likeOwnerIds = dto.likeOwnerIds.firstOrNull() ?: 0,
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

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)


class Converter {
    @TypeConverter
    fun fromListInt(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListInt(data: String): List<Int> {
        return data.split(",").mapNotNull { it.toIntOrNull() }
    }

    @TypeConverter
    fun fromListUsers(users: Users): String {
        return Gson().toJson(users)
    }

    @TypeConverter
    fun toListUsers(json: String): Users {
        return Gson().fromJson(json, Users::class.java)
    }

}