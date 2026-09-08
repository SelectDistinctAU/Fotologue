package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.*
import com.fotologue.app.util.ExposureSuggester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

data class AddLogUiState(
    val notes: String = "",
    val environmentalConditions: String = "",
    val selectedCameraId: Long? = null,
    val selectedLensId: Long? = null,
    val selectedFilmId: Long? = null,
    val selectedTeleconverterId: Long? = null,
    val selectedFilterId: Long? = null,
    val selectedFlashId: Long? = null,
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
    val availableApertures: List<String> = ExposureSuggester.commonApertures.map { "f/${if (it % 1.0 == 0.0) it.toInt() else it}" },
    val selectedSubjectId: Long? = null,
    val isKitVisible: Boolean = false,
    val isTeleconverterVisible: Boolean = false,
    val isFilterVisible: Boolean = false,
    val isFlashVisible: Boolean = false,
    val currentRoll: Int = 1,
    val currentFrame: Int = 1,
    val expectedExposures: Int = 36,
    val showRollFinishedDialog: Boolean = false
)

@HiltViewModel
class AddLogViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val gearConfigurationManager: GearConfigurationManager,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLogUiState(
        notes = savedStateHandle["notes"] ?: "",
        environmentalConditions = savedStateHandle["environmentalConditions"] ?: ""
    ))
    val uiState: StateFlow<AddLogUiState> = _uiState.asStateFlow()

    val allCameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilms: StateFlow<List<Film>> = repository.allFilms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeleconverters: StateFlow<List<Teleconverter>> = repository.allTeleconverters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilters: StateFlow<List<Filter>> = repository.allFilters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFlashes: StateFlow<List<Flash>> = repository.allFlashes
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
        _uiState.map { it.selectedCameraId }.distinctUntilChanged()
    ) { lenses, refs, cameras, cameraId ->
        if (cameraId == null) return@combine lenses

        val camera = cameras.find { it.id == cameraId }
        val associatedLensIds = refs.filter { it.cameraId == cameraId }.map { it.lensId }.toSet()

        lenses.filter { lens ->
            val isDirectlyAssociated = associatedLensIds.contains(lens.id)
            if (isDirectlyAssociated) return@filter true

            if (camera != null && camera.mount.isNotBlank() && lens.mount.isNotBlank() && camera.mount.equals(lens.mount, ignoreCase = true)) {
                return@filter true
            }
            false
        }
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
            combine(allTeleconverters, allFilters, allFlashes, allCameraKits) { tcs, filters, flashes, kits ->
                _uiState.update { it.copy(
                    isTeleconverterVisible = tcs.isNotEmpty(),
                    isFilterVisible = filters.isNotEmpty(),
                    isFlashVisible = flashes.isNotEmpty(),
                    isKitVisible = kits.isNotEmpty()
                ) }
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
                            faceDetected = result.faceDetected
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
        
        // Auto-selection of single items
        viewModelScope.launch {
            if (editingLogId == null) {
                allCameras.collect { cameras ->
                    if (_uiState.value.selectedCameraId == null && cameras.size == 1) {
                        onCameraSelected(cameras.first().id, true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                allFilms.collect { films ->
                    if (_uiState.value.selectedFilmId == null && films.size == 1) {
                        val film = films.first()
                        onFilmSelected(film.id, film.iso, true)
                    }
                }
            }
        }
        viewModelScope.launch {
            if (editingLogId == null) {
                filteredLenses.collect { lenses ->
                    if (_uiState.value.selectedLensId == null && lenses.size == 1) {
                        onLensSelected(lenses.first().id, true)
                    }
                }
            }
        }

        // Apply persistent configuration on start if not editing
        viewModelScope.launch {
            if (editingLogId == null) {
                val config = gearConfigurationManager.currentConfiguration.first()
                config.cameraId?.let { onCameraSelected(it, false) }
                config.lensId?.let { onLensSelected(it, false) }
                config.filmId?.let { onFilmSelected(it, config.iso ?: 400, false) }
                _uiState.update { it.copy(
                    selectedTeleconverterId = config.teleconverterId,
                    selectedFilterId = config.filterId,
                    selectedFlashId = config.flashId,
                    selectedKitId = config.kitId
                ) }
                updateSuggestedRollNumber()
            }
        }

        // Active Roll/Frame tracking
        viewModelScope.launch {
            combine<Any?, Unit>(
                listOf(
                    settingsRepository.currentRoll,
                    settingsRepository.currentFrame,
                    allCameras,
                    allFilms,
                    _uiState.map { it.selectedCameraId }.distinctUntilChanged(),
                    _uiState.map { it.selectedFilmId }.distinctUntilChanged()
                )
            ) { args: Array<Any?> ->
                val roll = args[0] as Int
                val frame = args[1] as Int
                val cameras = args[2] as List<Camera>
                val films = args[3] as List<Film>
                val cameraId = args[4] as Long?
                val filmId = args[5] as Long?

                val camera = cameras.find { it.id == cameraId }
                val film = films.find { it.id == filmId }
                val expected = film?.expectedExposures ?: 36
                val isHalf = camera?.isHalfFrame ?: false
                val totalExpected = if (isHalf) expected * 2 else expected
                
                _uiState.update { it.copy(
                    currentRoll = roll,
                    currentFrame = frame,
                    expectedExposures = totalExpected
                ) }
            }.collect {}
        }
    }

    private fun updateSuggestedRollNumber() {
        if (editingLogId != null) return
        viewModelScope.launch {
            val state = _uiState.value
            val suggested = repository.getSuggestedFilmRoll(state.selectedCameraId, state.selectedFilmId)
            _uiState.update { it.copy(filmRoll = suggested) }
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
                    environmentalConditions = entry.environmentalConditions,
                    selectedCameraId = entry.cameraId,
                    selectedLensId = entry.lensId,
                    selectedFilmId = entry.filmId,
                    selectedTeleconverterId = entry.teleconverterId,
                    selectedFilterId = entry.filterId,
                    selectedFlashId = entry.flashId,
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
                        gear.tags.forEach { add(it.id) } 
                    }
                ) }
            }
        }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
        savedStateHandle["notes"] = notes
    }

    fun onEnvironmentalConditionsChange(conditions: String) {
        _uiState.update { it.copy(environmentalConditions = conditions) }
        savedStateHandle["environmentalConditions"] = conditions
    }
    
    fun onTagToggled(tagId: Long) {
        _uiState.update { currentState ->
            val current = currentState.selectedTagIds
            currentState.copy(
                selectedTagIds = if (current.contains(tagId)) current - tagId else current + tagId
            )
        }
    }

    fun onCameraSelected(cameraId: Long?, updatePersistent: Boolean = true) {
        if (cameraId == null) {
            _uiState.update { 
                it.copy(
                    selectedCameraId = null,
                    selectedKitId = null,
                    availableShutterSpeeds = ExposureSuggester.defaultShutterSpeeds
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
                        ExposureSuggester.commonApertures.map { "f/${if (it % 1.0 == 0.0) it.toInt() else it}" }
                    } else {
                        it.availableApertures
                    }
                )
            }
            if (updatePersistent) syncGearToPersistent()
            updateSuggestedRollNumber()
        }
    }

    fun onLensSelected(lensId: Long?, updatePersistent: Boolean = true) {
        viewModelScope.launch {
            val lens = if (lensId != null) repository.getLensById(lensId).first() else null
            
            var apertures = ExposureSuggester.commonApertures

            if (lens != null) {
                apertures = apertures.filter { it in lens.minAperture..lens.maxAperture }
                if (!apertures.contains(lens.minAperture)) apertures = apertures + lens.minAperture
                if (!apertures.contains(lens.maxAperture)) apertures = apertures + lens.maxAperture
                apertures = apertures.sorted()
            }

            _uiState.update { 
                it.copy(
                    selectedLensId = lensId,
                    selectedKitId = null,
                    focalLength = lens?.minFocalLength?.toFloat(),
                    availableApertures = apertures.map { "f/${if (it % 1.0 == 0.0) it.toInt() else it}" }
                )
            }
            if (updatePersistent) syncGearToPersistent()
            updateSuggestedRollNumber()
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

    fun onFilmSelected(filmId: Long?, filmIso: Int, updatePersistent: Boolean = true) {
        _uiState.update { it.copy(selectedFilmId = filmId, iso = filmIso) }
        if (updatePersistent) syncGearToPersistent()
        updateSuggestedRollNumber()
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
                        iso = iso,
                        focalLength = lens?.minFocalLength?.toFloat(),
                        availableShutterSpeeds = shutterSpeeds,
                        availableApertures = apertures.map { "f/${if (it % 1.0 == 0.0) it.toInt() else it}" }
                    )
                }
                updateSuggestedRollNumber()
                
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
                            kitId = kitId,
                            iso = iso
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
                kitId = state.selectedKitId,
                iso = state.iso
            )
        )
    }

    fun onFocalLengthChange(focalLength: Float?) {
        _uiState.update { it.copy(focalLength = focalLength) }
    }

    fun onFilmRollChange(filmRoll: String) {
        _uiState.update { it.copy(filmRoll = filmRoll) }
    }

    fun onIsoChange(iso: Int) {
        _uiState.update { it.copy(iso = iso) }
    }

    fun onSubjectSelected(subjectId: Long?) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
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
                longitude = location?.longitude
            ) }
        }
    }

    fun finishRollAndStartNew() {
        viewModelScope.launch {
            val nextRoll = _uiState.value.currentRoll + 1
            settingsRepository.setCurrentRollAndFrame(nextRoll, 1)
            _uiState.update { it.copy(showRollFinishedDialog = false) }
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
            
            val effectiveAperture = if (tc != null) {
                val baseAp = state.aperture.replace("f/", "").toDoubleOrNull() ?: 0.0
                val effectiveAp = baseAp * sqrt(2.0).pow(tc.lightLossStops)
                "f/${if (effectiveAp % 1.0 == 0.0) effectiveAp.toInt() else String.format("%.1f", effectiveAp)}"
            } else {
                state.aperture
            }

            if (editingLogId != null) {
                // UPDATE existing entry
                val existingEntry = repository.getLogEntryById(editingLogId!!).first()
                existingEntry?.let { entry ->
                    val updatedEntry = entry.copy(
                        notes = state.notes.trim(),
                        environmentalConditions = state.environmentalConditions.trim(),
                        cameraId = state.selectedCameraId,
                        filmId = state.selectedFilmId,
                        lensId = state.selectedLensId,
                        teleconverterId = state.selectedTeleconverterId,
                        filterId = state.selectedFilterId,
                        kitId = state.selectedKitId,
                        flashId = state.selectedFlashId,
                        focalLength = state.focalLength,
                        effectiveFocalLength = effectiveFocalLength,
                        effectiveAperture = effectiveAperture,
                        exposureSettings = ExposureSettings(
                            state.shutterSpeed.trim(),
                            state.aperture.trim(),
                            state.iso,
                            state.thumbnailUri ?: entry.exposureSettings.thumbnailUri
                        ),
                        latitude = state.latitude,
                        longitude = state.longitude,
                        filmRoll = state.filmRoll.trim()
                    )
                    repository.updateLogEntry(updatedEntry, state.selectedTagIds.toList())
                }
            } else {
                // CREATE new entry
                repository.logExposure(
                    notes = state.notes.trim(),
                    environmentalConditions = state.environmentalConditions.trim(),
                    cameraId = state.selectedCameraId,
                    filmId = state.selectedFilmId,
                    lensId = state.selectedLensId,
                    teleconverterId = state.selectedTeleconverterId,
                    filterId = state.selectedFilterId,
                    kitId = state.selectedKitId,
                    flashId = state.selectedFlashId,
                    focalLength = state.focalLength,
                    effectiveFocalLength = effectiveFocalLength,
                    effectiveAperture = effectiveAperture,
                    exposureSettings = ExposureSettings(state.shutterSpeed.trim(), state.aperture.trim(), state.iso, state.thumbnailUri),
                    tagIds = state.selectedTagIds.toList(),
                    latitude = state.latitude,
                    longitude = state.longitude,
                    filmRoll = state.filmRoll.trim()
                )
                // Increment the frame counter in settings if we're not editing an old log
                val currentFrame = _uiState.value.currentFrame
                val expected = _uiState.value.expectedExposures
                
                if (currentFrame >= expected) {
                    _uiState.update { it.copy(showRollFinishedDialog = true) }
                } else {
                    settingsRepository.incrementFrame()
                }
            }
        }
    }
}
