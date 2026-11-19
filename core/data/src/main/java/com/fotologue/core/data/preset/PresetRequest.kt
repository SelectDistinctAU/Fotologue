package com.fotologue.core.data.preset

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class CameraDetails(
    val type: String? = null,
    val mount: String? = null,
    val format: String? = null,
    val shutterSpeeds: String? = null
)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class LensDetails(
    val type: String? = null,
    val mount: String? = null,
    val focalLength: String? = null,
    val maxAperture: String? = null,
    val minAperture: String? = null,
    val filterSize: String? = null
)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class PresetRequestPayload(
    val requestType: String,
    val appVersion: String,
    val platform: String = "android",
    val brand: String,
    val name: String,
    val modelNumber: String? = null,
    val cameraDetails: CameraDetails? = null,
    val lensDetails: LensDetails? = null,
    val referenceUrl: String? = null,
    val notes: String? = null
) {
    fun trimmed(): PresetRequestPayload {
        return copy(
            brand = brand.trim().take(50),
            name = name.trim().take(80),
            modelNumber = modelNumber?.trim()?.take(50),
            cameraDetails = cameraDetails?.copy(
                mount = cameraDetails.mount?.trim()?.take(40),
                format = cameraDetails.format?.trim()?.take(40),
                shutterSpeeds = cameraDetails.shutterSpeeds?.trim()?.take(60)
            ),
            lensDetails = lensDetails?.copy(
                mount = lensDetails.mount?.trim()?.take(40),
                focalLength = lensDetails.focalLength?.trim()?.take(30),
                maxAperture = lensDetails.maxAperture?.trim()?.take(20),
                minAperture = lensDetails.minAperture?.trim()?.take(20),
                filterSize = lensDetails.filterSize?.trim()?.take(20)
            ),
            referenceUrl = referenceUrl?.trim()?.take(200),
            notes = notes?.trim()?.take(250)
        )
    }

    companion object {
        val strictJson = Json {
            ignoreUnknownKeys = false
            encodeDefaults = true
            explicitNulls = false
        }

        fun validateAndEncode(payload: PresetRequestPayload): String {
            val trimmedPayload = payload.trimmed()
            
            trimmedPayload.referenceUrl?.let { url ->
                if (url.isNotBlank()) {
                    val uri = try { java.net.URI(url) } catch (e: Exception) {
                        throw IllegalArgumentException("referenceUrl must be a valid https URL with a host name", e)
                    }
                    require(uri.scheme == "https" && !uri.host.isNullOrBlank()) {
                        "referenceUrl must be a valid https URL with a host name"
                    }
                }
            }

            val encoded = strictJson.encodeToString(serializer(), trimmedPayload)
            require(encoded.toByteArray().size <= 4096) {
                "Payload exceeds maximum cap of 4096 bytes"
            }
            return encoded
        }
    }
}