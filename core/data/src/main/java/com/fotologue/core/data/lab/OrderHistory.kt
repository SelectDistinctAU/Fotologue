package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_order_history",
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
data class OrderHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplierId: Long?,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "IN_TRANSIT", // IN_TRANSIT, DELIVERED, WAITLISTED, PACKAGING, LOAN_OUT, RENTAL_IN, SERVICE_REPAIR
    val totalCost: String = "",
    val trackingNumber: String = "",
    val notes: String = "",
    val assetCategory: String = "Other",
    val assetRefId: Long? = null
)
