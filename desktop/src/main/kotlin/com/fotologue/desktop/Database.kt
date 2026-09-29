package com.fotologue.desktop

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object MasterRollsTable : IntIdTable("master_rolls") {
    val status = varchar("status", 50)
    val contributorId = integer("contributor_id")
    val cameraName = varchar("camera_name", 255)
    val filmName = varchar("film_name", 255)
    val filmRollSerial = varchar("film_roll_serial", 255)
    val frameCount = integer("frame_count")
}

class MasterRollEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MasterRollEntity>(MasterRollsTable)
    var status by MasterRollsTable.status
    var contributorId by MasterRollsTable.contributorId
    var cameraName by MasterRollsTable.cameraName
    var filmName by MasterRollsTable.filmName
    var filmRollSerial by MasterRollsTable.filmRollSerial
    var frameCount by MasterRollsTable.frameCount
    val frames by MasterFrameEntity referrersOn MasterFramesTable.rollId
}

object MasterFramesTable : IntIdTable("master_frames") {
    val rollId = reference("roll_id", MasterRollsTable)
    val frameNumber = integer("frame_number")
    val cameraName = varchar("camera_name", 255)
    val filmName = varchar("film_name", 255)
    val shutterSpeed = varchar("shutter_speed", 50)
    val aperture = varchar("aperture", 50)
    val iso = integer("iso")
    val timestampMs = long("timestamp_ms")
    val dateDisplay = varchar("date_display", 100)
    val notes = text("notes")
    val tags = text("tags") // stored as comma-separated string
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val developerName = varchar("developer_name", 255).nullable()
    val paperStock = varchar("paper_stock", 255).nullable()
    val enlargerLens = varchar("enlarger_lens", 255).nullable()
    val baseSec = varchar("base_sec", 50).nullable()
    val gradeFilter = varchar("grade_filter", 50).nullable()
    val dodgeBurnNotes = text("dodge_burn_notes").nullable()
    val outcomeStatus = varchar("outcome_status", 50).nullable()
    val learningTag = varchar("learning_tag", 255).nullable()
    val thumbnailPath = varchar("thumbnail_path", 512).nullable()
}

class MasterFrameEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MasterFrameEntity>(MasterFramesTable)
    var roll by MasterRollEntity referencedOn MasterFramesTable.rollId
    var frameNumber by MasterFramesTable.frameNumber
    var cameraName by MasterFramesTable.cameraName
    var filmName by MasterFramesTable.filmName
    var shutterSpeed by MasterFramesTable.shutterSpeed
    var aperture by MasterFramesTable.aperture
    var iso by MasterFramesTable.iso
    var timestampMs by MasterFramesTable.timestampMs
    var dateDisplay by MasterFramesTable.dateDisplay
    var notes by MasterFramesTable.notes
    var tags by MasterFramesTable.tags
    var latitude by MasterFramesTable.latitude
    var longitude by MasterFramesTable.longitude
    var developerName by MasterFramesTable.developerName
    var paperStock by MasterFramesTable.paperStock
    var enlargerLens by MasterFramesTable.enlargerLens
    var baseSec by MasterFramesTable.baseSec
    var gradeFilter by MasterFramesTable.gradeFilter
    var dodgeBurnNotes by MasterFramesTable.dodgeBurnNotes
    var outcomeStatus by MasterFramesTable.outcomeStatus
    var learningTag by MasterFramesTable.learningTag
    var thumbnailPath by MasterFramesTable.thumbnailPath
}

object FoundationCamerasTable : IntIdTable("foundation_cameras") {
    val name = varchar("name", 255).uniqueIndex()
    val modelNumber = varchar("model_number", 255).nullable()
    val serialNumber = varchar("serial_number", 255).nullable()
    val type = varchar("type", 50).default("Film SLR")
    val mount = varchar("mount", 50).nullable()
    val format = varchar("format", 50).default("35mm")
    val shutterSpeeds = varchar("shutter_speeds", 500).nullable()
}

class FoundationCameraEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationCameraEntity>(FoundationCamerasTable)
    var name by FoundationCamerasTable.name
    var modelNumber by FoundationCamerasTable.modelNumber
    var serialNumber by FoundationCamerasTable.serialNumber
    var type by FoundationCamerasTable.type
    var mount by FoundationCamerasTable.mount
    var format by FoundationCamerasTable.format
    var shutterSpeeds by FoundationCamerasTable.shutterSpeeds
}

object FoundationFilmsTable : IntIdTable("foundation_films") {
    val name = varchar("name", 255).uniqueIndex()
    val abbreviation = varchar("abbreviation", 50)
    val iso = integer("iso").default(400)
    val type = varchar("type", 50).default("Color Negative")
    val format = varchar("format", 50).default("35mm")
    val expectedExposures = integer("expected_exposures").default(36)
}

class FoundationFilmEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationFilmEntity>(FoundationFilmsTable)
    var name by FoundationFilmsTable.name
    var abbreviation by FoundationFilmsTable.abbreviation
    var iso by FoundationFilmsTable.iso
    var type by FoundationFilmsTable.type
    var format by FoundationFilmsTable.format
    var expectedExposures by FoundationFilmsTable.expectedExposures
}

object FoundationSuppliersTable : IntIdTable("foundation_suppliers") {
    val name = varchar("name", 255).uniqueIndex()
    val website = varchar("website", 512).nullable()
    val region = varchar("region", 50).nullable()
    val email = varchar("email", 255).nullable()
    val phone = varchar("phone", 100).nullable()
    val address = text("address").nullable()
    val reliabilityRating = integer("reliability_rating").default(5)
    val notes = text("notes").nullable()
}

class FoundationSupplierEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationSupplierEntity>(FoundationSuppliersTable)
    var name by FoundationSuppliersTable.name
    var website by FoundationSuppliersTable.website
    var region by FoundationSuppliersTable.region
    var email by FoundationSuppliersTable.email
    var phone by FoundationSuppliersTable.phone
    var address by FoundationSuppliersTable.address
    var reliabilityRating by FoundationSuppliersTable.reliabilityRating
    var notes by FoundationSuppliersTable.notes
}

object FoundationConsumablesTable : IntIdTable("foundation_consumables") {
    val name = varchar("name", 255).uniqueIndex()
    val manufacturer = varchar("manufacturer", 255).nullable()
    val category = varchar("category", 100).default("Chemistry")
    val type = varchar("type", 100).nullable()
    val dilution = varchar("dilution", 50).nullable()
    val defaultUnit = varchar("default_unit", 50).default("ml")
    val isHazardous = bool("is_hazardous").default(false)
    val safetyNotes = text("safety_notes").nullable()
}

class FoundationConsumableEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationConsumableEntity>(FoundationConsumablesTable)
    var name by FoundationConsumablesTable.name
    var manufacturer by FoundationConsumablesTable.manufacturer
    var category by FoundationConsumablesTable.category
    var type by FoundationConsumablesTable.type
    var dilution by FoundationConsumablesTable.dilution
    var defaultUnit by FoundationConsumablesTable.defaultUnit
    var isHazardous by FoundationConsumablesTable.isHazardous
    var safetyNotes by FoundationConsumablesTable.safetyNotes
}

object FoundationLabEquipmentTable : IntIdTable("foundation_lab_equipment") {
    val name = varchar("name", 255).uniqueIndex()
    val manufacturer = varchar("manufacturer", 255).nullable()
    val modelNumber = varchar("model_number", 255).nullable()
    val serialNumber = varchar("serial_number", 255).nullable()
    val category = varchar("category", 100).default("Enlarger")
    val condition = varchar("condition", 50).default("Good")
    val notes = text("notes").nullable()
}

class FoundationLabEquipmentEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationLabEquipmentEntity>(FoundationLabEquipmentTable)
    var name by FoundationLabEquipmentTable.name
    var manufacturer by FoundationLabEquipmentTable.manufacturer
    var modelNumber by FoundationLabEquipmentTable.modelNumber
    var serialNumber by FoundationLabEquipmentTable.serialNumber
    var category by FoundationLabEquipmentTable.category
    var condition by FoundationLabEquipmentTable.condition
    var notes by FoundationLabEquipmentTable.notes
}

data class ReferenceCameraItem(
    val id: Int = 0,
    val name: String,
    val modelNumber: String? = null,
    val serialNumber: String? = null,
    val type: String = "Film SLR",
    val mount: String? = null,
    val format: String = "35mm",
    val shutterSpeeds: String? = null
)

data class ReferenceFilmItem(
    val id: Int = 0,
    val name: String,
    val abbreviation: String = "",
    val iso: Int = 400,
    val type: String = "Color Negative",
    val format: String = "35mm",
    val expectedExposures: Int = 36
)

data class ReferenceSupplierItem(
    val id: Int = 0,
    val name: String,
    val website: String? = null,
    val region: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val reliabilityRating: Int = 5,
    val notes: String? = null
)

data class ReferenceConsumableItem(
    val id: Int = 0,
    val name: String,
    val manufacturer: String? = null,
    val category: String = "Chemistry",
    val type: String? = null,
    val dilution: String? = null,
    val defaultUnit: String = "ml",
    val isHazardous: Boolean = false,
    val safetyNotes: String? = null
)

data class ReferenceLabEquipmentItem(
    val id: Int = 0,
    val name: String,
    val manufacturer: String? = null,
    val modelNumber: String? = null,
    val serialNumber: String? = null,
    val category: String = "Enlarger",
    val condition: String = "Good",
    val notes: String? = null
)

object FoundationReviewQueueTable : IntIdTable("foundation_review_queue") {
    val contributorId = integer("contributor_id")
    val itemType = varchar("item_type", 50) // e.g. "FILM", "CAMERA", "SUPPLIER"
    val submittedName = varchar("submitted_name", 255)
    val submittedIso = integer("submitted_iso").nullable()
    val submittedNotes = text("submitted_notes").nullable()
    val status = varchar("status", 50).default("PENDING") // PENDING, ACCEPTED, REJECTED
    val submissionDateMs = long("submission_date_ms")
}

class FoundationReviewQueueEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FoundationReviewQueueEntity>(FoundationReviewQueueTable)
    var contributorId by FoundationReviewQueueTable.contributorId
    var itemType by FoundationReviewQueueTable.itemType
    var submittedName by FoundationReviewQueueTable.submittedName
    var submittedIso by FoundationReviewQueueTable.submittedIso
    var submittedNotes by FoundationReviewQueueTable.submittedNotes
    var status by FoundationReviewQueueTable.status
    var submissionDateMs by FoundationReviewQueueTable.submissionDateMs
}

object DatabaseManager {
    val userHome = System.getProperty("user.home")
    val fotologueDir = File(userHome, ".fotologue")
    val thumbnailsDir = File(fotologueDir, "thumbnails")
    val dbFile = File(fotologueDir, "fotologue_master.db")

    fun initDatabase() {
        if (!fotologueDir.exists()) {
            fotologueDir.mkdirs()
        }
        if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs()
        }

        Database.connect("jdbc:sqlite:${dbFile.absolutePath}", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                MasterRollsTable, 
                MasterFramesTable, 
                FoundationCamerasTable,
                FoundationFilmsTable,
                FoundationSuppliersTable,
                FoundationConsumablesTable,
                FoundationLabEquipmentTable,
                FoundationReviewQueueTable
            )
        }
    }
}
