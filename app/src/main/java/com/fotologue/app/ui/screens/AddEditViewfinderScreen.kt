package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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
    var isCustomFormView by remember { mutableStateOf(viewModel.viewfinderId != null) }

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
                            if (!isCustomFormView && viewModel.viewfinderId == null)
                                "Select Viewfinder from Presets"
                            else if (viewModel.viewfinderId == null) 
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
                    if (isCustomFormView || viewModel.viewfinderId != null) {
                        IconButton(onClick = { viewModel.saveViewfinder(onSuccess) }) {
                            Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (!isCustomFormView && viewModel.viewfinderId == null) {
                // PRIMARY FULL-SCREEN PRESET CATALOG BROWSER
                ViewfinderPresetsBrowser(
                    onSelectPreset = { brand, model, type ->
                        val fullName = if (model.startsWith(brand, ignoreCase = true)) model else "$brand $model".trim()
                        viewModel.onNameChange(fullName)
                        if (type.isNotBlank()) viewModel.onTypeChange(type)
                        isCustomFormView = true
                    },
                    onAddCustomViewfinder = { isCustomFormView = true }
                )
            } else {
                // MANUAL CREATION / EDIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (viewModel.viewfinderId == null) {
                        OutlinedButton(
                            onClick = { isCustomFormView = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back to Preset Catalog", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Text(
                        stringResource(R.string.associated_cameras),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Viewfinder Name / Model") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.type,
                            onValueChange = viewModel::onTypeChange,
                            label = { Text("Viewfinder Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            singleLine = true,
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            uiState.viewfinderTypes.forEach { typeOption ->
                                DropdownMenuItem(
                                    text = { Text(typeOption) },
                                    onClick = {
                                        viewModel.onTypeChange(typeOption)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.serialNumber,
                        onValueChange = viewModel::onSerialNumberChange,
                        label = { Text("Serial Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.saveViewfinder(onSuccess) },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.action_save), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewfinderPresetsBrowser(
    onSelectPreset: (brand: String, model: String, type: String) -> Unit,
    onAddCustomViewfinder: () -> Unit
) {
    val viewfinderPresets = remember {
        com.fotologue.core.data.preset.MasterPresetEngine.getPresetsByCategory("VIEWFINDER")
    }
    var selectedBrandFilter by remember { mutableStateOf("All") }
    var selectedTypeFilter by remember { mutableStateOf("All") }
    var presetSearchQuery by remember { mutableStateOf("") }
    var brandExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val brandOptions = remember(viewfinderPresets) {
        listOf("All") + viewfinderPresets.map { it.brand }.distinct().sorted()
    }
    val typeOptions = remember(viewfinderPresets, selectedBrandFilter) {
        val baseList = if (selectedBrandFilter == "All") viewfinderPresets else viewfinderPresets.filter { it.brand.equals(selectedBrandFilter, true) }
        listOf("All") + baseList.mapNotNull { it.attributes["type"] }.distinct().sorted()
    }

    val filteredPresets = remember(viewfinderPresets, selectedBrandFilter, selectedTypeFilter, presetSearchQuery) {
        viewfinderPresets.filter { preset ->
            val matchesBrand = selectedBrandFilter == "All" || preset.brand.equals(selectedBrandFilter, true)
            val matchesType = selectedTypeFilter == "All" || (preset.attributes["type"] ?: "").equals(selectedTypeFilter, true)
            val matchesSearch = presetSearchQuery.isBlank() || preset.name.contains(presetSearchQuery, true) || preset.brand.contains(presetSearchQuery, true)
            matchesBrand && matchesType && matchesSearch
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
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

        OutlinedButton(
            onClick = onAddCustomViewfinder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Custom / Unlisted Viewfinder", fontWeight = FontWeight.Bold)
        }

        HorizontalDivider()

        if (filteredPresets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching viewfinder presets found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPresets.size) { idx ->
                    val p = filteredPresets[idx]
                    val vfType = p.attributes["type"] ?: "Eye-Level Prism"
                    val forCamera = p.attributes["forCamera"] ?: ""
                    Card(
                        onClick = { onSelectPreset(p.brand, p.name, vfType) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(p.name, fontWeight = FontWeight.Bold)
                            Text("Brand: ${p.brand} • Type: ${vfType}${if (forCamera.isNotBlank()) " • For: $forCamera" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
