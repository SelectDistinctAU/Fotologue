package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_filter_cross_ref",
    primaryKeys = ["cameraId", "filterId"],
    indices = [Index(value = ["filterId"])]
)
@Serializable
data class CameraFilterCrossRef(
    val cameraId: Long,
    val filterId: Long
)
