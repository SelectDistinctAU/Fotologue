package com.fotologue.app.data

import androidx.room.ForeignKey
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "camera_kits",
    indices = [
        Index("cameraId"),
        Index("lensId"),
        Index("filmId"),
        Index("teleconverterId"),
        Index("filterId"),
        Index("flashId"),
        Index("viewfinderId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Camera::class,
            parentColumns = ["id"],
            childColumns = ["cameraId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Lens::class,
            parentColumns = ["id"],
            childColumns = ["lensId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Film::class,
            parentColumns = ["id"],
            childColumns = ["filmId"],
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
    ]
)
@Serializable
data class CameraKit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cameraId: Long?,
    val lensId: Long?,
    val filmId: Long?,
    val teleconverterId: Long? = null,
    val filterId: Long? = null,
    val flashId: Long? = null,
    val viewfinderId: Long? = null,
    val position: Int = 0
)
