package com.fotologue.core.data.lab

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lab_orders",
    foreignKeys = [
        ForeignKey(
            entity = LabSupplier::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["supplier_id"])]
)
data class LabOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "supplier_id") val supplierId: Long,   // FK → lab_suppliers.id
    val filmRollId: String = "",                               // FK → film_roll_sessions / log_entries
    val serviceType: String = "DEV_AND_SCAN",                 // DEV_ONLY, DEV_AND_SCAN, PRINT_ONLY, DEV_SCAN_PRINT
    val pushPullInstruction: String = "BOX_SPEED",            // BOX_SPEED, PUSH_1, PUSH_2, PULL_1
    val orderStatus: String = "SUBMITTED",                    // SUBMITTED, IN_PROCESSING, SCANNED, RETURNED_COMPLETE
    val trackingNumber: String = "",
    val costAmount: String = "",
    val labNotes: String = "",
    val submittedDate: Long = System.currentTimeMillis(),
    val completedDate: Long? = null
)
