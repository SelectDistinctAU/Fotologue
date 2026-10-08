package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.*
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditCameraKitUiState(
    val name: String = "",
    val cameraId: Long? = null,
    val lensId: Long? = null,
    val filmId: Long? = null,
    val teleconverterId: Long? = null,
    val filterId: Long? = null,
    val flashId: Long? = null,
    val viewfinderId: Long? = null,
    val isNameValid: Boolean = true,
    val isCameraValid: Boolean = true,
    val isLensValid: Boolean = true,
    val isFilmValid: Boolean = true,
    val allCameras: List<Camera> = emptyList(),
    val filteredLenses: List<Lens> = emptyList(),
    val filteredViewfinders: List<Viewfinder> = emptyList(),
    val filteredTeleconverters: List<Teleconverter> = emptyList(),
    val filteredFilters: List<Filter> = emptyList(),
    val filteredFlashes: List<Flash> = emptyList(),
    val allFilms: List<Film> = emptyList(),
    val isTeleconverterVisible: Boolean = false,
    val isFilterVisible: Boolean = false,
    val isFlashVisible: Boolean = false,
    val isViewfinderVisible: Boolean = false
)

@HiltViewModel
class AddEditCameraKitViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditCameraKitUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        cameraId = savedStateHandle.get<Long>("cameraId"),
        lensId = savedStateHandle.get<Long>("lensId"),
        filmId = savedStateHandle.get<Long>("filmId"),
        teleconverterId = savedStateHandle.get<Long>("teleconverterId"),
        filterId = savedStateHandle.get<Long>("filterId"),
        flashId = savedStateHandle.get<Long>("flashId"),
        viewfinderId = savedStateHandle.get<Long>("viewfinderId")
    ))
    val uiState: StateFlow<AddEditCameraKitUiState> = _uiState.asStateFlow()

    private val allLenses = MutableStateFlow<List<Lens>>(emptyList())
    private val crossRefs = MutableStateFlow<List<CameraLensCrossRef>>(emptyList())

    var kitId: Long? = savedStateHandle.get<Long>("kitId")
        private set

    fun setKitId(id: Long?) {
        if (kitId == id) return
        kitId = id
        savedStateHandle["kitId"] = id
        loadKit()
    }

    init {
        viewModelScope.launch {
            combine(
                repository.allCameras,
                allLenses,
                crossRefs
            ) { cameras, lenses, refs ->
                // Rule 1: Only suggest cameras that have at least 1 registered lens or are fixed-lens bodies
                cameras.filter { camera ->
                    val hasAssociatedLens = refs.any { it.cameraId == camera.id }
                    val hasMountLens = camera.mount.isNotBlank() && lenses.any { it.mount.equals(camera.mount, ignoreCase = true) }
                    val isFixedLens = !camera.type.isInterchangeable || camera.mount.equals("Fixed", ignoreCase = true)
                    hasAssociatedLens || hasMountLens || isFixedLens
                }
            }.collect { eligibleCameras ->
                _uiState.update { it.copy(allCameras = eligibleCameras) }
                updateFilteredLenses()
            }
        }
        viewModelScope.launch {
            repository.allLenses.collect { lenses -> 
                allLenses.value = lenses
                updateFilteredLenses()
            }
        }
        viewModelScope.launch {
            repository.allCameraLensCrossRefs.collect { refs ->
                crossRefs.value = refs
                updateFilteredLenses()
            }
        }
        viewModelScope.launch {
            combine(
                repository.allViewfinders,
                repository.allCameraViewfinderCrossRefs,
                _uiState.map { it.cameraId }.distinctUntilChanged()
            ) { viewfinders, refs, cameraId ->
                val filtered = if (cameraId == null) {
                    viewfinders
                } else {
                    val associatedViewfinderIds = refs.filter { it.cameraId == cameraId }.map { it.viewfinderId }.toSet()
                    viewfinders.filter { vf ->
                        associatedViewfinderIds.contains(vf.id) || refs.none { it.viewfinderId == vf.id }
                    }
                }
                _uiState.update { it.copy(
                    filteredViewfinders = filtered,
                    isViewfinderVisible = viewfinders.isNotEmpty()
                ) }
            }.collect {}
        }
        viewModelScope.launch {
            combine(
                repository.allTeleconverters,
                repository.allCameraTeleconverterCrossRefs,
                _uiState.map { it.cameraId }.distinctUntilChanged()
            ) { tcs, refs, cameraId ->
                val filtered = if (cameraId == null) {
                    tcs
                } else {
                    val associatedIds = refs.filter { it.cameraId == cameraId }.map { it.teleconverterId }.toSet()
                    tcs.filter { tc -> associatedIds.contains(tc.id) }
                }
                _uiState.update { it.copy(
                    filteredTeleconverters = filtered,
                    isTeleconverterVisible = filtered.isNotEmpty(),
                    teleconverterId = if (filtered.isEmpty()) null else it.teleconverterId
                ) }
            }.collect {}
        }
        viewModelScope.launch {
            combine(
                repository.allFilters,
                repository.allCameraFilterCrossRefs,
                _uiState.map { it.cameraId }.distinctUntilChanged()
            ) { filterList, refs, cameraId ->
                val filtered = if (cameraId == null) {
                    filterList
                } else {
                    val associatedIds = refs.filter { it.cameraId == cameraId }.map { it.filterId }.toSet()
                    filterList.filter { f -> associatedIds.contains(f.id) || refs.none { it.filterId == f.id } }
                }
                _uiState.update { it.copy(
                    filteredFilters = filtered,
                    isFilterVisible = filterList.isNotEmpty()
                ) }
            }.collect {}
        }
        viewModelScope.launch {
            combine(
                repository.allFlashes,
                repository.allCameraFlashCrossRefs,
                _uiState.map { it.cameraId }.distinctUntilChanged()
            ) { flashes, refs, cameraId ->
                val filtered = if (cameraId == null) {
                    flashes
                } else {
                    val associatedIds = refs.filter { it.cameraId == cameraId }.map { it.flashId }.toSet()
                    flashes.filter { fl -> associatedIds.contains(fl.id) || refs.none { it.flashId == fl.id } }
                }
                _uiState.update { it.copy(
                    filteredFlashes = filtered,
                    isFlashVisible = flashes.isNotEmpty()
                ) }
            }.collect {}
        }
        viewModelScope.launch {
            combine(
                repository.allFilms,
                repository.allCameras,
                _uiState.map { it.cameraId }.distinctUntilChanged()
            ) { films, cameras, cameraId ->
                val camera = cameras.find { it.id == cameraId }
                if (camera == null) {
                    films
                } else {
                    val is120Cam = camera.format.contains("120", ignoreCase = true) || camera.type == CameraType.TLR || camera.type == CameraType.MEDIUM_FORMAT
                    films.filter { film ->
                        if (is120Cam) {
                            film.format.contains("120", ignoreCase = true)
                        } else {
                            val normCam = camera.format.trim().lowercase()
                            val normFilm = film.format.trim().lowercase()
                            normCam == normFilm || (normCam.contains("35mm") && normFilm.contains("35mm"))
                        }
                    }
                }
            }.collect { filteredFilms ->
                _uiState.update { state ->
                    val autoFilmId = if (state.filmId == null && filteredFilms.size == 1) filteredFilms[0].id else if (filteredFilms.any { it.id == state.filmId }) state.filmId else null
                    state.copy(allFilms = filteredFilms, filmId = autoFilmId)
                }
            }
        }
        loadKit()
    }

    private fun loadKit() {
        val kitId = this.kitId ?: return
        viewModelScope.launch {
            repository.getCameraKitById(kitId).collect { kit ->
                kit?.let {
                    _uiState.update { it.copy(
                        name = savedStateHandle.get<String>("name") ?: kit.name,
                        cameraId = savedStateHandle.get<Long>("cameraId") ?: kit.cameraId,
                        lensId = savedStateHandle.get<Long>("lensId") ?: kit.lensId,
                        filmId = savedStateHandle.get<Long>("filmId") ?: kit.filmId,
                        teleconverterId = savedStateHandle.get<Long>("teleconverterId") ?: kit.teleconverterId,
                        filterId = savedStateHandle.get<Long>("filterId") ?: kit.filterId,
                        flashId = savedStateHandle.get<Long>("flashId") ?: kit.flashId,
                        viewfinderId = savedStateHandle.get<Long>("viewfinderId") ?: kit.viewfinderId
                    ) }
                    updateFilteredLenses()
                }
            }
        }
    }

    private fun updateFilteredLenses() {
        val cameraId = _uiState.value.cameraId
        val cameras = _uiState.value.allCameras
        val lenses = allLenses.value
        val refs = crossRefs.value

        if (cameraId == null) {
            _uiState.update { it.copy(filteredLenses = lenses) }
            return
        }

        val camera = cameras.find { it.id == cameraId }
        val associatedLensIds = refs.filter { it.cameraId == cameraId }.map { it.lensId }.toSet()

        val filtered = lenses.filter { lens ->
            val isDirectlyAssociated = associatedLensIds.contains(lens.id)
            if (isDirectlyAssociated) return@filter true

            if (camera != null && camera.mount.isNotBlank() && lens.mount.isNotBlank() && camera.mount.equals(lens.mount, ignoreCase = true)) {
                return@filter true
            }
            false
        }

        // Rule 2: Single-Lens Auto-Default Rule: If exactly 1 compatible lens exists, auto-select it!
        val autoSelectedLensId = if (filtered.size == 1) filtered.first().id else _uiState.value.lensId

        _uiState.update { it.copy(
            filteredLenses = filtered,
            lensId = autoSelectedLensId,
            isLensValid = autoSelectedLensId != null || it.isLensValid
        ) }
    }

    fun onNameChange(newName: String) {
        savedStateHandle["name"] = newName
        _uiState.update { it.copy(name = newName, isNameValid = newName.isNotBlank()) }
    }

    fun onCameraSelected(cameraId: Long) {
        savedStateHandle["cameraId"] = cameraId
        _uiState.update { it.copy(cameraId = cameraId, isCameraValid = true) }
        updateFilteredLenses()
    }

    fun onLensSelected(lensId: Long) {
        savedStateHandle["lensId"] = lensId
        _uiState.update { it.copy(lensId = lensId, isLensValid = true) }
    }

    fun onFilmSelected(filmId: Long) {
        savedStateHandle["filmId"] = filmId
        _uiState.update { it.copy(filmId = filmId, isFilmValid = true) }
    }

    fun onTeleconverterSelected(tcId: Long?) {
        savedStateHandle["teleconverterId"] = tcId
        _uiState.update { it.copy(teleconverterId = tcId) }
    }

    fun onFilterSelected(filterId: Long?) {
        savedStateHandle["filterId"] = filterId
        _uiState.update { it.copy(filterId = filterId) }
    }

    fun onFlashSelected(flashId: Long?) {
        savedStateHandle["flashId"] = flashId
        _uiState.update { it.copy(flashId = flashId) }
    }

    fun onViewfinderSelected(vfId: Long?) {
        savedStateHandle["viewfinderId"] = vfId
        _uiState.update { it.copy(viewfinderId = vfId) }
    }

    fun saveKit(onComplete: () -> Unit) {
        val currentState = _uiState.value
        val isNameValid = currentState.name.isNotBlank()
        val isCameraValid = currentState.cameraId != null
        val isLensValid = currentState.lensId != null
        val isFilmValid = currentState.filmId != null

        if (!isNameValid || !isCameraValid || !isLensValid || !isFilmValid) {
            _uiState.update { it.copy(
                isNameValid = isNameValid,
                isCameraValid = isCameraValid,
                isLensValid = isLensValid,
                isFilmValid = isFilmValid
            ) }
            return
        }

        viewModelScope.launch {
            val kit = CameraKit(
                id = kitId ?: 0,
                name = currentState.name.trim(),
                cameraId = currentState.cameraId,
                lensId = currentState.lensId,
                filmId = currentState.filmId,
                teleconverterId = currentState.teleconverterId,
                filterId = currentState.filterId,
                flashId = currentState.flashId,
                viewfinderId = currentState.viewfinderId
            )
            if (kitId == null) {
                repository.insertCameraKit(kit)
                settingsRepository.setLocalFoundationChangesPending(true)
            } else {
                repository.updateCameraKit(kit)
                settingsRepository.setLocalFoundationChangesPending(true)
            }
            onComplete()
        }
    }
}
