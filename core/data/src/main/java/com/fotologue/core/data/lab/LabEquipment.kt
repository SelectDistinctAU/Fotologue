package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_equipment")
@Serializable
data class LabEquipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Enlarger, Enlarger Lens, Dev Tank, Tray, Light Table
    val notes: String = ""
)
