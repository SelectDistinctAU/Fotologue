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
                    camera.name.contains("Pentax LX", ignoreCase = true) ||
                    camera.name.contains("Nikon F", ignoreCase = true) ||
                    camera.name.contains("Hasselblad", ignoreCase = true) ||
                    camera.name.contains("Leica", ignoreCase = true) ||
                    camera.name.contains("Mamiya", ignoreCase = true) ||
                    camera.name.contains("Canon F-1", ignoreCase = true) ||
                    camera.name.contains("Exakta", ignoreCase = true) ||
                    (camera.type.isInterchangeable && camera.mount != "Fixed")
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
