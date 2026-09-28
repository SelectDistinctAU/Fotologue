package com.fotologue.desktop

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.encodeToString

object DatabaseRepository {

    fun loadAllRolls(): List<DesktopMasterRollItem> {
        return transaction {
            MasterRollEntity.all().map { rollEntity ->
                DesktopMasterRollItem(
                    id = rollEntity.id.value.toString(),
                    status = try { RollStatus.valueOf(rollEntity.status) } catch (_: Exception) { RollStatus.UNASSIGNED },
                    contributorId = rollEntity.contributorId,
                    cameraName = rollEntity.cameraName,
                    filmName = rollEntity.filmName,
                    filmRollSerial = rollEntity.filmRollSerial,
                    frameCount = rollEntity.frameCount,
                    frames = rollEntity.frames.map { frameEntity ->
                        DesktopFrameDetail(
                            frameNumber = frameEntity.frameNumber,
                            cameraName = frameEntity.cameraName,
                            filmName = frameEntity.filmName,
                            shutterSpeed = frameEntity.shutterSpeed,
                            aperture = frameEntity.aperture,
                            iso = frameEntity.iso,
                            timestampMs = frameEntity.timestampMs,
                            dateDisplay = frameEntity.dateDisplay,
                            notes = frameEntity.notes,
                            tags = frameEntity.tags.split(",").filter { it.isNotBlank() },
                            latitude = frameEntity.latitude,
                            longitude = frameEntity.longitude,
                            developerName = frameEntity.developerName,
                            paperStock = frameEntity.paperStock,
                            enlargerLens = frameEntity.enlargerLens,
                            baseSec = frameEntity.baseSec,
                            gradeFilter = frameEntity.gradeFilter,
                            dodgeBurnNotes = frameEntity.dodgeBurnNotes,
                            outcomeStatus = frameEntity.outcomeStatus,
                            learningTag = frameEntity.learningTag,
                            imageBytes = frameEntity.thumbnailPath?.let { File(it).readBytes() }
                        )
                    }.sortedBy { it.frameNumber }
                )
            }
        }
    }

    fun saveRoll(roll: DesktopMasterRollItem) {
        transaction {
            val rollId = roll.id.toIntOrNull()
            val rollEntity = if (rollId != null && MasterRollEntity.findById(rollId) != null) {
                MasterRollEntity[rollId]
            } else {
                MasterRollEntity.new { }
            }

            rollEntity.status = roll.status.name
            rollEntity.contributorId = roll.contributorId
            rollEntity.cameraName = roll.cameraName
            rollEntity.filmName = roll.filmName
            rollEntity.filmRollSerial = roll.filmRollSerial
            rollEntity.frameCount = roll.frameCount

            // Save frames
            roll.frames.forEach { frame ->
                val existingFrame = rollEntity.frames.find { it.timestampMs == frame.timestampMs }
                val frameEntity = existingFrame ?: MasterFrameEntity.new {
                    this.roll = rollEntity
                    this.timestampMs = frame.timestampMs
                }
                
                frameEntity.frameNumber = frame.frameNumber
                frameEntity.cameraName = frame.cameraName
                frameEntity.filmName = frame.filmName
                frameEntity.shutterSpeed = frame.shutterSpeed
                frameEntity.aperture = frame.aperture
                frameEntity.iso = frame.iso
                frameEntity.dateDisplay = frame.dateDisplay
                frameEntity.notes = frame.notes
                frameEntity.tags = frame.tags.joinToString(",")
                frameEntity.latitude = frame.latitude
                frameEntity.longitude = frame.longitude
                frameEntity.developerName = frame.developerName
                frameEntity.paperStock = frame.paperStock
                frameEntity.enlargerLens = frame.enlargerLens
                frameEntity.baseSec = frame.baseSec
                frameEntity.gradeFilter = frame.gradeFilter
                frameEntity.dodgeBurnNotes = frame.dodgeBurnNotes
                frameEntity.outcomeStatus = frame.outcomeStatus
                frameEntity.learningTag = frame.learningTag

                // Save thumbnail to disk if it has bytes but no path, or just update path
                if (frame.imageBytes != null && frameEntity.thumbnailPath == null) {
                    val thumbFile = File(DatabaseManager.thumbnailsDir, "${frame.timestampMs}.jpg")
                    thumbFile.writeBytes(frame.imageBytes!!)
                    frameEntity.thumbnailPath = thumbFile.absolutePath
                }
            }
        }
    }

    fun deleteRolls(rolls: List<DesktopMasterRollItem>) {
        transaction {
            rolls.forEach { roll ->
                val rollId = roll.id.toIntOrNull()
                if (rollId != null) {
                    MasterRollEntity.findById(rollId)?.delete()
                }
            }
        }
    }

