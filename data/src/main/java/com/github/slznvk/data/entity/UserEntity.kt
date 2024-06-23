package com.github.slznvk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.slznvk.domain.dto.User

@Entity
data class UserEntity(
    @PrimaryKey
    val id: Long,
    val avatar: String?,
    val login: String,
    val name: String,
    val isChooser: Boolean = false,
) {
    fun toDto() = User(
        id = id,
        avatar = avatar,
        login = login,
        name = name,
        isChooser = isChooser
    )

    companion object {
        fun fromDto(dto: User) = UserEntity(
            id = dto.id,
            avatar = dto.avatar,
            login = dto.login,
            name = dto.name,
            isChooser = dto.isChooser
        )

        fun List<User>.toEntity(): List<UserEntity> = map(UserEntity::fromDto)
    }
}