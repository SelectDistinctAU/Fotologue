package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_lens_cross_ref",
    primaryKeys = ["cameraId", "lensId"],
    indices = [Index(value = ["lensId"])]
)
@Serializable
data class CameraLensCrossRef(
    val cameraId: Long,
    val lensId: Long
)
