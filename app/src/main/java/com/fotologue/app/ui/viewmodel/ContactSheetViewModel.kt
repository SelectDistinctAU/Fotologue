package com.fotologue.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.Camera
import com.fotologue.app.data.Film
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.LogEntryWithGear
import com.fotologue.app.data.MacroCopyNamingOptions
import com.fotologue.app.data.SettingsRepository
import com.fotologue.app.util.LogExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RollKey(
    val cameraId: Long?,
    val filmId: Long?,
    val rollNumber: Int,
    val cameraName: String,
    val filmName: String
)

data class ContactSheetFrame(
    val logWithGear: LogEntryWithGear,
    val rollNumber: Int,
    val frameNumber: Int
)

data class ContactSheetUiState(
    val availableRolls: List<RollKey> = emptyList(),
    val selectedRollKey: RollKey? = null,
    val frames: List<ContactSheetFrame> = emptyList(),
    val selectedFrameIds: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false,
    val gridColumns: Int = 6,
    val cameraName: String = "",
    val filmName: String = "",
    val expectedExposures: Int = 36
)

@HiltViewModel
class ContactSheetViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val logExporter: LogExporter,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialCameraId: Long? = savedStateHandle.get<Long>("cameraId")
    private val initialFilmId: Long? = savedStateHandle.get<Long>("filmId")
    private val initialRollNumber: Int? = savedStateHandle.get<Int>("rollNumber")

    private val _uiState = MutableStateFlow(ContactSheetUiState())
    val uiState: StateFlow<ContactSheetUiState> = _uiState.asStateFlow()

    private val _selectedRoll = MutableStateFlow<RollKey?>(null)

    init {
        viewModelScope.launch {
            combine(
                repository.allLogEntriesWithGear,
                repository.allCameras,
                repository.allFilms,
                _selectedRoll
            ) { logs, cameras, films, currentSelectedRoll ->
                // Group all logs by (cameraId, filmId, rollNumber)
                val rollMap = mutableMapOf<RollKey, MutableList<ContactSheetFrame>>()

                logs.forEach { logWithGear ->
                    val (rollNum, frameNum) = parseRollAndFrame(logWithGear.logEntry.filmRoll)
                    val cam = logWithGear.camera
                    val film = logWithGear.film

                    val camName = cam?.name ?: "Unknown Camera"
                    val filmName = film?.name ?: "Unknown Film"

                    val key = RollKey(
                        cameraId = logWithGear.logEntry.cameraId,
                        filmId = logWithGear.logEntry.filmId,
                        rollNumber = rollNum,
                        cameraName = camName,
                        filmName = filmName
                    )

                    val frame = ContactSheetFrame(logWithGear, rollNum, frameNum)
                    rollMap.getOrPut(key) { mutableListOf() }.add(frame)
                }

                val availableRolls = rollMap.keys.sortedWith(
                    compareBy({ it.cameraName }, { it.filmName }, { it.rollNumber })
                )

                // Determine active roll
                val activeRoll = currentSelectedRoll ?: availableRolls.find {
                    (initialCameraId == null || it.cameraId == initialCameraId) &&
                    (initialFilmId == null || it.filmId == initialFilmId) &&
                    (initialRollNumber == null || it.rollNumber == initialRollNumber)
                } ?: availableRolls.firstOrNull()

                val activeFrames = if (activeRoll != null) {
                    rollMap[activeRoll]?.sortedBy { it.frameNumber } ?: emptyList()
                } else emptyList()

                // Calculate columns based on format
                val activeFilm = films.find { it.id == activeRoll?.filmId }
                val activeCamera = cameras.find { it.id == activeRoll?.cameraId }
                val format = activeFilm?.format ?: activeCamera?.format ?: "35mm"

                val cols = when {
                    format.contains("120", ignoreCase = true) -> 3
                    format.contains("Large", ignoreCase = true) -> 2
                    else -> 6 // 35mm default
                }

                val expected = activeFilm?.expectedExposures ?: 36
                val isHalf = activeCamera?.format == Film.FORMAT_35MM_HALF || activeCamera?.isHalfFrame == true
                val totalExpected = if (isHalf) expected * 2 else expected

                ContactSheetUiState(
                    availableRolls = availableRolls,
                    selectedRollKey = activeRoll,
                    frames = activeFrames,
                    gridColumns = cols,
                    cameraName = activeRoll?.cameraName ?: "",
                    filmName = activeRoll?.filmName ?: "",
                    expectedExposures = totalExpected,
                    selectedFrameIds = _uiState.value.selectedFrameIds,
                    isSelectionMode = _uiState.value.selectedFrameIds.isNotEmpty()
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectRoll(rollKey: RollKey) {
        _selectedRoll.value = rollKey
        clearSelection()
    }

    fun shiftFrameNumbers(startingFrameNumber: Int, delta: Int) {
        val activeRoll = _uiState.value.selectedRollKey ?: return
        viewModelScope.launch {
            repository.shiftRollFrameNumbers(
                cameraId = activeRoll.cameraId,
                filmId = activeRoll.filmId,
                rollNumber = activeRoll.rollNumber,
                startingFrameNumber = startingFrameNumber,
                delta = delta
            )
        }
    }

    fun toggleFrameSelection(id: Long) {
        _uiState.update { state ->
            val current = state.selectedFrameIds
            val updated = if (current.contains(id)) current - id else current + id
            state.copy(
                selectedFrameIds = updated,
                isSelectionMode = updated.isNotEmpty()
            )
        }
    }

    fun selectAllFrames() {
        _uiState.update { state ->
            val allIds = state.frames.map { it.logWithGear.logEntry.id }.toSet()
            state.copy(
                selectedFrameIds = allIds,
                isSelectionMode = allIds.isNotEmpty()
            )
        }
    }

    fun clearSelection() {
        _uiState.update { state ->
            state.copy(
                selectedFrameIds = emptySet(),
                isSelectionMode = false
            )
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val selected = _uiState.value.selectedFrameIds.toList()
            if (selected.isNotEmpty()) {
                repository.deleteLogEntries(selected)
                clearSelection()
            }
        }
    }

    fun exportSelected(
        type: ExportType, 
        uri: Uri, 
        macroCopyOptions: MacroCopyNamingOptions = MacroCopyNamingOptions()
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            val selected = state.selectedFrameIds
            val logsToExport = state.frames.filter { it.logWithGear.logEntry.id in selected }.map { it.logWithGear }
            
            if (logsToExport.isNotEmpty()) {
                val exportSize = settingsRepository.pictureExportSize.first()
                val success = logExporter.export(logsToExport, type, uri, exportSize, macroCopyOptions)
                if (success) {
                    val ids = logsToExport.map { it.logEntry.id }
                    when (type) {
                        ExportType.ZIP -> repository.markAsExportedZip(ids)
                        ExportType.CSV -> repository.markAsExportedCsv(ids)
                        ExportType.JSON -> repository.markAsExportedJson(ids)
                        ExportType.XMP, ExportType.PDF -> repository.markAsExportedXmp(ids)
                    }
                }
            }
            clearSelection()
        }
    }

    companion object {
        fun parseRollAndFrame(filmRollStr: String?): Pair<Int, Int> {
            if (filmRollStr.isNullOrBlank()) return Pair(1, 1)
            if (filmRollStr.contains(".")) {
                val parts = filmRollStr.split(".")
                val roll = parts.getOrNull(0)?.toIntOrNull() ?: 1
                val frame = parts.getOrNull(1)?.toIntOrNull() ?: 1
                return Pair(roll, frame)
            }
            val numbers = Regex("""\d+""").findAll(filmRollStr).map { it.value.toInt() }.toList()
            val roll = numbers.getOrNull(0) ?: 1
            val frame = numbers.getOrNull(1) ?: 1
            return Pair(roll, frame)
        }
    }
}
