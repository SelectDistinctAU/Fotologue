package com.fotologue.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.LogEntryWithGear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val repository: FotologueRepository
) : ViewModel() {

    private val _logId = MutableStateFlow<Long?>(null)

    fun setLogId(id: Long) {
        _logId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val entryWithGear: StateFlow<LogEntryWithGear?> = _logId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getLogEntryWithGearById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getShareText(context: Context, gear: LogEntryWithGear): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(gear.logEntry.timestamp))
        return "Exposure Log\n" +
                "Date: $dateStr\n" +
                "Camera: ${gear.camera?.name ?: "Unknown"}\n" +
                "Lens: ${gear.lens?.let { "${it.make} ${it.model}" } ?: "Unknown"}\n" +
                "Film: ${gear.film?.name ?: "Unknown"}\n" +
                "Exposure: ${gear.logEntry.exposureSettings.shutterSpeed} @ ${gear.logEntry.exposureSettings.aperture}"
    }
}
