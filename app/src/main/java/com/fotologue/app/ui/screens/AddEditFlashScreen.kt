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
    var isCustomFormView by remember { mutableStateOf(viewModel.flashId != null) }

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
                            if (!isCustomFormView && viewModel.flashId == null)
                                "Select Flash from Presets"
                            else if (viewModel.flashId == null) 
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
                    if (isCustomFormView || viewModel.flashId != null) {
                        IconButton(onClick = { viewModel.saveFlash(onSuccess) }) {
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
            if (!isCustomFormView && viewModel.flashId == null) {
                // PRIMARY FULL-SCREEN PRESET CATALOG BROWSER
                FlashPresetsBrowser(
                    onSelectPreset = { brand, model, guideNum ->
                        viewModel.onMakeChange(brand)
                        viewModel.onModelChange(model)
                        if (guideNum.isNotBlank()) viewModel.onGuideNumberChange(guideNum)
                        isCustomFormView = true
                    },
                    onAddCustomFlash = { isCustomFormView = true }
                )
            } else {
                // MANUAL CREATION / EDIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (viewModel.flashId == null) {
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
                        value = uiState.make,
                        onValueChange = viewModel::onMakeChange,
                        label = { Text("Brand / Make") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.model,
                        onValueChange = viewModel::onModelChange,
                        label = { Text("Model Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.guideNumber,
                        onValueChange = viewModel::onGuideNumberChange,
                        label = { Text("Guide Number (GN)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.serialNumber,
                        onValueChange = viewModel::onSerialNumberChange,
                        label = { Text("Serial Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.saveFlash(onSuccess) },
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
private fun FlashPresetsBrowser(
    onSelectPreset: (brand: String, model: String, guideNum: String) -> Unit,
    onAddCustomFlash: () -> Unit
) {
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Select a brand and type to search flash speedlights:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                label = { Text("Filter by Flash Brand") },
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
                label = { Text("Filter by Flash Type") },
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
            label = { Text("Search Flash Models") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = onAddCustomFlash,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Custom / Unlisted Flash", fontWeight = FontWeight.Bold)
        }

        HorizontalDivider()

        if (filteredPresets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching flash presets found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPresets.size) { idx ->
                    val p = filteredPresets[idx]
                    val guideNum = p.attributes["guideNumber"] ?: ""
                    Card(
                        onClick = { onSelectPreset(p.brand, p.name, guideNum) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(p.name, fontWeight = FontWeight.Bold)
                            Text("Brand: ${p.brand} • Type: ${p.attributes["type"] ?: "Shoe Mount"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
