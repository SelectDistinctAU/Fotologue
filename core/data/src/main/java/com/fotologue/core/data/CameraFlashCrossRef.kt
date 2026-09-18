package com.fotologue.core.data

import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_flash_cross_ref",
    primaryKeys = ["cameraId", "flashId"],
    indices = [Index(value = ["flashId"])]
)
@Serializable
data class CameraFlashCrossRef(
    val cameraId: Long,
    val flashId: Long
)
