package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_item_names")
@Serializable
data class LabItemName(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. Rapid Fixer, Multigrade RC Deluxe Pearl 8x10, ID-11
    val category: String = "Chemistry" // Chemistry, Paper, Safety Gear, Supplies
)
