package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.fotologue.app.data.FotologueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: FotologueRepository
) : ViewModel() {
    val isOnboardingComplete: Flow<Boolean> = repository.isOnboardingComplete
}
