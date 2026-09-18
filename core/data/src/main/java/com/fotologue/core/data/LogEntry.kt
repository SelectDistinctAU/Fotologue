package com.fotologue.core.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "log_entries",
    foreignKeys = [
        ForeignKey(
            entity = Camera::class,
            parentColumns = ["id"],
            childColumns = ["cameraId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Film::class,
            parentColumns = ["id"],
            childColumns = ["filmId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Lens::class,
            parentColumns = ["id"],
            childColumns = ["lensId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Teleconverter::class,
            parentColumns = ["id"],
            childColumns = ["teleconverterId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Filter::class,
            parentColumns = ["id"],
            childColumns = ["filterId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CameraKit::class,
            parentColumns = ["id"],
            childColumns = ["kitId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Flash::class,
            parentColumns = ["id"],
            childColumns = ["flashId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Viewfinder::class,
            parentColumns = ["id"],
            childColumns = ["viewfinderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("cameraId"),
        Index("filmId"),
        Index("lensId"),
        Index("teleconverterId"),
        Index("filterId"),
        Index("kitId"),
        Index("flashId"),
        Index("viewfinderId")
    ]
)
@Serializable
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val notes: String,
    val environmentalConditions: String,
    val cameraId: Long? = null,
    val filmId: Long? = null,
    val lensId: Long? = null,
    val teleconverterId: Long? = null,
    val filterId: Long? = null,
    val kitId: Long? = null,
    val flashId: Long? = null,
    val viewfinderId: Long? = null,
    val focalLength: Float? = null,
    val effectiveFocalLength: Float? = null,
    val effectiveAperture: String? = null,
    @Embedded val exposureSettings: ExposureSettings,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val filmRoll: String? = null,
    val lastUpdated: Long = 0,
    val isExportedZip: Boolean = false,
    val isExportedCsv: Boolean = false,
    val isExportedJson: Boolean = false,
    val isExportedXmp: Boolean = false
)
