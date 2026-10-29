package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditFilterViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditFilterScreen(
    viewModel: AddEditFilterViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isCustomFormView by remember { mutableStateOf(viewModel.filterId != null) }
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.filter_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (!isCustomFormView && viewModel.filterId == null)
                                "Select Filter from Presets"
                            else if (viewModel.filterId == null) 
                                stringResource(R.string.add_filter) 
                            else 
                                stringResource(R.string.edit_filter)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (isCustomFormView || viewModel.filterId != null) {
                        IconButton(onClick = { viewModel.saveFilter(onSuccess) }) {
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
            if (!isCustomFormView && viewModel.filterId == null) {
                // PRIMARY FULL-SCREEN PRESET CATALOG BROWSER
                FilterPresetsBrowser(
                    onSelectPreset = { brand, model, size, factor ->
                        val fullName = if (model.startsWith(brand, ignoreCase = true)) model else "$brand $model".trim()
                        viewModel.onNameChange(fullName)
                        if (size.isNotBlank()) viewModel.onThreadSizeChange(size)
                        if (factor.isNotBlank()) viewModel.onCompensationStopsChange(factor)
                        isCustomFormView = true
                    },
                    onAddCustomFilter = { isCustomFormView = true }
                )
            } else {
                // MANUAL CREATION / EDIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (viewModel.filterId == null) {
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
                        "Associated Lenses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (uiState.lenses.isEmpty()) {
                        Text(
                            "No lenses found. Please add a lens first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.lenses.forEach { lens ->
                                val isSelected = uiState.selectedLensIds.contains(lens.id)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onLensToggled(lens.id) },
                                    label = { Text("${lens.make} ${lens.model}") },
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
                        label = { Text("Filter Name / Specification") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.type,
                            onValueChange = viewModel::onTypeChange,
                            label = { Text("Filter Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            singleLine = true,
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            uiState.filterTypes.forEach { typeOption ->
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
                        value = uiState.threadSize,
                        onValueChange = viewModel::onThreadSizeChange,
                        label = { Text("Thread Size (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.compensationStops,
                        onValueChange = viewModel::onCompensationStopsChange,
                        label = { Text("Compensation (Stops, e.g. 3.0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.saveFilter(onSuccess) },
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
private fun FilterPresetsBrowser(
    onSelectPreset: (brand: String, model: String, size: String, factor: String) -> Unit,
    onAddCustomFilter: () -> Unit
) {
    val filterPresets = remember {
        com.fotologue.core.data.preset.MasterPresetEngine.getPresetsByCategory("FILTER")
    }
    var selectedBrandFilter by remember { mutableStateOf("All") }
    var presetSearchQuery by remember { mutableStateOf("") }
    var brandExpanded by remember { mutableStateOf(false) }

    val brandOptions = remember(filterPresets) {
        listOf("All") + filterPresets.map { it.brand }.distinct().sorted()
    }

    val filteredPresets = remember(filterPresets, selectedBrandFilter, presetSearchQuery) {
        filterPresets.filter { preset ->
            val matchesBrand = selectedBrandFilter == "All" || preset.brand.equals(selectedBrandFilter, true)
            val matchesSearch = presetSearchQuery.isBlank() || preset.name.contains(presetSearchQuery, true) || preset.brand.contains(presetSearchQuery, true)
            matchesBrand && matchesSearch
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Select a brand to view curated filter presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                label = { Text("Filter by Brand") },
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

        // Search Bar
        OutlinedTextField(
            value = presetSearchQuery,
            onValueChange = { presetSearchQuery = it },
            label = { Text("Search Filter Presets") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = onAddCustomFilter,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Custom / Unlisted Filter", fontWeight = FontWeight.Bold)
        }

        HorizontalDivider()

        if (filteredPresets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching filter presets found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPresets.size) { idx ->
                    val p = filteredPresets[idx]
                    val size = p.attributes["filterSize"] ?: p.attributes["size"] ?: ""
                    val factor = p.attributes["compensationStops"] ?: ""
                    Card(
                        onClick = { onSelectPreset(p.brand, p.name, size, factor) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(p.name, fontWeight = FontWeight.Bold)
                            Text("Brand: ${p.brand} • Size: ${size} • Stop Adjustment: ${factor}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
