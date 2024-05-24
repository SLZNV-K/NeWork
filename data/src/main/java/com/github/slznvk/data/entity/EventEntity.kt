package com.github.slznvk.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.github.slznvk.domain.dto.AdditionalProp
import com.github.slznvk.domain.dto.Attachment
import com.github.slznvk.domain.dto.Coords
import com.github.slznvk.domain.dto.Event

@TypeConverters(Converter::class)
@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @Embedded
    val attachment: Attachment? = null,
    val author: String,
    val authorAvatar: String? = null,
    val authorId: Long,
    val authorJob: String? = null,
    val content: String,
    @Embedded
    val coords: Coords? = null,
    val datetime: String = "",
    val likeOwnerIds: Long,
    val likedByMe: Boolean = false,
    val link: String? = null,
    val participantsIds: List<Long>,
    val participatedByMe: Boolean = false,
    val published: String,
    val speakerIds: List<Long>,
    val typeEvent: String = "",
    val users: Map<Long, AdditionalProp> = emptyMap(),
    val ownedByMe: Boolean = false,
) {

    fun toDto() = Event(
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
        published = published,
        users = users,
        datetime = datetime,
        participantsIds = participantsIds,
        participatedByMe = participatedByMe,
        speakerIds = speakerIds,
        type = typeEvent,
        ownedByMe = ownedByMe
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
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
                published = dto.published,
                users = dto.users,
                datetime = dto.datetime,
                participantsIds = dto.participantsIds,
                participatedByMe = dto.participatedByMe,
                speakerIds = dto.speakerIds,
                typeEvent = dto.type,
                ownedByMe = dto.ownedByMe
            )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)

