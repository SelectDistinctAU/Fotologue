package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fotologue.core.data.LogEntry
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_performance_logs",
    foreignKeys = [
        ForeignKey(
            entity = LogEntry::class,
            parentColumns = ["id"],
            childColumns = ["logEntryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InventoryItem::class,
            parentColumns = ["id"],
            childColumns = ["inventoryItemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("logEntryId"), Index("inventoryItemId")]
)
@Serializable
data class PerformanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val logEntryId: Long, // Roll.Frame ID from Capture
    val inventoryItemId: Long? = null, // Chem/Paper/Supply used (Nullable if skill reject)
    val outcomeStatus: String = "KEEPER", // KEEPER vs REJECT
    val rejectCategory: String? = null, // SYSTEM_ERROR vs PHOTOGRAPHER_LEARNING
    val systemErrorReason: String? = null, // EXPIRED_CHEMISTRY, PAPER_FOGGED, LIGHT_LEAK, HARDWARE_FAILURE
    val learningTag: String? = null, // BAD_FRAMING, MISSED_MOMENT, UNDEREXPOSED, OVEREXPOSED, FOCUS_MISSED, LOW_LIGHT_SHAKE
    val outcomeRating: Float = 5.0f, // 1.0 to 5.0 rating
    val notes: String = "" // Growth reflection notes
)
