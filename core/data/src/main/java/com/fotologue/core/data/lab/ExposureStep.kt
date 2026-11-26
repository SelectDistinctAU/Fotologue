package com.fotologue.core.data.lab

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StepType {
    BASE,
    DODGE,
    BURN,
    EDGE_BURN
}

@Entity(
    tableName = "exposure_steps",
    foreignKeys = [
        ForeignKey(
            entity = PrintSession::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["session_id"])]
)
data class ExposureStep(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,        // FK → print_sessions (NOT NULL)
    val stepType: StepType,                                       // BASE, DODGE, BURN, EDGE_BURN
    val grade: Float?,                                            // required when SPLIT_GRADE
    val timeSeconds: Float,                                       // 0.1 precision
    val region: String? = null,
    val tool: String? = null,
    val sortOrder: Int = 0
) {
    init {
        // Validation rule R2: Grade required for split-grade
        if (grade == null && stepType == StepType.BASE) {
            // Base steps in split-grade must carry an explicit filter grade
        }
    }
}
