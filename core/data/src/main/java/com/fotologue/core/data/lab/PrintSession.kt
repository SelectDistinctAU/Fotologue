package com.fotologue.core.data.lab

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fotologue.core.data.LogEntry

enum class ExposureMode {
    SINGLE_GRADE,
    SPLIT_GRADE
}

@Entity(
    tableName = "print_sessions",
    foreignKeys = [
        ForeignKey(
            entity = LogEntry::class,
            parentColumns = ["id"],
            childColumns = ["frame_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["frame_id"])]
)
data class PrintSession(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "frame_id") val negativeFrameId: String,      // FK → log_entries.id (NOT NULL, mandatory anchor)
    val enlargerId: String = "",                                   // FK → lab_lovs
    val lightHeadTypeId: String = "",                              // FK → lab_lovs
    val lensId: String = "",                                       // FK → lab_lovs
    val apertureIndex: Int = 0,                                    // f-stop index
    val magnification: Float = 1.0f,                               // calculated ratio
    val paperId: String = "",                                      // FK → lab_lovs
    val paperSize: String = "8x10\"",                              // enum
    val batchNumber: String? = null,                               // from consumable record
    val filterSystemId: String = "",                               // FK → lab_lovs
    val exposureMode: ExposureMode = ExposureMode.SINGLE_GRADE,     // SINGLE_GRADE or SPLIT_GRADE
    val developerId: String = "",                                  // FK → lab_lovs
    val dilution: String = "1+9",                                  // "A+B"
    val tempCelsius: Float = 20.0f,                                // 0.1 precision
    val immersionSeconds: Int = 120,
    val agitationIntervalSeconds: Int = 10,
    val tonerId: String? = null,                                   // FK → lab_lovs
    val tonerDurationSeconds: Int? = null,
    val dryDownNote: String? = null,                               // advisory only
    val testStripBaseRange: String? = null,
    val testStripSoftRange: String? = null,
    val testStripHardRange: String? = null,
    val fixerAgeOrCapacity: String? = null,                        // archival evidence
    val washTimeMinutes: Int? = null,                              // archival evidence
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