    fun checkOrQueueFoundationItem(contributorId: Int, itemType: String, name: String, iso: Int? = null) {
        if (name.isBlank() || name == "Unknown Camera" || name == "Unknown Film") return

        transaction {
            val exists = when (itemType) {
                "CAMERA" -> FoundationCameraEntity.find { FoundationCamerasTable.name eq name }.count() > 0
                "FILM" -> FoundationFilmEntity.find { FoundationFilmsTable.name eq name }.count() > 0
                "SUPPLIER" -> FoundationSupplierEntity.find { FoundationSuppliersTable.name eq name }.count() > 0
                else -> false
            }

            if (!exists) {
                // Check if it's already in the queue so we don't spam PENDING records
                val alreadyQueued = FoundationReviewQueueEntity.find { 
                    (FoundationReviewQueueTable.submittedName eq name) and 
                    (FoundationReviewQueueTable.itemType eq itemType) and 
                    (FoundationReviewQueueTable.status eq "PENDING") 
                }.count() > 0

                if (!alreadyQueued) {
                    FoundationReviewQueueEntity.new {
                        this.contributorId = contributorId
                        this.itemType = itemType
                        this.submittedName = name
                        this.submittedIso = iso
                        this.submissionDateMs = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    // --- REFERENCE DATA CRUD HELPERS ---

    fun loadFoundationCameras(): List<ReferenceCameraItem> {
        return transaction {
            FoundationCameraEntity.all().map {
                ReferenceCameraItem(
                    id = it.id.value,
                    name = it.name,
                    modelNumber = it.modelNumber,
                    type = it.type,
                    mount = it.mount
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationCamera(item: ReferenceCameraItem) {
        transaction {
            if (item.id > 0) {
                FoundationCameraEntity.findById(item.id)?.apply {
                    name = item.name
                    modelNumber = item.modelNumber
                    type = item.type
                    mount = item.mount
                }
            } else {
                FoundationCameraEntity.new {
                    name = item.name
                    modelNumber = item.modelNumber
                    type = item.type
                    mount = item.mount
                }
            }
        }
    }

    fun deleteFoundationCamera(id: Int) {
        transaction {
            FoundationCameraEntity.findById(id)?.delete()
        }
    }

    fun loadFoundationFilms(): List<ReferenceFilmItem> {
        return transaction {
            FoundationFilmEntity.all().map {
                ReferenceFilmItem(
                    id = it.id.value,
                    name = it.name,
                    abbreviation = it.abbreviation,
                    iso = it.iso,
                    type = it.type,
                    format = it.format
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationFilm(item: ReferenceFilmItem) {
        transaction {
            if (item.id > 0) {
                FoundationFilmEntity.findById(item.id)?.apply {
                    name = item.name
                    abbreviation = item.abbreviation
                    iso = item.iso
                    type = item.type
                    format = item.format
                }
            } else {
                FoundationFilmEntity.new {
                    name = item.name
                    abbreviation = item.abbreviation
                    iso = item.iso
                    type = item.type
                    format = item.format
                }
            }
        }
    }

    fun deleteFoundationFilm(id: Int) {
        transaction {
            FoundationFilmEntity.findById(id)?.delete()
        }
    }

    fun loadFoundationSuppliers(): List<ReferenceSupplierItem> {
        return transaction {
            FoundationSupplierEntity.all().map {
                ReferenceSupplierItem(
                    id = it.id.value,
                    name = it.name,
                    website = it.website,
                    region = it.region
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationSupplier(item: ReferenceSupplierItem) {
        transaction {
            if (item.id > 0) {
                FoundationSupplierEntity.findById(item.id)?.apply {
                    name = item.name
                    website = item.website
                    region = item.region
                }
            } else {
                FoundationSupplierEntity.new {
                    name = item.name
                    website = item.website
                    region = item.region
                }
            }
        }
    }

    fun deleteFoundationSupplier(id: Int) {
        transaction {
            FoundationSupplierEntity.findById(id)?.delete()
        }
    }

    fun exportReferenceSyncPackageJson(): String {
        val cameras = loadFoundationCameras()
        val films = loadFoundationFilms()
        val suppliers = loadFoundationSuppliers()

        return Json { prettyPrint = true }.encodeToString(
            mapOf(
                "timestamp" to System.currentTimeMillis(),
                "schemaVersion" to 3,
                "cameras" to cameras,
                "films" to films,
                "labSuppliers" to suppliers
            )
        )
    }
}
