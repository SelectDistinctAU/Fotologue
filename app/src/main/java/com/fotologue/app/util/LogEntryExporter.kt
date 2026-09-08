package com.fotologue.app.util

import com.fotologue.app.data.LogEntryWithGear
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogEntryExporter {
    fun toCsv(logs: List<LogEntryWithGear>): String {
        val header = "ID,Timestamp,Notes,Environmental Conditions,Camera,Lens,Teleconverter,Filter,Flash,Film,Film Roll #,Shutter Speed,Aperture,ISO,Latitude,Longitude,Focal Length,Effective Focal Length,Effective Aperture"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        val rows = logs.joinToString("\n") { entry ->
            val log = entry.logEntry
            listOf(
                log.id,
                dateFormat.format(Date(log.timestamp)),
                escapeCsv(log.notes),
                escapeCsv(log.environmentalConditions),
                escapeCsv(entry.camera?.name ?: "Unknown"),
                escapeCsv(entry.lens?.let { "${it.make} ${it.model}" } ?: ""),
                escapeCsv(entry.teleconverter?.name ?: ""),
                escapeCsv(entry.filter?.name ?: ""),
                escapeCsv(entry.flash?.let { "${it.make} ${it.model}" } ?: ""),
                escapeCsv(entry.film?.name ?: "Unknown"),
                escapeCsv(log.filmRoll ?: ""),
                escapeCsv(log.exposureSettings.shutterSpeed),
                escapeCsv(log.exposureSettings.aperture),
                log.exposureSettings.iso,
                log.latitude?.let { String.format(Locale.US, "%.6f", it) } ?: "",
                log.longitude?.let { String.format(Locale.US, "%.6f", it) } ?: "",
                log.focalLength ?: "",
                log.effectiveFocalLength ?: "",
                escapeCsv(log.effectiveAperture ?: "")
            ).joinToString(",")
        }
        
        return "$header\n$rows"
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
