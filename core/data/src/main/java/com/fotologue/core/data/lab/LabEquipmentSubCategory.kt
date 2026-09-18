package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_equipment_subcategories")
@Serializable
data class LabEquipmentSubCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // e.g. Dev Tank, Enlarger, Enlarger Lens
    val name: String // e.g. Multi-roll developing tank, LED head, 50mm Enlarging Lens
)
