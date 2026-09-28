package com.fotologue.desktop

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

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
}
