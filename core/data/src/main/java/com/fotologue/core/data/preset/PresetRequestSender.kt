package com.fotologue.core.data.preset

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

sealed class SendResult {
    object Success202 : SendResult()
    data class BadRequest400(val message: String) : SendResult()
    object RateLimited429 : SendResult()
    data class Unavailable503(val message: String) : SendResult()
}

object PresetRequestSender {
    const val RELAY_ENDPOINT = "https://fotologue-relay.selectdistinctau.workers.dev/v1/preset-request"

    suspend fun sendRequest(context: Context, payload: PresetRequestPayload): SendResult {
        return withContext(Dispatchers.IO) {
            val jsonPayload = try {
                PresetRequestPayload.validateAndEncode(payload)
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Validation failed"
                PresetRequestDraft.writeForensicLog(context, payload.toString(), 400, errorMsg)
                return@withContext SendResult.BadRequest400(errorMsg)
            }

            try {
                val url = URL(RELAY_ENDPOINT)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doOutput = true

                connection.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                when (responseCode) {
                    202 -> {
                        PresetRequestDraft.clearDraft(context)
                        SendResult.Success202
                    }
                    400 -> {
                        val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Bad Request"
                        PresetRequestDraft.writeForensicLog(context, jsonPayload, 400, errorText)
                        SendResult.BadRequest400(errorText)
                    }
                    429 -> {
                        SendResult.RateLimited429
                    }
                    else -> {
                        val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Service Unavailable"
                        PresetRequestDraft.saveDraft(context, payload)
                        SendResult.Unavailable503(errorText)
                    }
                }
            } catch (e: Exception) {
                PresetRequestDraft.saveDraft(context, payload)
                SendResult.Unavailable503(e.message ?: "Network error")
            }
        }
    }
}