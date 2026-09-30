package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.Flash
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditFlashUiState(
    val make: String = "",
    val model: String = "",
    val guideNumber: String = "",
    val syncType: String = "Hotshoe",
    val serialNumber: String = "",
    val syncTypes: List<String> = listOf("Hotshoe", "Coldshoe", "PC Sync Cord", "Bulb Sync"),
    val cameras: List<Camera> = emptyList(),
    val selectedCameraIds: Set<Long> = emptySet()
)

@HiltViewModel
class AddEditFlashViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditFlashUiState(
        make = savedStateHandle.get<String>("make") ?: "",
        model = savedStateHandle.get<String>("model") ?: "",
        guideNumber = savedStateHandle.get<String>("guideNumber") ?: ""
    ))
    val uiState: StateFlow<AddEditFlashUiState> = _uiState.asStateFlow()

    var flashId: Long? = savedStateHandle.get<Long>("flashId")
        private set

    init {
        viewModelScope.launch {
            repository.allCameras.collect { cameras ->
                _uiState.update { it.copy(cameras = cameras) }
            }
        }
        loadFlash()
    }

    fun setFlashId(id: Long?) {
        if (flashId == id) return
        flashId = id
        savedStateHandle["flashId"] = id
        loadFlash()
    }

    private fun loadFlash() {
        val id = flashId ?: return
        viewModelScope.launch {
            val flash = repository.getFlashById(id).firstOrNull()
            flash?.let { item ->
                val crossRefs = repository.allCameraFlashCrossRefs.firstOrNull() ?: emptyList()
                val associatedCameraIds = crossRefs.filter { it.flashId == item.id }.map { it.cameraId }.toSet()
                _uiState.update { state ->
                    state.copy(
                        make = savedStateHandle.get<String>("make") ?: item.make,
                        model = savedStateHandle.get<String>("model") ?: item.model,
                        guideNumber = savedStateHandle.get<String>("guideNumber") ?: item.guideNumber.toString(),
                        selectedCameraIds = associatedCameraIds
                    )
                }
            }
        }
    }

    fun onMakeChange(value: String) {
        savedStateHandle["make"] = value
        _uiState.update { it.copy(make = value) }
    }

    fun onModelChange(value: String) {
        savedStateHandle["model"] = value
        _uiState.update { it.copy(model = value) }
    }

    fun onGuideNumberChange(value: String) {
        savedStateHandle["guideNumber"] = value
        _uiState.update { it.copy(guideNumber = value) }
    }

    fun onSyncTypeChange(value: String) {
        savedStateHandle["syncType"] = value
        _uiState.update { it.copy(syncType = value) }
    }

    fun onSerialNumberChange(value: String) {
        savedStateHandle["serialNumber"] = value
        _uiState.update { it.copy(serialNumber = value) }
    }

    fun onCameraToggled(cameraId: Long) {
        _uiState.update { state ->
            val current = state.selectedCameraIds
            val updated = if (current.contains(cameraId)) current - cameraId else current + cameraId
            state.copy(selectedCameraIds = updated)
        }
    }

    fun saveFlash(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val flash = Flash(
                id = flashId ?: 0,
                make = state.make.trim(),
                model = state.model.trim(),
                guideNumber = state.guideNumber.toIntOrNull() ?: 0
            )

            if (flashId == null) {
                repository.insertFlash(flash, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            } else {
                repository.updateFlash(flash, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            }
            onSuccess()
        }
    }
}
