package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.CameraSpec
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.Film
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.GearDatabase
import com.fotologue.core.data.Lens
import com.fotologue.core.data.LensSpec
import com.fotologue.core.data.SettingsRepository
import com.fotologue.core.data.preset.MasterPresetEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditCameraUiState(
    val make: String = "Pentax",
    val name: String = "",
    val modelNumber: String = "",
    val serialNumber: String = "",
    val type: CameraType = CameraType.FILM_SLR,
    val mount: String = "",
    val shutterSpeeds: String = "",
    val isHalfFrame: Boolean = false,
    val format: String = Film.FORMAT_35MM,
    val triggerDeviceId: String = "",
    val fixedLens: LensSpec? = null,
    val isNameValid: Boolean = true,
    val searchResults: List<CameraSpec> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class AddEditCameraViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddEditCameraUiState(
            name = savedStateHandle["name"] ?: "",
            modelNumber = savedStateHandle["modelNumber"] ?: "",
            serialNumber = savedStateHandle["serialNumber"] ?: ""
        )
    )
    val uiState: StateFlow<AddEditCameraUiState> = _uiState.asStateFlow()

    var cameraId: Long? = null
        private set

    fun setCameraId(id: Long?) {
        cameraId = id
        if (id != null) {
            loadCamera()
        }
    }

    private fun loadCamera() {
        val id = cameraId ?: return
        viewModelScope.launch {
            val camera = repository.getCameraById(id).first()
            camera?.let { c ->
                _uiState.update { 
                    it.copy(
                        name = c.name,
                        modelNumber = c.modelNumber,
                        serialNumber = c.serialNumber,
                        type = c.type,
                        mount = c.mount,
                        shutterSpeeds = c.shutterSpeeds.joinToString(", "),
                        isHalfFrame = c.isHalfFrame,
                        format = c.format,
                        triggerDeviceId = c.triggerDeviceId ?: ""
                    )
                }
            }
        }
    }

    fun onMakeChange(make: String) {
        val matchingPresets = MasterPresetEngine.getPresetsByCategory("CAMERA")
            .filter { it.brand.equals(make.trim(), ignoreCase = true) }
        
        val distinctTypes = matchingPresets.mapNotNull { preset ->
            val subCat = preset.subCategory ?: preset.attributes["type"]
            CameraType.entries.find { it.displayName.equals(subCat, ignoreCase = true) }
        }.distinct()

        val autoSelectedType = if (distinctTypes.size == 1) distinctTypes.first() else null

        _uiState.update { state ->
            val newType = autoSelectedType ?: state.type
            val newFormat = if (newType == CameraType.MEDIUM_FORMAT || newType == CameraType.TLR) Film.FORMAT_120 else state.format
            state.copy(
                make = make,
                type = newType,
                format = newFormat,
                mount = if (!newType.isInterchangeable) "Fixed" else state.mount
            )
        }
        savedStateHandle["make"] = make
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, isNameValid = name.isNotBlank()) }
        savedStateHandle["name"] = name
    }

    fun onModelNumberChange(modelNumber: String) {
        _uiState.update { it.copy(modelNumber = modelNumber) }
        savedStateHandle["modelNumber"] = modelNumber
    }

    fun onSerialNumberChange(serialNumber: String) {
        _uiState.update { it.copy(serialNumber = serialNumber) }
        savedStateHandle["serialNumber"] = serialNumber
    }

    fun onTypeChange(type: CameraType) {
        _uiState.update { state ->
            val newFormat = if (type == CameraType.MEDIUM_FORMAT || type == CameraType.TLR) Film.FORMAT_120 else state.format
            state.copy(
                type = type,
                format = newFormat,
                mount = if (!type.isInterchangeable) "Fixed" else state.mount
            )
        }
    }

    fun onMountChange(mount: String) {
        _uiState.update { it.copy(mount = mount) }
    }

    fun onShutterSpeedsChange(shutterSpeeds: String) {
        _uiState.update { it.copy(shutterSpeeds = shutterSpeeds) }
    }

    fun onIsHalfFrameChange(isHalfFrame: Boolean) {
        _uiState.update { it.copy(isHalfFrame = isHalfFrame) }
    }

    fun onFormatChange(format: String) {
        _uiState.update { it.copy(format = format) }
    }

    fun onTriggerDeviceIdChange(id: String) {
        _uiState.update { it.copy(triggerDeviceId = id) }
        savedStateHandle["triggerDeviceId"] = id
    }

    fun searchDatabase(query: String) {
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
        _uiState.update { 
            val format = if (spec.type == CameraType.MEDIUM_FORMAT || spec.type == CameraType.TLR) Film.FORMAT_120 else it.format
            it.copy(
                name = spec.name,
                type = spec.type,
                mount = spec.mount,
                modelNumber = spec.modelNumber,
                shutterSpeeds = spec.shutterSpeeds.joinToString(", "),
                format = format,
                fixedLens = spec.fixedLens,
                searchResults = emptyList(),
                isNameValid = true
            )
        }
    }

    fun saveCamera(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(isNameValid = false) }
            return
        }

        viewModelScope.launch {
            val existingCameras = repository.allCameras.first()
            val baseName = currentState.name.trim()
            
            val nameExists = existingCameras.any { c -> c.id != cameraId && c.name.equals(baseName, ignoreCase = true) }
            
            val finalName = if (nameExists && cameraId == null) {
                val matchingNames: List<String> = existingCameras
                    .map { c -> c.name }
                    .filter { n -> n.equals(baseName, ignoreCase = true) || n.startsWith("$baseName (", ignoreCase = true) }
                
                var maxCount = 1
                for (name in matchingNames) {
                    if (name.equals(baseName, ignoreCase = true)) {
                        continue
                    }
                    val suffix = name.substringAfterLast("(", "").substringBefore(")", "").toIntOrNull()
                    if (suffix != null && suffix >= maxCount) {
                        maxCount = suffix + 1
                    }
                }
                "$baseName ($maxCount)"
            } else {
                baseName
            }

            val shutterSpeedList = currentState.shutterSpeeds
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val camera = Camera(
                id = cameraId ?: 0,
                name = finalName,
                modelNumber = currentState.modelNumber.trim(),
                serialNumber = currentState.serialNumber.trim(),
                type = currentState.type,
                mount = currentState.mount.trim(),
                shutterSpeeds = shutterSpeedList,
                isHalfFrame = currentState.isHalfFrame,
                format = currentState.format,
                triggerDeviceId = currentState.triggerDeviceId.ifBlank { null }
            )

            val newCamId = if (cameraId == null) {
                val newId = repository.insertCamera(camera)
                settingsRepository.setLocalFoundationChangesPending(true)
                newId
            } else {
                repository.updateCamera(camera)
                settingsRepository.setLocalFoundationChangesPending(true)
                cameraId!!
            }

            // Auto-populate fixed lens entry if camera has a fixedLens spec or is a non-interchangeable type
            val fixedSpec = currentState.fixedLens
            if (fixedSpec != null) {
                val fixedLensObj = Lens(
                    make = fixedSpec.make,
                    model = fixedSpec.model,
                    modelNumber = fixedSpec.modelNumber,
                    type = fixedSpec.type,
                    mount = "Fixed",
                    filterSize = fixedSpec.filterSize,
                    isStabilized = fixedSpec.isStabilized,
                    minFocusDistance = fixedSpec.minFocusDistance,
                    maxMagnification = fixedSpec.maxMagnification,
                    minAperture = fixedSpec.minAperture,
                    maxAperture = fixedSpec.maxAperture,
                    minFocalLength = fixedSpec.minFocalLength,
                    maxFocalLength = fixedSpec.maxFocalLength
                )
                repository.insertLens(fixedLensObj, listOf(newCamId))
            } else if (!currentState.type.isInterchangeable || currentState.mount.equals("Fixed", ignoreCase = true)) {
                // Generates a generic fixed taking lens if the preset didn't define one
                val genericFixedLens = Lens(
                    make = currentState.name.substringBefore(" "),
                    model = "Taking Lens",
                    mount = "Fixed",
                    type = "Prime",
                    maxAperture = 3.5,
                    minAperture = 22.0,
                    minFocalLength = 80,
                    maxFocalLength = 80,
                    filterSize = 0,
                    isStabilized = false,
                    minFocusDistance = 1.0,
                    maxMagnification = 0.0
                )
                repository.insertLens(genericFixedLens, listOf(newCamId))
            }

            onSuccess()
        }
    }
}
