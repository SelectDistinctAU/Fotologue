package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.Flash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditFlashUiState(
    val make: String = "",
    val model: String = "",
    val guideNumber: String = ""
)

@HiltViewModel
class AddEditFlashViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditFlashUiState(
        make = savedStateHandle.get<String>("make") ?: "",
        model = savedStateHandle.get<String>("model") ?: "",
        guideNumber = savedStateHandle.get<String>("guideNumber") ?: ""
    ))
    val uiState: StateFlow<AddEditFlashUiState> = _uiState.asStateFlow()

    var flashId: Long? = savedStateHandle.get<Long>("flashId")
        private set

    fun setFlashId(id: Long?) {
        if (flashId == id) return
        flashId = id
        savedStateHandle["flashId"] = id
        loadFlash()
    }

    private fun loadFlash() {
        val id = flashId ?: return
        viewModelScope.launch {
            val flash = repository.getFlashById(id).firstOrNull()
            flash?.let {
                _uiState.update { state ->
                    state.copy(
                        make = savedStateHandle.get<String>("make") ?: it.make,
                        model = savedStateHandle.get<String>("model") ?: it.model,
                        guideNumber = savedStateHandle.get<String>("guideNumber") ?: it.guideNumber.toString()
                    )
                }
            }
        }
    }

    fun onMakeChange(value: String) {
        savedStateHandle["make"] = value
        _uiState.update { it.copy(make = value) }
    }

    fun onModelChange(value: String) {
        savedStateHandle["model"] = value
        _uiState.update { it.copy(model = value) }
    }

    fun onGuideNumberChange(value: String) {
        savedStateHandle["guideNumber"] = value
        _uiState.update { it.copy(guideNumber = value) }
    }

    fun saveFlash(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val flash = Flash(
                id = flashId ?: 0,
                make = state.make.trim(),
                model = state.model.trim(),
                guideNumber = state.guideNumber.toIntOrNull() ?: 0
            )

            if (flashId == null) {
                repository.insertFlash(flash)
            } else {
                repository.updateFlash(flash)
            }
            onSuccess()
        }
    }
}
