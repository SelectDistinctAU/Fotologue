package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.app.connectivity.BleConnectionState
import com.fotologue.app.connectivity.BleManager
import com.fotologue.app.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(
    private val bleManager: BleManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val connectionState = bleManager.connectionState
    
    private val _messageLog = MutableStateFlow<List<String>>(emptyList())
    val messageLog: StateFlow<List<String>> = _messageLog.asStateFlow()

    val bleServiceUuid = settingsRepository.bleServiceUuid
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val bleCharacteristicUuid = settingsRepository.bleCharacteristicUuid
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
        
    val bleDeviceName = settingsRepository.bleDeviceName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        viewModelScope.launch {
            bleManager.receivedData.collect { message ->
                _messageLog.update { (it + message).takeLast(50) }
            }
        }
    }

    fun connect() {
        bleManager.connect()
    }

    fun disconnect() {
        bleManager.disconnect()
    }

    fun clearLog() {
        _messageLog.value = emptyList()
    }

    fun updateServiceUuid(uuid: String) {
        viewModelScope.launch {
            settingsRepository.setBleServiceUuid(uuid)
        }
    }

    fun updateCharacteristicUuid(uuid: String) {
        viewModelScope.launch {
            settingsRepository.setBleCharacteristicUuid(uuid)
        }
    }

    fun updateDeviceName(name: String) {
        viewModelScope.launch {
            settingsRepository.setBleDeviceName(name)
        }
    }
}
