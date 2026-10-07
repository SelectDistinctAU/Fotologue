package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.Viewfinder
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditViewfinderUiState(
    val name: String = "",
    val modelNumber: String = "",
    val type: String = "Eye-Level Prism",
    val serialNumber: String = "",
    val viewfinderTypes: List<String> = listOf("Eye-Level Prism", "Waist-Level Finder", "Action Finder", "Right-Angle Finder", "Magnifier / Diopter", "Coldshoe Auxiliary Finder"),
    val cameras: List<Camera> = emptyList(),
    val selectedCameraIds: Set<Long> = emptySet()
)

@HiltViewModel
class AddEditViewfinderViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditViewfinderUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        modelNumber = savedStateHandle.get<String>("modelNumber") ?: ""
    ))
    val uiState: StateFlow<AddEditViewfinderUiState> = _uiState.asStateFlow()

    var viewfinderId: Long? = savedStateHandle.get<Long>("viewfinderId")
        private set

    init {
        viewModelScope.launch {
            repository.allCameras.collect { cameras ->
                val interchangeableFinderCameras = cameras.filter { camera ->
                    val name = camera.name.uppercase()
                    val isExplicitAE1 = name.contains("AE-1") || name.contains("AE1")
                    if (isExplicitAE1) return@filter false

                    val isSupportedModel = name.contains("LX") ||
                        name.contains("HASSELBLAD") ||
                        name.contains("LEICA") ||
                        name.contains("MAMIYA") ||
                        name.contains("F-1") ||
                        name.contains("EXAKTA") ||
                        name.contains("NIKON F2") ||
                        name.contains("NIKON F3") ||
                        name.contains("NIKON F4") ||
                        name.contains("NIKON F5") ||
                        camera.type.name.contains("MEDIUM_FORMAT", ignoreCase = true) ||
                        camera.type.name.contains("RANGEFINDER", ignoreCase = true) ||
                        camera.type.name.contains("TLR", ignoreCase = true) ||
                        camera.type.name.contains("LARGE_FORMAT", ignoreCase = true)

                    isSupportedModel
                }
                _uiState.update { it.copy(cameras = interchangeableFinderCameras) }
            }
        }
        loadViewfinder()
    }

    fun setViewfinderId(id: Long?) {
        if (viewfinderId == id) return
        viewfinderId = id
        savedStateHandle["viewfinderId"] = id
        loadViewfinder()
    }

    private fun loadViewfinder() {
        val id = viewfinderId ?: return
        viewModelScope.launch {
            val viewfinder = repository.getViewfinderById(id).firstOrNull()
            viewfinder?.let { vf ->
                val crossRefs = repository.allCameraViewfinderCrossRefs.firstOrNull() ?: emptyList()
                val associatedCameraIds = crossRefs.filter { it.viewfinderId == vf.id }.map { it.cameraId }.toSet()
                _uiState.update { state ->
                    state.copy(
                        name = savedStateHandle.get<String>("name") ?: vf.name,
                        modelNumber = savedStateHandle.get<String>("modelNumber") ?: vf.modelNumber,
                        selectedCameraIds = associatedCameraIds
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        savedStateHandle["name"] = value
        _uiState.update { it.copy(name = value) }
    }

    fun onModelNumberChange(value: String) {
        savedStateHandle["modelNumber"] = value
        _uiState.update { it.copy(modelNumber = value) }
    }

    fun onTypeChange(value: String) {
        savedStateHandle["type"] = value
        _uiState.update { it.copy(type = value) }
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

    fun saveViewfinder(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val viewfinder = Viewfinder(
                id = viewfinderId ?: 0,
                name = state.name.trim(),
                modelNumber = state.modelNumber.trim()
            )

            if (viewfinderId == null) {
                repository.insertViewfinder(viewfinder, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            } else {
                repository.updateViewfinder(viewfinder, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            }
            onSuccess()
        }
    }
}
