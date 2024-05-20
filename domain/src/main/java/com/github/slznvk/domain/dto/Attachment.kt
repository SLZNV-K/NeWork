package com.github.slznvk.domain.dto

data class Attachment(
    val type: AttachmentType,
    val url: String
)

enum class AttachmentType {
    IMAGE, VIDEO, AUDIO
}