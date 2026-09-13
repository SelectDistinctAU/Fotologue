package com.fotologue.app.util

import android.content.Context
import com.fotologue.app.data.Camera
import com.fotologue.app.data.CameraType
import com.fotologue.app.data.ExposureSettings
import com.fotologue.app.data.Film
import com.fotologue.app.data.LogEntry
import com.fotologue.app.data.LogEntryWithGear
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
