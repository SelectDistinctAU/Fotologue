package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_stock_lots",
    foreignKeys = [
        ForeignKey(
            entity = InventoryMaster::class,
            parentColumns = ["id"],
            childColumns = ["masterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LabSupplier::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("masterId"), Index("supplierId")]
)
@Serializable
data class StockLot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val masterId: Long, // FK to InventoryMaster
    val supplierId: Long? = null, // FK to LabSupplier
    val batchNumber: String = "", // Batch / Lot Number
    val location: String = "Chemical Shelf", // Physical location of this stock lot
    val cost: String = "", // Cost paid
    val expiryDate: Long? = null,
    val openedDate: Long? = null, // Set when moved from ON_HAND to IN_USE
    val status: String = "ON_HAND", // ON_HAND, IN_USE, IN_TRANSIT, EXHAUSTED
    val quantity: Int = 1, // Number of containers in this lot
    val purchaseDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
