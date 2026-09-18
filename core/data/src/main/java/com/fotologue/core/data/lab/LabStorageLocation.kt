package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_storage_locations")
@Serializable
data class LabStorageLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sleeveNumber: String = "Sleeve #1",
    val locationType: String // Negative Sleeve, Print Box, Framing Case, Safe
)
