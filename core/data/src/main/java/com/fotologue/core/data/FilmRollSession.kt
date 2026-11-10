package com.fotologue.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "film_roll_sessions")
@Serializable
data class FilmRollSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cameraId: Long,
    val filmId: Long,
    val rollNumber: String = "Roll #1",
    val nextFrameNumber: Int = 1,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val targetIso: Int = 400,
    val pushPullStops: Float = 0.0f,
    val status: String = "ACTIVE", // ACTIVE, EXPOSED, DEVELOPED, ARCHIVED
    val notes: String = ""
)
