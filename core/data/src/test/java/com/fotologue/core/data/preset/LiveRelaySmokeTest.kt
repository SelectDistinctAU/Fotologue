package com.fotologue.core.data.preset

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

@Ignore("Executed during live relay smoke qualification protocol")
class LiveRelaySmokeTest {

    @Test
    fun executeLiveRelaySmokeQualification() = runBlocking {
        val mockContext = io.mockk.mockk<android.content.Context>(relaxed = true) {
            io.mockk.every { filesDir } returns java.io.File(System.getProperty("java.io.tmpdir") ?: ".")
        }
        
        println("Waiting 65 seconds for initial rate-limit budget reset...")
        Thread.sleep(65000)

        // 1. Valid Camera Request
        val cameraPayload = PresetRequestPayload(
            requestType = "CAMERA",
            appVersion = "0.0.0-smoke",
            platform = "android",
            brand = "ZZ Relay Smoke",
            name = "Camera Smoke Test",
            notes = "Live qualification smoke test payload 1"
        )
        println("Sending Camera Smoke Payload...")
        val res1 = PresetRequestSender.sendRequest(mockContext, cameraPayload)
        println("Result 1: $res1")
        assertTrue(res1 is SendResult.Success202)

        // 2. Valid Lens Request
        val lensPayload = PresetRequestPayload(
            requestType = "LENS",
            appVersion = "0.0.0-smoke",
            platform = "android",
            brand = "ZZ Relay Smoke",
            name = "Lens Smoke Test",
            notes = "Live qualification smoke test payload 2"
        )
        println("Sending Lens Smoke Payload...")
        val res2 = PresetRequestSender.sendRequest(mockContext, lensPayload)
        println("Result 2: $res2")
        assertTrue(res2 is SendResult.Success202)

        // 3. Deliberate 400 Bad Request
        val invalidPayload = cameraPayload.copy(brand = "")
        println("Sending Deliberate 400 Payload...")
        val res3 = PresetRequestSender.sendRequest(mockContext, invalidPayload)
        println("Result 3: $res3")
        assertTrue(res3 is SendResult.BadRequest400)

        println("Pausing 65 seconds for rate-limit window reset...")
        Thread.sleep(65000)

        // 4. Rate Limit 429 Test
        println("Executing 4 rapid posts for 429 check...")
        val rapidPayload = cameraPayload.copy(name = "Rapid Post Test")
        val r1 = PresetRequestSender.sendRequest(mockContext, rapidPayload)
        val r2 = PresetRequestSender.sendRequest(mockContext, rapidPayload)
        val r3 = PresetRequestSender.sendRequest(mockContext, rapidPayload)
        val r4 = PresetRequestSender.sendRequest(mockContext, rapidPayload)

        println("Rapid Results: 1=$r1, 2=$r2, 3=$r3, 4=$r4")
        assertTrue(r1 is SendResult.Success202)
        assertTrue(r2 is SendResult.Success202)
        assertTrue(r3 is SendResult.Success202)
        assertTrue(r4 is SendResult.RateLimited429)
    }
}