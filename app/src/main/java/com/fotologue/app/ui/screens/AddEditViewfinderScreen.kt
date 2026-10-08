package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditViewfinderViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditViewfinderScreen(
    viewModel: AddEditViewfinderViewModel,
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
                            painter = painterResource(R.drawable.viewfinders_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (viewModel.viewfinderId == null) 
                                "Add Viewfinder" 
                            else 
                                "Edit Viewfinder"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveViewfinder(onSuccess) }) {
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

            Text(
                "Note: Only camera bodies with removable viewfinders (e.g. Pentax LX, Nikon F3, Hasselblad 500C/M) or coldshoe finders (e.g. Leica M3) are listed.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            var showVfPresetDialog by remember { mutableStateOf(false) }

            if (showVfPresetDialog) {
                val vfPresets = remember {
                    com.fotologue.core.data.preset.MasterPresetEngine.getPresetsByCategory("VIEWFINDER")
                }
                var selectedBrandFilter by remember { mutableStateOf("All") }
                var selectedTypeFilter by remember { mutableStateOf("All") }
                var presetSearchQuery by remember { mutableStateOf("") }
                var brandExpanded by remember { mutableStateOf(false) }
                var typeExpanded by remember { mutableStateOf(false) }

                val brandOptions = remember(vfPresets) {
                    listOf("All") + vfPresets.map { it.brand }.distinct().sorted()
                }
                val typeOptions = remember(vfPresets, selectedBrandFilter) {
                    val baseList = if (selectedBrandFilter == "All") vfPresets else vfPresets.filter { it.brand.equals(selectedBrandFilter, true) }
                    listOf("All") + baseList.mapNotNull { it.attributes["type"] }.distinct().sorted()
                }

                val filteredPresets = remember(vfPresets, selectedBrandFilter, selectedTypeFilter, presetSearchQuery) {
                    vfPresets.filter { preset ->
                        val matchesBrand = selectedBrandFilter == "All" || preset.brand.equals(selectedBrandFilter, true)
                        val matchesType = selectedTypeFilter == "All" || (preset.attributes["type"] ?: "").equals(selectedTypeFilter, true)
                        val matchesSearch = presetSearchQuery.isBlank() || preset.name.contains(presetSearchQuery, true) || preset.brand.contains(presetSearchQuery, true)
                        matchesBrand && matchesType && matchesSearch
                    }
                }

                AlertDialog(
                    onDismissRequest = { showVfPresetDialog = false },
                    title = { Text("Select Viewfinder from Presets", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Select a brand and type to search interchangeable viewfinders:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            // Brand Filter
                            var brandQuery by remember { mutableStateOf("") }
                            val filteredBrandOptions = remember(brandOptions, brandQuery) {
                                if (brandQuery.isBlank()) brandOptions else brandOptions.filter { it.contains(brandQuery, ignoreCase = true) }
                            }

                            ExposedDropdownMenuBox(expanded = brandExpanded, onExpandedChange = { brandExpanded = !brandExpanded }) {
                                OutlinedTextField(
                                    value = if (brandExpanded) brandQuery else selectedBrandFilter,
                                    onValueChange = {
                                        brandQuery = it
                                        brandExpanded = true
                                    },
                                    label = { Text("Filter by Viewfinder Brand") },
                                    placeholder = { Text(selectedBrandFilter) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                    singleLine = true,
                                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = brandExpanded && filteredBrandOptions.isNotEmpty(), onDismissRequest = { brandExpanded = false; brandQuery = "" }) {
                                    filteredBrandOptions.forEach { bOpt ->
                                        DropdownMenuItem(text = { Text(bOpt) }, onClick = { selectedBrandFilter = bOpt; brandExpanded = false; brandQuery = "" })
                                    }
                                }
                            }

                            // Type Filter
                            var typeQuery by remember { mutableStateOf("") }
                            val filteredTypeOptions = remember(typeOptions, typeQuery) {
                                if (typeQuery.isBlank()) typeOptions else typeOptions.filter { it.contains(typeQuery, ignoreCase = true) }
                            }

                            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                                OutlinedTextField(
                                    value = if (typeExpanded) typeQuery else selectedTypeFilter,
                                    onValueChange = {
                                        typeQuery = it
                                        typeExpanded = true
                                    },
                                    label = { Text("Filter by Viewfinder Type") },
                                    placeholder = { Text(selectedTypeFilter) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                    singleLine = true,
                                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                                )
                                ExposedDropdownMenu(expanded = typeExpanded && filteredTypeOptions.isNotEmpty(), onDismissRequest = { typeExpanded = false; typeQuery = "" }) {
                                    filteredTypeOptions.forEach { tOpt ->
                                        DropdownMenuItem(text = { Text(tOpt) }, onClick = { selectedTypeFilter = tOpt; typeExpanded = false; typeQuery = "" })
                                    }
                                }
                            }

                            // Search Bar
                            OutlinedTextField(
                                value = presetSearchQuery,
                                onValueChange = { presetSearchQuery = it },
                                label = { Text("Search Viewfinder Models") },
                                leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Fallback Action Button
                            OutlinedButton(
                                onClick = {
                                    if (selectedBrandFilter != "All") viewModel.onNameChange("$selectedBrandFilter Viewfinder")
                                    showVfPresetDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(painterResource(R.drawable.ic_add), contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (selectedBrandFilter != "All") "Add Custom $selectedBrandFilter Viewfinder" else "Add Custom / Unlisted Viewfinder")
                            }

                            HorizontalDivider()

                            filteredPresets.forEach { preset ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onNameChange(preset.name)
                                            preset.modelNumber?.let { viewModel.onModelNumberChange(it) }
                                            preset.attributes["type"]?.let { viewModel.onTypeChange(it) }
                                            showVfPresetDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(preset.name, fontWeight = FontWeight.Bold)
                                        Text("Brand: ${preset.brand} • Type: ${preset.attributes["type"] ?: "Viewfinder"} ${if (!preset.attributes["compatibleCamera"].isNullOrBlank()) "• For: ${preset.attributes["compatibleCamera"]}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showVfPresetDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = { showVfPresetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_settings), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Viewfinder from Presets", fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Viewfinder Name") },
                modifier = Modifier.fillMaxWidth()
            )

            var vfTypeExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = vfTypeExpanded,
                onExpandedChange = { vfTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Viewfinder Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vfTypeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = vfTypeExpanded,
                    onDismissRequest = { vfTypeExpanded = false }
                ) {
                    uiState.viewfinderTypes.forEach { typeOpt ->
                        DropdownMenuItem(
                            text = { Text(typeOpt) },
                            onClick = {
                                viewModel.onTypeChange(typeOpt)
                                vfTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.modelNumber,
                onValueChange = viewModel::onModelNumberChange,
                label = { Text("Model Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = { Text("Serial Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { viewModel.saveViewfinder(onSuccess) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Save Viewfinder")
            }
        }
    }
}
