package com.fotologue.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "viewfinders")
@Serializable
data class Viewfinder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val modelNumber: String = "",
    val position: Int = 0
)

data class ViewfinderWithCameras(
    val viewfinder: Viewfinder,
    val cameras: List<Camera> = emptyList()
)
