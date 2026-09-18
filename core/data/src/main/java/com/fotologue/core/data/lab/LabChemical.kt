package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_chemicals")
@Serializable
data class LabChemical(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // Film Developer, Paper Developer, Stop Bath, Fixer, Archival Wash, Wetting Agent
    val dilution: String = "1+1",
    val mixDate: Long = System.currentTimeMillis(),
    val capacityRollsTotal: Int = 12,
    val capacityRollsUsed: Int = 0,
    val isExhausted: Boolean = false,
    val quantityOnHand: Int = 1,
    val unopenedQuantity: Int = 1, // Unopened stock in fridge/storage
    val openedQuantity: Int = 0, // Opened stock in active use
    val volumeAmount: Float = 1.0f,
    val volumeUnit: String = "L", // L, mL, fl oz, gal, pt
    val expirationDate: Long? = null,
    val supplier: String = "",
    val cost: String = "",
    val userRating: Float = 0.0f, // 0.0 to 5.0 rating
    val performanceNotes: String = "" // Notes on what film/paper worked well or poorly
)
