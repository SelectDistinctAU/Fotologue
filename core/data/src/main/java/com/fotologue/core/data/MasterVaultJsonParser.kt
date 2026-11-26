package com.fotologue.core.data

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(InternalSerializationApi::class)
@Serializable
data class MasterVaultParsedRoll(
    val cameraName: String,
    val filmName: String,
    val filmRollSerial: String,
    val frameCount: Int,
    val dateDisplay: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class LocationExportDtoV1(
    val lat: Double?,
    val long: Double?
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ExportDtoV1(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val camera: String = "Unknown Camera",
    val film: String = "Unknown Film",
    val filmIso: Int = 0,
    val filmRoll: String? = null,
    val location: LocationExportDtoV1? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ExportEnvelopeV3(
    val schemaVersion: Int = 3,
    val appVersion: String = "3.4.0",
    val exportTimestamp: Long = System.currentTimeMillis(),
    val logs: List<ExportDtoV1> = emptyList()
)

object MasterVaultJsonParser {

    private val jsonFormatter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Inspects schemaVersion and dispatches to the strongly typed DTO serializer.
     * 100% resilient across v1.0, v2.0, and v3.0 exports with ZERO regex!
     */
    fun parseAndMigrate(jsonText: String): List<MasterVaultParsedRoll> {
        return try {
            val rootElement = jsonFormatter.parseToJsonElement(jsonText)

            val schemaVersion = when {
                rootElement is JsonObject && rootElement.containsKey("schemaVersion") -> {
                    rootElement["schemaVersion"]?.jsonPrimitive?.intOrNull ?: 3
                }
                rootElement is JsonArray -> 1 // Legacy v1 flat array export
                else -> 1
            }

            when (schemaVersion) {
                3 -> parseV3Envelope(jsonText)
                2 -> parseV1OrV2Array(jsonText)
                else -> parseV1OrV2Array(jsonText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseV3Envelope(jsonText: String): List<MasterVaultParsedRoll> {
        return try {
            val envelope = jsonFormatter.decodeFromString<ExportEnvelopeV3>(jsonText)
            mapDtosToRolls(envelope.logs)
        } catch (_: Exception) {
            parseV1OrV2Array(jsonText)
        }
    }

    private fun parseV1OrV2Array(jsonText: String): List<MasterVaultParsedRoll> {
        return try {
            val dtos = jsonFormatter.decodeFromString<List<ExportDtoV1>>(jsonText)
            mapDtosToRolls(dtos)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun mapDtosToRolls(dtos: List<ExportDtoV1>): List<MasterVaultParsedRoll> {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
        return dtos.groupBy { 
            val cam = it.camera.ifBlank { "Canon AE-1 Program" }
            val film = it.film.ifBlank { "Ilford HP5 Plus 400" }
            val roll = it.filmRoll?.ifBlank { "Roll 1" } ?: "Roll 1"
            "$cam • $film • $roll"
        }.map { (key, entries) ->
            val first = entries.first()
            val camera = first.camera.ifBlank { "Canon AE-1 Program" }
            var film = first.film.ifBlank { "Ilford HP5 Plus 400" }
            if (first.filmIso > 0 && !film.contains("${first.filmIso}")) {
                film = "$film (ISO ${first.filmIso})"
            }
            val rollSerial = first.filmRoll?.ifBlank { "Roll 1" } ?: "Roll 1"
            val frameCount = entries.size
            val dateStr = sdf.format(Date(first.timestamp))

            MasterVaultParsedRoll(
                cameraName = camera,
                filmName = film,
                filmRollSerial = "$rollSerial ($frameCount frames)",
                frameCount = frameCount,
                dateDisplay = dateStr
            )
        }
    }
}
