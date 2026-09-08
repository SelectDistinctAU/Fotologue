package com.fotologue.app.data

import androidx.room.Embedded
import androidx.room.Relation

data class LogEntryWithGear(
    @Embedded val logEntry: LogEntry,
    @Relation(
        parentColumn = "cameraId",
        entityColumn = "id"
    )
    var camera: Camera? = null,
    @Relation(
        parentColumn = "filmId",
        entityColumn = "id"
    )
    var film: Film? = null,
    @Relation(
        parentColumn = "lensId",
        entityColumn = "id"
    )
    var lens: Lens? = null,
    @Relation(
        parentColumn = "teleconverterId",
        entityColumn = "id"
    )
    var teleconverter: Teleconverter? = null,
    @Relation(
        parentColumn = "filterId",
        entityColumn = "id"
    )
    var filter: Filter? = null,
    @Relation(
        parentColumn = "kitId",
        entityColumn = "id"
    )
    var kit: CameraKit? = null,
    @Relation(
        parentColumn = "flashId",
        entityColumn = "id"
    )
    var flash: Flash? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = LogEntryTagCrossRef::class,
            parentColumn = "logEntryId",
            entityColumn = "tagId"
        )
    )
    var tags: List<Tag> = emptyList()
)
