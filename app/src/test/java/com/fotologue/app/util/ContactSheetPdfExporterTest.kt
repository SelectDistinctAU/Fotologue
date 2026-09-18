package com.fotologue.app.util

import android.content.Context
import com.fotologue.core.data.Camera
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.ExposureSettings
import com.fotologue.core.data.Film
import com.fotologue.core.data.LogEntry
import com.fotologue.core.data.LogEntryWithGear
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.ByteArrayOutputStream

class ContactSheetPdfExporterTest {

    private val context: Context = mockk(relaxed = true)

    @Test
    fun `generatePdf returns false for empty logs list`() {
        val outputStream = ByteArrayOutputStream()
        val result = ContactSheetPdfExporter.generatePdf(
            context = context,
            logs = emptyList(),
            outputStream = outputStream
        )
        assertFalse(result)
    }
}
