package com.fotologue.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "cameras")
@Serializable
data class Camera(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CameraType,
    val mount: String = "",
    @ColumnInfo(name = "modelNumber") val modelNumber: String = "",
    val shutterSpeeds: List<String> = emptyList(),
    val isHalfFrame: Boolean = false,
    val format: String = "35mm",
    val position: Int = 0
)
