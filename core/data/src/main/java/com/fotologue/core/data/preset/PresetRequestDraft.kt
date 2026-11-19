package com.fotologue.core.data.preset

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import java.io.File

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
object PresetRequestDraft {
    private const val DRAFT_FILE_NAME = "preset_request_draft.json"
    private const val FORENSIC_LOG_FILE_NAME = "preset_request_forensic.log"

    fun saveDraft(context: Context, payload: PresetRequestPayload) {
        try {
            val jsonStr = PresetRequestPayload.strictJson.encodeToString(PresetRequestPayload.serializer(), payload)
            File(context.filesDir, DRAFT_FILE_NAME).writeText(jsonStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDraft(context: Context): PresetRequestPayload? {
        val file = File(context.filesDir, DRAFT_FILE_NAME)
        if (!file.exists()) return null
        return try {
            val jsonStr = file.readText()
            PresetRequestPayload.strictJson.decodeFromString(PresetRequestPayload.serializer(), jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    fun clearDraft(context: Context) {
        try {
            File(context.filesDir, DRAFT_FILE_NAME).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeForensicLog(context: Context, rawPayload: String, statusCode: Int, errorMessage: String) {
        try {
            val logFile = File(context.filesDir, FORENSIC_LOG_FILE_NAME)
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(java.util.Date())
            val logEntry = "[$timestamp] HTTP $statusCode: $errorMessage\nPayload: $rawPayload\n----------------------------------------\n"
            logFile.appendText(logEntry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}