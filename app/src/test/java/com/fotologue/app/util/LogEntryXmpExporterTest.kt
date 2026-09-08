package com.fotologue.app.util

import com.fotologue.app.data.*
import org.junit.Assert.assertTrue
import org.junit.Test

class LogEntryXmpExporterTest {

    @Test
    fun `toXmp generates valid XMP structure`() {
        val tag = Tag(1, "Landscape", category = Tag.CATEGORY_CONTENT, parentId = 2)
        val parent = Tag(2, "Nature", category = Tag.CATEGORY_SUBJECT)
        
        val entry = LogEntryWithGear(
            logEntry = LogEntry(
                id = 1,
                timestamp = 1672531200000, // 2023-01-01
                notes = "Golden Hour shot",
                environmentalConditions = "Slightly cloudy",
                cameraId = 1,
                filmId = 1,
                exposureSettings = ExposureSettings("1/250", "f/5.6", 400),
                focalLength = 35f,
                latitude = -33.8688,
                longitude = 151.2093
            ),
            camera = Camera(1, "Leica M6", CameraType.FILM_SLR),
            film = Film(1, "Kodak Portra 400", 400, Film.TYPE_COLOR_NEGATIVE),
            tags = listOf(tag)
        )

        val xmp = LogEntryXmpExporter.toXmp(entry, listOf(tag, parent))

        assertTrue(xmp.contains("<x:xmpmeta"))
        assertTrue(xmp.contains("exif:FNumber=\"5.6\""))
        assertTrue(xmp.contains("exif:ISOSpeedRatings=\"400\""))
        assertTrue(xmp.contains("exif:ExposureTime=\"0.004\""))
        assertTrue(xmp.contains("tiff:Make=\"Leica M6\""))
        assertTrue(xmp.contains("Nature|Landscape"))
        assertTrue(xmp.contains("Golden Hour shot"))
        assertTrue(xmp.contains("-33.8688"))
    }
}
