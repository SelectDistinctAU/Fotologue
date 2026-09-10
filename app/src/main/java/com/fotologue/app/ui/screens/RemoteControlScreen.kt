package com.fotologue.app.ui.screens

import android.Manifest
import com.fotologue.core.designsystem.FotologueTheme
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.connectivity.BleConnectionState
import com.fotologue.app.ui.viewmodel.RemoteControlViewModel
import com.fotologue.app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RemoteControlScreen(
    viewModel: RemoteControlViewModel,
    onBack: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val messageLog by viewModel.messageLog.collectAsStateWithLifecycle()
    
    val bleServiceUuid by viewModel.bleServiceUuid.collectAsStateWithLifecycle()
    val bleCharacteristicUuid by viewModel.bleCharacteristicUuid.collectAsStateWithLifecycle()
    val bleDeviceName by viewModel.bleDeviceName.collectAsStateWithLifecycle()

    RemoteControlContent(
        connectionState = connectionState,
        messageLog = messageLog,
        bleServiceUuid = bleServiceUuid,
        bleCharacteristicUuid = bleCharacteristicUuid,
        bleDeviceName = bleDeviceName,
        onBack = onBack,
        onConnect = { viewModel.connect() },
        onDisconnect = { viewModel.disconnect() },
        onClearLog = { viewModel.clearLog() },
        onUpdateServiceUuid = { viewModel.updateServiceUuid(it) },
        onUpdateCharacteristicUuid = { viewModel.updateCharacteristicUuid(it) },
        onUpdateDeviceName = { viewModel.updateDeviceName(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RemoteControlContent(
    connectionState: BleConnectionState,
    messageLog: List<String>,
    bleServiceUuid: String,
    bleCharacteristicUuid: String,
    bleDeviceName: String,
    onBack: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onClearLog: () -> Unit,
    onUpdateServiceUuid: (String) -> Unit,
    onUpdateCharacteristicUuid: (String) -> Unit,
    onUpdateDeviceName: (String) -> Unit
) {
    val context = LocalContext.current
    val bluetoothManager = remember { context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    val bluetoothAdapter = remember { bluetoothManager.adapter }
    
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    DisposableEffect(Unit) {
        isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        onDispose { }
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.remote_control_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = onClearLog) {
                        Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_clear_log))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (!permissionsState.allPermissionsGranted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.bluetooth_permission_required))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text(stringResource(R.string.action_grant_permissions))
                    }
                }
            } else if (!isBluetoothEnabled) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.bluetooth_disabled))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { 
                        isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
                    }) {
                        Text(stringResource(R.string.action_check_again))
                    }
                }
            } else {
                ConnectionStatusCard(
                    state = connectionState,
                    deviceName = bleDeviceName,
                    onConnect = onConnect,
                    onDisconnect = onDisconnect
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.header_ble_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedTextField(
                    value = bleDeviceName,
                    onValueChange = onUpdateDeviceName,
                    label = { Text(stringResource(R.string.label_device_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = bleServiceUuid,
                    onValueChange = onUpdateServiceUuid,
                    label = { Text(stringResource(R.string.label_service_uuid)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = bleCharacteristicUuid,
                    onValueChange = onUpdateCharacteristicUuid,
                    label = { Text(stringResource(R.string.label_characteristic_uuid)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.header_message_log),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        reverseLayout = true,
                        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        items(messageLog.reversed()) { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemoteControlContentPreview() {
    FotologueTheme {
        RemoteControlContent(
            connectionState = BleConnectionState.CONNECTED,
            messageLog = listOf("Connected", "Battery: 90%", "Trigger pressed"),
            bleServiceUuid = "4fafc201-1fb5-459e-8fcc-c5c9c331914b",
            bleCharacteristicUuid = "beb5483e-36e1-4688-b7f5-ea073791391d",
            bleDeviceName = "FotologueCameraTrigger",
            onBack = {},
            onConnect = {},
            onDisconnect = {},
            onClearLog = {},
            onUpdateServiceUuid = {},
            onUpdateCharacteristicUuid = {},
            onUpdateDeviceName = {}
        )
    }
}

@Composable
fun ConnectionStatusCard(
    state: BleConnectionState,
    deviceName: String,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val (statusText, iconRes, color) = when (state) {
        BleConnectionState.DISCONNECTED -> Triple(stringResource(R.string.status_disconnected), R.drawable.ic_bluetooth, MaterialTheme.colorScheme.outline)
        BleConnectionState.SCANNING -> Triple(stringResource(R.string.status_scanning, deviceName), R.drawable.ic_bluetooth_searching, MaterialTheme.colorScheme.secondary)
        BleConnectionState.CONNECTING -> Triple(stringResource(R.string.status_connecting), R.drawable.ic_bluetooth_searching, MaterialTheme.colorScheme.secondary)
        BleConnectionState.CONNECTED -> Triple(stringResource(R.string.status_connected, deviceName), R.drawable.ic_bluetooth_connected, MaterialTheme.colorScheme.primary)
        BleConnectionState.ERROR -> Triple(stringResource(R.string.status_error), R.drawable.ic_bluetooth, MaterialTheme.colorScheme.error)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(iconRes), contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = statusText, style = MaterialTheme.typography.titleMedium, color = color)
            }
            
            if (state == BleConnectionState.CONNECTED || state == BleConnectionState.CONNECTING || state == BleConnectionState.SCANNING) {
                TextButton(onClick = onDisconnect) {
                    Text(stringResource(R.string.action_disconnect))
                }
            } else {
                Button(onClick = onConnect) {
                    Text(stringResource(R.string.action_connect))
                }
            }
        }
    }
}
