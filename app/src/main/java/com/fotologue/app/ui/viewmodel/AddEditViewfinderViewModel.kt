package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.Viewfinder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditViewfinderUiState(
    val name: String = "",
    val modelNumber: String = ""
)

@HiltViewModel
class AddEditViewfinderViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditViewfinderUiState(
        name = savedStateHandle.get<String>("name") ?: "",
        modelNumber = savedStateHandle.get<String>("modelNumber") ?: ""
    ))
    val uiState: StateFlow<AddEditViewfinderUiState> = _uiState.asStateFlow()

    var viewfinderId: Long? = savedStateHandle.get<Long>("viewfinderId")
        private set

    fun setViewfinderId(id: Long?) {
        if (viewfinderId == id) return
        viewfinderId = id
        savedStateHandle["viewfinderId"] = id
        loadViewfinder()
    }

    private fun loadViewfinder() {
        val id = viewfinderId ?: return
        viewModelScope.launch {
            val viewfinder = repository.getViewfinderById(id).firstOrNull()
            viewfinder?.let {
                _uiState.update { state ->
                    state.copy(
                        name = savedStateHandle.get<String>("name") ?: it.name,
                        modelNumber = savedStateHandle.get<String>("modelNumber") ?: it.modelNumber
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        savedStateHandle["name"] = value
        _uiState.update { it.copy(name = value) }
    }

    fun onModelNumberChange(value: String) {
        savedStateHandle["modelNumber"] = value
        _uiState.update { it.copy(modelNumber = value) }
    }

    fun saveViewfinder(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            val viewfinder = Viewfinder(
                id = viewfinderId ?: 0,
                name = state.name.trim(),
                modelNumber = state.modelNumber.trim()
            )

            if (viewfinderId == null) {
                repository.insertViewfinder(viewfinder)
            } else {
                repository.updateViewfinder(viewfinder)
            }
            onSuccess()
        }
    }
}
