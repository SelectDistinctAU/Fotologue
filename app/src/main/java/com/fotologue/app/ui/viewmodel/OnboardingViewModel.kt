package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.ConfigurationManager
import com.fotologue.core.data.SettingsRepository
import com.fotologue.core.data.PictureExportSize
import android.net.Uri
import android.content.ContentResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val hasCamera: Boolean = false,
    val hasLens: Boolean = false,
    val hasFilm: Boolean = false,
    val isOnboardingComplete: Boolean = false,
    val isThumbnailEnabled: Boolean = false,
    val isFaceDetectionEnabled: Boolean = false,
    val faceDetectionDisclosureShown: Boolean = false,
    val pictureExportSize: PictureExportSize = PictureExportSize.NATURAL,
    val isReadyToGo: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
    private val configurationManager: ConfigurationManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine<Any?, OnboardingUiState>(
                listOf(
                    repository.hasCamera,
                    repository.hasLens,
                    repository.hasFilm,
                    repository.isOnboardingComplete,
                    settingsRepository.isThumbnailEnabled,
                    settingsRepository.isFaceDetectionEnabled,
                    settingsRepository.pictureExportSize,
                    settingsRepository.faceDetectionDisclosureShown
                )
            ) { args: Array<Any?> ->
                OnboardingUiState(
                    hasCamera = args[0] as Boolean,
                    hasLens = args[1] as Boolean,
                    hasFilm = args[2] as Boolean,
                    isOnboardingComplete = args[3] as Boolean,
                    isThumbnailEnabled = args[4] as Boolean,
                    isFaceDetectionEnabled = args[5] as Boolean,
                    pictureExportSize = args[6] as PictureExportSize,
                    faceDetectionDisclosureShown = args[7] as Boolean,
                    isReadyToGo = (args[0] as Boolean) && (args[1] as Boolean) && (args[2] as Boolean)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            ensureDefaultTags()
            settingsRepository.setWelcomeDismissed(true)
        }
    }

    private fun ensureDefaultTags() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDefaultTagsIfNeeded()
        }
    }

    fun toggleThumbnail(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setThumbnailEnabled(enabled)
        }
    }

    fun toggleFaceDetection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFaceDetectionEnabled(enabled)
        }
    }

    fun setFaceDetectionDisclosureShown() {
        viewModelScope.launch {
            settingsRepository.setFaceDetectionDisclosureShown(shown = true)
        }
    }

    fun setPictureExportSize(size: PictureExportSize) {
        viewModelScope.launch {
            settingsRepository.setPictureExportSize(size)
        }
    }

    fun restoreBackup(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    configurationManager.importConfig(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
