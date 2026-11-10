package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.ConfigurationManager
import com.fotologue.core.data.MacroCopyPreset
import com.fotologue.core.data.PictureExportSize
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context
import com.fotologue.app.util.LogEntryZipExporter
import com.fotologue.core.data.FotologueRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SettingsRepository,
    private val fotologueRepository: FotologueRepository,
    private val configurationManager: ConfigurationManager
) : ViewModel() {
    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    val isThumbnailEnabled: StateFlow<Boolean> = repository.isThumbnailEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isFaceDetectionEnabled: StateFlow<Boolean> = repository.isFaceDetectionEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val faceDetectionDisclosureShown: StateFlow<Boolean> = repository.faceDetectionDisclosureShown
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val evCalibration: StateFlow<Float> = repository.evCalibration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0f)

    val blackLevelOffset: StateFlow<Float> = repository.blackLevelOffset
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0f)

    val luminanceGain: StateFlow<Float> = repository.luminanceGain
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val pictureExportSize: StateFlow<PictureExportSize> = repository.pictureExportSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PictureExportSize.NATURAL)

    val appTheme: StateFlow<String> = repository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM_DEFAULT")

    val photoCanvasMode: StateFlow<String> = repository.photoCanvasMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ISO_MID_GRAY")

    val safelightMode: StateFlow<String> = repository.safelightMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "OFF")

    val macroCopyPresets: StateFlow<List<MacroCopyPreset>> = repository.macroCopyPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MacroCopyPreset.DEFAULT_PRESETS)

    private val _creatorName = MutableStateFlow("")
    val creatorName: StateFlow<String> = _creatorName.asStateFlow()

    private val _copyrightNotice = MutableStateFlow("")
    val copyrightNotice: StateFlow<String> = _copyrightNotice.asStateFlow()

    private val _isAutoCopyrightEnabled = MutableStateFlow(false)
    val isAutoCopyrightEnabled: StateFlow<Boolean> = _isAutoCopyrightEnabled.asStateFlow()

    private val _contactInfo = MutableStateFlow("")
    val contactInfo: StateFlow<String> = _contactInfo.asStateFlow()

    private val _contributorId = MutableStateFlow(1)
    val contributorId: StateFlow<Int> = _contributorId.asStateFlow()

    private val _contributorAlias = MutableStateFlow("P1")
    val contributorAlias: StateFlow<String> = _contributorAlias.asStateFlow()

    private val _contributorFullName = MutableStateFlow("")
    val contributorFullName: StateFlow<String> = _contributorFullName.asStateFlow()

    init {
        viewModelScope.launch {
            repository.creatorName.collect { _creatorName.value = it }
        }
        viewModelScope.launch {
            repository.copyrightNotice.collect { _copyrightNotice.value = it }
        }
        viewModelScope.launch {
            repository.isAutoCopyrightEnabled.collect { _isAutoCopyrightEnabled.value = it }
        }
        viewModelScope.launch {
            repository.contactInfo.collect { _contactInfo.value = it }
        }
        viewModelScope.launch {
            repository.contributorId.collect { _contributorId.value = it }
        }
        viewModelScope.launch {
            repository.contributorAlias.collect { _contributorAlias.value = it }
        }
        viewModelScope.launch {
            repository.contributorFullName.collect { _contributorFullName.value = it }
        }
    }

    private val _presetRefreshStatus = MutableStateFlow<String?>(null)
    val presetRefreshStatus: StateFlow<String?> = _presetRefreshStatus.asStateFlow()

    fun refreshMasterPresetsFromCloud() {
        viewModelScope.launch {
            _presetRefreshStatus.value = "🔄 Connecting to GitHub Cloud Catalog..."
            val countBefore = com.fotologue.core.data.preset.MasterPresetEngine.getTotalPresetCount()
            val success = com.fotologue.core.data.preset.MasterPresetEngine.refreshFromCloud()
            val countAfter = com.fotologue.core.data.preset.MasterPresetEngine.getTotalPresetCount()
            if (success) {
                val newItems = (countAfter - countBefore).coerceAtLeast(0)
                _presetRefreshStatus.value = if (newItems > 0) {
                    "✅ Synced $newItems new community presets! Total: $countAfter presets."
                } else {
                    "✅ Catalog Up-to-Date! ($countAfter presets loaded)"
                }
            } else {
                _presetRefreshStatus.value = "⚠️ Cloud refresh offline. Operating on local catalog ($countBefore presets)."
            }
        }
    }

    fun toggleThumbnail(enabled: Boolean) {
        viewModelScope.launch {
            repository.setThumbnailEnabled(enabled)
        }
    }

    fun toggleFaceDetection(enabled: Boolean) {
        viewModelScope.launch {
            repository.setFaceDetectionEnabled(enabled)
        }
    }

    fun setFaceDetectionDisclosureShown() {
        viewModelScope.launch {
            repository.setFaceDetectionDisclosureShown(true)
        }
    }

    fun updateCalibration(calibration: Float) {
        viewModelScope.launch {
            repository.setEvCalibration(calibration)
        }
    }

    fun resetHardwareCalibration() {
        viewModelScope.launch {
            repository.setCalibrationConstants(0.0f, 1.0f)
        }
    }

    fun setPictureExportSize(size: PictureExportSize) {
        viewModelScope.launch {
            repository.setPictureExportSize(size)
        }
    }

    fun setAppTheme(themeName: String) {
        viewModelScope.launch {
            repository.setAppTheme(themeName)
        }
    }

    fun setPhotoCanvasMode(modeName: String) {
        viewModelScope.launch {
            repository.setPhotoCanvasMode(modeName)
        }
    }

    fun setSafelightMode(modeName: String) {
        viewModelScope.launch {
            repository.setSafelightMode(modeName)
        }
    }

    fun setCreatorName(name: String) {
        _creatorName.value = name
        viewModelScope.launch {
            repository.setCreatorName(name.trim())
        }
    }

    fun setCopyrightNotice(notice: String) {
        _copyrightNotice.value = notice
        viewModelScope.launch {
            repository.setCopyrightNotice(notice.trim())
        }
    }

    fun setContributorProfile(id: Int, alias: String, fullName: String) {
        _contributorId.value = id
        _contributorAlias.value = alias
        _contributorFullName.value = fullName
        viewModelScope.launch {
            repository.setContributorProfile(id, alias.trim(), fullName.trim())
        }
    }

    fun setAutoCopyrightEnabled(enabled: Boolean) {
        _isAutoCopyrightEnabled.value = enabled
        viewModelScope.launch {
            repository.setAutoCopyrightEnabled(enabled)
        }
    }

    fun setContactInfo(info: String) {
        _contactInfo.value = info
        viewModelScope.launch {
            repository.setContactInfo(info.trim())
        }
    }

    fun saveMacroCopyPreset(preset: MacroCopyPreset) {
        viewModelScope.launch {
            repository.saveMacroCopyPreset(preset)
        }
    }

    fun deleteMacroCopyPreset(id: String) {
        viewModelScope.launch {
            repository.deleteMacroCopyPreset(id)
        }
    }

    val autoBackupFolderUri: StateFlow<String?> = repository.autoBackupFolderUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val autoBackupCycle: StateFlow<String> = repository.autoBackupCycle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ON_EVERY_LOG")

    val autoBackupScope: StateFlow<String> = repository.autoBackupScope
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FULL_VAULT")

    val lastAutoBackupStatus: StateFlow<String?> = repository.lastAutoBackupStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setAutoBackupConfig(folderUri: String?, cycle: String, scope: String) {
        viewModelScope.launch {
            repository.setAutoBackupConfig(folderUri, cycle, scope)
        }
    }

    private val _consolidationStatus = MutableStateFlow<String?>(null)
    val consolidationStatus: StateFlow<String?> = _consolidationStatus.asStateFlow()

    fun consolidateRollsFromUris(uris: List<android.net.Uri>) {
        viewModelScope.launch {
            if (uris.isEmpty()) return@launch
            _consolidationStatus.value = "⏳ Consolidating ${uris.size} backup files into Darkroom LAB..."
            var totalCount = 0
            uris.forEach { uri ->
                val count = fotologueRepository.consolidateRollsFromUri(context, uri)
                totalCount += count
            }
            _consolidationStatus.value = "✅ Consolidated $totalCount Film Rolls into Darkroom LAB!"
            _events.emit(SettingsEvent.RestoreSuccess)
        }
    }

    suspend fun backupZip(outputStream: OutputStream) {
        try {
            val logs = fotologueRepository.allLogEntriesWithGear.first()
            val allTags = fotologueRepository.allTags.first()
            LogEntryZipExporter.toZip(
                context = context,
                logs = logs,
                outputStream = outputStream,
                allTags = allTags,
                includeFoundationData = true,
                repository = fotologueRepository
            )
            _events.emit(SettingsEvent.BackupSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "Zip backup failed"))
        }
    }

    suspend fun exportCsvSummaryLog(outputStream: OutputStream) {
        try {
            val logs = fotologueRepository.allLogEntriesWithGear.first()
            val csv = com.fotologue.app.util.LogEntryExporter.toCsv(logs)
            outputStream.use { it.write(csv.toByteArray()) }
            _events.emit(SettingsEvent.BackupSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "CSV export failed"))
        }
    }

    suspend fun backup(outputStream: OutputStream) {
        try {
            val configJson = configurationManager.exportConfig()
            outputStream.use { it.write(configJson.toByteArray()) }
            _events.emit(SettingsEvent.BackupSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "Backup failed"))
        }
    }

    suspend fun exportFoundationManifest(outputStream: OutputStream) {
        try {
            val configJson = configurationManager.exportFoundationManifest()
            outputStream.use { it.write(configJson.toByteArray()) }
            _events.emit(SettingsEvent.BackupSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "Reference Manifest Export failed"))
        }
    }

    suspend fun restore(inputStream: InputStream) {
        try {
            configurationManager.importConfig(inputStream)
            _events.emit(SettingsEvent.RestoreSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "Restore failed"))
        }
    }

    suspend fun importFoundationManifest(inputStream: InputStream) {
        try {
            configurationManager.importFoundationManifest(inputStream)
            _events.emit(SettingsEvent.RestoreSuccess)
        } catch (e: Exception) {
            _events.emit(SettingsEvent.Error(e.message ?: "Reference Manifest Import failed"))
        }
    }
}

sealed class SettingsEvent {
    object BackupSuccess : SettingsEvent()
    object RestoreSuccess : SettingsEvent()
    data class Error(val message: String) : SettingsEvent()
}
