package com.fotologue.core.data

import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_teleconverter_cross_ref",
    primaryKeys = ["cameraId", "teleconverterId"],
    indices = [Index(value = ["teleconverterId"])]
)
@Serializable
data class CameraTeleconverterCrossRef(
    val cameraId: Long,
    val teleconverterId: Long
)
