package com.fotologue.core.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lenses"
)
@Serializable
data class Lens(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val make: String,
    val model: String,
    @ColumnInfo(name = "modelNumber") val modelNumber: String = "",
    val type: String, // Prime, Zoom, Telephoto
    val mount: String,
    val filterSize: Int, // in mm
    val isStabilized: Boolean,
    val minFocusDistance: Double, // in meters
    val maxMagnification: Double,
    val minAperture: Double,
    val maxAperture: Double,
    val minFocalLength: Int,
    val maxFocalLength: Int,
    val position: Int = 0
) {
    companion object {
        val TYPES = listOf("Prime", "Zoom", "Telephoto")
    }
}

data class LensWithCameras(
    val lens: Lens,
    val cameras: List<Camera> = emptyList()
)
