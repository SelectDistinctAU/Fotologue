package com.fotologue.app.util

import android.content.Context
import android.net.Uri
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.LogEntryWithGear
import com.fotologue.app.data.PictureExportSize
import com.fotologue.app.data.SettingsRepository
import com.fotologue.app.ui.viewmodel.ExportType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.OutputStream
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogExporter @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val repository: FotologueRepository
) {
    fun export(
        logs: List<LogEntryWithGear>,
        type: ExportType,
        uri: Uri,
        exportSize: PictureExportSize = PictureExportSize.NATURAL
    ) : Boolean {
        try {
            val creator = runBlocking { settingsRepository.creatorName.first() }
            val isAutoCopyright = runBlocking { settingsRepository.isAutoCopyrightEnabled.first() }
            val manualCopyright = runBlocking { settingsRepository.copyrightNotice.first() }
            val contact = runBlocking { settingsRepository.contactInfo.first() }
            val allTags = runBlocking { repository.allTags.first() }

            val copyright = if (isAutoCopyright) {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                "© $year $creator"
            } else {
                manualCopyright
            }

            return context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                when (type) {
                    ExportType.CSV -> {
                        val csv = LogEntryExporter.toCsv(logs)
                        outputStream.write(csv.toByteArray())
                        outputStream.flush()
                    }
                    ExportType.JSON -> {
                        val json = LogEntryJsonExporter.toJson(logs)
                        outputStream.write(json.toByteArray())
                        outputStream.flush()
                    }
                    ExportType.ZIP -> {
                        LogEntryZipExporter.toZip(context, logs, outputStream, exportSize, allTags, creator, copyright, contact)
                    }
                }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
