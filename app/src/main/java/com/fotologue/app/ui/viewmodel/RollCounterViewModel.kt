package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RollCounterUiState(
    val currentRoll: Int = 1,
    val currentFrame: Int = 1,
    val expectedExposures: Int = 36,
    val isHalfFrame: Boolean = false,
    val activeCamera: Camera? = null,
    val activeFilm: Film? = null,
    val hasActiveSession: Boolean = false
)

@HiltViewModel
class RollCounterViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RollCounterUiState())
    val uiState: StateFlow<RollCounterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.lastCameraId,
                settingsRepository.lastFilmId
            ) { cameraId, filmId ->
                if (cameraId != null && filmId != null) {
                    val camera = repository.getCameraById(cameraId).first()
                    val film = repository.getFilmById(filmId).first()
                    
                    if (camera != null && film != null) {
                        repository.getFilmRollSession(cameraId, filmId).collect { session ->
                            val currentSession = session ?: FilmRollSession(cameraId, filmId)
                            val expected = film.expectedExposures
                            val isHalf = camera.isHalfFrame
                            
                            _uiState.update { it.copy(
                                currentRoll = currentSession.rollNumber,
                                currentFrame = currentSession.nextFrameNumber,
                                expectedExposures = if (isHalf) expected * 2 else expected,
                                isHalfFrame = isHalf,
                                activeCamera = camera,
                                activeFilm = film,
                                hasActiveSession = true
                            ) }
                        }
                    } else {
                        _uiState.update { it.copy(hasActiveSession = false) }
                    }
                } else {
                    _uiState.update { it.copy(hasActiveSession = false) }
                }
            }.collect {}
        }
    }

    fun updateRoll(roll: Int) {
        val state = _uiState.value
        if (!state.hasActiveSession) return
        viewModelScope.launch {
            repository.updateFilmRollSession(
                FilmRollSession(
                    cameraId = state.activeCamera!!.id,
                    filmId = state.activeFilm!!.id,
                    rollNumber = roll,
                    nextFrameNumber = state.currentFrame
                )
            )
        }
    }

    fun updateFrame(frame: Int) {
        val state = _uiState.value
        if (!state.hasActiveSession) return
        viewModelScope.launch {
            repository.updateFilmRollSession(
                FilmRollSession(
                    cameraId = state.activeCamera!!.id,
                    filmId = state.activeFilm!!.id,
                    rollNumber = state.currentRoll,
                    nextFrameNumber = frame
                )
            )
        }
    }

    fun finishRoll() {
        val state = _uiState.value
        if (!state.hasActiveSession) return
        viewModelScope.launch {
            repository.updateFilmRollSession(
                FilmRollSession(
                    cameraId = state.activeCamera!!.id,
                    filmId = state.activeFilm!!.id,
                    rollNumber = state.currentRoll + 1,
                    nextFrameNumber = 1
                )
            )
        }
    }

    fun resetRollCount() {
        val state = _uiState.value
        if (!state.hasActiveSession) return
        viewModelScope.launch {
            repository.updateFilmRollSession(
                FilmRollSession(
                    cameraId = state.activeCamera!!.id,
                    filmId = state.activeFilm!!.id,
                    rollNumber = 1,
                    nextFrameNumber = 1
                )
            )
        }
    }
}
