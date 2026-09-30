package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.Teleconverter
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTeleconverterUiState(
    val name: String = "",
    val magnification: String = "1.4",
    val lightLossStops: String = "1.0",
    val cameras: List<Camera> = emptyList(),
    val selectedCameraIds: Set<Long> = emptySet()
)

@HiltViewModel
class AddEditTeleconverterViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTeleconverterUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        magnification = savedStateHandle.get<String>("magnification") ?: "1.4",
        lightLossStops = savedStateHandle.get<String>("lightLossStops") ?: "1.0"
    ))
    val uiState: StateFlow<AddEditTeleconverterUiState> = _uiState.asStateFlow()

    var teleconverterId: Long? = savedStateHandle.get<Long>("teleconverterId")
        private set

    init {
        viewModelScope.launch {
            repository.allCameras.collect { cameras ->
                val interchangeable = cameras.filter { it.type.isInterchangeable && it.mount != "Fixed" }
                _uiState.update { it.copy(cameras = interchangeable) }
            }
        }
        loadTeleconverter()
    }

    fun setTeleconverterId(id: Long?) {
        if (teleconverterId == id) return
        teleconverterId = id
        savedStateHandle["teleconverterId"] = id
        loadTeleconverter()
    }

    private fun loadTeleconverter() {
        val id = teleconverterId ?: return
        viewModelScope.launch {
            val tc = repository.getTeleconverterById(id).firstOrNull()
            tc?.let { item ->
                val crossRefs = repository.allCameraTeleconverterCrossRefs.firstOrNull() ?: emptyList()
                val associatedCameraIds = crossRefs.filter { it.teleconverterId == item.id }.map { it.cameraId }.toSet()
                _uiState.update { state ->
                    state.copy(
                        name = savedStateHandle.get<String>("name") ?: item.name,
                        magnification = savedStateHandle.get<String>("magnification") ?: item.magnification.toString(),
                        lightLossStops = savedStateHandle.get<String>("lightLossStops") ?: item.lightLossStops.toString(),
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

    fun onMagnificationChange(value: String) {
        savedStateHandle["magnification"] = value
        _uiState.update { it.copy(magnification = value) }
    }

    fun onLightLossStopsChange(value: String) {
        savedStateHandle["lightLossStops"] = value
        _uiState.update { it.copy(lightLossStops = value) }
    }

    fun onCameraToggled(cameraId: Long) {
        _uiState.update { state ->
            val current = state.selectedCameraIds
            val updated = if (current.contains(cameraId)) current - cameraId else current + cameraId
            state.copy(selectedCameraIds = updated)
        }
    }

    fun saveTeleconverter(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val tc = Teleconverter(
                id = teleconverterId ?: 0,
                name = state.name.trim(),
                magnification = state.magnification.toDoubleOrNull() ?: 1.4,
                lightLossStops = state.lightLossStops.toDoubleOrNull() ?: 1.0
            )

            if (teleconverterId == null) {
                repository.insertTeleconverter(tc, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            } else {
                repository.updateTeleconverter(tc, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            }
            onSuccess()
        }
    }
}
