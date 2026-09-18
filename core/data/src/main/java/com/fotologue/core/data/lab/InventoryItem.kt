package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_inventory_items",
    foreignKeys = [
        ForeignKey(
            entity = LabSupplier::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("supplierId")]
)
@Serializable
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Chemistry, Paper, Safety Gear, Supplies
    val type: String = "", // e.g. Film Dev, Fixer, Gloves, Negative Sleeves
    val manufacturer: String = "",
    val batchNumber: String = "", // Recalled or specific lot number
    val containerSize: String = "1 L", // e.g. 1 L, 500 mL, Box of 100
    val supplierId: Long? = null,
    val cost: String = "",
    val quantityOnHand: Int = 1, // Unopened stock
    val quantityInUse: Int = 0, // Opened / active stock
    val quantityInTransit: Int = 0, // Ordered, not yet received
    val expiryDate: Long? = null,
    val notes: String = ""
)
