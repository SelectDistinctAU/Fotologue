package com.fotologue.core.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return value?.let { Json.decodeFromString<List<String>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromCameraType(value: CameraType): String {
        return value.name
    }

    @TypeConverter
    fun toCameraType(value: String): CameraType {
        return CameraType.valueOf(value)
    }
}
