package com.fotologue.app.data

import com.fotologue.core.data.*

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MacroCopyPresetTest {

    @Test
    fun `default presets are pre-seeded correctly`() {
        val defaults = MacroCopyPreset.DEFAULT_PRESETS
        assertTrue(defaults.isNotEmpty())
        assertTrue(defaults.any { it.options.prefix == "_DSF" })
        assertTrue(defaults.any { it.options.prefix == "IMG_" })
        assertTrue(defaults.any { it.options.prefix == "DSC_" })
    }

    @Test
    fun `formatFileName formats camera copy prefix and digit count`() {
        val options = MacroCopyNamingOptions(
            enabled = true,
            prefix = "_DSF",
            startNumber = 1024,
            digitCount = 4,
            isReverse = false
        )
        val fileName = options.formatFileName(0, 36)
        assertEquals("_DSF1024", fileName)

        val secondFileName = options.formatFileName(1, 36)
        assertEquals("_DSF1025", secondFileName)
    }

    @Test
    fun `formatFileName formats reverse order sequence correctly`() {
        val options = MacroCopyNamingOptions(
            enabled = true,
            prefix = "IMG_",
            startNumber = 1000,
            digitCount = 4,
            isReverse = true
        )
        // For total 36 items, index 0 is (1000 + 35 - 0) = 1035
        val firstFileName = options.formatFileName(0, 36)
        assertEquals("IMG_1035", firstFileName)

        // Index 35 is (1000 + 35 - 35) = 1000
        val lastFileName = options.formatFileName(35, 36)
        assertEquals("IMG_1000", lastFileName)
    }
}
