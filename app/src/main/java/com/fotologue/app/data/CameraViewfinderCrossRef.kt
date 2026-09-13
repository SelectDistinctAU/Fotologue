package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_viewfinder_cross_ref",
    primaryKeys = ["cameraId", "viewfinderId"],
    indices = [Index(value = ["viewfinderId"])]
)
@Serializable
data class CameraViewfinderCrossRef(
    val cameraId: Long,
    val viewfinderId: Long
)
