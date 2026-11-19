package com.fotologue.core.data.preset

import kotlinx.serialization.SerializationException
import org.junit.Assert.*
import org.junit.Test

class PresetRequestSenderTest {

    @Test
    fun `PresetRequestPayload trims user input and caps lengths`() {
        val payload = PresetRequestPayload(
            requestType = "CAMERA",
            appVersion = "1.0.27",
            platform = "android",
            brand = "   Pentax   ",
            name = "   LX   ",
            notes = "A".repeat(300)
        )

        val trimmed = payload.trimmed()
        assertEquals("Pentax", trimmed.brand)
        assertEquals("LX", trimmed.name)
        assertEquals(250, trimmed.notes?.length)
    }

    @Test
    fun `PresetRequestPayload validates https URL with host name`() {
        val validPayload = PresetRequestPayload(
            requestType = "CAMERA",
            appVersion = "1.0.27",
            platform = "android",
            brand = "Leica",
            name = "M6",
            referenceUrl = "https://camera-wiki.org/wiki/Leica_M6"
        )
        val encoded = PresetRequestPayload.validateAndEncode(validPayload)
        assertTrue(encoded.contains("camera-wiki.org"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `PresetRequestPayload rejects invalid referenceUrl without host`() {
        val invalidPayload = PresetRequestPayload(
            requestType = "CAMERA",
            appVersion = "1.0.27",
            platform = "android",
            brand = "Leica",
            name = "M6",
            referenceUrl = "https://"
        )
        PresetRequestPayload.validateAndEncode(invalidPayload)
    }

    @Test(expected = SerializationException::class)
    fun `PresetRequestPayload strictJson enforces closed schema and rejects unknown keys`() {
        val jsonWithUnknownKey = """
            {
              "requestType": "CAMERA",
              "appVersion": "1.0.27",
              "platform": "android",
              "brand": "Canon",
              "name": "AE-1",
              "extraMaliciousKey": "hacked"
            }
        """.trimIndent()

        PresetRequestPayload.strictJson.decodeFromString(PresetRequestPayload.serializer(), jsonWithUnknownKey)
    }
}