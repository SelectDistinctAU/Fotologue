package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.*
import com.fotologue.core.data.lab.AssetAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GearLibraryUiState(
    val selectedCameraIds: Set<Long> = emptySet(),
    val selectedFilmIds: Set<Long> = emptySet(),
    val selectedLensIds: Set<Long> = emptySet(),
    val selectedTeleconverterIds: Set<Long> = emptySet(),
    val selectedFilterIds: Set<Long> = emptySet(),
    val selectedFlashIds: Set<Long> = emptySet(),
    val selectedViewfinderIds: Set<Long> = emptySet(),
    val selectedKitIds: Set<Long> = emptySet(),
    val selectedSubjectIds: Set<Long> = emptySet(),
    val selectedContentIds: Set<Long> = emptySet(),
    val selectedIntentIds: Set<Long> = emptySet(),
    val selectedCategoryIndex: Int = 0,
    val isSelectionMode: Boolean = false,
    val isReorderMode: Boolean = false,
    val showTagDialog: Boolean = false,
    val showContentDialog: Boolean = false,
    val showIntentDialog: Boolean = false,
    val editingTag: Tag? = null,
)

@HiltViewModel
class GearLibraryViewModel @Inject constructor(private val repository: FotologueRepository) : ViewModel() {
    fun updateAssetStatus(category: String, refId: Long, newStatus: String, reason: String? = null) {
        viewModelScope.launch {
            repository.setAssetAvailability(
                category = category,
                refId = refId,
                status = newStatus,
                reason = reason
            )
        }
    }

