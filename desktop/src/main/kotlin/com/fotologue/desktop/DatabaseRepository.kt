package com.fotologue.desktop

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlinx.serialization.json.*
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
                val bytes = frame.imageBytes
                if (bytes != null && frameEntity.thumbnailPath == null) {
                    val thumbFile = File(DatabaseManager.thumbnailsDir, "${frame.timestampMs}.jpg")
                    thumbFile.writeBytes(bytes)
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
                    serialNumber = it.serialNumber,
                    type = it.type,
                    mount = it.mount,
                    format = it.format,
                    shutterSpeeds = it.shutterSpeeds
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationCamera(item: ReferenceCameraItem) {
        transaction {
            val existingCameras = FoundationCameraEntity.all().toList()
            val baseName = item.name.trim()

            val finalName = if (item.id == 0 && existingCameras.any { it.name.equals(baseName, ignoreCase = true) }) {
                val matching = existingCameras.map { it.name }.filter { it.equals(baseName, ignoreCase = true) || it.startsWith("$baseName (", ignoreCase = true) }
                var maxCount = 1
                for (name in matching) {
                    if (name.equals(baseName, ignoreCase = true)) continue
                    val suffix = name.substringAfterLast("(", "").substringBefore(")", "").toIntOrNull()
                    if (suffix != null && suffix >= maxCount) {
                        maxCount = suffix + 1
                    }
                }
                "$baseName ($maxCount)"
            } else {
                baseName
            }

            if (item.id > 0) {
                FoundationCameraEntity.findById(item.id)?.apply {
                    name = item.name
                    modelNumber = item.modelNumber
                    serialNumber = item.serialNumber
                    type = item.type
                    mount = item.mount
                    format = item.format
                    shutterSpeeds = item.shutterSpeeds
                }
            } else {
                FoundationCameraEntity.new {
                    name = finalName
                    modelNumber = item.modelNumber
                    serialNumber = item.serialNumber
                    type = item.type
                    mount = item.mount
                    format = item.format
                    shutterSpeeds = item.shutterSpeeds
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
                    format = it.format,
                    expectedExposures = it.expectedExposures
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
                    expectedExposures = item.expectedExposures
                }
            } else {
                FoundationFilmEntity.new {
                    name = item.name
                    abbreviation = item.abbreviation
                    iso = item.iso
                    type = item.type
                    format = item.format
                    expectedExposures = item.expectedExposures
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
                    region = it.region,
                    email = it.email,
                    phone = it.phone,
                    address = it.address,
                    reliabilityRating = it.reliabilityRating,
                    notes = it.notes
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
                    email = item.email
                    phone = item.phone
                    address = item.address
                    reliabilityRating = item.reliabilityRating
                    notes = item.notes
                }
            } else {
                FoundationSupplierEntity.new {
                    name = item.name
                    website = item.website
                    region = item.region
                    email = item.email
                    phone = item.phone
                    address = item.address
                    reliabilityRating = item.reliabilityRating
                    notes = item.notes
                }
            }
        }
    }

    fun deleteFoundationSupplier(id: Int) {
        transaction {
            FoundationSupplierEntity.findById(id)?.delete()
        }
    }

    fun loadFoundationConsumables(): List<ReferenceConsumableItem> {
        return transaction {
            FoundationConsumableEntity.all().map {
                ReferenceConsumableItem(
                    id = it.id.value,
                    name = it.name,
                    manufacturer = it.manufacturer,
                    category = it.category,
                    type = it.type,
                    dilution = it.dilution,
                    containerAmount = it.containerAmount,
                    containerUnit = it.containerUnit,
                    innerYieldTotal = it.innerYieldTotal,
                    innerUnit = it.innerUnit,
                    defaultUnit = it.defaultUnit,
                    supplierName = it.supplierName,
                    storageLocationName = it.storageLocationName,
                    isHazardous = it.isHazardous,
                    safetyNotes = it.safetyNotes
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationConsumable(item: ReferenceConsumableItem) {
        transaction {
            if (item.id > 0) {
                FoundationConsumableEntity.findById(item.id)?.apply {
                    name = item.name
                    manufacturer = item.manufacturer
                    category = item.category
                    type = item.type
                    dilution = item.dilution
                    containerAmount = item.containerAmount
                    containerUnit = item.containerUnit
                    innerYieldTotal = item.innerYieldTotal
                    innerUnit = item.innerUnit
                    defaultUnit = item.defaultUnit
                    supplierName = item.supplierName
                    storageLocationName = item.storageLocationName
                    isHazardous = item.isHazardous
                    safetyNotes = item.safetyNotes
                }
            } else {
                FoundationConsumableEntity.new {
                    name = item.name
                    manufacturer = item.manufacturer
                    category = item.category
                    type = item.type
                    dilution = item.dilution
                    containerAmount = item.containerAmount
                    containerUnit = item.containerUnit
                    innerYieldTotal = item.innerYieldTotal
                    innerUnit = item.innerUnit
                    defaultUnit = item.defaultUnit
                    supplierName = item.supplierName
                    storageLocationName = item.storageLocationName
                    isHazardous = item.isHazardous
                    safetyNotes = item.safetyNotes
                }
            }
        }
    }

    fun deleteFoundationConsumable(id: Int) {
        transaction {
            FoundationConsumableEntity.findById(id)?.delete()
        }
    }

    fun loadFoundationLabEquipment(): List<ReferenceLabEquipmentItem> {
        return transaction {
            FoundationLabEquipmentEntity.all().map {
                ReferenceLabEquipmentItem(
                    id = it.id.value,
                    name = it.name,
                    manufacturer = it.manufacturer,
                    modelNumber = it.modelNumber,
                    serialNumber = it.serialNumber,
                    category = it.category,
                    condition = it.condition,
                    parentEquipmentName = it.parentEquipmentName,
                    notes = it.notes
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationLabEquipment(item: ReferenceLabEquipmentItem) {
        transaction {
            if (item.id > 0) {
                FoundationLabEquipmentEntity.findById(item.id)?.apply {
                    name = item.name
                    manufacturer = item.manufacturer
                    modelNumber = item.modelNumber
                    serialNumber = item.serialNumber
                    category = item.category
                    condition = item.condition
                    parentEquipmentName = item.parentEquipmentName
                    notes = item.notes
                }
            } else {
                FoundationLabEquipmentEntity.new {
                    name = item.name
                    manufacturer = item.manufacturer
                    modelNumber = item.modelNumber
                    serialNumber = item.serialNumber
                    category = item.category
                    condition = item.condition
                    parentEquipmentName = item.parentEquipmentName
                    notes = item.notes
                }
            }
        }
    }

    fun deleteFoundationLabEquipment(id: Int) {
        transaction {
            FoundationLabEquipmentEntity.findById(id)?.delete()
        }
    }

    // --- LENSES CRUD HELPERS ---
    fun loadFoundationLenses(): List<ReferenceLensItem> {
        return transaction {
            FoundationLensEntity.all().map {
                ReferenceLensItem(
                    id = it.id.value,
                    name = it.name,
                    make = it.make,
                    model = it.model,
                    type = it.type,
                    mount = it.mount,
                    adapterUsed = it.adapterUsed,
                    focalLength = it.focalLength,
                    maxAperture = it.maxAperture,
                    minAperture = it.minAperture,
                    filterSize = it.filterSize,
                    isImageStabilised = it.isImageStabilised,
                    minFocusMeters = it.minFocusMeters,
                    maxMagnification = it.maxMagnification,
                    serialNumber = it.serialNumber
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationLens(item: ReferenceLensItem) {
        transaction {
            if (item.id > 0) {
                FoundationLensEntity.findById(item.id)?.apply {
                    name = item.name
                    make = item.make
                    model = item.model
                    type = item.type
                    mount = item.mount
                    adapterUsed = item.adapterUsed
                    focalLength = item.focalLength
                    maxAperture = item.maxAperture
                    minAperture = item.minAperture
                    filterSize = item.filterSize
                    isImageStabilised = item.isImageStabilised
                    minFocusMeters = item.minFocusMeters
                    maxMagnification = item.maxMagnification
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationLensEntity.new {
                    name = item.name
                    make = item.make
                    model = item.model
                    type = item.type
                    mount = item.mount
                    adapterUsed = item.adapterUsed
                    focalLength = item.focalLength
                    maxAperture = item.maxAperture
                    minAperture = item.minAperture
                    filterSize = item.filterSize
                    isImageStabilised = item.isImageStabilised
                    minFocusMeters = item.minFocusMeters
                    maxMagnification = item.maxMagnification
                    serialNumber = item.serialNumber
                }
            }
        }
    }

    fun deleteFoundationLens(id: Int) {
        transaction {
            FoundationCameraLensesTable.deleteWhere { FoundationCameraLensesTable.lensId eq id }
            FoundationLensEntity.findById(id)?.delete()
        }
    }

    fun loadCameraIdsForLens(lensId: Int): Set<Int> {
        return transaction {
            FoundationCameraLensesTable
                .selectAll()
                .where { FoundationCameraLensesTable.lensId eq lensId }
                .map { it[FoundationCameraLensesTable.cameraId] }
                .toSet()
        }
    }

    fun saveCameraLensAssociations(lensId: Int, cameraIds: Set<Int>) {
        transaction {
            FoundationCameraLensesTable.deleteWhere { FoundationCameraLensesTable.lensId eq lensId }
            cameraIds.forEach { camId ->
                FoundationCameraLensesTable.insert {
                    it[FoundationCameraLensesTable.cameraId] = camId
                    it[FoundationCameraLensesTable.lensId] = lensId
                }
            }
        }
    }

    // --- ACCESSORIES CRUD HELPERS ---
    fun loadFoundationAccessories(): List<ReferenceAccessoryItem> {
        return transaction {
            FoundationAccessoryEntity.all().map {
                ReferenceAccessoryItem(
                    id = it.id.value,
                    name = it.name,
                    type = it.type,
                    specs = it.specs
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationAccessory(item: ReferenceAccessoryItem) {
        transaction {
            if (item.id > 0) {
                FoundationAccessoryEntity.findById(item.id)?.apply {
                    name = item.name
                    type = item.type
                    specs = item.specs
                }
            } else {
                FoundationAccessoryEntity.new {
                    name = item.name
                    type = item.type
                    specs = item.specs
                }
            }
        }
    }

    fun deleteFoundationAccessory(id: Int) {
        transaction {
            FoundationAccessoryEntity.findById(id)?.delete()
        }
    }

    // --- TELECONVERTERS CRUD HELPERS ---
    fun loadFoundationTeleconverters(): List<ReferenceTeleconverterItem> {
        return transaction {
            FoundationTeleconverterEntity.all().map {
                ReferenceTeleconverterItem(
                    id = it.id.value,
                    name = it.name,
                    make = it.make,
                    converterType = it.converterType,
                    magnification = it.magnification,
                    lightLossStops = it.lightLossStops,
                    mount = it.mount,
                    serialNumber = it.serialNumber
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationTeleconverter(item: ReferenceTeleconverterItem) {
        transaction {
            if (item.id > 0) {
                FoundationTeleconverterEntity.findById(item.id)?.apply {
                    name = item.name
                    make = item.make
                    converterType = item.converterType
                    magnification = item.magnification
                    lightLossStops = item.lightLossStops
                    mount = item.mount
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationTeleconverterEntity.new {
                    name = item.name
                    make = item.make
                    converterType = item.converterType
                    magnification = item.magnification
                    lightLossStops = item.lightLossStops
                    mount = item.mount
                    serialNumber = item.serialNumber
                }
            }
        }
    }

    fun deleteFoundationTeleconverter(id: Int) {
        transaction {
            FoundationTeleconverterEntity.findById(id)?.delete()
        }
    }

    // --- FILTERS CRUD HELPERS ---
    fun loadFoundationFilters(): List<ReferenceFilterItem> {
        return transaction {
            FoundationFilterEntity.all().map {
                ReferenceFilterItem(
                    id = it.id.value,
                    name = it.name,
                    threadSize = it.threadSize,
                    filterType = it.filterType,
                    serialNumber = it.serialNumber
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationFilter(item: ReferenceFilterItem) {
        transaction {
            if (item.id > 0) {
                FoundationFilterEntity.findById(item.id)?.apply {
                    name = item.name
                    threadSize = item.threadSize
                    filterType = item.filterType
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationFilterEntity.new {
                    name = item.name
                    threadSize = item.threadSize
                    filterType = item.filterType
                    serialNumber = item.serialNumber
                }
            }
        }
    }

    fun deleteFoundationFilter(id: Int) {
        transaction {
            FoundationFilterEntity.findById(id)?.delete()
        }
    }

    // --- FLASHES CRUD HELPERS ---
    fun loadFoundationFlashes(): List<ReferenceFlashItem> {
        return transaction {
            FoundationFlashEntity.all().map {
                ReferenceFlashItem(
                    id = it.id.value,
                    name = it.name,
                    guideNumber = it.guideNumber,
                    syncType = it.syncType,
                    serialNumber = it.serialNumber
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationFlash(item: ReferenceFlashItem) {
        transaction {
            if (item.id > 0) {
                FoundationFlashEntity.findById(item.id)?.apply {
                    name = item.name
                    guideNumber = item.guideNumber
                    syncType = item.syncType
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationFlashEntity.new {
                    name = item.name
                    guideNumber = item.guideNumber
                    syncType = item.syncType
                    serialNumber = item.serialNumber
                }
            }
        }
    }

    fun deleteFoundationFlash(id: Int) {
        transaction {
            FoundationFlashEntity.findById(id)?.delete()
        }
    }

    // --- VIEWFINDERS CRUD HELPERS ---
    fun loadFoundationViewfinders(): List<ReferenceViewfinderItem> {
        return transaction {
            FoundationViewfinderEntity.all().map {
                ReferenceViewfinderItem(
                    id = it.id.value,
                    name = it.name,
                    type = it.type,
                    compatibleCamera = it.compatibleCamera,
                    serialNumber = it.serialNumber
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationViewfinder(item: ReferenceViewfinderItem) {
        transaction {
            if (item.id > 0) {
                FoundationViewfinderEntity.findById(item.id)?.apply {
                    name = item.name
                    type = item.type
                    compatibleCamera = item.compatibleCamera
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationViewfinderEntity.new {
                    name = item.name
                    type = item.type
                    compatibleCamera = item.compatibleCamera
                    serialNumber = item.serialNumber
                }
            }
        }
    }

    fun deleteFoundationViewfinder(id: Int) {
        transaction {
            FoundationViewfinderEntity.findById(id)?.delete()
        }
    }

    // --- KITS CRUD HELPERS ---
    fun loadFoundationKits(): List<ReferenceKitItem> {
        return transaction {
            FoundationKitEntity.all().map {
                ReferenceKitItem(
                    id = it.id.value,
                    name = it.name,
                    contributorId = it.contributorId,
                    cameraName = it.cameraName,
                    lensNames = it.lensNames,
                    accessoryNames = it.accessoryNames,
                    defaultFilmName = it.defaultFilmName,
                    notes = it.notes
                )
            }.sortedBy { it.name }
        }
    }

    fun saveFoundationKit(item: ReferenceKitItem) {
        transaction {
            if (item.id > 0) {
                FoundationKitEntity.findById(item.id)?.apply {
                    name = item.name
                    contributorId = item.contributorId
                    cameraName = item.cameraName
                    lensNames = item.lensNames
                    accessoryNames = item.accessoryNames
                    defaultFilmName = item.defaultFilmName
                    notes = item.notes
                }
            } else {
                FoundationKitEntity.new {
                    name = item.name
                    contributorId = item.contributorId
                    cameraName = item.cameraName
                    lensNames = item.lensNames
                    accessoryNames = item.accessoryNames
                    defaultFilmName = item.defaultFilmName
                    notes = item.notes
                }
            }
        }
    }

    fun deleteFoundationKit(id: Int) {
        transaction {
            FoundationKitEntity.findById(id)?.delete()
        }
    }

    // --- STORAGE LOCATIONS CRUD HELPERS ---
    fun loadStorageLocations(): List<ReferenceStorageLocationItem> {
        return transaction {
            StorageLocationEntity.all().map {
                ReferenceStorageLocationItem(
                    id = it.id.value,
                    name = it.name,
                    type = it.type,
                    notes = it.notes
                )
            }.sortedBy { it.name }
        }
    }

    fun saveStorageLocation(item: ReferenceStorageLocationItem) {
        transaction {
            if (item.id > 0) {
                StorageLocationEntity.findById(item.id)?.apply {
                    name = item.name
                    type = item.type
                    notes = item.notes
                }
            } else {
                StorageLocationEntity.new {
                    name = item.name
                    type = item.type
                    notes = item.notes
                }
            }
        }
    }

    fun deleteStorageLocation(id: Int) {
        transaction {
            StorageLocationEntity.findById(id)?.delete()
        }
    }

    // --- CUSTODY INTERACTIONS CRUD HELPERS ---
    fun loadCustodyInteractions(): List<ReferenceCustodyInteractionItem> {
        return transaction {
            CustodyInteractionEntity.all().map {
                ReferenceCustodyInteractionItem(
                    id = it.id.value,
                    assetType = it.assetType,
                    assetName = it.assetName,
                    contactName = it.contactName,
                    interactionType = it.interactionType,
                    orderStatus = it.orderStatus,
                    trackingNumber = it.trackingNumber,
                    carrier = it.carrier,
                    storageLocationName = it.storageLocationName,
                    startDateMs = it.startDateMs,
                    dueDateMs = it.dueDateMs,
                    returnedDateMs = it.returnedDateMs,
                    costOrFee = it.costOrFee,
                    ratingStars = it.ratingStars,
                    isReturned = it.isReturned,
                    notes = it.notes
                )
            }.sortedBy { it.id }
        }
    }

    fun saveCustodyInteraction(item: ReferenceCustodyInteractionItem) {
        transaction {
            if (item.id > 0) {
                CustodyInteractionEntity.findById(item.id)?.apply {
                    assetType = item.assetType
                    assetName = item.assetName
                    contactName = item.contactName
                    interactionType = item.interactionType
                    orderStatus = item.orderStatus
                    trackingNumber = item.trackingNumber
                    carrier = item.carrier
                    storageLocationName = item.storageLocationName
                    startDateMs = item.startDateMs
                    dueDateMs = item.dueDateMs
                    returnedDateMs = item.returnedDateMs
                    costOrFee = item.costOrFee
                    ratingStars = item.ratingStars
                    isReturned = item.isReturned
                    notes = item.notes
                }
            } else {
                CustodyInteractionEntity.new {
                    assetType = item.assetType
                    assetName = item.assetName
                    contactName = item.contactName
                    interactionType = item.interactionType
                    orderStatus = item.orderStatus
                    trackingNumber = item.trackingNumber
                    carrier = item.carrier
                    storageLocationName = item.storageLocationName
                    startDateMs = item.startDateMs
                    dueDateMs = item.dueDateMs
                    returnedDateMs = item.returnedDateMs
                    costOrFee = item.costOrFee
                    ratingStars = item.ratingStars
                    isReturned = item.isReturned
                    notes = item.notes
                }
            }
        }
    }

    fun deleteCustodyInteraction(id: Int) {
        transaction {
            CustodyInteractionEntity.findById(id)?.delete()
        }
    }

    // --- DYNAMIC LOV (LIST OF VALUES) HELPERS ---
    val defaultLensMounts = listOf("Pentax K", "Canon FD", "Nikon F", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed")

    fun loadLensMountLovs(): List<String> {
        return loadLovsByCategory("LENS_MOUNT", defaultLensMounts)
    }

    fun loadLovsByCategory(category: String, defaultOptions: List<String>): List<String> {
        return transaction {
            val userCustoms = ReferenceLovEntity.find { ReferenceLovsTable.category eq category }.map { it.value }
            (defaultOptions + userCustoms).distinct().filter { it.isNotBlank() }
        }
    }

    fun saveLovValue(category: String, customValue: String) {
        val catName = category
        val trimmed = customValue.trim()
        if (trimmed.isBlank()) return
        transaction {
            val existing = ReferenceLovEntity.find { (ReferenceLovsTable.category eq catName) and (ReferenceLovsTable.value eq trimmed) }.firstOrNull()
            if (existing == null) {
                val newLov = ReferenceLovEntity.new { }
                newLov.category = catName
                newLov.value = trimmed
                newLov.isUserCustom = true
            }
        }
    }

    fun deleteLov(id: Int) {
        transaction {
            ReferenceLovEntity.findById(id)?.delete()
        }
    }

    // --- CONTRIBUTORS CRUD HELPERS ---
    fun loadContributors(): List<ContributorItem> {
        return transaction {
            val list = ContributorEntity.all().map {
                ContributorItem(
                    id = it.id.value,
                    alias = it.alias,
                    fullName = it.fullName,
                    email = it.email,
                    lastSyncTimestamp = it.lastSyncTimestamp
                )
            }.sortedBy { it.id }
            if (list.isEmpty()) {
                val defaultContrib = ContributorEntity.new(1) {
                    alias = "P1"
                    fullName = "Gerard Bendotti"
                    email = "gerard@fotologue.app"
                    lastSyncTimestamp = System.currentTimeMillis()
                }
                listOf(ContributorItem(id = defaultContrib.id.value, alias = defaultContrib.alias, fullName = defaultContrib.fullName, email = defaultContrib.email, lastSyncTimestamp = defaultContrib.lastSyncTimestamp))
            } else list
        }
    }

    // --- PRESET CATALOGUE LOOKUP HELPERS ---

    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun getPresetsResourceDir(): File {
        var rootDir = File(System.getProperty("user.dir"))
        while (rootDir.exists() && !File(rootDir, "settings.gradle.kts").exists()) {
            val parent = rootDir.parentFile ?: break
            rootDir = parent
        }
        return File(rootDir, "core/data/src/main/resources/presets")
    }

    fun loadRawMasterPresetItems(category: String): List<MasterPresetItem> {
        return try {
            val fileName = when (category.uppercase()) {
                "CAMERA" -> "cameras.json"
                "LENS" -> "lenses.json"
                "FILM" -> "films.json"
                "TELECONVERTER" -> "teleconverters.json"
                "FILTER" -> "filters.json"
                "FLASH" -> "flashes.json"
                "VIEWFINDER" -> "viewfinders.json"
                "CONSUMABLE" -> "consumables.json"
                "EQUIPMENT" -> "lab_equipment.json"
                "SUPPLIER" -> "suppliers.json"
                else -> "${category.lowercase()}s.json"
            }
            val jsonFile = File(getPresetsResourceDir(), fileName)
            if (jsonFile.exists()) {
                jsonParser.decodeFromString<List<MasterPresetItem>>(jsonFile.readText())
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun searchPresetCameras(query: String): List<ReferenceCameraItem> {
        val rawItems = loadRawMasterPresetItems("CAMERA")
        val presets = rawItems.map { item ->
            ReferenceCameraItem(
                name = item.name,
                modelNumber = item.modelNumber ?: item.name,
                type = item.attributes["type"] ?: item.subCategory ?: "Film SLR",
                mount = item.attributes["mount"] ?: "Standard",
                format = if (item.attributes["type"] == "TLR" || item.attributes["type"] == "Medium Format SLR") "120" else "35mm",
                shutterSpeeds = item.attributes["shutterSpeeds"] ?: "1s - 1/1000s, B"
            )
        }

        val listToUse = if (presets.isNotEmpty()) presets else listOf(
            ReferenceCameraItem(name = "Pentax LX", modelNumber = "LX", type = "Film SLR", mount = "Pentax K", format = "35mm", shutterSpeeds = "125s - 1/2000s, B"),
            ReferenceCameraItem(name = "Yashica Mat-124G", modelNumber = "Mat-124G", type = "TLR", mount = "Fixed", format = "120", shutterSpeeds = "1s - 1/500s, B")
        )

        if (query.isBlank()) return listToUse
        return listToUse.filter { it.name.contains(query, ignoreCase = true) || (it.modelNumber?.contains(query, ignoreCase = true) == true) }
    }

    fun searchPresetLenses(query: String): List<ReferenceLensItem> {
        val presets = listOf(
            ReferenceLensItem(name = "Canon EF 100mm f/2.8L Macro IS USM", make = "Canon", model = "EF10028LIS", type = "Macro", mount = "Canon EF", focalLength = "100mm", maxAperture = "f/2.8", minAperture = "f/32", filterSize = "67mm", isImageStabilised = true),
            ReferenceLensItem(name = "Canon FD 50mm f/1.4", make = "Canon", model = "FD5014", type = "Prime", mount = "Canon FD", focalLength = "50mm", maxAperture = "f/1.4", minAperture = "f/16", filterSize = "52mm", isImageStabilised = false),
            ReferenceLensItem(name = "Nikkor 50mm f/1.4 AI-S", make = "Nikon", model = "AI-S 50mm", type = "Prime", mount = "Nikon F", focalLength = "50mm", maxAperture = "f/1.4", minAperture = "f/16", filterSize = "52mm", isImageStabilised = false),
            ReferenceLensItem(name = "Carl Zeiss Planar T* 50mm f/1.4", make = "Carl Zeiss", model = "Planar 50/1.4", type = "Prime", mount = "Contax C/Y", focalLength = "50mm", maxAperture = "f/1.4", minAperture = "f/16", filterSize = "55mm", isImageStabilised = false),
            ReferenceLensItem(name = "SMC Pentax-M 50mm f/1.4", make = "Pentax", model = "Pentax-M 50/1.4", type = "Prime", mount = "Pentax K", focalLength = "50mm", maxAperture = "f/1.4", minAperture = "f/22", filterSize = "49mm", isImageStabilised = false),
            ReferenceLensItem(name = "Leica Summicron-M 50mm f/2", make = "Leica", model = "Summicron 50/2", type = "Prime", mount = "Leica M", focalLength = "50mm", maxAperture = "f/2.0", minAperture = "f/16", filterSize = "39mm", isImageStabilised = false)
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || (it.model?.contains(query, ignoreCase = true) == true) || (it.make?.contains(query, ignoreCase = true) == true) }
    }

    fun searchPresetFilms(query: String): List<ReferenceFilmItem> {
        val presets = listOf(
            // Kodak
            ReferenceFilmItem(name = "Kodak Portra 160", abbreviation = "P160", iso = 160, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Portra 400", abbreviation = "P400", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Portra 800", abbreviation = "P800", iso = 800, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Ektar 100", abbreviation = "E100", iso = 100, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Gold 200", abbreviation = "G200", iso = 200, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak UltraMax 400", abbreviation = "U400", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Tri-X 400", abbreviation = "TX400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak T-Max 100", abbreviation = "TM100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak T-Max 400", abbreviation = "TM400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Ektachrome E100", abbreviation = "E100G", iso = 100, type = "Colour Slide / Reversal (E-6)", format = "35mm", expectedExposures = 36),

            // Ilford
            ReferenceFilmItem(name = "Ilford HP5 Plus 400", abbreviation = "HP5", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford FP4 Plus 125", abbreviation = "FP4", iso = 125, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford Delta 100 Professional", abbreviation = "D100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford Delta 400 Professional", abbreviation = "D400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford Delta 3200 Professional", abbreviation = "D3200", iso = 3200, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford Pan F Plus 50", abbreviation = "PANF", iso = 50, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford XP2 Super 400", abbreviation = "XP2", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Fujifilm
            ReferenceFilmItem(name = "Fujifilm Fujicolor 200", abbreviation = "F200", iso = 200, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Superia X-TRA 400", abbreviation = "S400", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Velvia 50", abbreviation = "V50", iso = 50, type = "Colour Slide / Reversal (E-6)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Velvia 100", abbreviation = "V100", iso = 100, type = "Colour Slide / Reversal (E-6)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Provia 100F", abbreviation = "RDP", iso = 100, type = "Colour Slide / Reversal (E-6)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Neopan 100 Acros II", abbreviation = "ACROS", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Cinestill
            ReferenceFilmItem(name = "Cinestill 50Daylight", abbreviation = "50D", iso = 50, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Cinestill 800Tungsten", abbreviation = "800T", iso = 800, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Cinestill 400Dynamic", abbreviation = "400D", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Cinestill BwXX", abbreviation = "XX", iso = 250, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Foma
            ReferenceFilmItem(name = "Foma Fomapan 100 Classic", abbreviation = "FOM100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Foma Fomapan 200 Creative", abbreviation = "FOM200", iso = 200, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Foma Fomapan 400 Action", abbreviation = "FOM400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Lomography
            ReferenceFilmItem(name = "Lomography Color Negative 100", abbreviation = "L100", iso = 100, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Lomography Color Negative 400", abbreviation = "L400", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Lomography Color Negative 800", abbreviation = "L800", iso = 800, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Lomography LomoChrome Metropolis", abbreviation = "METRO", iso = 200, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Lomography LomoChrome Purple", abbreviation = "PURP", iso = 400, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),

            // AgfaPhoto
            ReferenceFilmItem(name = "AgfaPhoto APX 100 Professional", abbreviation = "APX100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "AgfaPhoto APX 400 Professional", abbreviation = "APX400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "AgfaPhoto Vista 200", abbreviation = "VIST", iso = 200, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),

            // Rollei
            ReferenceFilmItem(name = "Rollei Retro 80s", abbreviation = "R80S", iso = 80, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Rollei Retro 400s", abbreviation = "R400S", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Rollei RPX 100", abbreviation = "RPX100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Rollei RPX 400", abbreviation = "RPX400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Rollei Superpan 200", abbreviation = "SPAN", iso = 200, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Bergger
            ReferenceFilmItem(name = "Bergger Pancro 400", abbreviation = "PAN400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),

            // Harman
            ReferenceFilmItem(name = "Harman Phoenix 200", abbreviation = "PHX", iso = 200, type = "Colour Negative (C-41)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Harman Kentmere Pan 100", abbreviation = "KP100", iso = 100, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Harman Kentmere Pan 400", abbreviation = "KP400", iso = 400, type = "Black & White Negative (B&W)", format = "35mm", expectedExposures = 36)
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || it.abbreviation.contains(query, ignoreCase = true) }
    }

    fun searchPresetConsumables(query: String): List<ReferenceConsumableItem> {
        val presets = listOf(
            ReferenceConsumableItem(name = "Kodak D-76", manufacturer = "Kodak", category = "Chemistry", type = "Developer", dilution = "1+1", defaultUnit = "ml", isHazardous = false),
            ReferenceConsumableItem(name = "Ilford Rapid Fixer", manufacturer = "Ilford", category = "Chemistry", type = "Fixer", dilution = "1+4", defaultUnit = "ml", isHazardous = true),
            ReferenceConsumableItem(name = "Rodinal / R09 One Shot", manufacturer = "Agfa / Adox", category = "Chemistry", type = "Developer", dilution = "1+50", defaultUnit = "ml", isHazardous = true),
            ReferenceConsumableItem(name = "Kodak Xtol", manufacturer = "Kodak", category = "Chemistry", type = "Developer", dilution = "1+1", defaultUnit = "ml", isHazardous = false),
            ReferenceConsumableItem(name = "Ilford Multigrade FB Glossy", manufacturer = "Ilford", category = "Paper", type = "FB Glossy", dilution = "N/A", defaultUnit = "sheets", isHazardous = false)
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || (it.manufacturer?.contains(query, ignoreCase = true) == true) }
    }

    fun searchPresetEquipment(query: String): List<ReferenceLabEquipmentItem> {
        val presets = listOf(
            // Enlargers
            ReferenceLabEquipmentItem(name = "Intrepid Compact Enlarger (35mm / 120)", manufacturer = "Intrepid", category = "Enlarger", condition = "Excellent", notes = "LED Light Source with Color/Multigrade Filters"),
            ReferenceLabEquipmentItem(name = "Beseler 23C II", manufacturer = "Beseler", modelNumber = "23C II", category = "Enlarger", condition = "Excellent", notes = "Dichro / Condenser Enlarger"),
            ReferenceLabEquipmentItem(name = "Omega D2 4x5", manufacturer = "Omega", modelNumber = "D2", category = "Enlarger", condition = "Excellent", notes = "Large Format Condenser Enlarger"),
            ReferenceLabEquipmentItem(name = "Durst M605", manufacturer = "Durst", modelNumber = "M605", category = "Enlarger", condition = "Excellent", notes = "Medium Format Enlarger"),
            ReferenceLabEquipmentItem(name = "Kaiser VP 6000", manufacturer = "Kaiser", modelNumber = "VP 6000", category = "Enlarger", condition = "Excellent", notes = "B&W / Color Enlarger"),
            ReferenceLabEquipmentItem(name = "Leitz Focomat V35", manufacturer = "Leitz", modelNumber = "V35", category = "Enlarger", condition = "Excellent", notes = "Autofocus 35mm Enlarger"),
            ReferenceLabEquipmentItem(name = "Saunders LPL 4500II 4x5", manufacturer = "Saunders LPL", modelNumber = "4500II", category = "Enlarger", condition = "Excellent", notes = "Professional 4x5 Color / B&W Enlarger"),
            ReferenceLabEquipmentItem(name = "De Vere 504 Dichroic 4x5", manufacturer = "De Vere", modelNumber = "504", category = "Enlarger", condition = "Excellent", notes = "Professional Bench Enlarger"),

            // Enlarger Lenses
            ReferenceLabEquipmentItem(name = "Rodenstock Rodagon 50mm f/2.8", manufacturer = "Rodenstock", modelNumber = "Rodagon 50mm", category = "Enlarger Lens", condition = "Excellent", notes = "35mm Format Enlarger Lens"),
            ReferenceLabEquipmentItem(name = "Rodenstock Rodagon 80mm f/4", manufacturer = "Rodenstock", modelNumber = "Rodagon 80mm", category = "Enlarger Lens", condition = "Excellent", notes = "120 Medium Format Enlarger Lens"),
            ReferenceLabEquipmentItem(name = "Schneider Componon-S 50mm f/2.8", manufacturer = "Schneider", modelNumber = "Componon-S 50mm", category = "Enlarger Lens", condition = "Excellent", notes = "35mm Format Premium Lens"),
            ReferenceLabEquipmentItem(name = "Schneider Componon-S 80mm f/4", manufacturer = "Schneider", modelNumber = "Componon-S 80mm", category = "Enlarger Lens", condition = "Excellent", notes = "120 Format Premium Lens"),
            ReferenceLabEquipmentItem(name = "El-Nikkor 50mm f/2.8 N", manufacturer = "Nikon", modelNumber = "50mm f/2.8 N", category = "Enlarger Lens", condition = "Excellent", notes = "Nikkor 35mm Enlarger Lens"),
            ReferenceLabEquipmentItem(name = "El-Nikkor 80mm f/5.6 N", manufacturer = "Nikon", modelNumber = "80mm f/5.6 N", category = "Enlarger Lens", condition = "Excellent", notes = "Nikkor 120 Enlarger Lens"),
            ReferenceLabEquipmentItem(name = "Fujinon EX 50mm f/2.8", manufacturer = "Fujifilm", modelNumber = "EX 50mm", category = "Enlarger Lens", condition = "Excellent", notes = "EBC Coated 35mm Enlarger Lens"),

            // Dev Tanks & Reels
            ReferenceLabEquipmentItem(name = "Paterson Super System 4 (2-Roll Tank)", manufacturer = "Paterson", modelNumber = "System 4", category = "Dev Tank", condition = "Excellent", notes = "Standard Plastic Tank for 2x35mm or 1x120"),
            ReferenceLabEquipmentItem(name = "Paterson Super System 4 Multi-Reel (5-Roll)", manufacturer = "Paterson", modelNumber = "System 4 Multi", category = "Dev Tank", condition = "Excellent", notes = "Multi-roll developing tank"),
            ReferenceLabEquipmentItem(name = "AP Compact Developing Tank", manufacturer = "AP", modelNumber = "Compact", category = "Dev Tank", condition = "Excellent", notes = "Auto-load spiral system"),
            ReferenceLabEquipmentItem(name = "Jobo 1520 Tank System", manufacturer = "Jobo", modelNumber = "1520", category = "Dev Tank", condition = "Excellent", notes = "Rotary & Inversion Tank"),
            ReferenceLabEquipmentItem(name = "Jobo 2500 System Tank (4x5 Sheet Film)", manufacturer = "Jobo", modelNumber = "2500", category = "Dev Tank", condition = "Excellent", notes = "Sheet Film Tank System"),
            ReferenceLabEquipmentItem(name = "Kindermann Stainless Steel Tank & Reel", manufacturer = "Kindermann", modelNumber = "SS Tank", category = "Dev Tank", condition = "Excellent", notes = "Single 35mm SS Tank"),

            // Trays, Washers & Light Tables
            ReferenceLabEquipmentItem(name = "Paterson 8x10 Developing Trays (3-Pack)", manufacturer = "Paterson", modelNumber = "8x10 Trays", category = "Tray", condition = "Excellent", notes = "Color-Coded Darkroom Trays"),
            ReferenceLabEquipmentItem(name = "Paterson Auto Print Washer", manufacturer = "Paterson", modelNumber = "Auto Washer", category = "Tray", condition = "Excellent", notes = "Flow-Through Print Washer"),
            ReferenceLabEquipmentItem(name = "Nova Archival Print Washer 12x16", manufacturer = "Nova", modelNumber = "12x16 Slot", category = "Tray", condition = "Excellent", notes = "Vertical Archival Slot Washer"),
            ReferenceLabEquipmentItem(name = "Kaiser Slimlite Plano LED Light Table", manufacturer = "Kaiser", modelNumber = "Slimlite", category = "Light Table", condition = "Excellent", notes = "5000K Color Balanced Light Table"),
            ReferenceLabEquipmentItem(name = "Paterson Film Squeegee & Drying Clips", manufacturer = "Paterson", modelNumber = "Squeegee", category = "Squeegee & Drying", condition = "Excellent", notes = "Anti-Scratch Rubber Film Squeegee"),

            // Scanners & Digitisers
            ReferenceLabEquipmentItem(name = "Epson Perfection V850 Pro", manufacturer = "Epson", modelNumber = "V850 Pro", category = "Scanner / Digitiser", condition = "Excellent", notes = "Dual Lens Film/Flatbed Scanner"),
            ReferenceLabEquipmentItem(name = "Plustek OpticFilm 8200i AI", manufacturer = "Plustek", modelNumber = "8200i AI", category = "Scanner / Digitiser", condition = "Excellent", notes = "Dedicated 35mm Film Scanner")
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || (it.manufacturer?.contains(query, ignoreCase = true) == true) || it.category.contains(query, ignoreCase = true) }
    }

    fun searchPresetSuppliers(query: String): List<ReferenceSupplierItem> {
        val rawItems = loadRawMasterPresetItems("SUPPLIER")
        val presets = rawItems.map { item ->
            ReferenceSupplierItem(
                name = item.name,
                region = item.attributes["region"] ?: "Global / International",
                website = item.attributes["website"] ?: "",
                notes = item.attributes["type"] ?: "Vendor / Supplier"
            )
        }

        val listToUse = if (presets.isNotEmpty()) presets else listOf(
            ReferenceSupplierItem(name = "Vanbar Imaging", region = "Australia & Oceania", website = "https://www.vanbar.com.au"),
            ReferenceSupplierItem(name = "B&H Photo Video", region = "North America (USA/Canada)", website = "https://www.bhphotovideo.com")
        )

        if (query.isBlank()) return listToUse
        return listToUse.filter { it.name.contains(query, ignoreCase = true) || (it.region?.contains(query, ignoreCase = true) == true) }
    }

    fun loadAllLovs(): List<ReferenceLovItem> {
        return transaction {
            ReferenceLovEntity.all().map {
                ReferenceLovItem(
                    id = it.id.value,
                    category = it.category,
                    value = it.value,
                    isUserCustom = it.isUserCustom
                )
            }
        }
    }

    private val jsonFormatter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun exportReferenceSyncPackageJson(): String {
        val contributors = loadContributors()
        val cameras = loadFoundationCameras()
        val films = loadFoundationFilms()
        val lenses = loadFoundationLenses()
        val teleconverters = loadFoundationTeleconverters()
        val filters = loadFoundationFilters()
        val flashes = loadFoundationFlashes()
        val viewfinders = loadFoundationViewfinders()
        val accessories = loadFoundationAccessories()
        val kits = loadFoundationKits()
        val storageLocations = loadStorageLocations()
        val custodyInteractions = loadCustodyInteractions()
        val suppliers = loadFoundationSuppliers()
        val consumables = loadFoundationConsumables()
        val equipment = loadFoundationLabEquipment()
        val lovs = loadAllLovs()

        return jsonFormatter.encodeToString(
            mapOf(
                "timestamp" to System.currentTimeMillis(),
                "schemaVersion" to 4,
                "contributors" to contributors,
                "cameras" to cameras,
                "films" to films,
                "lenses" to lenses,
                "teleconverters" to teleconverters,
                "filters" to filters,
                "flashes" to flashes,
                "viewfinders" to viewfinders,
                "accessories" to accessories,
                "kits" to kits,
                "storageLocations" to storageLocations,
                "custodyInteractions" to custodyInteractions,
                "labSuppliers" to suppliers,
                "consumables" to consumables,
                "labEquipment" to equipment,
                "lovs" to lovs
            )
        )
    }

    fun importReferenceSyncPackageJson(jsonPayload: String) {
        try {
            val jsonElement = jsonFormatter.parseToJsonElement(jsonPayload).jsonObject

            transaction {
                // WIPE EXISTING DESKTOP FOUNDATION TABLES BEFORE RE-POPULATING
                CustodyInteractionEntity.all().forEach { it.delete() }
                FoundationSupplierEntity.all().forEach { it.delete() }
                StorageLocationEntity.all().forEach { it.delete() }
                FoundationConsumableEntity.all().forEach { it.delete() }
                FoundationLabEquipmentEntity.all().forEach { it.delete() }
                FoundationKitEntity.all().forEach { it.delete() }
                FoundationFilmEntity.all().forEach { it.delete() }
                FoundationFlashEntity.all().forEach { it.delete() }
                FoundationFilterEntity.all().forEach { it.delete() }
                FoundationTeleconverterEntity.all().forEach { it.delete() }
                FoundationViewfinderEntity.all().forEach { it.delete() }
                FoundationLensEntity.all().forEach { it.delete() }
                FoundationCameraEntity.all().forEach { it.delete() }

                // Extract Cameras
                jsonElement["cameras"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val model = obj["modelNumber"]?.jsonPrimitive?.contentOrNull
                        val serial = obj["serialNumber"]?.jsonPrimitive?.contentOrNull
                        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: "FILM_SLR"
                        val mount = obj["mount"]?.jsonPrimitive?.contentOrNull ?: "Standard"
                        val format = obj["format"]?.jsonPrimitive?.contentOrNull ?: "35mm"
                        val shutterList = obj["shutterSpeeds"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList()
                        if (name.isNotBlank()) {
                            saveFoundationCamera(
                                ReferenceCameraItem(
                                    name = name,
                                    modelNumber = model,
                                    serialNumber = serial,
                                    type = type,
                                    mount = mount,
                                    format = format,
                                    shutterSpeeds = shutterList.joinToString(", ")
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Lenses
                jsonElement["lenses"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val make = obj["make"]?.jsonPrimitive?.contentOrNull ?: "Generic"
                        val model = obj["model"]?.jsonPrimitive?.contentOrNull ?: "Lens"
                        val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: "$make $model"
                        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: "Prime"
                        val mount = obj["mount"]?.jsonPrimitive?.contentOrNull ?: "Standard"
                        val minFocal = obj["minFocalLength"]?.jsonPrimitive?.contentOrNull
                        val maxFocal = obj["maxFocalLength"]?.jsonPrimitive?.contentOrNull
                        val focal = if (minFocal != null && maxFocal != null && minFocal != maxFocal) "${minFocal}-${maxFocal}mm" else "${minFocal ?: maxFocal ?: "50"}mm"
                        val maxAp = obj["maxAperture"]?.jsonPrimitive?.contentOrNull ?: "f/1.4"
                        val minAp = obj["minAperture"]?.jsonPrimitive?.contentOrNull ?: "f/22"
                        if (name.isNotBlank()) {
                            saveFoundationLens(
                                ReferenceLensItem(
                                    name = name,
                                    make = make,
                                    model = model,
                                    type = type,
                                    mount = mount,
                                    focalLength = focal,
                                    maxAperture = if (maxAp.startsWith("f/")) maxAp else "f/$maxAp",
                                    minAperture = if (minAp.startsWith("f/")) minAp else "f/$minAp"
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Films
                jsonElement["films"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val abbr = obj["abbreviation"]?.jsonPrimitive?.contentOrNull ?: ""
                        val iso = obj["iso"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 400
                        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: "Color Negative"
                        val format = obj["format"]?.jsonPrimitive?.contentOrNull ?: "35mm"
                        val exp = obj["expectedExposures"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 36
                        if (name.isNotBlank()) {
                            saveFoundationFilm(
                                ReferenceFilmItem(
                                    name = name,
                                    abbreviation = abbr,
                                    iso = iso,
                                    type = type,
                                    format = format,
                                    expectedExposures = exp
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Suppliers
                jsonElement["labSuppliers"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val region = obj["region"]?.jsonPrimitive?.contentOrNull ?: "Global"
                        val website = obj["website"]?.jsonPrimitive?.contentOrNull ?: ""
                        if (name.isNotBlank()) {
                            saveFoundationSupplier(
                                ReferenceSupplierItem(
                                    name = name,
                                    region = region,
                                    website = website
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Storage Locations
                jsonElement["labStorageLocations"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val type = obj["locationType"]?.jsonPrimitive?.contentOrNull ?: obj["type"]?.jsonPrimitive?.contentOrNull ?: "Cabinet"
                        val notes = obj["notes"]?.jsonPrimitive?.contentOrNull ?: ""
                        if (name.isNotBlank()) {
                            saveStorageLocation(
                                ReferenceStorageLocationItem(
                                    name = name,
                                    type = type,
                                    notes = notes
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Consumables / Lab Chemical
                jsonElement["labChemicals"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: "Chemistry"
                        val unit = obj["volumeUnit"]?.jsonPrimitive?.contentOrNull ?: "L"
                        if (name.isNotBlank()) {
                            saveFoundationConsumable(
                                ReferenceConsumableItem(
                                    name = name,
                                    type = type,
                                    category = "Chemistry",
                                    defaultUnit = unit
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }

                // Extract Lab Equipment
                jsonElement["labEquipment"]?.jsonArray?.forEach { el ->
                    try {
                        val obj = el.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: ""
                        val category = obj["category"]?.jsonPrimitive?.contentOrNull ?: "Processing Gear"
                        val condition = obj["condition"]?.jsonPrimitive?.contentOrNull ?: "Good"
                        if (name.isNotBlank()) {
                            saveFoundationLabEquipment(
                                ReferenceLabEquipmentItem(
                                    name = name,
                                    category = category,
                                    condition = condition
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
