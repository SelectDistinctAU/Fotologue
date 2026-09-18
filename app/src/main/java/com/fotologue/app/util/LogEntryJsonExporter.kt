package com.fotologue.app.util

import com.fotologue.core.data.LogEntryWithGear
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object LogEntryJsonExporter {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun toJson(logs: List<LogEntryWithGear>): String {
        val dtos = logs.map { it.toExportDto() }
        return json.encodeToString(dtos)
    }

    private fun LogEntryWithGear.toExportDto() = LogEntryExportDto(
        id = logEntry.id,
        timestamp = logEntry.timestamp,
        notes = logEntry.notes,
        environmentalConditions = logEntry.environmentalConditions,
        camera = camera?.name ?: "Unknown",
        lens = lens?.let { "${it.make} ${it.model}" },
        film = film?.name ?: "Unknown",
        filmIso = film?.iso ?: 0,
        tc = teleconverter?.name,
        filter = filter?.name,
        flash = flash?.let { "${it.make} ${it.model}" },
        filmRoll = logEntry.filmRoll,
        exposure = ExposureExportDto(
            aperture = logEntry.exposureSettings.aperture,
            shutterSpeed = logEntry.exposureSettings.shutterSpeed,
            iso = logEntry.exposureSettings.iso,
            focalLength = logEntry.focalLength,
            effectiveFocalLength = logEntry.effectiveFocalLength,
            effectiveAperture = logEntry.effectiveAperture
        ),
        location = if (logEntry.latitude != null || logEntry.longitude != null) {
            LocationExportDto(logEntry.latitude, logEntry.longitude)
        } else null,
        tags = tags.map { it.name }
    )
}

@Serializable
data class LogEntryExportDto(
    val id: Long,
    val timestamp: Long,
    val notes: String,
    val environmentalConditions: String,
    val camera: String,
    val lens: String?,
    val film: String,
    val filmIso: Int,
    val tc: String?,
    val filter: String?,
    val flash: String?,
    val filmRoll: String?,
    val exposure: ExposureExportDto,
    val location: LocationExportDto?,
    val tags: List<String>
)

@Serializable
data class ExposureExportDto(
    val aperture: String,
    val shutterSpeed: String,
    val iso: Int,
    val focalLength: Float?,
    val effectiveFocalLength: Float?,
    val effectiveAperture: String?
)

@Serializable
data class LocationExportDto(
    val lat: Double?,
    val long: Double?
)
