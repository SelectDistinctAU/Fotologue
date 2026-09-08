package com.fotologue.app.connectivity

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import com.fotologue.app.data.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

enum class BleConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

@SuppressLint("MissingPermission")
class BleManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    private val _connectionState = MutableStateFlow(BleConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BleConnectionState> = _connectionState.asStateFlow()

    private val _receivedData = MutableSharedFlow<String>(replay = 10)
    val receivedData: SharedFlow<String> = _receivedData.asSharedFlow()

    private var bluetoothGatt: BluetoothGatt? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    _connectionState.value = BleConnectionState.CONNECTING
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    _connectionState.value = BleConnectionState.DISCONNECTED
                    gatt.close()
                    bluetoothGatt = null
                }
            } else {
                _connectionState.value = BleConnectionState.ERROR
                gatt.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                scope.launch {
                    val serviceUuidStr = settingsRepository.bleServiceUuid.first()
                    val charUuidStr = settingsRepository.bleCharacteristicUuid.first()
                    
                    val service = gatt.getService(UUID.fromString(serviceUuidStr))
                    val characteristic = service?.getCharacteristic(UUID.fromString(charUuidStr))
                    
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)
                        val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        if (descriptor != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                            } else {
                                @Suppress("DEPRECATION")
                                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                @Suppress("DEPRECATION")
                                gatt.writeDescriptor(descriptor)
                            }
                        }
                        _connectionState.value = BleConnectionState.CONNECTED
                    } else {
                        Log.e("BleManager", "Characteristic not found")
                        _connectionState.value = BleConnectionState.ERROR
                        disconnect()
                    }
                }
            } else {
                _connectionState.value = BleConnectionState.ERROR
                disconnect()
            }
        }

        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value?.toString(Charsets.UTF_8) ?: ""
            scope.launch {
                _receivedData.emit(data)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            val data = value.toString(Charsets.UTF_8)
            scope.launch {
                _receivedData.emit(data)
            }
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            bluetoothAdapter.bluetoothLeScanner?.stopScan(this)
            connectToDevice(device)
        }

        override fun onScanFailed(errorCode: Int) {
            _connectionState.value = BleConnectionState.ERROR
        }
    }

    fun connect() {
        if (_connectionState.value != BleConnectionState.DISCONNECTED && _connectionState.value != BleConnectionState.ERROR) {
            return
        }

        scope.launch {
            val deviceName = settingsRepository.bleDeviceName.first()
            _connectionState.value = BleConnectionState.SCANNING
            
            val scanner = bluetoothAdapter.bluetoothLeScanner
            if (scanner == null) {
                _connectionState.value = BleConnectionState.ERROR
                return@launch
            }

            val filter = ScanFilter.Builder().setDeviceName(deviceName).build()
            val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
            
            scanner.startScan(listOf(filter), settings, scanCallback)
            
            // Timeout after 30 seconds
            delay(30000)
            if (_connectionState.value == BleConnectionState.SCANNING) {
                scanner.stopScan(scanCallback)
                _connectionState.value = BleConnectionState.DISCONNECTED
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        _connectionState.value = BleConnectionState.CONNECTING
        @Suppress("DEPRECATION")
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
        _connectionState.value = BleConnectionState.DISCONNECTED
    }
}
