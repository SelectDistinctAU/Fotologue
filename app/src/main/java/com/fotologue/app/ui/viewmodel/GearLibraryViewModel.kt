package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.Camera
import com.fotologue.app.data.Film
import com.fotologue.app.data.Lens
import com.fotologue.app.data.Teleconverter
import com.fotologue.app.data.Filter
import com.fotologue.app.data.Flash
import com.fotologue.app.data.CameraKit
import com.fotologue.app.data.CameraKitWithGear
import com.fotologue.app.data.LensWithCameras
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GearLibraryUiState(
    val selectedCameraIds: Set<Long> = emptySet(),
    val selectedFilmIds: Set<Long> = emptySet(),
    val selectedLensIds: Set<Long> = emptySet(),
    val selectedTeleconverterIds: Set<Long> = emptySet(),
    val selectedFilterIds: Set<Long> = emptySet(),
    val selectedFlashIds: Set<Long> = emptySet(),
    val selectedKitIds: Set<Long> = emptySet(),
    val selectedSubjectIds: Set<Long> = emptySet(),
    val selectedContentIds: Set<Long> = emptySet(),
    val selectedCategoryIndex: Int = 0,
    val isSelectionMode: Boolean = false,
    val showTagDialog: Boolean = false,
    val showContentDialog: Boolean = false,
    val editingTag: Tag? = null
)

@HiltViewModel
class GearLibraryViewModel @Inject constructor(private val repository: FotologueRepository) : ViewModel() {
    val allCameras: StateFlow<List<Camera>> = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFilms: StateFlow<List<Film>> = repository.allFilms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLenses: StateFlow<List<Lens>> = repository.allLenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allLensesWithCameras: StateFlow<List<LensWithCameras>> = combine(
        repository.allLenses,
        repository.allCameras,
        repository.allCameraLensCrossRefs
    ) { lenses, cameras, crossRefs ->
        lenses.map { lens ->
            val associatedCameraIds = crossRefs.filter { it.lensId == lens.id }.map { it.cameraId }
            val associatedCameras = cameras.filter { associatedCameraIds.contains(it.id) }
            LensWithCameras(lens, associatedCameras)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTeleconverters: StateFlow<List<Teleconverter>> = repository.allTeleconverters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFilters: StateFlow<List<Filter>> = repository.allFilters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFlashes: StateFlow<List<Flash>> = repository.allFlashes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCameraKits: StateFlow<List<CameraKitWithGear>> = repository.allCameraKitsWithGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTags: StateFlow<List<Tag>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allSubjects: StateFlow<List<Tag>> = repository.allTags
        .map { tags -> tags.filter { it.category == Tag.CATEGORY_SUBJECT }.sortedBy { it.name.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContent: StateFlow<List<Tag>> = repository.allTags
        .map { tags -> tags.filter { it.category == Tag.CATEGORY_CONTENT }.sortedBy { it.name.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(GearLibraryUiState())
    val uiState: StateFlow<GearLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.insertDefaultTagsIfNeeded()
        }
    }

    fun toggleCameraSelection(cameraId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedCameraIds.contains(cameraId)) {
                currentState.selectedCameraIds - cameraId
            } else {
                currentState.selectedCameraIds + cameraId
            }
            currentState.updateSelectionMode(selectedCameraIds = newSelection)
        }
    }

    fun toggleFilmSelection(filmId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFilmIds.contains(filmId)) {
                currentState.selectedFilmIds - filmId
            } else {
                currentState.selectedFilmIds + filmId
            }
            currentState.updateSelectionMode(selectedFilmIds = newSelection)
        }
    }

