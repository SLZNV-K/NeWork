package com.github.slznvk.data.entity

import androidx.room.TypeConverter
import com.github.slznvk.domain.dto.AdditionalProp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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