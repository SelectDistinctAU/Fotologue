package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditFlashViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditFlashScreen(
    viewModel: AddEditFlashViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_flash_on),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (viewModel.flashId == null) 
                                stringResource(R.string.add_flash) 
                            else 
                                stringResource(R.string.edit_flash)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveFlash(onSuccess) }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.associated_cameras),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.cameras.isEmpty()) {
                Text(
                    stringResource(R.string.error_no_cameras),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.cameras.forEach { camera ->
                        val isSelected = uiState.selectedCameraIds.contains(camera.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCameraToggled(camera.id) },
                            label = { Text(camera.name) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            var showFlashPresetDialog by remember { mutableStateOf(false) }

            if (showFlashPresetDialog) {
                val flashPresets = remember {
                    com.fotologue.core.data.preset.MasterPresetEngine.getPresetsByCategory("FLASH")
                }
                var selectedBrandFilter by remember { mutableStateOf("All") }
                var selectedTypeFilter by remember { mutableStateOf("All") }
                var presetSearchQuery by remember { mutableStateOf("") }
                var brandExpanded by remember { mutableStateOf(false) }
                var typeExpanded by remember { mutableStateOf(false) }

                val brandOptions = remember(flashPresets) {
                    listOf("All") + flashPresets.map { it.brand }.distinct().sorted()
                }
                val typeOptions = remember(flashPresets, selectedBrandFilter) {
                    val baseList = if (selectedBrandFilter == "All") flashPresets else flashPresets.filter { it.brand.equals(selectedBrandFilter, true) }
                    listOf("All") + baseList.mapNotNull { it.attributes["type"] }.distinct().sorted()
                }

                val filteredPresets = remember(flashPresets, selectedBrandFilter, selectedTypeFilter, presetSearchQuery) {
                    flashPresets.filter { preset ->
                        val matchesBrand = selectedBrandFilter == "All" || preset.brand.equals(selectedBrandFilter, true)
                        val matchesType = selectedTypeFilter == "All" || (preset.attributes["type"] ?: "").equals(selectedTypeFilter, true)
                        val matchesSearch = presetSearchQuery.isBlank() || preset.name.contains(presetSearchQuery, true) || preset.brand.contains(presetSearchQuery, true)
                        matchesBrand && matchesType && matchesSearch
                    }
                }

                AlertDialog(
                    onDismissRequest = { showFlashPresetDialog = false },
                    title = { Text("Select Flash from Presets", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Select a brand and type to search flash speedlights:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            // Brand Filter
                            ExposedDropdownMenuBox(expanded = brandExpanded, onExpandedChange = { brandExpanded = it }) {
                                OutlinedTextField(
                                    value = selectedBrandFilter,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Filter by Flash Brand") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = brandExpanded, onDismissRequest = { brandExpanded = false }) {
                                    brandOptions.forEach { bOpt ->
                                        DropdownMenuItem(text = { Text(bOpt) }, onClick = { selectedBrandFilter = bOpt; brandExpanded = false })
                                    }
                                }
                            }

                            // Type Filter
                            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                                OutlinedTextField(
                                    value = selectedTypeFilter,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Filter by Flash Type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                                    typeOptions.forEach { tOpt ->
                                        DropdownMenuItem(text = { Text(tOpt) }, onClick = { selectedTypeFilter = tOpt; typeExpanded = false })
                                    }
                                }
                            }

                            // Search Bar
                            OutlinedTextField(
                                value = presetSearchQuery,
                                onValueChange = { presetSearchQuery = it },
                                label = { Text("Search Flash Models") },
                                leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Fallback Action Button
                            OutlinedButton(
                                onClick = {
                                    if (selectedBrandFilter != "All") viewModel.onMakeChange(selectedBrandFilter)
                                    showFlashPresetDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(painterResource(R.drawable.ic_add), contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (selectedBrandFilter != "All") "Add Custom $selectedBrandFilter Flash" else "Add Custom / Unlisted Flash")
                            }

                            HorizontalDivider()

                            filteredPresets.forEach { preset ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onMakeChange(preset.brand)
                                            viewModel.onModelChange(preset.name.substringAfter(preset.brand).trim().ifBlank { preset.name })
                                            preset.attributes["guideNumber"]?.let { gn ->
                                                val digits = gn.filter { c -> c.isDigit() || c == '.' }
                                                if (digits.isNotBlank()) viewModel.onGuideNumberChange(digits)
                                            }
                                            preset.attributes["type"]?.let { viewModel.onFlashTypeChange(it) }
                                            preset.attributes["syncType"]?.let { viewModel.onSyncTypeChange(it) }
                                            showFlashPresetDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(preset.name, fontWeight = FontWeight.Bold)
                                        Text("Brand: ${preset.brand} • Type: ${preset.attributes["type"] ?: "Speedlight"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFlashPresetDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = { showFlashPresetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_settings), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Flash Speedlight from Presets", fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = uiState.make,
                onValueChange = viewModel::onMakeChange,
                label = { Text("Brand / Manufacturer") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = { Text("Model Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.guideNumber,
                onValueChange = viewModel::onGuideNumberChange,
                label = { Text("Guide Number (ISO 100, meters)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            var flashTypeExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = flashTypeExpanded,
                onExpandedChange = { flashTypeExpanded = !flashTypeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.flashType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Flash Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = flashTypeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = flashTypeExpanded,
                    onDismissRequest = { flashTypeExpanded = false }
                ) {
                    uiState.flashTypes.forEach { fTypeOpt ->
                        DropdownMenuItem(
                            text = { Text(fTypeOpt) },
                            onClick = {
                                viewModel.onFlashTypeChange(fTypeOpt)
                                flashTypeExpanded = false
                            }
                        )
                    }
                }
            }

            var syncTypeExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = syncTypeExpanded,
                onExpandedChange = { syncTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.syncType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sync Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = syncTypeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = syncTypeExpanded,
                    onDismissRequest = { syncTypeExpanded = false }
                ) {
                    uiState.syncTypes.forEach { syncOpt ->
                        DropdownMenuItem(
                            text = { Text(syncOpt) },
                            onClick = {
                                viewModel.onSyncTypeChange(syncOpt)
                                syncTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = { Text("Serial Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { viewModel.saveFlash(onSuccess) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_flash))
            }
        }
    }
}
