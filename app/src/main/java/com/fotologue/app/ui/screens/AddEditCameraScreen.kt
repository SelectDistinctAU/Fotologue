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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.R
import com.fotologue.app.ui.viewmodel.AddEditCameraViewModel
import com.fotologue.core.data.CameraSpec
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.Film
import com.fotologue.core.data.preset.MasterPresetEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCameraScreen(
    viewModel: AddEditCameraViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isCustomFormView by remember { mutableStateOf(viewModel.cameraId != null) }
    var expanded by remember { mutableStateOf(false) }

    val masterPresetItems = remember { MasterPresetEngine.getPresetsByCategory("CAMERA") }
    val masterBrands = remember {
        listOf(
            "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
            "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
            "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
            "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "➕ Add new Brand..."
        )
    }

    var selectedBrandFilter by remember { mutableStateOf(if (uiState.make.isNotBlank() && masterBrands.contains(uiState.make)) uiState.make else "All") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (viewModel.cameraId == null) {
                            if (isCustomFormView) "Add Custom Camera" else "Select Camera from Presets"
                        } else "Edit Camera"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isCustomFormView && viewModel.cameraId == null) {
                            onBack()
                        } else if (viewModel.cameraId == null) {
                            isCustomFormView = false
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (isCustomFormView) {
                        IconButton(onClick = { viewModel.saveCamera(onBack) }) {
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
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isCustomFormView && viewModel.cameraId == null) {
                // PRIMARY TARGET: FULL-SCREEN PRESET CATALOG BROWSER
                Text(
                    text = "Select a brand and type to search camera body presets:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. FILTER BY CAMERA BRAND (Top Dropdown)
                var brandQuery by remember { mutableStateOf("") }
                val allBrandOptions = remember(masterBrands) { (listOf("All") + masterBrands.filter { it != "➕ Add new Brand..." }) }
                val filteredBrandOptions = remember(allBrandOptions, brandQuery) {
                    if (brandQuery.isBlank()) allBrandOptions else allBrandOptions.filter { it.contains(brandQuery, ignoreCase = true) }
                }

                ExposedDropdownMenuBox(
                    expanded = brandDropdownExpanded,
                    onExpandedChange = { brandDropdownExpanded = !brandDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (brandDropdownExpanded) brandQuery else selectedBrandFilter,
                        onValueChange = {
                            brandQuery = it
                            brandDropdownExpanded = true
                        },
                        label = { Text("Filter by Camera Brand") },
                        placeholder = { Text(selectedBrandFilter) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandDropdownExpanded) },
                        singleLine = true,
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = brandDropdownExpanded && filteredBrandOptions.isNotEmpty(),
                        onDismissRequest = {
                            brandDropdownExpanded = false
                            brandQuery = ""
                        }
                    ) {
                        filteredBrandOptions.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b, fontWeight = if (b == selectedBrandFilter) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedBrandFilter = b
                                    brandDropdownExpanded = false
                                    brandQuery = ""
                                }
                            )
                        }
                    }
                }

                // 2. FILTER BY CAMERA TYPE (Second Dropdown, Dynamic Cascade)
                var typeQuery by remember { mutableStateOf("") }
                val filteredTypeOptions = remember(availableTypesForBrand, typeQuery) {
                    if (typeQuery.isBlank()) availableTypesForBrand else availableTypesForBrand.filter { it.contains(typeQuery, ignoreCase = true) }
                }

                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (typeDropdownExpanded) typeQuery else selectedCategoryFilter,
                        onValueChange = {
                            typeQuery = it
                            typeDropdownExpanded = true
                        },
                        label = { Text("Filter by Camera Type") },
                        placeholder = { Text(selectedCategoryFilter) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        singleLine = true,
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded && filteredTypeOptions.isNotEmpty(),
                        onDismissRequest = {
                            typeDropdownExpanded = false
                            typeQuery = ""
                        }
                    ) {
                        filteredTypeOptions.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, fontWeight = if (cat == selectedCategoryFilter) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedCategoryFilter = cat
                                    typeDropdownExpanded = false
                                    typeQuery = ""
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Camera Models") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
                )

                // Fallback Action Button: Can't find camera? Add Custom {Brand} Camera
                val fallbackButtonLabel = if (selectedBrandFilter != "All") "➕ Can't find model? Add Custom $selectedBrandFilter Camera" else "➕ Add Custom / Unlisted Camera"
                OutlinedButton(
                    onClick = {
                        if (selectedBrandFilter != "All") {
                            viewModel.onMakeChange(selectedBrandFilter)
                        }
                        if (selectedCategoryFilter != "All") {
                            val selectedTypeEnum = CameraType.entries.find { it.displayName.equals(selectedCategoryFilter, ignoreCase = true) }
                            selectedTypeEnum?.let { viewModel.onTypeChange(it) }
                        }
                        isCustomFormView = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(fallbackButtonLabel, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }

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
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("No camera presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(onClick = { isCustomFormView = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Custom Camera")
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredPresets.size) { idx ->
                            val presetItem = filteredPresets[idx]
                            val subCatStr = presetItem.subCategory ?: presetItem.attributes["type"] ?: "Film SLR"
                            val cType = CameraType.fromString(subCatStr)
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
                                        viewModel.saveCamera(onBack)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text("${spec.type.displayName} • Mount: ${spec.mount}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.applySpec(spec)
                                            viewModel.saveCamera(onBack)
                                        },
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("Select", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // SECONDARY FALLBACK VIEW: MANUAL / CUSTOM CAMERA FORM
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (viewModel.cameraId == null) {
                        OutlinedButton(
                            onClick = { isCustomFormView = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back to Preset Catalog", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    var brandExpanded by remember { mutableStateOf(false) }

                    // Field 1: Brand (Controlled Dropdown Selector, No Default Brand)
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
                                        if (brand == "➕ Add new Brand...") {
                                            viewModel.onMakeChange("")
                                        } else {
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

                    // Field 4: Film Format Dropdown
                    var formatExpanded by remember { mutableStateOf(false) }
                    val filmFormats = listOf("35mm", "120", "Sheet", "Half-Frame")

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
                            filmFormats.forEach { formatOption ->
                                DropdownMenuItem(
                                    text = { Text(formatOption) },
                                    onClick = {
                                        viewModel.onFormatChange(formatOption)
                                        formatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Field 5: Lens Mount
                    var mountExpanded by remember { mutableStateOf(false) }
                    val mounts = listOf("Pentax K", "Canon FD", "Nikon F", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed")

                    ExposedDropdownMenuBox(
                        expanded = mountExpanded,
                        onExpandedChange = { mountExpanded = !mountExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.mount,
                            onValueChange = viewModel::onMountChange,
                            label = { Text("Lens Mount") },
                            placeholder = { Text("e.g. Canon FD, Nikon F, Pentax K") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mountExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = mountExpanded,
                            onDismissRequest = { mountExpanded = false }
                        ) {
                            mounts.forEach { mnt ->
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

                    // Field 6: Shutter Speeds
                    OutlinedTextField(
                        value = uiState.shutterSpeeds,
                        onValueChange = viewModel::onShutterSpeedsChange,
                        label = { Text("Shutter Speeds") },
                        placeholder = { Text("e.g. 1s - 1/1000s, B") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Field 7: Serial Number
                    OutlinedTextField(
                        value = uiState.serialNumber,
                        onValueChange = viewModel::onSerialNumberChange,
                        label = { Text("Serial Number (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.saveCamera(onBack) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (viewModel.cameraId == null) "Save Camera" else "Update Camera")
                    }
                }
            }
        }
    }
}
