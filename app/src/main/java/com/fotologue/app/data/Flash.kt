package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "flashes")
@Serializable
data class Flash(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val make: String,
    val model: String,
    val guideNumber: Int,
    val position: Int = 0
)

data class FlashWithCameras(
    val flash: Flash,
    val cameras: List<Camera> = emptyList()
)
