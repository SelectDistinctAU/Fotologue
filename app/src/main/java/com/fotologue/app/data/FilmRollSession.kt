package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "film_roll_sessions",
    primaryKeys = ["cameraId", "filmId"]
)
@Serializable
data class FilmRollSession(
    val cameraId: Long,
    val filmId: Long,
    val rollNumber: Int = 1,
    val nextFrameNumber: Int = 1
)