    val allAvailability: StateFlow<List<AssetAvailability>> = repository.allAvailability
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFilms: StateFlow<List<Film>> = repository.allFilms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLenses: StateFlow<List<Lens>> = repository.allLenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allLensesWithCameras: StateFlow<List<LensWithCameras>> = combine(
        repository.allLenses,
        repository.allCameraLensCrossRefs,
        allCameras
    ) { lenses, refs, cameras ->
        lenses.map { lens ->
            val associatedCameraIds = refs.filter { it.lensId == lens.id }.map { it.cameraId }.toSet()
            val associatedCameras = cameras.filter { it.id in associatedCameraIds }
            LensWithCameras(lens, associatedCameras)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeleconvertersWithCameras: StateFlow<List<TeleconverterWithCameras>> = combine(
        repository.allTeleconverters,
        repository.allCameraTeleconverterCrossRefs,
        allCameras
    ) { tcs, refs, cameras ->
        tcs.map { tc ->
            val associatedCameraIds = refs.filter { it.teleconverterId == tc.id }.map { it.cameraId }.toSet()
            val associatedCameras = cameras.filter { it.id in associatedCameraIds }
            TeleconverterWithCameras(tc, associatedCameras)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFiltersWithCameras: StateFlow<List<FilterWithCameras>> = combine(
        repository.allFilters,
        repository.allCameraFilterCrossRefs,
        allCameras
    ) { filters, refs, cameras ->
        filters.map { filter ->
            val associatedCameraIds = refs.filter { it.filterId == filter.id }.map { it.cameraId }.toSet()
            val associatedCameras = cameras.filter { it.id in associatedCameraIds }
            FilterWithCameras(filter, associatedCameras)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFlashesWithCameras: StateFlow<List<FlashWithCameras>> = combine(
        repository.allFlashes,
        repository.allCameraFlashCrossRefs,
        allCameras
    ) { flashes, refs, cameras ->
        flashes.map { flash ->
            val associatedCameraIds = refs.filter { it.flashId == flash.id }.map { it.cameraId }.toSet()
            val associatedCameras = cameras.filter { it.id in associatedCameraIds }
            FlashWithCameras(flash, associatedCameras)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allViewfindersWithCameras: StateFlow<List<ViewfinderWithCameras>> = combine(
        repository.allViewfinders,
        repository.allCameraViewfinderCrossRefs,
        allCameras
    ) { viewfinders, refs, cameras ->
        viewfinders.map { viewfinder ->
            val associatedCameraIds = refs.filter { it.viewfinderId == viewfinder.id }.map { it.cameraId }.toSet()
            val associatedCameras = cameras.filter { it.id in associatedCameraIds }
            ViewfinderWithCameras(viewfinder, associatedCameras)
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
    
    val allCameraKits: StateFlow<List<CameraKitWithGear>> = repository.allCameraKitsWithGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allSubjects: StateFlow<List<Tag>> = repository.allTags
        .map { tags -> tags.filter { it.category == Tag.CATEGORY_SUBJECT } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContent: StateFlow<List<Tag>> = repository.allTags
        .map { tags -> tags.filter { it.category == Tag.CATEGORY_CONTENT } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIntents: StateFlow<List<Tag>> = repository.allTags
        .map { tags -> tags.filter { it.category == Tag.CATEGORY_INTENT } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(GearLibraryUiState())
    val uiState: StateFlow<GearLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.insertDefaultTagsIfNeeded()
        }
    }

    fun toggleReorderMode() {
        _uiState.update { it.copy(isReorderMode = !it.isReorderMode, isSelectionMode = false) }
    }

    fun moveKit(fromIndex: Int, toIndex: Int) {
        val list = allCameraKits.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
    }

    fun moveCamera(fromIndex: Int, toIndex: Int) {
        val list = allCameras.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateCameraPositions(list)
    }

    private fun updateCameraPositions(list: List<Camera>) {
        viewModelScope.launch {
            repository.updateCameras(list.mapIndexed { index, camera -> camera.copy(position = index) })
        }
    }

    fun moveFilm(fromIndex: Int, toIndex: Int) {
        val list = allFilms.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateFilmPositions(list)
    }

    private fun updateFilmPositions(list: List<Film>) {
        viewModelScope.launch {
            repository.updateFilms(list.mapIndexed { index, film -> film.copy(position = index) })
        }
    }

    fun moveLens(fromIndex: Int, toIndex: Int) {
        val list = allLenses.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateLensPositions(list)
    }

    private fun updateLensPositions(list: List<Lens>) {
        viewModelScope.launch {
            repository.updateLenses(list.mapIndexed { index, lens -> lens.copy(position = index) })
        }
    }

    fun moveTeleconverter(fromIndex: Int, toIndex: Int) {
        val list = allTeleconverters.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateTeleconverterPositions(list)
    }

    private fun updateTeleconverterPositions(list: List<Teleconverter>) {
        viewModelScope.launch {
            repository.updateTeleconverters(list.mapIndexed { index, tc -> tc.copy(position = index) })
        }
    }

    fun moveFilter(fromIndex: Int, toIndex: Int) {
        val list = allFilters.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateFilterPositions(list)
    }

    private fun updateFilterPositions(list: List<Filter>) {
        viewModelScope.launch {
            repository.updateFilters(list.mapIndexed { index, filter -> filter.copy(position = index) })
        }
    }

    fun moveFlash(fromIndex: Int, toIndex: Int) {
        val list = allFlashes.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateFlashPositions(list)
    }

    private fun updateFlashPositions(list: List<Flash>) {
        viewModelScope.launch {
            repository.updateFlashes(list.mapIndexed { index, flash -> flash.copy(position = index) })
        }
    }

    fun moveViewfinder(fromIndex: Int, toIndex: Int) {
        val list = allViewfinders.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateViewfinderPositions(list)
    }

    private fun updateViewfinderPositions(list: List<Viewfinder>) {
        viewModelScope.launch {
            repository.updateViewfinders(list.mapIndexed { index, viewfinder -> viewfinder.copy(position = index) })
        }
    }

    fun moveSubject(fromIndex: Int, toIndex: Int) {
        val list = allSubjects.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateTagPositions(list)
    }

    fun moveContent(fromIndex: Int, toIndex: Int) {
        val list = allContent.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateTagPositions(list)
    }

    fun moveIntent(fromIndex: Int, toIndex: Int) {
        val list = allIntents.value.toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        updateTagPositions(list)
    }

    private fun updateTagPositions(list: List<Tag>) {
        viewModelScope.launch {
            repository.updateTags(list.mapIndexed { index, tag -> tag.copy(position = index) })
        }
    }

    fun toggleCameraSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedCameraIds.contains(id)) {
                currentState.selectedCameraIds - id
            } else {
                currentState.selectedCameraIds + id
            }
            currentState.updateSelectionMode(selectedCameraIds = newSelection)
        }
    }

    fun toggleFilmSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFilmIds.contains(id)) {
                currentState.selectedFilmIds - id
            } else {
                currentState.selectedFilmIds + id
            }
            currentState.updateSelectionMode(selectedFilmIds = newSelection)
        }
    }

    fun toggleLensSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedLensIds.contains(id)) {
                currentState.selectedLensIds - id
            } else {
                currentState.selectedLensIds + id
            }
            currentState.updateSelectionMode(selectedLensIds = newSelection)
        }
    }

    fun toggleTeleconverterSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedTeleconverterIds.contains(id)) {
                currentState.selectedTeleconverterIds - id
            } else {
                currentState.selectedTeleconverterIds + id
            }
            currentState.updateSelectionMode(selectedTeleconverterIds = newSelection)
        }
    }

    fun toggleFilterSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFilterIds.contains(id)) {
                currentState.selectedFilterIds - id
            } else {
                currentState.selectedFilterIds + id
            }
            currentState.updateSelectionMode(selectedFilterIds = newSelection)
        }
    }

    fun toggleFlashSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFlashIds.contains(id)) {
                currentState.selectedFlashIds - id
            } else {
                currentState.selectedFlashIds + id
            }
            currentState.updateSelectionMode(selectedFlashIds = newSelection)
        }
    }

    fun toggleViewfinderSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedViewfinderIds.contains(id)) {
                currentState.selectedViewfinderIds - id
            } else {
                currentState.selectedViewfinderIds + id
            }
            currentState.updateSelectionMode(selectedViewfinderIds = newSelection)
        }
    }

    fun toggleKitSelection(kitId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedKitIds.contains(kitId)) {
                currentState.selectedKitIds - kitId
            } else {
                currentState.selectedKitIds + kitId
            }
            currentState.updateSelectionMode(selectedKitIds = newSelection)
        }
    }

    fun toggleSubjectSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedSubjectIds.contains(id)) {
                currentState.selectedSubjectIds - id
            } else {
                currentState.selectedSubjectIds + id
            }
            currentState.updateSelectionMode(selectedSubjectIds = newSelection)
        }
    }

    fun toggleContentSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedContentIds.contains(id)) {
                currentState.selectedContentIds - id
            } else {
                currentState.selectedContentIds + id
            }
            currentState.updateSelectionMode(selectedContentIds = newSelection)
        }
    }

    fun toggleIntentSelection(id: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedIntentIds.contains(id)) {
                currentState.selectedIntentIds - id
            } else {
                currentState.selectedIntentIds + id
            }
            currentState.updateSelectionMode(selectedIntentIds = newSelection)
        }
    }

    private fun GearLibraryUiState.updateSelectionMode(
        selectedCameraIds: Set<Long> = this.selectedCameraIds,
        selectedFilmIds: Set<Long> = this.selectedFilmIds,
        selectedLensIds: Set<Long> = this.selectedLensIds,
        selectedTeleconverterIds: Set<Long> = this.selectedTeleconverterIds,
        selectedFilterIds: Set<Long> = this.selectedFilterIds,
        selectedFlashIds: Set<Long> = this.selectedFlashIds,
        selectedViewfinderIds: Set<Long> = this.selectedViewfinderIds,
        selectedKitIds: Set<Long> = this.selectedKitIds,
        selectedSubjectIds: Set<Long> = this.selectedSubjectIds,
        selectedContentIds: Set<Long> = this.selectedContentIds,
        selectedIntentIds: Set<Long> = this.selectedIntentIds
    ): GearLibraryUiState {
        val hasSelection = selectedCameraIds.isNotEmpty() || 
                           selectedFilmIds.isNotEmpty() || 
                           selectedLensIds.isNotEmpty() || 
                           selectedTeleconverterIds.isNotEmpty() || 
                           selectedFilterIds.isNotEmpty() ||
                           selectedFlashIds.isNotEmpty() ||
                           selectedViewfinderIds.isNotEmpty() ||
                           selectedKitIds.isNotEmpty() ||
                           selectedSubjectIds.isNotEmpty() ||
                           selectedContentIds.isNotEmpty() ||
                           selectedIntentIds.isNotEmpty()
        return copy(
            selectedCameraIds = selectedCameraIds,
            selectedFilmIds = selectedFilmIds,
            selectedLensIds = selectedLensIds,
            selectedTeleconverterIds = selectedTeleconverterIds,
            selectedFilterIds = selectedFilterIds,
            selectedFlashIds = selectedFlashIds,
            selectedViewfinderIds = selectedViewfinderIds,
            selectedKitIds = selectedKitIds,
            selectedSubjectIds = selectedSubjectIds,
            selectedContentIds = selectedContentIds,
            selectedIntentIds = selectedIntentIds,
            isSelectionMode = hasSelection,
            isReorderMode = if (hasSelection) false else this.isReorderMode
        )
    }

    fun clearSelection() {
        _uiState.update { 
            it.copy(
                selectedCameraIds = emptySet(), 
                selectedFilmIds = emptySet(), 
                selectedLensIds = emptySet(),
                selectedTeleconverterIds = emptySet(),
                selectedFilterIds = emptySet(),
                selectedFlashIds = emptySet(),
                selectedViewfinderIds = emptySet(),
                selectedKitIds = emptySet(),
                selectedSubjectIds = emptySet(),
                selectedContentIds = emptySet(),
                selectedIntentIds = emptySet(),
                isSelectionMode = false
            ) 
        }
    }

    fun deleteSelectedGear() {
        viewModelScope.launch {
            val cameraIds = uiState.value.selectedCameraIds.toList()
            val filmIds = uiState.value.selectedFilmIds.toList()
            val lensIds = uiState.value.selectedLensIds.toList()
            val teleconverterIds = uiState.value.selectedTeleconverterIds.toList()
            val filterIds = uiState.value.selectedFilterIds.toList()
            val flashIds = uiState.value.selectedFlashIds.toList()
            val viewfinderIds = uiState.value.selectedViewfinderIds.toList()
            val kitIds = uiState.value.selectedKitIds.toList()
            val subjectIds = uiState.value.selectedSubjectIds.toList()
            val contentIds = uiState.value.selectedContentIds.toList()
            val intentIds = uiState.value.selectedIntentIds.toList()
            
            if (cameraIds.isNotEmpty()) repository.deleteCameras(cameraIds)
            if (filmIds.isNotEmpty()) repository.deleteFilms(filmIds)
            if (lensIds.isNotEmpty()) repository.deleteLenses(lensIds)
            if (teleconverterIds.isNotEmpty()) repository.deleteTeleconverters(teleconverterIds)
            if (filterIds.isNotEmpty()) repository.deleteFilters(filterIds)
            if (flashIds.isNotEmpty()) repository.deleteFlashes(flashIds)
            if (viewfinderIds.isNotEmpty()) repository.deleteViewfinders(viewfinderIds)
            if (kitIds.isNotEmpty()) repository.deleteCameraKits(kitIds)
            if (subjectIds.isNotEmpty()) repository.deleteTags(subjectIds)
            if (contentIds.isNotEmpty()) repository.deleteTags(contentIds)
            if (intentIds.isNotEmpty()) repository.deleteTags(intentIds)
            
            clearSelection()
        }
    }

    fun showAddTagDialog() {
        _uiState.update { it.copy(showTagDialog = true, editingTag = null) }
    }

    fun showAddContentDialog() {
        _uiState.update { it.copy(showContentDialog = true, editingTag = null) }
    }

    fun showAddIntentDialog() {
        _uiState.update { it.copy(showIntentDialog = true, editingTag = null) }
    }

    fun showEditTagDialog(tag: Tag) {
        when (tag.category) {
            Tag.CATEGORY_SUBJECT -> _uiState.update { it.copy(showTagDialog = true, editingTag = tag) }
            Tag.CATEGORY_INTENT -> _uiState.update { it.copy(showIntentDialog = true, editingTag = tag) }
            else -> _uiState.update { it.copy(showContentDialog = true, editingTag = tag) }
        }
    }

    fun dismissTagDialog() {
        _uiState.update { it.copy(showTagDialog = false, showContentDialog = false, showIntentDialog = false, editingTag = null) }
    }

    fun addTag(name: String, category: String, parentId: Long?) {
        viewModelScope.launch {
            repository.insertTag(Tag(name = name.trim(), category = category, parentId = parentId))
            dismissTagDialog()
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag.copy(name = tag.name.trim()))
            dismissTagDialog()
        }
    }

    fun onCategorySelected(index: Int) {
        _uiState.update { it.copy(selectedCategoryIndex = index, isReorderMode = false) }
    }

    fun selectAll() {
        _uiState.update { currentState ->
            when (currentState.selectedCategoryIndex) {
                0 -> currentState.updateSelectionMode(selectedKitIds = allCameraKits.value.map { it.cameraKit.id }.toSet())
                1 -> currentState.updateSelectionMode(selectedCameraIds = allCameras.value.map { it.id }.toSet())
                2 -> currentState.updateSelectionMode(selectedLensIds = allLenses.value.map { it.id }.toSet())
                3 -> currentState.updateSelectionMode(selectedFilmIds = allFilms.value.map { it.id }.toSet())
                4 -> currentState.updateSelectionMode(selectedTeleconverterIds = allTeleconverters.value.map { it.id }.toSet())
                5 -> currentState.updateSelectionMode(selectedFilterIds = allFilters.value.map { it.id }.toSet())
                6 -> currentState.updateSelectionMode(selectedFlashIds = allFlashes.value.map { it.id }.toSet())
                7 -> currentState.updateSelectionMode(selectedViewfinderIds = allViewfinders.value.map { it.id }.toSet())
                8 -> currentState.updateSelectionMode(selectedSubjectIds = allSubjects.value.map { it.id }.toSet())
                9 -> currentState.updateSelectionMode(selectedContentIds = allContent.value.map { it.id }.toSet())
                10 -> currentState.updateSelectionMode(selectedIntentIds = allIntents.value.map { it.id }.toSet())
                else -> currentState
            }
        }
    }
}
