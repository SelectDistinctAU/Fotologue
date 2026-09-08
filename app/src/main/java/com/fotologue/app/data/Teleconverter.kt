package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "teleconverters")
@Serializable
data class Teleconverter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val magnification: Double, // e.g. 1.4, 2.0
    val lightLossStops: Double, // e.g. 1.0, 2.0
    val position: Int = 0
)
