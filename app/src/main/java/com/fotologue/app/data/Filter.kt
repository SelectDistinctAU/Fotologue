package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "filters")
@Serializable
data class Filter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // ND, Polarizer, UV, etc.
    val compensationStops: Double, // e.g. 1.0, 2.0, 3.0
    val position: Int = 0
)

data class FilterWithCameras(
    val filter: Filter,
    val cameras: List<Camera> = emptyList()
)
