package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.Film
import com.fotologue.app.data.FilmPreset
import com.fotologue.app.data.FilmPresets
import com.fotologue.app.data.FotologueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditFilmUiState(
    val name: String = "",
    val abbreviation: String = "",
    val iso: String = "400",
    val type: String = Film.TYPE_COLOR_NEGATIVE,
    val expectedExposures: String = "36",
    val format: String = Film.FORMAT_35MM,
    val isNameValid: Boolean = true,
    val isTypeValid: Boolean = true,
    val filteredPresets: List<FilmPreset> = emptyList()
)

@HiltViewModel
class AddEditFilmViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditFilmUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        abbreviation = savedStateHandle.get<String>("abbreviation") ?: "",
        iso = savedStateHandle.get<String>("iso") ?: "400",
        type = savedStateHandle.get<String>("type") ?: Film.TYPE_COLOR_NEGATIVE,
        expectedExposures = savedStateHandle.get<String>("expectedExposures") ?: "36",
        format = savedStateHandle.get<String>("format") ?: Film.FORMAT_35MM
    ))
    val uiState: StateFlow<AddEditFilmUiState> = _uiState.asStateFlow()

    var filmId: Long? = savedStateHandle.get<Long>("filmId")
        private set

    fun setFilmId(id: Long?) {
        if (filmId == id) return
        filmId = id
        savedStateHandle["filmId"] = id
        loadFilm()
    }

    private fun loadFilm() {
        val id = filmId ?: return
        viewModelScope.launch {
            repository.getFilmById(id).collect { film ->
                film?.let { filmData ->
                    _uiState.update { 
                        it.copy(
                            name = savedStateHandle.get<String>("name") ?: filmData.name, 
                            abbreviation = savedStateHandle.get<String>("abbreviation") ?: filmData.abbreviation,
                            iso = savedStateHandle.get<String>("iso") ?: filmData.iso.toString(), 
                            type = savedStateHandle.get<String>("type") ?: filmData.type,
                            expectedExposures = savedStateHandle.get<String>("expectedExposures") ?: filmData.expectedExposures.toString(),
                            format = savedStateHandle.get<String>("format") ?: filmData.format,
                            isTypeValid = (savedStateHandle.get<String>("type") ?: filmData.type) in Film.ALL_TYPES
                        ) 
                    }
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        savedStateHandle["name"] = newName
        val filtered = if (newName.length >= 2) {
            FilmPresets.presets.filter { 
                it.displayName.contains(newName, ignoreCase = true) 
            }
        } else {
            emptyList()
        }
        
        // Suggest abbreviation if empty
        val currentAbbr = _uiState.value.abbreviation
        val suggestedAbbr = if (currentAbbr.isBlank() && newName.length >= 3) {
            newName.take(4).uppercase().filter { it.isLetterOrDigit() }
        } else currentAbbr

        _uiState.update { it.copy(
            name = newName, 
            abbreviation = suggestedAbbr,
            isNameValid = newName.isNotBlank(), 
            filteredPresets = filtered
        ) }
        savedStateHandle["abbreviation"] = suggestedAbbr
    }

    fun onAbbreviationChange(newAbbr: String) {
        val filteredAbbr = newAbbr.uppercase().take(6).filter { it.isLetterOrDigit() }
        savedStateHandle["abbreviation"] = filteredAbbr
        _uiState.update { it.copy(abbreviation = filteredAbbr) }
    }

    fun onPresetSelected(preset: FilmPreset) {
        val newName = preset.displayName
        val newAbbr = preset.abbreviation
        val newIso = preset.iso.toString()
        val newType = preset.type
        val newExpected = preset.expectedExposures.toString()
        
        savedStateHandle["name"] = newName
        savedStateHandle["abbreviation"] = newAbbr
        savedStateHandle["iso"] = newIso
        savedStateHandle["type"] = newType
        savedStateHandle["expectedExposures"] = newExpected
        
        _uiState.update { 
            it.copy(
                name = newName,
                abbreviation = newAbbr,
                iso = newIso,
                type = newType,
                expectedExposures = newExpected,
                filteredPresets = emptyList(),
                isNameValid = true,
                isTypeValid = true
            )
        }
    }

    fun onIsoChange(newIso: String) {
        savedStateHandle["iso"] = newIso
        _uiState.update { it.copy(iso = newIso) }
    }

    fun onExpectedExposuresChange(newExposures: String) {
        savedStateHandle["expectedExposures"] = newExposures
        _uiState.update { it.copy(expectedExposures = newExposures) }
    }

    fun onTypeChange(newType: String) {
        savedStateHandle["type"] = newType
        _uiState.update { it.copy(type = newType, isTypeValid = newType in Film.ALL_TYPES) }
    }

    fun onFormatChange(newFormat: String) {
        savedStateHandle["format"] = newFormat
        val defaultExposures = when (newFormat) {
            Film.FORMAT_120 -> "12"
            Film.FORMAT_LARGE -> "1"
            else -> "36"
        }
        _uiState.update { it.copy(format = newFormat, expectedExposures = defaultExposures) }
        savedStateHandle["expectedExposures"] = defaultExposures
    }

    fun saveFilm(onComplete: () -> Unit) {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(isNameValid = false) }
            return
        }

        viewModelScope.launch {
            val film = Film(
                id = filmId ?: 0,
                name = _uiState.value.name.trim(),
                abbreviation = _uiState.value.abbreviation.trim().ifBlank { _uiState.value.name.take(3).uppercase() },
                iso = _uiState.value.iso.toIntOrNull() ?: 400,
                type = _uiState.value.type.trim(),
                expectedExposures = _uiState.value.expectedExposures.toIntOrNull() ?: 36,
                format = _uiState.value.format
            )
            if (filmId == null) {
                repository.insertFilm(film)
            } else {
                repository.updateFilm(film)
            }
            onComplete()
        }
    }
}
