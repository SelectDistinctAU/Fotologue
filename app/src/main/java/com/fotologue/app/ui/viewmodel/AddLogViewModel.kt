package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.*
import com.fotologue.app.util.ExposureSuggester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

data class AddLogUiState(
    val notes: String = "",
    val selectedCameraId: Long? = null,
    val selectedLensId: Long? = null,
    val selectedFilmId: Long? = null,
    val selectedTeleconverterId: Long? = null,
    val selectedFilterId: Long? = null,
    val selectedFlashId: Long? = null,
    val selectedViewfinderId: Long? = null,
    val selectedKitId: Long? = null,
    val focalLength: Float? = null,
    val iso: Int = 400,
    val shutterSpeed: String = "1/125",
    val aperture: String = "f/8",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val filmRoll: String = "",
    val thumbnailUri: String? = null,
    val faceDetected: Boolean = false,
    val selectedTagIds: Set<Long> = emptySet(),
    val allTags: List<Tag> = emptyList(),
    val availableShutterSpeeds: List<String> = ExposureSuggester.defaultShutterSpeeds,
    val availableApertures: List<String> = ExposureSuggester.commonApertures.map { apertureValue -> "f/${if ((apertureValue % 1.0) == 0.0) apertureValue.toInt() else apertureValue}" },
    val selectedSubjectId: Long? = null,
    val isKitVisible: Boolean = false,
    val isTeleconverterVisible: Boolean = false,
    val isFilterVisible: Boolean = false,
    val isFlashVisible: Boolean = false,
    val isViewfinderVisible: Boolean = false,
    val currentRoll: Int = 1,
    val currentFrame: Int = 1,
    val expectedExposures: Int = 36,
    val showRollFinishedDialog: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddLogViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val gearConfigurationManager: GearConfigurationManager,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddLogUiState(
            notes = savedStateHandle["notes"] ?: "",
        )
    )
    val uiState: StateFlow<AddLogUiState> = _uiState.asStateFlow()

    val allCameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilms: StateFlow<List<Film>> = combine(
        repository.allFilms,
        allCameras,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged()
    ) { films, cameras, cameraId ->
        if (cameraId == null) return@combine films
        
        val camera = cameras.find { it.id == cameraId } ?: return@combine films
        films.filter { 
            it.format == camera.format || 
            (camera.format == Film.FORMAT_35MM_HALF && it.format == Film.FORMAT_35MM) 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeleconverters: StateFlow<List<Teleconverter>> = repository.allTeleconverters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilters: StateFlow<List<Filter>> = repository.allFilters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFlashes: StateFlow<List<Flash>> = repository.allFlashes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allViewfinders: StateFlow<List<Viewfinder>> = repository.allViewfinders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allCameraKits: StateFlow<List<CameraKit>> = repository.allCameraKits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCameraKitsWithGear: StateFlow<List<CameraKitWithGear>> = repository.allCameraKitsWithGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allLenses = repository.allLenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allCameraLensCrossRefs = repository.allCameraLensCrossRefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredLenses: StateFlow<List<Lens>> = combine(
        allLenses,
        allCameraLensCrossRefs,
        allCameras,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
    ) { lenses, refs, cameras, cameraId ->
        if (cameraId == null) return@combine lenses

        val camera = cameras.find { it.id == cameraId }
        val associatedLensIds = refs.asSequence().filter { it.cameraId == cameraId }.map { it.lensId }.toSet()

        lenses.filter { lens ->
            val isDirectlyAssociated = associatedLensIds.contains(lens.id)
            if (isDirectlyAssociated) return@filter true

            if (camera != null && camera.mount.isNotBlank() && lens.mount.isNotBlank() && camera.mount.equals(lens.mount, ignoreCase = true)) {
                return@filter true
            }
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allCameraViewfinderCrossRefs = repository.allCameraViewfinderCrossRefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredViewfinders: StateFlow<List<Viewfinder>> = combine(
        allViewfinders,
        allCameraViewfinderCrossRefs,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
    ) { viewfinders, refs, cameraId ->
        if (cameraId == null) return@combine viewfinders

        val associatedViewfinderIds = refs.asSequence().filter { it.cameraId == cameraId }.map { it.viewfinderId }.toSet()
        viewfinders.filter { vf ->
            associatedViewfinderIds.contains(vf.id) || refs.none { it.viewfinderId == vf.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allCameraTeleconverterCrossRefs = repository.allCameraTeleconverterCrossRefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTeleconverters: StateFlow<List<Teleconverter>> = combine(
        allTeleconverters,
        allCameraTeleconverterCrossRefs,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
    ) { tcs, refs, cameraId ->
        if (cameraId == null) return@combine tcs
        val associatedIds = refs.asSequence().filter { it.cameraId == cameraId }.map { it.teleconverterId }.toSet()
        tcs.filter { tc -> associatedIds.contains(tc.id) || refs.none { it.teleconverterId == tc.id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allCameraFilterCrossRefs = repository.allCameraFilterCrossRefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFilters: StateFlow<List<Filter>> = combine(
        allFilters,
        allCameraFilterCrossRefs,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
    ) { filters, refs, cameraId ->
        if (cameraId == null) return@combine filters
        val associatedIds = refs.asSequence().filter { it.cameraId == cameraId }.map { it.filterId }.toSet()
        filters.filter { f -> associatedIds.contains(f.id) || refs.none { it.filterId == f.id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allCameraFlashCrossRefs = repository.allCameraFlashCrossRefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFlashes: StateFlow<List<Flash>> = combine(
        allFlashes,
        allCameraFlashCrossRefs,
        _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
    ) { flashes, refs, cameraId ->
        if (cameraId == null) return@combine flashes
        val associatedIds = refs.asSequence().filter { it.cameraId == cameraId }.map { it.flashId }.toSet()
        flashes.filter { fl -> associatedIds.contains(fl.id) || refs.none { it.flashId == fl.id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var editingLogId: Long? = null
        private set

    init {
        viewModelScope.launch {
            repository.insertDefaultTagsIfNeeded()
            repository.allTags.collect { tags ->
                _uiState.update { it.copy(allTags = tags) }
            }
        }
        viewModelScope.launch {
            combine(
                filteredTeleconverters,
                filteredFilters,
                filteredFlashes,
                filteredViewfinders,
                allCameraKits
            ) { tcs, filterList, flashes, vfs, kits ->
                _uiState.update { state ->
                    state.copy(
                        isTeleconverterVisible = tcs.isNotEmpty(),
                        isFilterVisible = filterList.isNotEmpty(),
                        isFlashVisible = flashes.isNotEmpty(),
                        isViewfinderVisible = vfs.isNotEmpty(),
                        isKitVisible = kits.isNotEmpty(),
                        selectedTeleconverterId = if (tcs.any { it.id == state.selectedTeleconverterId }) state.selectedTeleconverterId else null,
                        selectedFilterId = if (filterList.any { it.id == state.selectedFilterId }) state.selectedFilterId else null,
                        selectedFlashId = if (flashes.any { it.id == state.selectedFlashId }) state.selectedFlashId else null,
                        selectedViewfinderId = if (vfs.any { it.id == state.selectedViewfinderId }) state.selectedViewfinderId else null
                    )
                }
            }.collect {}
        }
        viewModelScope.launch {
            repository.latestMeteringResult.collect { result ->
                result?.let {
                    _uiState.update { state ->
                        state.copy(
                            shutterSpeed = result.shutterSpeed,
                            aperture = result.aperture,
                            iso = result.iso,
                            thumbnailUri = result.thumbnailUri,
                            faceDetected = result.faceDetected,
                        )
                    }
                    repository.clearMeteringResult()
                }
            }
        }
        
        viewModelScope.launch {
            repository.newlyAddedLensId.collect { lensId ->
                onLensSelected(lensId)
            }
        }

        viewModelScope.launch {
            repository.newlyAddedFilmId.collect { filmId ->
                val film = repository.getFilmById(filmId).first()
                if (film != null) {
                    onFilmSelected(filmId = film.id, filmIso = film.iso, updatePersistent = true)
                }
            }
        }
        
        // Auto-selection of single items
        viewModelScope.launch {
            if (editingLogId == null) {
                allCameras.collect { cameras ->
                    if (_uiState.value.selectedCameraId == null && cameras.size == 1) {
                        onCameraSelected(cameraId = cameras.first().id, updatePersistent = true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                allFilms.collect { films ->
                    if (_uiState.value.selectedFilmId == null && films.size == 1) {
                        val film = films.first()
                        onFilmSelected(filmId = film.id, filmIso = film.iso, updatePersistent = true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                filteredLenses.collect { lenses ->
                    if (_uiState.value.selectedLensId == null && lenses.size == 1) {
                        onLensSelected(lensId = lenses.first().id, updatePersistent = true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                filteredViewfinders.collect { viewfinders ->
                    if (_uiState.value.selectedViewfinderId == null && viewfinders.size == 1) {
                        onViewfinderSelected(vfId = viewfinders.first().id, updatePersistent = true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                filteredLenses.collect { lenses ->
                    if (_uiState.value.selectedLensId == null && lenses.size == 1) {
                        onLensSelected(lensId = lenses.first().id, updatePersistent = true)
                    }
                }
            }
        }

        // Apply persistent configuration on start if not editing
        viewModelScope.launch {
            if (editingLogId == null) {
                val config = gearConfigurationManager.currentConfiguration.first()
                config.cameraId?.let { onCameraSelected(cameraId = it, updatePersistent = false) }
                config.lensId?.let { onLensSelected(lensId = it, updatePersistent = false) }
                config.filmId?.let { onFilmSelected(filmId = it, filmIso = config.iso ?: 400, updatePersistent = false) }
                _uiState.update { it.copy(
                    selectedTeleconverterId = config.teleconverterId,
                    selectedFilterId = config.filterId,
                    selectedFlashId = config.flashId,
                    selectedViewfinderId = config.viewfinderId,
                    selectedKitId = config.kitId,
                ) }
            }
        }

        // Active Roll/Frame tracking per gear combination
        viewModelScope.launch {
            _uiState.map { Triple(it.selectedCameraId, it.selectedFilmId, editingLogId) }
                .distinctUntilChanged()
                .flatMapLatest { (cameraId, filmId, editingId) ->
                    if (editingId == null) {
                        repository.getSuggestedFilmRollFlow(cameraId, filmId).map { suggested ->
                            val camera = allCameras.value.find { it.id == cameraId }
                            val film = allFilms.value.find { it.id == filmId }
                            val expected = film?.expectedExposures ?: 36
                            val isHalf = camera?.format == Film.FORMAT_35MM_HALF || camera?.isHalfFrame == true
                            val totalExpected = if (isHalf) expected * 2 else expected
                            
                            _uiState.update { it.copy(
                                expectedExposures = totalExpected,
                                filmRoll = suggested,
                            ) }
                        }
                    } else {
                        flowOf(Unit)
                    }
                }.collect {}
        }
    }

    fun setEditingLogId(id: Long?) {
        editingLogId = id
        if (id != null) {
            loadLogEntry(id)
        }
    }

    private fun loadLogEntry(id: Long) {
        viewModelScope.launch {
            val logWithGear = repository.getLogEntryWithGearById(id).first()
            logWithGear?.let { gear ->
                val entry = gear.logEntry
                _uiState.update { it.copy(
                    notes = entry.notes,
                    selectedCameraId = entry.cameraId,
                    selectedLensId = entry.lensId,
                    selectedFilmId = entry.filmId,
                    selectedTeleconverterId = entry.teleconverterId,
                    selectedFilterId = entry.filterId,
                    selectedFlashId = entry.flashId,
                    selectedViewfinderId = entry.viewfinderId,
                    selectedKitId = entry.kitId,
                    focalLength = entry.focalLength,
                    iso = entry.exposureSettings.iso,
                    shutterSpeed = entry.exposureSettings.shutterSpeed,
                    aperture = entry.exposureSettings.aperture,
                    latitude = entry.latitude,
                    longitude = entry.longitude,
                    filmRoll = entry.filmRoll ?: "",
                    thumbnailUri = entry.exposureSettings.thumbnailUri,
                    selectedTagIds = mutableSetOf<Long>().apply { 
                        gear.tags.forEach { tag -> add(tag.id) } 
                    },
                ) }
            }
        }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
        savedStateHandle["notes"] = notes
    }
    
    fun onTagToggled(tagId: Long) {
        _uiState.update { currentState ->
            val current = currentState.selectedTagIds
            currentState.copy(
                selectedTagIds = if (current.contains(tagId)) current - tagId else current + tagId,
            )
        }
    }

    fun onCameraSelected(cameraId: Long?, updatePersistent: Boolean = true) {
        if (cameraId == null) {
            _uiState.update { 
                it.copy(
                    selectedCameraId = null,
                    selectedKitId = null,
                    availableShutterSpeeds = ExposureSuggester.defaultShutterSpeeds,
                )
            }
            if (updatePersistent) syncGearToPersistent()
            return
        }
        viewModelScope.launch {
            val camera = repository.getCameraById(cameraId).first()
            _uiState.update { 
                it.copy(
                    selectedCameraId = cameraId,
                    selectedKitId = null,
                    availableShutterSpeeds = if (camera?.shutterSpeeds.isNullOrEmpty()) {
                        ExposureSuggester.defaultShutterSpeeds
                    } else {
                        camera.shutterSpeeds
                    },
                    availableApertures = if (it.selectedLensId == null) {
                        ExposureSuggester.commonApertures.map { apertureValue -> "f/${if ((apertureValue % 1.0) == 0.0) apertureValue.toInt() else apertureValue}" }
                    } else {
                        it.availableApertures
                    },
                )
            }
            if (updatePersistent) syncGearToPersistent()
        }
    }

    fun onLensSelected(lensId: Long?, updatePersistent: Boolean = true) {
        viewModelScope.launch {
            val lens = if (lensId != null) repository.getLensById(lensId).first() else null
            
            var apertures = ExposureSuggester.commonApertures

            if (lens != null) {
                apertures = apertures.filter { it in lens.minAperture..lens.maxAperture }
                if (!apertures.contains(lens.minAperture)) apertures += lens.minAperture
                if (!apertures.contains(lens.maxAperture)) apertures += lens.maxAperture
                apertures = apertures.sorted()
            }

            _uiState.update { 
                it.copy(
                    selectedLensId = lensId,
                    selectedKitId = null,
                    focalLength = lens?.minFocalLength?.toFloat(),
                    availableApertures = apertures.map { apertureValue -> "f/${if ((apertureValue % 1.0) == 0.0) apertureValue.toInt() else apertureValue}" },
                )
            }
            if (updatePersistent) syncGearToPersistent()
        }
    }

    fun onTeleconverterSelected(tcId: Long?) {
        _uiState.update { it.copy(selectedTeleconverterId = tcId) }
        syncGearToPersistent()
    }

    fun onFilterSelected(filterId: Long?) {
        _uiState.update { it.copy(selectedFilterId = filterId, selectedKitId = null) }
        syncGearToPersistent()
    }

    fun onFlashSelected(flashId: Long?) {
        _uiState.update { it.copy(selectedFlashId = flashId, selectedKitId = null) }
        syncGearToPersistent()
    }

    fun onViewfinderSelected(vfId: Long?, updatePersistent: Boolean = true) {
        _uiState.update { it.copy(selectedViewfinderId = vfId, selectedKitId = null) }
        if (updatePersistent) syncGearToPersistent()
    }

    fun onFilmSelected(filmId: Long?, filmIso: Int, updatePersistent: Boolean = true) {
        _uiState.update { it.copy(selectedFilmId = filmId, iso = filmIso) }
        if (updatePersistent) syncGearToPersistent()
    }

    fun onCameraKitSelected(kitId: Long?) {
        if (kitId == null) {
            _uiState.update { it.copy(selectedKitId = null) }
            syncGearToPersistent()
            return
        }
        viewModelScope.launch {
            val kitWithGear = repository.allCameraKitsWithGear.first().find { it.cameraKit.id == kitId }
            kitWithGear?.let { kg ->
                val kit = kg.cameraKit
                val camera = kg.camera
                val lens = kg.lens
                val film = kg.film
                val iso = film?.iso ?: 400

                // Prepare shutter speeds
                val shutterSpeeds = if (camera?.shutterSpeeds.isNullOrEmpty()) {
                    ExposureSuggester.defaultShutterSpeeds
                } else {
                    camera.shutterSpeeds
                }

                // Prepare apertures
                var apertures = ExposureSuggester.commonApertures
                if (lens != null) {
                    apertures = apertures.filter { it in lens.minAperture..lens.maxAperture }
                    if (!apertures.contains(lens.minAperture)) apertures = apertures + lens.minAperture
                    if (!apertures.contains(lens.maxAperture)) apertures = apertures + lens.maxAperture
                    apertures = apertures.sorted()
                }

                _uiState.update { 
                    it.copy(
                        selectedKitId = kitId,
                        selectedCameraId = kit.cameraId,
                        selectedLensId = kit.lensId,
                        selectedFilmId = kit.filmId,
                        selectedTeleconverterId = kit.teleconverterId,
                        selectedFilterId = kit.filterId,
                        selectedFlashId = kit.flashId,
                        selectedViewfinderId = kit.viewfinderId,
                        iso = iso,
                        focalLength = lens?.minFocalLength?.toFloat(),
                        availableShutterSpeeds = shutterSpeeds,
                        availableApertures = apertures.map { apertureValue -> "f/${if ((apertureValue % 1.0) == 0.0) apertureValue.toInt() else apertureValue}" },
                    )
                }
                
                // Manually sync to ensure we use the values from the kit immediately
                if (editingLogId == null) {
                    gearConfigurationManager.updateConfiguration(
                        GearConfiguration(
                            cameraId = kit.cameraId,
                            lensId = kit.lensId,
                            filmId = kit.filmId,
                            teleconverterId = kit.teleconverterId,
                            filterId = kit.filterId,
                            flashId = kit.flashId,
                            viewfinderId = kit.viewfinderId,
                            kitId = kitId,
                            iso = iso,
                        )
                    )
                }
            }
        }
    }

    private fun syncGearToPersistent() {
        if (editingLogId != null) return // Don't sync when editing old entries
        
        val state = _uiState.value
        gearConfigurationManager.updateConfiguration(
            GearConfiguration(
                cameraId = state.selectedCameraId,
                lensId = state.selectedLensId,
                filmId = state.selectedFilmId,
                teleconverterId = state.selectedTeleconverterId,
                filterId = state.selectedFilterId,
                flashId = state.selectedFlashId,
                viewfinderId = state.selectedViewfinderId,
                kitId = state.selectedKitId,
                iso = state.iso,
            )
        )
    }

    fun onFocalLengthChange(focalLength: Float?) {
        _uiState.update { it.copy(focalLength = focalLength) }
    }

    fun onFilmRollChange(filmRoll: String) {
        _uiState.update { it.copy(filmRoll = filmRoll) }
        
        // Attempt to sync manual typing back to the repository if it matches the format
        val state = _uiState.value
        if (editingLogId == null && filmRoll.contains(".") && state.selectedCameraId != null && state.selectedFilmId != null) {
            val parts = filmRoll.split(".")
            if (parts.size == 2) {
                val roll = parts[0].toIntOrNull()
                val frame = parts[1].toIntOrNull()
                if (roll != null && frame != null) {
                    viewModelScope.launch {
                        repository.updateFilmRollSession(
                            FilmRollSession(state.selectedCameraId, state.selectedFilmId, roll, frame)
                        )
                    }
                }
            }
        }
    }

    fun onIsoChange(iso: Int) {
        _uiState.update { it.copy(iso = iso) }
    }

    fun onShutterSpeedChange(speed: String) {
        _uiState.update { it.copy(shutterSpeed = speed) }
    }

    fun onApertureChange(aperture: String) {
        _uiState.update { it.copy(aperture = aperture) }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            val location = repository.getCurrentLocation()
            _uiState.update { it.copy(
                latitude = location?.latitude,
                longitude = location?.longitude,
            ) }
        }
    }

    fun onRollFinishedDismissed() {
        _uiState.update { it.copy(showRollFinishedDialog = false) }
    }

    fun saveLog() {
        val state = _uiState.value
        viewModelScope.launch {
            val tc = if (state.selectedTeleconverterId != null) repository.getTeleconverterById(state.selectedTeleconverterId).first() else null
            
            val effectiveFocalLength = if (tc != null && state.focalLength != null) {
                state.focalLength * tc.magnification.toFloat()
            } else {
                state.focalLength
            }
            
            val effectiveApertureValue = if (tc != null) {
                val baseAp = state.aperture.replace("f/", "").toDoubleOrNull() ?: 0.0
                val effectiveAp = baseAp * sqrt(2.0).pow(tc.lightLossStops)
                "f/${if ((effectiveAp % 1.0) == 0.0) effectiveAp.toInt() else String.format(Locale.US, "%.1f", effectiveAp)}"
            } else {
                state.aperture
            }

            if (editingLogId != null) {
                // UPDATE existing entry
                val existingEntry = repository.getLogEntryById(editingLogId!!).first()
                existingEntry?.let { entry ->
                    val updatedEntry = entry.copy(
                        notes = state.notes.trim(),
                        environmentalConditions = "",
                        cameraId = state.selectedCameraId,
                        filmId = state.selectedFilmId,
                        lensId = state.selectedLensId,
                        teleconverterId = state.selectedTeleconverterId,
                        filterId = state.selectedFilterId,
                        kitId = state.selectedKitId,
                        flashId = state.selectedFlashId,
                        viewfinderId = state.selectedViewfinderId,
                        focalLength = state.focalLength,
                        effectiveFocalLength = effectiveFocalLength,
                        effectiveAperture = effectiveApertureValue,
                        exposureSettings = ExposureSettings(
                            state.shutterSpeed.trim(),
                            state.aperture.trim(),
                            state.iso,
                            state.thumbnailUri ?: entry.exposureSettings.thumbnailUri,
                        ),
                        latitude = state.latitude,
                        longitude = state.longitude,
                        filmRoll = state.filmRoll.trim(),
                    )
                    repository.updateLogEntry(updatedEntry, state.selectedTagIds.toList())
                }
            } else {
                // CREATE new entry
                repository.logExposure(
                    notes = state.notes.trim(),
                    environmentalConditions = "",
                    cameraId = state.selectedCameraId,
                    filmId = state.selectedFilmId,
                    lensId = state.selectedLensId,
                    teleconverterId = state.selectedTeleconverterId,
                    filterId = state.selectedFilterId,
                    kitId = state.selectedKitId,
                    flashId = state.selectedFlashId,
                    viewfinderId = state.selectedViewfinderId,
                    focalLength = state.focalLength,
                    effectiveFocalLength = effectiveFocalLength,
                    effectiveAperture = effectiveApertureValue,
                    exposureSettings = ExposureSettings(state.shutterSpeed.trim(), state.aperture.trim(), state.iso, state.thumbnailUri),
                    tagIds = state.selectedTagIds.toList(),
                    latitude = state.latitude,
                    longitude = state.longitude,
                    filmRoll = state.filmRoll.trim(),
                )
                
                // Roll check logic for new entries
                if (state.currentFrame >= state.expectedExposures) {
                    _uiState.update { it.copy(showRollFinishedDialog = true) }
                }
            }
        }
    }
}
