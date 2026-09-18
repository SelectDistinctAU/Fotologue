package com.fotologue.app.util

import com.fotologue.core.data.Lens
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExposureSuggesterTest {

    @Test
    fun `generateExposureSuggestions filters out apertures wider than lens minAperture`() {
        val lens = Lens(
            make = "Test",
            model = "Lens",
            type = "Prime",
            mount = "M",
            filterSize = 49,
            isStabilized = false,
            minFocusDistance = 0.5,
            maxMagnification = 0.1,
            minAperture = 2.8, // Widest (smallest f-number)
            maxAperture = 22.0, // Smallest (largest f-number)
            minFocalLength = 50,
            maxFocalLength = 50
        )
        
        val suggestions = ExposureSuggester.generateExposureSuggestions(
            ev100 = 15.0,
            targetIso = 100,
            lens = lens
        )
        
        // commonApertures includes 1.4 and 2.0, which are < 2.8 (wider)
        assertFalse("Should not include f/1.4", suggestions.any { it.aperture == "f/1.4" })
        assertFalse("Should not include f/2", suggestions.any { it.aperture == "f/2" })
        assertTrue("Should include f/2.8", suggestions.any { it.aperture == "f/2.8" })
        assertTrue("Should include f/16", suggestions.any { it.aperture == "f/16" })
        assertTrue("Should include f/22", suggestions.any { it.aperture == "f/22" })
    }

    @Test
    fun `generateExposureSuggestions uses default shutter speeds when no camera provided`() {
        val suggestions = ExposureSuggester.generateExposureSuggestions(
            ev100 = 12.0, // EV 12 at ISO 100
            targetIso = 100
        )
        
        // At EV 12, ISO 100, f/8: t = 8^2 / 2^12 = 64 / 4096 = 1/64
        // Closest default is 1/60
        assertTrue("Should include 1/60 for f/8 at EV 12", suggestions.any { it.aperture == "f/8" && it.shutterSpeed == "1/60" })
        
        // Verify all suggestions use only default shutter speeds
        suggestions.forEach { suggestion ->
            assertTrue(
                "Shutter speed ${suggestion.shutterSpeed} should be in default list",
                ExposureSuggester.defaultShutterSpeeds.contains(suggestion.shutterSpeed)
            )
        }
    }
}
