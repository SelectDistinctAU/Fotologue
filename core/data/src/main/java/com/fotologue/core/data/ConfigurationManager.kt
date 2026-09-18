package com.fotologue.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

class ConfigurationManager(
    private val context: Context,
    private val database: AppDatabase
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportConfig(): String {
        val cameras = database.cameraDao().getAllCamerasOnce()
        val lenses = database.lensDao().getAllLensesOnce()
        val cameraLensCrossRefs = database.lensDao().getAllCameraLensCrossRefsOnce()
        val viewfinders = database.viewfinderDao().getAllViewfindersOnce()
        val cameraViewfinderCrossRefs = database.viewfinderDao().getAllCameraViewfinderCrossRefsOnce()
        val teleconverters = database.teleconverterDao().getAllTeleconvertersOnce()
        val cameraTeleconverterCrossRefs = database.teleconverterDao().getAllCameraTeleconverterCrossRefsOnce()
        val filters = database.filterDao().getAllFiltersOnce()
        val cameraFilterCrossRefs = database.filterDao().getAllCameraFilterCrossRefsOnce()
        val flashes = database.flashDao().getAllFlashesOnce()
        val cameraFlashCrossRefs = database.flashDao().getAllCameraFlashCrossRefsOnce()
        val films = database.filmDao().getAllFilmsOnce()
        val kits = database.cameraKitDao().getAllCameraKitsOnce()
        val tags = database.tagDao().getAllTagsOnce()
        val logs = database.logEntryDao().getAllLogEntriesOnce()
        val logEntryTags = database.tagDao().getAllLogEntryTagCrossRefsOnce()
        val filmRollSessions = database.filmRollSessionDao().getAllSessionsOnce()

        val labEquipment = database.labEquipmentDao().getAllEquipmentOnce()
        val labChemicals = database.labChemicalDao().getAllChemicalsOnce()
        val labSuppliers = database.labSupplierDao().getAllSuppliersOnce()
        val inventoryItems = database.inventoryItemDao().getAllItemsOnce()
        val orderHistory = database.orderHistoryDao().getAllOrdersOnce()
        val performanceLogs = database.performanceLogDao().getAllLogsOnce()
        val labProcessEvents = database.labProcessEventDao().getAllProcessEventsOnce()
        val printRecipes = database.printRecipeDao().getAllPrintRecipesOnce()
        val labStorageLocations = database.labStorageLocationDao().getAllStorageLocationsOnce()
        val printOutcomes = database.printOutcomeDao().getAllOutcomesOnce()

        val settingsMap = context.dataStore.data.first().asMap().mapKeys { it.key.name }.mapValues { it.value.toString() }

        val config = FotologueConfig(
            cameras = cameras,
            lenses = lenses,
            cameraLensCrossRefs = cameraLensCrossRefs,
            viewfinders = viewfinders,
            cameraViewfinderCrossRefs = cameraViewfinderCrossRefs,
            teleconverters = teleconverters,
            cameraTeleconverterCrossRefs = cameraTeleconverterCrossRefs,
            filters = filters,
            cameraFilterCrossRefs = cameraFilterCrossRefs,
            flashes = flashes,
            cameraFlashCrossRefs = cameraFlashCrossRefs,
            films = films,
            kits = kits,
            tags = tags,
            logs = logs,
            logEntryTags = logEntryTags,
            filmRollSessions = filmRollSessions,
            labEquipment = labEquipment,
            labChemicals = labChemicals,
            labSuppliers = labSuppliers,
            inventoryItems = inventoryItems,
            orderHistory = orderHistory,
            performanceLogs = performanceLogs,
            labProcessEvents = labProcessEvents,
            printRecipes = printRecipes,
            labStorageLocations = labStorageLocations,
            printOutcomes = printOutcomes,
            settings = settingsMap
        )

        return json.encodeToString(FotologueConfig.serializer(), config)
    }

    /**
     * Replaces local database with backup JSON config file.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun importConfig(inputStream: InputStream) {
        val config = inputStream.use { stream ->
            json.decodeFromStream<FotologueConfig>(stream)
        }

        database.withTransaction {
            // Clear existing data (including Lab tables)
            database.performanceLogDao().deleteAllLogs()
            database.inventoryItemDao().deleteAllItems()
            database.orderHistoryDao().deleteAllOrders()
            database.labSupplierDao().deleteAllSuppliers()
            database.printOutcomeDao().deleteAllOutcomes()
            database.printRecipeDao().deleteAllPrintRecipes()
            database.labProcessEventDao().deleteAllProcessEvents()
            database.labStorageLocationDao().deleteAllStorageLocations()
            database.labChemicalDao().deleteAllChemicals()
            database.labEquipmentDao().deleteAllEquipment()

            database.tagDao().deleteAllLogEntryTagCrossRefs()
            database.logEntryDao().deleteAllLogEntries()
            database.cameraKitDao().deleteAllCameraKits()
            database.viewfinderDao().deleteAllCameraViewfinderCrossRefs()
            database.viewfinderDao().deleteAllViewfinders()
            database.teleconverterDao().deleteAllCameraTeleconverterCrossRefs()
            database.teleconverterDao().deleteAllTeleconverters()
            database.filterDao().deleteAllCameraFilterCrossRefs()
            database.filterDao().deleteAllFilters()
            database.flashDao().deleteAllCameraFlashCrossRefs()
            database.flashDao().deleteAllFlashes()
            database.lensDao().deleteAllCameraLensCrossRefs()
            database.cameraDao().deleteAllCameras()
            database.lensDao().deleteAllLenses()
            database.filmDao().deleteAllFilms()
            database.tagDao().deleteAllTags()
            database.filmRollSessionDao().deleteAllSessions()

            // Insert new data (including Lab tables)
            config.cameras.forEach { database.cameraDao().insertCamera(it) }
            config.lenses.forEach { database.lensDao().insertLens(it) }
            config.cameraLensCrossRefs.forEach { database.lensDao().insertCameraLensCrossRef(it) }
            config.viewfinders.forEach { database.viewfinderDao().insertViewfinder(it) }
            config.cameraViewfinderCrossRefs.forEach { database.viewfinderDao().insertCameraViewfinderCrossRef(it) }
            config.teleconverters.forEach { database.teleconverterDao().insertTeleconverter(it) }
            config.cameraTeleconverterCrossRefs.forEach { database.teleconverterDao().insertCameraTeleconverterCrossRef(it) }
            config.filters.forEach { database.filterDao().insertFilter(it) }
            config.cameraFilterCrossRefs.forEach { database.filterDao().insertCameraFilterCrossRef(it) }
            config.flashes.forEach { database.flashDao().insertFlash(it) }
            config.cameraFlashCrossRefs.forEach { database.flashDao().insertCameraFlashCrossRef(it) }
            config.films.forEach { database.filmDao().insertFilm(it) }
            config.tags.forEach { database.tagDao().insertTag(it) }
            config.kits.forEach { database.cameraKitDao().insertCameraKit(it) }
            config.logs.forEach { database.logEntryDao().insertLogEntry(it) }
            config.logEntryTags.forEach { database.tagDao().insertLogEntryTagCrossRef(it) }
            config.filmRollSessions.forEach { database.filmRollSessionDao().insertSession(it) }

            config.labEquipment.forEach { database.labEquipmentDao().insertEquipment(it) }
            config.labChemicals.forEach { database.labChemicalDao().insertChemical(it) }
            config.labSuppliers.forEach { database.labSupplierDao().insertSupplier(it) }
            config.inventoryItems.forEach { database.inventoryItemDao().insertItem(it) }
            config.orderHistory.forEach { database.orderHistoryDao().insertOrder(it) }
            config.performanceLogs.forEach { database.performanceLogDao().insertLog(it) }
            config.labProcessEvents.forEach { database.labProcessEventDao().insertProcessEvent(it) }
            config.printRecipes.forEach { database.printRecipeDao().insertPrintRecipe(it) }
            config.labStorageLocations.forEach { database.labStorageLocationDao().insertStorageLocation(it) }
            config.printOutcomes.forEach { database.printOutcomeDao().insertPrintOutcome(it) }
        }

        // Restore settings
        context.dataStore.edit { prefs ->
            config.settings.forEach { (keyName, value) ->
                when (keyName) {
                    "is_thumbnail_enabled",
                    "is_face_detection_enabled",
                    "face_detection_disclosure_shown",
                    "is_auto_copyright_enabled" -> prefs[booleanPreferencesKey(keyName)] = value.toBoolean()
                    
                    "ev_calibration",
                    "last_focal_length" -> prefs[floatPreferencesKey(keyName)] = value.toFloatOrNull() ?: 0f
                    
                    "last_iso" -> prefs[intPreferencesKey(keyName)] = value.toIntOrNull() ?: 0
                    
                    "last_camera_id",
                    "last_film_id",
                    "last_lens_id",
                    "last_tc_id",
                    "last_filter_id",
                    "last_flash_id",
                    "last_viewfinder_id",
                    "last_kit_id" -> prefs[longPreferencesKey(keyName)] = value.toLongOrNull() ?: 0L
                    
                    "picture_export_size",
                    "ble_service_uuid",
                    "ble_characteristic_uuid",
                    "ble_device_name",
                    "creator_name",
                    "copyright_notice",
                    "contact_info",
                    "last_shutter_speed",
                    "last_aperture" -> prefs[stringPreferencesKey(keyName)] = value
                    
                    else -> {
                        // Skip unknown keys
                    }
                }
            }
        }
    }

    /**
     * Merges historical backup JSON records into local database WITHOUT wiping existing local data.
     * Ideal for restoring historical Roll.Frame IDs from Google Drive / Base Camp archives.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun mergeImportConfig(inputStream: InputStream) {
        val config = inputStream.use { stream ->
            json.decodeFromStream<FotologueConfig>(stream)
        }

        database.withTransaction {
            // MERGE MODE: Insert or Update without clearing existing data
            config.cameras.forEach { database.cameraDao().insertCamera(it) }
            config.lenses.forEach { database.lensDao().insertLens(it) }
            config.cameraLensCrossRefs.forEach { database.lensDao().insertCameraLensCrossRef(it) }
            config.viewfinders.forEach { database.viewfinderDao().insertViewfinder(it) }
            config.cameraViewfinderCrossRefs.forEach { database.viewfinderDao().insertCameraViewfinderCrossRef(it) }
            config.teleconverters.forEach { database.teleconverterDao().insertTeleconverter(it) }
            config.cameraTeleconverterCrossRefs.forEach { database.teleconverterDao().insertCameraTeleconverterCrossRef(it) }
            config.filters.forEach { database.filterDao().insertFilter(it) }
            config.cameraFilterCrossRefs.forEach { database.filterDao().insertCameraFilterCrossRef(it) }
            config.flashes.forEach { database.flashDao().insertFlash(it) }
            config.cameraFlashCrossRefs.forEach { database.flashDao().insertCameraFlashCrossRef(it) }
            config.films.forEach { database.filmDao().insertFilm(it) }
            config.tags.forEach { database.tagDao().insertTag(it) }
            config.kits.forEach { database.cameraKitDao().insertCameraKit(it) }
            config.logs.forEach { database.logEntryDao().insertLogEntry(it) }
            config.logEntryTags.forEach { database.tagDao().insertLogEntryTagCrossRef(it) }
            config.filmRollSessions.forEach { database.filmRollSessionDao().insertSession(it) }

            config.labEquipment.forEach { database.labEquipmentDao().insertEquipment(it) }
            config.labChemicals.forEach { database.labChemicalDao().insertChemical(it) }
            config.labSuppliers.forEach { database.labSupplierDao().insertSupplier(it) }
            config.inventoryItems.forEach { database.inventoryItemDao().insertItem(it) }
            config.orderHistory.forEach { database.orderHistoryDao().insertOrder(it) }
            config.performanceLogs.forEach { database.performanceLogDao().insertLog(it) }
            config.labProcessEvents.forEach { database.labProcessEventDao().insertProcessEvent(it) }
            config.printRecipes.forEach { database.printRecipeDao().insertPrintRecipe(it) }
            config.labStorageLocations.forEach { database.labStorageLocationDao().insertStorageLocation(it) }
            config.printOutcomes.forEach { database.printOutcomeDao().insertPrintOutcome(it) }
        }
    }
}
