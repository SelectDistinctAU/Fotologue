package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.LogEntryWithGear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val repository: FotologueRepository
) : ViewModel() {
    private val _logId = MutableStateFlow<Long?>(null)

    fun setLogId(id: Long) {
        _logId.value = id
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val logEntry: StateFlow<LogEntryWithGear?> = _logId.flatMapLatest { id ->
        if (id != null) {
            repository.getLogEntryWithGearById(id)
        } else {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
