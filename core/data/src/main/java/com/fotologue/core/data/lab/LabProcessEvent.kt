package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "lab_process_events",
    indices = [Index("filmRollId")]
)
@Serializable
data class LabProcessEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filmRollId: String,
    val developerName: String,
    val temperatureCelsius: Float = 20.0f,
    val durationSeconds: Int = 540,
    val agitationSchedule: String = "Initial 30s continuous, 10s/min",
    val pushPullStops: Float = 0.0f
)
