package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.Film
import com.fotologue.app.ui.viewmodel.AddEditCameraViewModel
import com.fotologue.app.R
import com.fotologue.core.data.CameraSpec

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCameraScreen(
    viewModel: AddEditCameraViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var showCameraPresetsDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    if (showCameraPresetsDialog) {
        val categories = remember { listOf("All") + CameraType.entries.map { it.displayName } }
        var categoryDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCameraPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.frame_inspect_24px), contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Camera Body from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select a category filter or type to search camera presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // Single Source Camera Type Category Filter Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Camera Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = if (cat == selectedCategoryFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedCategoryFilter = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Cameras") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
                    )

                    HorizontalDivider()

                    val allPresets = listOf(
                        CameraSpec("Pentax LX", "LX", CameraType.FILM_SLR, "Pentax K", listOf("125s - 1/2000s, B")),
                        CameraSpec("Pentax ME Super", "ME Super", CameraType.FILM_SLR, "Pentax K", listOf("4s - 1/2000s, B")),
                        CameraSpec("Pentax K1000", "K1000", CameraType.FILM_SLR, "Pentax K", listOf("1s - 1/1000s, B")),
                        CameraSpec("Canon AE-1 Program", "AE-1P", CameraType.FILM_SLR, "Canon FD", listOf("2s - 1/1000s, B")),
                        CameraSpec("Canon F-1", "F-1", CameraType.FILM_SLR, "Canon FD", listOf("1s - 1/2000s, B")),
                        CameraSpec("Nikon F3", "F3", CameraType.FILM_SLR, "Nikon F", listOf("8s - 1/2000s, B")),
                        CameraSpec("Nikon FM2", "FM2", CameraType.FILM_SLR, "Nikon F", listOf("1s - 1/4000s, B")),
                        CameraSpec("Leica M3", "M3", CameraType.RANGEFINDER, "Leica M", listOf("1s - 1/1000s, B")),
                        CameraSpec("Leica M6", "M6", CameraType.RANGEFINDER, "Leica M", listOf("1s - 1/1000s, B")),
                        CameraSpec("Hasselblad 500C/M", "500C/M", CameraType.MEDIUM_FORMAT, "Hasselblad V", listOf("1s - 1/500s, B")),
                        CameraSpec("Yashica Mat-124G", "Mat-124G", CameraType.TLR, "Fixed", listOf("1s - 1/500s, B"))
                    )

                    val filteredPresets = if (selectedCategoryFilter == "All") {
                        allPresets
                    } else {
                        allPresets.filter { it.type.displayName.contains(selectedCategoryFilter, ignoreCase = true) }
                    }

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No camera presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val spec = filteredPresets[idx]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.applySpec(spec)
                                            showCameraPresetsDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(spec.name, fontWeight = FontWeight.Bold)
                                            Text("${spec.type.displayName} • Mount: ${spec.mount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.applySpec(spec)
                                                showCameraPresetsDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Select", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showCameraPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.cameraId == null) stringResource(R.string.add_camera) else stringResource(R.string.edit_camera)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveCamera(onBack) }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showCameraPresetsDialog = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }

            // Field 1: Brand / Manufacturer
            OutlinedTextField(
                value = uiState.make,
                onValueChange = viewModel::onMakeChange,
                label = { Text("Brand / Manufacturer") },
                placeholder = { Text("e.g. Pentax, Nikon, Canon, Leica") },
                modifier = Modifier.fillMaxWidth()
            )

            // Field 2: Model Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Model Name") },
                placeholder = { Text("e.g. LX, ME Super, K1000, AE-1 Program") },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isNameValid,
                supportingText = if (!uiState.isNameValid) {
                    { Text(stringResource(R.string.error_name_empty)) }
                } else null
            )

            // Field 3: Serial Number (Optional)
            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = { Text("Serial Number (Optional)") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            // Field 5: Camera Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_camera_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CameraType.entries.forEach { typeOption ->
                        DropdownMenuItem(
                            text = { Text(typeOption.displayName) },
                            onClick = {
                                viewModel.onTypeChange(typeOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Field 6: Film Format Dropdown
            var formatExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = formatExpanded,
                onExpandedChange = { formatExpanded = !formatExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.format,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Film Format") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = formatExpanded,
                    onDismissRequest = { formatExpanded = false }
                ) {
                    Film.ALL_FORMATS.forEach { format ->
                        DropdownMenuItem(
                            text = { Text(format) },
                            onClick = {
                                viewModel.onFormatChange(format)
                                formatExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Hint: If your camera uses multiple film formats (e.g. interchangeable backs), add it once for each format.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Field 7: Lens Mount
            if (uiState.type.isInterchangeable) {
                OutlinedTextField(
                    value = uiState.mount,
                    onValueChange = viewModel::onMountChange,
                    label = { Text("Lens Mount") },
                    placeholder = { Text("e.g. Pentax K, Canon FD, Leica M, Fixed") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Field 8: Shutter Speeds
            OutlinedTextField(
                value = uiState.shutterSpeeds,
                onValueChange = viewModel::onShutterSpeedsChange,
                label = { Text("Shutter Speeds") },
                placeholder = { Text("1s - 1/1000s, B") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Hint: Enter speeds as a range (e.g. 1s - 1/1000s, B) or comma separated (e.g. 1/1000, 1/500, 1/250, B).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Button(
                onClick = { viewModel.saveCamera(onBack) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_camera))
            }
        }
    }
}
