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
                    name = item.name
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
                    defaultUnit = it.defaultUnit,
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
                    defaultUnit = item.defaultUnit
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
                    defaultUnit = item.defaultUnit
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
            FoundationLensEntity.findById(id)?.delete()
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
                    magnification = it.magnification,
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
                    magnification = item.magnification
                    mount = item.mount
                    serialNumber = item.serialNumber
                }
            } else {
                FoundationTeleconverterEntity.new {
                    name = item.name
                    make = item.make
                    magnification = item.magnification
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

    fun searchPresetCameras(query: String): List<ReferenceCameraItem> {
        val presets = listOf(
            ReferenceCameraItem(name = "Canon AE-1 Program", modelNumber = "AE-1P", type = "Film SLR", mount = "Canon FD", format = "35mm", shutterSpeeds = "2s - 1/1000s, B"),
            ReferenceCameraItem(name = "Leica M3", modelNumber = "M3", type = "Rangefinder", mount = "Leica M", format = "35mm", shutterSpeeds = "1s - 1/1000s, B"),
            ReferenceCameraItem(name = "Pentax K1000", modelNumber = "K1000", type = "Film SLR", mount = "Pentax K", format = "35mm", shutterSpeeds = "1s - 1/1000s, B"),
            ReferenceCameraItem(name = "Hasselblad 500C/M", modelNumber = "500C/M", type = "Medium Format SLR", mount = "Hasselblad V", format = "120", shutterSpeeds = "1s - 1/500s, B"),
            ReferenceCameraItem(name = "Nikon F3", modelNumber = "F3", type = "Film SLR", mount = "Nikon F", format = "35mm", shutterSpeeds = "8s - 1/2000s, B"),
            ReferenceCameraItem(name = "Olympus OM-1", modelNumber = "OM-1", type = "Film SLR", mount = "Olympus OM", format = "35mm", shutterSpeeds = "1s - 1/1000s, B"),
            ReferenceCameraItem(name = "Yashica Mat-124G", modelNumber = "Mat-124G", type = "TLR", mount = "Fixed Lens", format = "120", shutterSpeeds = "1s - 1/500s, B")
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || (it.modelNumber?.contains(query, ignoreCase = true) == true) }
    }

    fun searchPresetFilms(query: String): List<ReferenceFilmItem> {
        val presets = listOf(
            ReferenceFilmItem(name = "Ilford HP5 Plus 400", abbreviation = "HP5", iso = 400, type = "Black & White Negative", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Tri-X 400", abbreviation = "TX400", iso = 400, type = "Black & White Negative", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Kodak Portra 400", abbreviation = "PR400", iso = 400, type = "Color Negative", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Fujifilm Velvia 50", abbreviation = "RVP50", iso = 50, type = "Color Slide / Reversal", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Cinestill 800T", abbreviation = "800T", iso = 800, type = "Color Negative", format = "35mm", expectedExposures = 36),
            ReferenceFilmItem(name = "Ilford FP4 Plus 125", abbreviation = "FP4", iso = 125, type = "Black & White Negative", format = "35mm", expectedExposures = 36)
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
            ReferenceLabEquipmentItem(name = "Beseler 23C II", manufacturer = "Beseler", modelNumber = "23C II", category = "Enlarger", condition = "Excellent"),
            ReferenceLabEquipmentItem(name = "Paterson Super System 4 Dev Tank", manufacturer = "Paterson", modelNumber = "System 4", category = "Dev Tank", condition = "Excellent"),
            ReferenceLabEquipmentItem(name = "Rodenstock Rogenar 50mm f/2.8", manufacturer = "Rodenstock", modelNumber = "Rogenar 50mm", category = "Enlarger Lens", condition = "Excellent")
        )
        if (query.isBlank()) return presets
        return presets.filter { it.name.contains(query, ignoreCase = true) || (it.manufacturer?.contains(query, ignoreCase = true) == true) }
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

        return jsonFormatter.encodeToString(
            mapOf(
                "timestamp" to System.currentTimeMillis(),
                "schemaVersion" to 3,
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
                "labEquipment" to equipment
            )
        )
    }
}
