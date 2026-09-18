package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.fotologue.core.data.Camera
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.core.data.MacroCopyNamingOptions
import com.fotologue.core.data.SettingsRepository

import com.fotologue.app.util.LogExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ExportType {
    CSV, JSON, ZIP, XMP, PDF
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val logExporter: LogExporter
) : ViewModel() {
    private val _selectedCameraId = MutableStateFlow<Long?>(null)
    val selectedCameraId: StateFlow<Long?> = _selectedCameraId.asStateFlow()

    private val _showOnlyUnexported = MutableStateFlow(false)
    val showOnlyUnexported: StateFlow<Boolean> = _showOnlyUnexported.asStateFlow()

    val allLogEntries: StateFlow<List<LogEntryWithGear>> = repository.allLogEntriesWithGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logEntries: StateFlow<List<LogEntryWithGear>> = combine(
        repository.allLogEntriesWithGear,
        _selectedCameraId,
        _showOnlyUnexported
    ) { logs, cameraId, unexportedOnly ->
        var filtered = logs
        if (cameraId != null) {
            filtered = filtered.filter { it.logEntry.cameraId == cameraId }
        }
        if (unexportedOnly) {
            filtered = filtered.filter { logWithGear ->
                val log = logWithGear.logEntry
                !log.isExportedZip && !log.isExportedCsv && !log.isExportedJson && !log.isExportedXmp
            }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isOnboardingComplete: StateFlow<Boolean> = repository.isOnboardingComplete
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDefaultTagsIfNeeded()
        }
    }

    fun setCameraFilter(cameraId: Long?) {
        _selectedCameraId.value = cameraId
    }

    fun toggleUnexportedFilter() {
        _showOnlyUnexported.value = !_showOnlyUnexported.value
    }

    fun toggleSelection(id: Long) {
        val current = _selectedIds.value
        if (id in current) {
            _selectedIds.value = current - id
        } else {
            _selectedIds.value = current + id
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun selectAll() {
        _selectedIds.value = logEntries.value.map { it.logEntry.id }.toSet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteLogEntries(_selectedIds.value.toList())
            clearSelection()
        }
    }

    fun getSelectedLogs(): List<LogEntryWithGear> {
        val selected = _selectedIds.value
        return logEntries.value.filter { it.logEntry.id in selected }
    }

    fun exportLogs(
        type: ExportType, 
        uri: Uri, 
        macroCopyOptions: MacroCopyNamingOptions = MacroCopyNamingOptions()
    ) {
        viewModelScope.launch {
            val logs = getSelectedLogs()
            val exportSize = settingsRepository.pictureExportSize.first()
            val success = logExporter.export(logs, type, uri, exportSize, macroCopyOptions)
            
            if (success) {
                val ids = logs.map { it.logEntry.id }
                when (type) {
                    ExportType.ZIP -> repository.markAsExportedZip(ids)
                    ExportType.CSV -> repository.markAsExportedCsv(ids)
                    ExportType.JSON -> repository.markAsExportedJson(ids)
                    ExportType.XMP, ExportType.PDF -> repository.markAsExportedXmp(ids)
                }
            }
            clearSelection()
        }
    }
}
