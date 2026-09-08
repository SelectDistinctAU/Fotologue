package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.Camera
import com.fotologue.app.data.Film
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.SettingsRepository
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
    val activeFilm: Film? = null
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
                settingsRepository.currentRoll,
                settingsRepository.currentFrame,
                settingsRepository.lastCameraId,
                settingsRepository.lastFilmId
            ) { roll, frame, cameraId, filmId ->
                val camera = cameraId?.let { repository.getCameraById(it).first() }
                val film = filmId?.let { repository.getFilmById(it).first() }
                
                val expected = film?.expectedExposures ?: 36
                val isHalf = camera?.isHalfFrame ?: false
                
                RollCounterUiState(
                    currentRoll = roll,
                    currentFrame = frame,
                    expectedExposures = if (isHalf) expected * 2 else expected,
                    isHalfFrame = isHalf,
                    activeCamera = camera,
                    activeFilm = film
                )
            }.collect { _uiState.value = it }
        }
    }

    fun updateRoll(roll: Int) {
        viewModelScope.launch {
            settingsRepository.setCurrentRollAndFrame(roll, _uiState.value.currentFrame)
        }
    }

    fun updateFrame(frame: Int) {
        viewModelScope.launch {
            settingsRepository.setCurrentRollAndFrame(_uiState.value.currentRoll, frame)
        }
    }

    fun finishRoll() {
        viewModelScope.launch {
            val nextRoll = _uiState.value.currentRoll + 1
            settingsRepository.setCurrentRollAndFrame(nextRoll, 1)
        }
    }

    fun resetRollCount() {
        viewModelScope.launch {
            settingsRepository.setCurrentRollAndFrame(1, 1)
        }
    }
}
