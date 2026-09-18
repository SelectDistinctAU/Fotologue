package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_categories")
@Serializable
data class LabCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. Chemistry, Paper, Safety Gear, Supplies
    val defaultUnit: String = "L", // Associated default unit (e.g. L, sheets, box)
    val isSystemCore: Boolean = false // If true, fixed core unit
)
