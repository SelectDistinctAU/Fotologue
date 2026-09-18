package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_supplier_issue_logs",
    foreignKeys = [
        ForeignKey(
            entity = LabSupplier::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("supplierId")]
)
@Serializable
data class SupplierIssueLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplierId: Long, // FK to LabSupplier
    val date: Long = System.currentTimeMillis(),
    val issueSummary: String, // e.g. "Missing 2x boxes of 8x10 paper in shipment"
    val status: String = "OPEN", // OPEN, FOLLOWED_UP, RESOLVED, CLOSED
    val contactPerson: String = "", // e.g. "Called Sarah in dispatch"
    val resolutionReason: String = "", // e.g. "Express posted replacement box"
    val notes: String = ""
)
