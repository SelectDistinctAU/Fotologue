package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fotologue.core.data.LogEntry
import kotlinx.serialization.Serializable

@Entity(
    tableName = "print_recipes",
    foreignKeys = [
        ForeignKey(
            entity = LogEntry::class,
            parentColumns = ["id"],
            childColumns = ["logEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("logEntryId")]
)
@Serializable
data class PrintRecipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val logEntryId: Long,
    val paperName: String,
    val paperSize: String = "11x14",
    val enlargerLens: String = "",
    val baseExposureSeconds: Float = 14.5f,
    val gradeFilter: String = "Grade 2.5",
    val dodgeBurnMapUri: String? = null
)
