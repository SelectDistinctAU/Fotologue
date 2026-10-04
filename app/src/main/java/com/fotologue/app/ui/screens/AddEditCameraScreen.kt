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
import com.fotologue.core.data.preset.MasterPresetEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCameraScreen(
    viewModel: AddEditCameraViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var showCameraPresetsDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    if (showCameraPresetsDialog) {
        val masterPresetItems = remember { MasterPresetEngine.getPresetsByCategory("CAMERA") }
        val allBrands = remember { listOf("All") + masterPresetItems.map { it.brand }.distinct().sorted() }
        
        var selectedBrandFilter by remember { mutableStateOf(if (uiState.make.isNotBlank() && allBrands.contains(uiState.make)) uiState.make else "All") }
        var selectedCategoryFilter by remember { mutableStateOf("All") }

        var brandDropdownExpanded by remember { mutableStateOf(false) }
        var typeDropdownExpanded by remember { mutableStateOf(false) }

        // Dynamic Camera Types available for selected Brand
        val availableTypesForBrand = remember(selectedBrandFilter) {
            val matchingItems = if (selectedBrandFilter == "All") {
                masterPresetItems
            } else {
                masterPresetItems.filter { it.brand.equals(selectedBrandFilter, ignoreCase = true) }
            }
            val types = matchingItems.mapNotNull { it.subCategory ?: it.attributes["type"] }.distinct().sorted()
            
            // Auto-select type if brand produces only 1 type
            if (types.size == 1) {
                selectedCategoryFilter = types.first()
            } else if (!types.contains(selectedCategoryFilter)) {
                selectedCategoryFilter = "All"
            }
            listOf("All") + types
        }

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
                    modifier = Modifier.fillMaxWidth().height(440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select brand and type filters to search camera presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // 1. FILTER BY CAMERA BRAND (Top Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = brandDropdownExpanded,
                        onExpandedChange = { brandDropdownExpanded = !brandDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBrandFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Camera Brand") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandDropdownExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = brandDropdownExpanded,
                            onDismissRequest = { brandDropdownExpanded = false }
                        ) {
                            allBrands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, fontWeight = if (b == selectedBrandFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedBrandFilter = b
                                        brandDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. FILTER BY CAMERA TYPE (Second Dropdown, Restricted by Brand)
                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Camera Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            availableTypesForBrand.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = if (cat == selectedCategoryFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedCategoryFilter = cat
                                        typeDropdownExpanded = false
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

                    val filteredPresets = remember(selectedBrandFilter, selectedCategoryFilter, searchQuery) {
                        masterPresetItems.filter { item ->
                            val matchesBrand = selectedBrandFilter == "All" || item.brand.equals(selectedBrandFilter, ignoreCase = true)
                            val subCat = item.subCategory ?: item.attributes["type"] ?: ""
                            val matchesType = selectedCategoryFilter == "All" || subCat.contains(selectedCategoryFilter, ignoreCase = true)
                            val matchesQuery = searchQuery.isBlank() || (
                                item.name.contains(searchQuery, ignoreCase = true) ||
                                item.brand.contains(searchQuery, ignoreCase = true) ||
                                (item.modelNumber?.contains(searchQuery, ignoreCase = true) == true)
                            )
                            matchesBrand && matchesType && matchesQuery
                        }
                    }

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No camera presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val presetItem = filteredPresets[idx]
                                val subCatStr = presetItem.subCategory ?: presetItem.attributes["type"] ?: "Film SLR"
                                val cType = CameraType.entries.find { it.displayName.equals(subCatStr, ignoreCase = true) } ?: CameraType.FILM_SLR
                                val spec = CameraSpec(
                                    name = presetItem.name,
                                    modelNumber = presetItem.modelNumber ?: presetItem.name,
                                    type = cType,
                                    mount = presetItem.attributes["mount"] ?: "Standard",
                                    shutterSpeeds = listOf(presetItem.attributes["shutterSpeeds"] ?: "1s - 1/1000s, B")
                                )

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

            val masterBrands = remember {
                listOf(
                    "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
                    "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
                    "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
                    "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "Other..."
                )
            }
            var brandExpanded by remember { mutableStateOf(false) }

            // Field 1: Brand (Controlled Dropdown Selector)
            ExposedDropdownMenuBox(
                expanded = brandExpanded,
                onExpandedChange = { brandExpanded = !brandExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.make,
                    onValueChange = viewModel::onMakeChange,
                    label = { Text("Brand") },
                    placeholder = { Text("Select or type Brand") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = brandExpanded,
                    onDismissRequest = { brandExpanded = false }
                ) {
                    masterBrands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand) },
                            onClick = {
                                if (brand != "Other...") {
                                    viewModel.onMakeChange(brand)
                                }
                                brandExpanded = false
                            }
                        )
                    }
                }
            }

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

            // Field 3: Camera Type Dropdown
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
                var mountExpanded by remember { mutableStateOf(false) }
                val defaultMounts = remember { listOf("Pentax K", "Canon FD", "Nikon F", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed") }

                ExposedDropdownMenuBox(
                    expanded = mountExpanded,
                    onExpandedChange = { mountExpanded = !mountExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.mount,
                        onValueChange = viewModel::onMountChange,
                        label = { Text("Lens Mount") },
                        placeholder = { Text("e.g. Pentax K, Canon FD, Fixed") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mountExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = mountExpanded,
                        onDismissRequest = { mountExpanded = false }
                    ) {
                        defaultMounts.forEach { mnt ->
                            DropdownMenuItem(
                                text = { Text(mnt) },
                                onClick = {
                                    viewModel.onMountChange(mnt)
                                    mountExpanded = false
                                }
                            )
                        }
                    }
                }
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

            // Field 8 (LAST): Serial Number (Optional)
            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = { Text("Serial Number (Optional)") },
                placeholder = { Text("Optional") },
                modifier = Modifier.fillMaxWidth()
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
