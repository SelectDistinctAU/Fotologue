package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditFilterUiState(
    val name: String = "",
    val type: String = "ND",
    val compensationStops: String = "0.0",
    val filterTypes: List<String> = listOf("ND", "Polarizer", "UV", "Color", "Graduated ND", "Other")
)

@HiltViewModel
class AddEditFilterViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditFilterUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        type = savedStateHandle.get<String>("type") ?: "ND",
        compensationStops = savedStateHandle.get<String>("compensationStops") ?: "0.0"
    ))
    val uiState: StateFlow<AddEditFilterUiState> = _uiState.asStateFlow()

    var filterId: Long? = savedStateHandle.get<Long>("filterId")
        private set

    fun setFilterId(id: Long?) {
        if (filterId == id) return
        filterId = id
        savedStateHandle["filterId"] = id
        loadFilter()
    }

    private fun loadFilter() {
        val id = filterId ?: return
        viewModelScope.launch {
            val filter = repository.getFilterById(id).firstOrNull()
            filter?.let {
                _uiState.update { state ->
                    state.copy(
                        name = savedStateHandle.get<String>("name") ?: it.name,
                        type = savedStateHandle.get<String>("type") ?: it.type,
                        compensationStops = savedStateHandle.get<String>("compensationStops") ?: it.compensationStops.toString()
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        savedStateHandle["name"] = value
        _uiState.update { it.copy(name = value) }
    }

    fun onTypeChange(value: String) {
        savedStateHandle["type"] = value
        _uiState.update { it.copy(type = value) }
    }

    fun onCompensationStopsChange(value: String) {
        savedStateHandle["compensationStops"] = value
        _uiState.update { it.copy(compensationStops = value) }
    }

    fun saveFilter(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val filter = Filter(
                id = filterId ?: 0,
                name = state.name.trim(),
                type = state.type.trim(),
                compensationStops = state.compensationStops.toDoubleOrNull() ?: 0.0
            )

            if (filterId == null) {
                repository.insertFilter(filter)
            } else {
                repository.updateFilter(filter)
            }
            onSuccess()
        }
    }
}
