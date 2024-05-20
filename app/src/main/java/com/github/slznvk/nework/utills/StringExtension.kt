package com.github.slznvk.nework.utills

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.formatDateTime(): String {
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    val localDateTime = LocalDateTime.parse(this, inputFormatter)
    return localDateTime.format(outputFormatter)
}