    fun toggleLensSelection(lensId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedLensIds.contains(lensId)) {
                currentState.selectedLensIds - lensId
            } else {
                currentState.selectedLensIds + lensId
            }
            currentState.updateSelectionMode(selectedLensIds = newSelection)
        }
    }

    fun toggleTeleconverterSelection(teleconverterId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedTeleconverterIds.contains(teleconverterId)) {
                currentState.selectedTeleconverterIds - teleconverterId
            } else {
                currentState.selectedTeleconverterIds + teleconverterId
            }
            currentState.updateSelectionMode(selectedTeleconverterIds = newSelection)
        }
    }

    fun toggleFilterSelection(filterId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFilterIds.contains(filterId)) {
                currentState.selectedFilterIds - filterId
            } else {
                currentState.selectedFilterIds + filterId
            }
            currentState.updateSelectionMode(selectedFilterIds = newSelection)
        }
    }

    fun toggleFlashSelection(flashId: Long) {
        _uiState.update { currentState ->
            val newSelection = if (currentState.selectedFlashIds.contains(flashId)) {
                currentState.selectedFlashIds - flashId
            } else {
                currentState.selectedFlashIds + flashId
            }
            currentState.updateSelectionMode(selectedFlashIds = newSelection)
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

    private fun GearLibraryUiState.updateSelectionMode(
        selectedCameraIds: Set<Long> = this.selectedCameraIds,
        selectedFilmIds: Set<Long> = this.selectedFilmIds,
        selectedLensIds: Set<Long> = this.selectedLensIds,
        selectedTeleconverterIds: Set<Long> = this.selectedTeleconverterIds,
        selectedFilterIds: Set<Long> = this.selectedFilterIds,
        selectedFlashIds: Set<Long> = this.selectedFlashIds,
        selectedKitIds: Set<Long> = this.selectedKitIds,
        selectedSubjectIds: Set<Long> = this.selectedSubjectIds,
        selectedContentIds: Set<Long> = this.selectedContentIds
    ): GearLibraryUiState {
        val hasSelection = selectedCameraIds.isNotEmpty() || 
                           selectedFilmIds.isNotEmpty() || 
                           selectedLensIds.isNotEmpty() || 
                           selectedTeleconverterIds.isNotEmpty() || 
                           selectedFilterIds.isNotEmpty() ||
                           selectedFlashIds.isNotEmpty() ||
                           selectedKitIds.isNotEmpty() ||
                           selectedSubjectIds.isNotEmpty() ||
                           selectedContentIds.isNotEmpty()
        return copy(
            selectedCameraIds = selectedCameraIds,
            selectedFilmIds = selectedFilmIds,
            selectedLensIds = selectedLensIds,
            selectedTeleconverterIds = selectedTeleconverterIds,
            selectedFilterIds = selectedFilterIds,
            selectedFlashIds = selectedFlashIds,
            selectedKitIds = selectedKitIds,
            selectedSubjectIds = selectedSubjectIds,
            selectedContentIds = selectedContentIds,
            isSelectionMode = hasSelection
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
                selectedKitIds = emptySet(),
                selectedSubjectIds = emptySet(),
                selectedContentIds = emptySet(),
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
            val kitIds = uiState.value.selectedKitIds.toList()
            val subjectIds = uiState.value.selectedSubjectIds.toList()
            val contentIds = uiState.value.selectedContentIds.toList()
            
            if (cameraIds.isNotEmpty()) repository.deleteCameras(cameraIds)
            if (filmIds.isNotEmpty()) repository.deleteFilms(filmIds)
            if (lensIds.isNotEmpty()) repository.deleteLenses(lensIds)
            if (teleconverterIds.isNotEmpty()) repository.deleteTeleconverters(teleconverterIds)
            if (filterIds.isNotEmpty()) repository.deleteFilters(filterIds)
            if (flashIds.isNotEmpty()) repository.deleteFlashes(flashIds)
            if (kitIds.isNotEmpty()) repository.deleteCameraKits(kitIds)
            if (subjectIds.isNotEmpty()) repository.deleteTags(subjectIds)
            if (contentIds.isNotEmpty()) repository.deleteTags(contentIds)
            
            clearSelection()
        }
    }

    fun showAddTagDialog() {
        _uiState.update { it.copy(showTagDialog = true, editingTag = null) }
    }

    fun showAddContentDialog(parentId: Long? = null) {
        _uiState.update { it.copy(showContentDialog = true, editingTag = null) }
    }

    fun showEditTagDialog(tag: Tag) {
        if (tag.category == Tag.CATEGORY_SUBJECT) {
            _uiState.update { it.copy(showTagDialog = true, editingTag = tag) }
        } else {
            _uiState.update { it.copy(showContentDialog = true, editingTag = tag) }
        }
    }

    fun dismissTagDialog() {
        _uiState.update { it.copy(showTagDialog = false, showContentDialog = false, editingTag = null) }
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
        _uiState.update { it.copy(selectedCategoryIndex = index) }
    }

    fun selectAll() {
        _uiState.update { currentState ->
            when (currentState.selectedCategoryIndex) {
                0 -> currentState.updateSelectionMode(selectedKitIds = allCameraKits.value.map { it.cameraKit.id }.toSet())
                1 -> currentState.updateSelectionMode(selectedCameraIds = allCameras.value.map { it.id }.toSet())
                2 -> currentState.updateSelectionMode(selectedLensIds = allLenses.value.map { it.id }.toSet())
                3 -> {
                    currentState.updateSelectionMode(
                        selectedTeleconverterIds = allTeleconverters.value.map { it.id }.toSet(),
                        selectedFilterIds = allFilters.value.map { it.id }.toSet(),
                        selectedFlashIds = allFlashes.value.map { it.id }.toSet()
                    )
                }
                4 -> currentState.updateSelectionMode(selectedFilmIds = allFilms.value.map { it.id }.toSet())
                5 -> currentState.updateSelectionMode(selectedSubjectIds = allSubjects.value.map { it.id }.toSet())
                6 -> currentState.updateSelectionMode(selectedContentIds = allContent.value.map { it.id }.toSet())
                else -> currentState
            }
        }
    }
}
