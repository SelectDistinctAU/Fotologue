package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.Teleconverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTeleconverterUiState(
    val name: String = "",
    val magnification: String = "1.4",
    val lightLossStops: String = "1.0"
)

@HiltViewModel
class AddEditTeleconverterViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTeleconverterUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        magnification = savedStateHandle.get<String>("magnification") ?: "1.4",
        lightLossStops = savedStateHandle.get<String>("lightLossStops") ?: "1.0"
    ))
    val uiState: StateFlow<AddEditTeleconverterUiState> = _uiState.asStateFlow()

    var teleconverterId: Long? = savedStateHandle.get<Long>("teleconverterId")
        private set

    fun setTeleconverterId(id: Long?) {
        if (teleconverterId == id) return
        teleconverterId = id
        savedStateHandle["teleconverterId"] = id
        loadTeleconverter()
    }

    private fun loadTeleconverter() {
        val id = teleconverterId ?: return
        viewModelScope.launch {
            val tc = repository.getTeleconverterById(id).firstOrNull()
            tc?.let {
                _uiState.update { state ->
                    state.copy(
                        name = savedStateHandle.get<String>("name") ?: it.name,
                        magnification = savedStateHandle.get<String>("magnification") ?: it.magnification.toString(),
                        lightLossStops = savedStateHandle.get<String>("lightLossStops") ?: it.lightLossStops.toString()
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        savedStateHandle["name"] = value
        _uiState.update { it.copy(name = value) }
    }

    fun onMagnificationChange(value: String) {
        savedStateHandle["magnification"] = value
        _uiState.update { it.copy(magnification = value) }
    }

    fun onLightLossStopsChange(value: String) {
        savedStateHandle["lightLossStops"] = value
        _uiState.update { it.copy(lightLossStops = value) }
    }

    fun saveTeleconverter(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val tc = Teleconverter(
                id = teleconverterId ?: 0,
                name = state.name.trim(),
                magnification = state.magnification.toDoubleOrNull() ?: 1.4,
                lightLossStops = state.lightLossStops.toDoubleOrNull() ?: 1.0
            )

            if (teleconverterId == null) {
                repository.insertTeleconverter(tc)
            } else {
                repository.updateTeleconverter(tc)
            }
            onSuccess()
        }
    }
}
