package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_inventory_masters")
@Serializable
data class InventoryMaster(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Chemistry, Paper, Safety Gear, Supplies, Unassigned
    val name: String, // e.g. Rapid Fixer, Multigrade RC Deluxe
    val manufacturer: String = "", // e.g. Ilford, Kodak, Ansell
    val containerSizeAmount: Float = 1.0f, // Numeric amount (e.g. 100, 1.0, 25)
    val containerSizeUnit: String = "L", // Container unit (e.g. "box", "pack", "bottle", "L")
    val innerUnitYieldTotal: Int = 1, // Quantifiable inner count (e.g. 25 sheets in a box, 1000 mL in a bottle)
    val innerItemUnit: String = "units", // Inner unit (e.g. "sheets", "mL", "frames", "pairs")
    val containerSize: String = "1 L", // Display size
    val defaultLocation: String = "Chemical Shelf",
    val notes: String = ""
) {
    val displaySize: String
        get() {
            val amountStr = if (containerSizeAmount % 1.0f == 0.0f) containerSizeAmount.toInt().toString() else containerSizeAmount.toString()
            return if (innerUnitYieldTotal > 1) {
                "$amountStr $containerSizeUnit ($innerUnitYieldTotal $innerItemUnit)"
            } else {
                "$amountStr $containerSizeUnit"
            }
        }
}
