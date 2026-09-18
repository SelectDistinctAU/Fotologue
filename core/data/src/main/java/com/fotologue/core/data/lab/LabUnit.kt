package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_units")
@Serializable
data class LabUnit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. L, mL, fl oz, gal, sheets, box, pair, pack, roll
    val isSystemCore: Boolean = false // If true, fixed core unit
)
