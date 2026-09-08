package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.Camera
import com.fotologue.app.data.CameraSpec
import com.fotologue.app.data.CameraType
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.GearDatabase
import com.fotologue.app.data.Lens
import com.fotologue.app.data.LensSpec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditCameraUiState(
    val name: String = "",
    val modelNumber: String = "",
    val type: CameraType = CameraType.COMPACT,
    val mount: String = "",
    val shutterSpeeds: String = "",
    val isHalfFrame: Boolean = false,
    val fixedLens: LensSpec? = null,
    val isNameValid: Boolean = true,
    val searchResults: List<CameraSpec> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class AddEditCameraViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCameraUiState(
        name = savedStateHandle["name"] ?: "",
        modelNumber = savedStateHandle["modelNumber"] ?: ""
    ))
    val uiState: StateFlow<AddEditCameraUiState> = _uiState.asStateFlow()

    var cameraId: Long? = null
        private set

    fun setCameraId(id: Long?) {
        if (cameraId == id) return
        cameraId = id
        loadCamera()
    }

    private fun loadCamera() {
        val id = cameraId ?: return
        viewModelScope.launch {
            repository.getCameraById(id).collect { camera ->
                camera?.let {
                    _uiState.update { it.copy(
                        name = camera.name,
                        modelNumber = camera.modelNumber,
                        type = camera.type,
                        mount = camera.mount,
                        shutterSpeeds = camera.shutterSpeeds.joinToString(", "),
                        isHalfFrame = camera.isHalfFrame
                    ) }
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, isNameValid = newName.isNotBlank()) }
        savedStateHandle["name"] = newName
        searchDatabase(newName)
    }

    fun onModelNumberChange(newModelNumber: String) {
        _uiState.update { it.copy(modelNumber = newModelNumber) }
        savedStateHandle["modelNumber"] = newModelNumber
        searchDatabase(newModelNumber)
    }

    fun onTypeChange(newType: CameraType) {
        _uiState.update { it.copy(type = newType) }
        if (!newType.isInterchangeable) {
            _uiState.update { it.copy(mount = "Fixed") }
        }
    }

    fun onMountChange(newMount: String) {
        _uiState.update { it.copy(mount = newMount) }
    }

    fun onShutterSpeedsChange(speeds: String) {
        _uiState.update { it.copy(shutterSpeeds = speeds) }
    }

    fun onIsHalfFrameChange(isHalfFrame: Boolean) {
        _uiState.update { it.copy(isHalfFrame = isHalfFrame) }
    }

    private fun searchDatabase(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            val results = GearDatabase.searchCameras(query)
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun applySpec(spec: CameraSpec) {
        _uiState.update { it.copy(
            name = spec.name,
            modelNumber = spec.modelNumber,
            type = spec.type,
            mount = spec.mount,
            shutterSpeeds = spec.shutterSpeeds.joinToString(", "),
            fixedLens = spec.fixedLens,
            searchResults = emptyList()
        ) }
    }

    fun saveCamera(onComplete: () -> Unit) {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(isNameValid = false) }
            return
        }

        viewModelScope.launch {
            val camera = Camera(
                id = cameraId ?: 0,
                name = _uiState.value.name.trim(),
                modelNumber = _uiState.value.modelNumber.trim(),
                type = _uiState.value.type,
                mount = _uiState.value.mount.trim(),
                shutterSpeeds = _uiState.value.shutterSpeeds.split(",").map { it.trim() }.filter { it.isNotBlank() },
                isHalfFrame = _uiState.value.isHalfFrame
            )
            val finalCameraId = if (cameraId == null) {
                repository.insertCamera(camera)
            } else {
                repository.updateCamera(camera)
                cameraId!!
            }

            // Auto-create lens for fixed-lens cameras if it's a new camera and we have the spec
            if (cameraId == null && !_uiState.value.type.isInterchangeable && _uiState.value.fixedLens != null) {
                val spec = _uiState.value.fixedLens!!
                val lens = Lens(
                    make = spec.make,
                    model = spec.model,
                    modelNumber = spec.modelNumber,
                    type = spec.type,
                    mount = spec.mount,
                    filterSize = spec.filterSize,
                    isStabilized = spec.isStabilized,
                    minFocusDistance = spec.minFocusDistance,
                    maxMagnification = spec.maxMagnification,
                    minAperture = spec.minAperture,
                    maxAperture = spec.maxAperture,
                    minFocalLength = spec.minFocalLength,
                    maxFocalLength = spec.maxFocalLength
                )
                repository.insertLens(lens, listOf(finalCameraId))
            }

            onComplete()
        }
    }
}
