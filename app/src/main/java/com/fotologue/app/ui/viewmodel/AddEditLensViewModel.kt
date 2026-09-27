package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.CameraLensCrossRef
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.GearDatabase
import com.fotologue.core.data.Lens
import com.fotologue.core.data.LensSpec
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditLensUiState(
    val make: String = "",
    val model: String = "",
    val modelNumber: String = "",
    val type: String = "Prime",
    val mount: String = "",
    val filterSize: String = "",
    val isStabilized: Boolean = false,
    val minFocusDistance: String = "",
    val maxMagnification: String = "",
    val minAperture: String = "",
    val maxAperture: String = "",
    val minFocalLength: String = "",
    val maxFocalLength: String = "",
    val selectedCameraIds: Set<Long> = emptySet(),
    val searchResults: List<LensSpec> = emptyList(),
    val showSearchResults: Boolean = false,
    val isSearching: Boolean = false,
    val isInterchangeable: Boolean = true,
    val cameras: List<Camera> = emptyList(),
    val isCameraSelectionValid: Boolean = true
)

@HiltViewModel
class AddEditLensViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditLensUiState(
        make = savedStateHandle.get<String>("make") ?: "",
        model = savedStateHandle.get<String>("model") ?: "",
        modelNumber = savedStateHandle.get<String>("modelNumber") ?: "",
        type = savedStateHandle.get<String>("type") ?: "Prime",
        mount = savedStateHandle.get<String>("mount") ?: "",
        filterSize = savedStateHandle.get<String>("filterSize") ?: "",
        isStabilized = savedStateHandle.get<Boolean>("isStabilized") ?: false,
        minFocusDistance = savedStateHandle.get<String>("minFocusDistance") ?: "",
        maxMagnification = savedStateHandle.get<String>("maxMagnification") ?: "",
        minAperture = savedStateHandle.get<String>("minAperture") ?: "",
        maxAperture = savedStateHandle.get<String>("maxAperture") ?: "",
        minFocalLength = savedStateHandle.get<String>("minFocalLength") ?: "",
        maxFocalLength = savedStateHandle.get<String>("maxFocalLength") ?: "",
        selectedCameraIds = savedStateHandle.get<LongArray>("selectedCameraIds")?.toSet() ?: emptySet()
    ))
    val uiState: StateFlow<AddEditLensUiState> = _uiState.asStateFlow()

    var lensId: Long? = savedStateHandle.get<Long>("lensId")
        private set

    private var cameraLensCrossRefs: List<CameraLensCrossRef> = emptyList()

    init {
        viewModelScope.launch {
            repository.allCameras.collect { camerasList ->
                _uiState.update { it.copy(cameras = camerasList) }
            }
        }
        viewModelScope.launch {
            repository.allCameraLensCrossRefs.collect { cameraLensCrossRefs = it }
        }
    }

    fun setLensId(id: Long?) {
        if (lensId == id) return
        lensId = id
        savedStateHandle["lensId"] = id
        loadLens()
    }

    private fun loadLens() {
        val id = lensId ?: return
        viewModelScope.launch {
            val lens = repository.getLensById(id).firstOrNull()
            val associatedCameras = repository.getCamerasForLens(id).firstOrNull() ?: emptyList()
            lens?.let {
                _uiState.update { state ->
                    state.copy(
                        selectedCameraIds = savedStateHandle.get<LongArray>("selectedCameraIds")?.toSet() ?: associatedCameras.map { it.id }.toSet(),
                        make = savedStateHandle.get<String>("make") ?: it.make,
                        model = savedStateHandle.get<String>("model") ?: it.model,
                        modelNumber = savedStateHandle.get<String>("modelNumber") ?: it.modelNumber,
                        type = savedStateHandle.get<String>("type") ?: (if (it.type in Lens.TYPES) it.type else "Prime"),
                        mount = savedStateHandle.get<String>("mount") ?: it.mount,
                        filterSize = savedStateHandle.get<String>("filterSize") ?: it.filterSize.toString(),
                        isStabilized = savedStateHandle.get<Boolean>("isStabilized") ?: it.isStabilized,
                        minFocusDistance = savedStateHandle.get<String>("minFocusDistance") ?: it.minFocusDistance.toString(),
                        maxMagnification = savedStateHandle.get<String>("maxMagnification") ?: it.maxMagnification.toString(),
                        minAperture = savedStateHandle.get<String>("minAperture") ?: it.minAperture.toString(),
                        maxAperture = savedStateHandle.get<String>("maxAperture") ?: it.maxAperture.toString(),
                        minFocalLength = savedStateHandle.get<String>("minFocalLength") ?: it.minFocalLength.toString(),
                        maxFocalLength = savedStateHandle.get<String>("maxFocalLength") ?: it.maxFocalLength.toString(),
                        isInterchangeable = it.mount != "Fixed"
                    )
                }
            }
        }
    }

    fun preSelectCameraId(cameraId: Long) {
        _uiState.update { state ->
            val updated = state.selectedCameraIds + cameraId
            savedStateHandle["selectedCameraIds"] = updated.toLongArray()
            state.copy(
                selectedCameraIds = updated,
                isCameraSelectionValid = true
            )
        }
    }

    fun onCameraToggled(cameraId: Long) {
        _uiState.update { state ->
            val camera = state.cameras.find { it.id == cameraId }
            
            val currentIds = state.selectedCameraIds
            val newIds = if (currentIds.contains(cameraId)) {
                currentIds - cameraId
            } else {
                if (camera?.type?.isInterchangeable == false) {
                    setOf(cameraId)
                } else {
                    val hasFixed = currentIds.any { id ->
                        state.cameras.find { it.id == id }?.type?.isInterchangeable == false
                    }
                    if (hasFixed) setOf(cameraId) else currentIds + cameraId
                }
            }
            
            val isInterchangeable = newIds.isEmpty() || newIds.all { id ->
                state.cameras.find { it.id == id }?.type?.isInterchangeable ?: true
            }
            
            val mount = if (!isInterchangeable) "Fixed" else state.mount
            
            savedStateHandle["selectedCameraIds"] = newIds.toLongArray()
            savedStateHandle["mount"] = mount
            
            state.copy(
                selectedCameraIds = newIds,
                isInterchangeable = isInterchangeable,
                mount = mount,
                isCameraSelectionValid = true
            )
        }
    }

    fun onSearch(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), showSearchResults = false, isSearching = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            val results = GearDatabase.searchLenses(query)
            _uiState.update { it.copy(searchResults = results, showSearchResults = results.isNotEmpty(), isSearching = false) }
        }
    }

    fun onSelectSpec(spec: LensSpec) {
        val type = Lens.TYPES.find { it.equals(spec.type, ignoreCase = true) } 
            ?: Lens.TYPES.find { spec.type.contains(it, ignoreCase = true) }
            ?: "Prime"
        
        savedStateHandle["make"] = spec.make
        savedStateHandle["model"] = spec.model
        savedStateHandle["modelNumber"] = spec.modelNumber
        savedStateHandle["type"] = type
        savedStateHandle["mount"] = spec.mount
        savedStateHandle["filterSize"] = spec.filterSize.toString()
        savedStateHandle["isStabilized"] = spec.isStabilized
        savedStateHandle["minFocusDistance"] = spec.minFocusDistance.toString()
        savedStateHandle["maxMagnification"] = spec.maxMagnification.toString()
        savedStateHandle["minAperture"] = spec.minAperture.toString()
        savedStateHandle["maxAperture"] = spec.maxAperture.toString()
        savedStateHandle["minFocalLength"] = spec.minFocalLength.toString()
        savedStateHandle["maxFocalLength"] = spec.maxFocalLength.toString()

        _uiState.update { state ->
            state.copy(
                make = spec.make,
                model = spec.model,
                modelNumber = spec.modelNumber,
                type = type,
                mount = spec.mount,
                filterSize = spec.filterSize.toString(),
                isStabilized = spec.isStabilized,
                minFocusDistance = spec.minFocusDistance.toString(),
                maxMagnification = spec.maxMagnification.toString(),
                minAperture = spec.minAperture.toString(),
                maxAperture = spec.maxAperture.toString(),
                minFocalLength = spec.minFocalLength.toString(),
                maxFocalLength = spec.maxFocalLength.toString(),
                showSearchResults = false
            )
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
    fun onTypeChange(value: String) { 
        savedStateHandle["type"] = value
        _uiState.update { it.copy(type = value) } 
    }
    fun onMountChange(value: String) { 
        savedStateHandle["mount"] = value
        _uiState.update { it.copy(mount = value) } 
    }
    fun onFilterSizeChange(value: String) { 
        savedStateHandle["filterSize"] = value
        _uiState.update { it.copy(filterSize = value) } 
    }
    fun onIsStabilizedChange(value: Boolean) { 
        savedStateHandle["isStabilized"] = value
        _uiState.update { it.copy(isStabilized = value) } 
    }
    fun onMinFocusDistanceChange(value: String) { 
        savedStateHandle["minFocusDistance"] = value
        _uiState.update { it.copy(minFocusDistance = value) } 
    }
    fun onMaxMagnificationChange(value: String) { 
        savedStateHandle["maxMagnification"] = value
        _uiState.update { it.copy(maxMagnification = value) } 
    }
    fun onMinApertureChange(value: String) { 
        savedStateHandle["minAperture"] = value
        _uiState.update { it.copy(minAperture = value) } 
    }
    fun onMaxApertureChange(value: String) { 
        savedStateHandle["maxAperture"] = value
        _uiState.update { it.copy(maxAperture = value) } 
    }
    fun onMinFocalLengthChange(value: String) { 
        savedStateHandle["minFocalLength"] = value
        _uiState.update { it.copy(minFocalLength = value) } 
    }
    fun onMaxFocalLengthChange(value: String) { 
        savedStateHandle["maxFocalLength"] = value
        _uiState.update { it.copy(maxFocalLength = value) } 
    }
    fun onModelNumberChange(value: String) { 
        savedStateHandle["modelNumber"] = value
        _uiState.update { it.copy(modelNumber = value) } 
    }

    fun saveLens(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validation: If cameras exist, at least one must be selected
        if (state.cameras.isNotEmpty() && state.selectedCameraIds.isEmpty()) {
            _uiState.update { it.copy(isCameraSelectionValid = false) }
            return
        }

        viewModelScope.launch {
            val finalMinFocal = state.minFocalLength.toIntOrNull() ?: 0
            val finalMaxFocal = if (state.type == "Prime") finalMinFocal else (state.maxFocalLength.toIntOrNull() ?: finalMinFocal)

            val a1 = state.minAperture.toDoubleOrNull() ?: 0.0
            val a2 = state.maxAperture.toDoubleOrNull() ?: 0.0

            val lens = Lens(
                id = lensId ?: 0,
                make = state.make.trim(),
                model = state.model.trim(),
                modelNumber = state.modelNumber.trim(),
                type = if (state.type in Lens.TYPES) state.type else "Prime",
                mount = if (state.isInterchangeable) state.mount.trim() else "Fixed",
                filterSize = state.filterSize.toIntOrNull() ?: 0,
                isStabilized = state.isStabilized,
                minFocusDistance = state.minFocusDistance.toDoubleOrNull() ?: 0.0,
                maxMagnification = state.maxMagnification.toDoubleOrNull() ?: 0.0,
                minAperture = minOf(a1, a2),
                maxAperture = maxOf(a1, a2),
                minFocalLength = minOf(finalMinFocal, finalMaxFocal),
                maxFocalLength = maxOf(finalMinFocal, finalMaxFocal)
            )

            if (lensId == null) {
                repository.insertLens(lens, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            } else {
                repository.updateLens(lens, state.selectedCameraIds.toList())
                settingsRepository.setLocalFoundationChangesPending(true)
            }
            onSuccess()
        }
    }
}
