package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val isOnboardingComplete: Flow<Boolean> = repository.isOnboardingComplete
    val isSafelightActive: Flow<Boolean> = repository.isSafelightActive
    val appTheme: Flow<String> = settingsRepository.appTheme

    fun setSafelightActive(active: Boolean) {
        repository.setSafelightActive(active)
    }

    fun toggleSafelight() {
        repository.toggleSafelight()
    }
}
