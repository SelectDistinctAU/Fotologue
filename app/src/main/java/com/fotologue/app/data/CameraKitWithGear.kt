package com.fotologue.app.data

import androidx.room.Embedded
import androidx.room.Relation

data class CameraKitWithGear(
    @Embedded val cameraKit: CameraKit,
    @Relation(
        parentColumn = "cameraId",
        entityColumn = "id"
    )
    var camera: Camera? = null,
    @Relation(
        parentColumn = "lensId",
        entityColumn = "id"
    )
    var lens: Lens? = null,
    @Relation(
        parentColumn = "filmId",
        entityColumn = "id"
    )
    var film: Film? = null,
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
        parentColumn = "flashId",
        entityColumn = "id"
    )
    var flash: Flash? = null,
    @Relation(
        parentColumn = "viewfinderId",
        entityColumn = "id"
    )
    var viewfinder: Viewfinder? = null
)
