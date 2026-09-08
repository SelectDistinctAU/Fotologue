package com.fotologue.app.data

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
        val films = database.filmDao().getAllFilmsOnce()
        val kits = database.cameraKitDao().getAllCameraKitsOnce()
        val teleconverters = database.teleconverterDao().getAllTeleconvertersOnce()
        val filters = database.filterDao().getAllFiltersOnce()
        val flashes = database.flashDao().getAllFlashesOnce()
        val tags = database.tagDao().getAllTagsOnce()
        val logs = database.logEntryDao().getAllLogEntriesOnce()
        val logEntryTags = database.tagDao().getAllLogEntryTagCrossRefsOnce()

        val settingsMap = context.dataStore.data.first().asMap().mapKeys { it.key.name }.mapValues { it.value.toString() }

        val config = FotologueConfig(
            cameras = cameras,
            lenses = lenses,
            cameraLensCrossRefs = cameraLensCrossRefs,
            films = films,
            kits = kits,
            teleconverters = teleconverters,
            filters = filters,
            flashes = flashes,
            tags = tags,
            logs = logs,
            logEntryTags = logEntryTags,
            settings = settingsMap
        )

        return json.encodeToString(FotologueConfig.serializer(), config)
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun importConfig(inputStream: InputStream) {
        val config = inputStream.use { stream ->
            json.decodeFromStream<FotologueConfig>(stream)
        }

        database.withTransaction {
            // Clear existing data
            database.tagDao().deleteAllLogEntryTagCrossRefs()
            database.logEntryDao().deleteAllLogEntries()
            database.cameraKitDao().deleteAllCameraKits()
            database.lensDao().deleteAllCameraLensCrossRefs()
            database.cameraDao().deleteAllCameras()
            database.lensDao().deleteAllLenses()
            database.filmDao().deleteAllFilms()
            database.teleconverterDao().deleteAllTeleconverters()
            database.filterDao().deleteAllFilters()
            database.flashDao().deleteAllFlashes()
            database.tagDao().deleteAllTags()

            // Insert new data
            config.cameras.forEach { database.cameraDao().insertCamera(it) }
            config.lenses.forEach { database.lensDao().insertLens(it) }
            config.cameraLensCrossRefs.forEach { database.lensDao().insertCameraLensCrossRef(it) }
            config.films.forEach { database.filmDao().insertFilm(it) }
            config.teleconverters.forEach { database.teleconverterDao().insertTeleconverter(it) }
            config.filters.forEach { database.filterDao().insertFilter(it) }
            config.flashes.forEach { database.flashDao().insertFlash(it) }
            config.tags.forEach { database.tagDao().insertTag(it) }
            config.kits.forEach { database.cameraKitDao().insertCameraKit(it) }
            config.logs.forEach { database.logEntryDao().insertLogEntry(it) }
            config.logEntryTags.forEach { database.tagDao().insertLogEntryTagCrossRef(it) }
        }

        // Restore settings
        context.dataStore.edit { prefs ->
            config.settings.forEach { (keyName, value) ->
                // We attempt to restore based on known keys or just store as strings if unknown
                // But DataStore is typed, so we need to be careful.
                // For this implementation, we'll focus on the keys defined in SettingsRepository.
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
                        // Skip unknown keys to avoid type pollution
                    }
                }
            }
        }
    }
}
