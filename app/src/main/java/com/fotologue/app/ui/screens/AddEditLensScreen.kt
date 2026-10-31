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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.fotologue.core.data.Lens
import com.fotologue.core.data.LensSpec
import com.fotologue.core.data.preset.MasterPresetEngine
import com.fotologue.app.ui.viewmodel.AddEditLensViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditLensScreen(
    viewModel: AddEditLensViewModel,
    onBack: () -> Unit,
    isPreArrivalMode: Boolean = false,
    onSavePreArrivalOrder: (String, String, String, Long) -> Unit = { _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var isCustomFormView by remember { mutableStateOf(viewModel.lensId != null) }

    val masterBrands = remember {
        listOf(
            "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
            "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
            "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
            "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "Other..."
        )
    }
    var makeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(com.fotologue.core.designsystem.R.drawable.lensd),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (!isCustomFormView && viewModel.lensId == null) "Select Lens from Presets" else stringResource(R.string.lens_details))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
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
            if (!isCustomFormView && viewModel.lensId == null) {
                // PRIMARY TARGET: FULL-SCREEN PRESET CATALOG BROWSER
                LensPresetsBrowser(
                    initialMake = uiState.make,
                    onSelectSpec = { spec ->
                        viewModel.onSelectSpec(spec)
                        if (isPreArrivalMode) {
                            viewModel.savePreArrivalLens { newLensId ->
                                val specs = "${spec.make} • type: ${spec.type}, mount: ${spec.mount}, filterSize: ${spec.filterSize}mm"
                                onSavePreArrivalOrder(spec.model, specs, spec.make, newLensId)
                                onBack()
                            }
                        } else {
                            isCustomFormView = true
                        }
                    },
                    onAddCustomLens = { isCustomFormView = true }
                )
            } else {
                // MANUAL CREATION / EDIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (viewModel.lensId == null) {
                        OutlinedButton(
                            onClick = { isCustomFormView = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back to Preset Catalog", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    // Associated Cameras Selection (Multi-select)
                    Text(
                        "Associated Cameras",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Select cameras compatible with this lens:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.cameras.isEmpty()) {
                        Text(
                            "No cameras found. Please add a camera first.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.cameras.forEach { camera ->
                                val isSelected = uiState.selectedCameraIds.contains(camera.id)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onCameraToggled(camera.id) },
                                    label = { Text(camera.name) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    if (uiState.selectedCameraIds.isEmpty() && uiState.cameras.isNotEmpty()) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ Please select at least one associated camera above",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (isPreArrivalMode) {
                                viewModel.savePreArrivalLens { newLensId ->
                                    val name = if (uiState.model.startsWith(uiState.make, ignoreCase = true)) uiState.model else "${uiState.make} ${uiState.model}".trim()
                                    val specs = "${uiState.make} • type: ${uiState.type}, mount: ${uiState.mount}, filterSize: ${uiState.filterSize}mm"
                                    onSavePreArrivalOrder(name, specs, uiState.make, newLensId)
                                    onBack()
                                }
                            } else {
                                viewModel.saveLens(onSuccess = onBack)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (isPreArrivalMode) "📦 Add to Pre-Arrival Inbound Register" else stringResource(R.string.save_lens), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Manual Entry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Brand (Make) Selection
                    ExposedDropdownMenuBox(
                        expanded = makeExpanded,
                        onExpandedChange = { makeExpanded = !makeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.make,
                            onValueChange = viewModel::onMakeChange,
                            label = { Text("Brand / Make") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = makeExpanded) },
                            singleLine = true,
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = makeExpanded,
                            onDismissRequest = { makeExpanded = false }
                        ) {
                            masterBrands.forEach { brand ->
                                DropdownMenuItem(
                                    text = { Text(brand) },
                                    onClick = {
                                        if (brand == "Other...") {
                                            viewModel.onMakeChange("")
                                        } else {
                                            viewModel.onMakeChange(brand)
                                        }
                                        makeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Model Name
                    OutlinedTextField(
                        value = uiState.model,
                        onValueChange = viewModel::onModelChange,
                        label = { Text("Model Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Type (Prime, Zoom, Macro, etc.)
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.type,
                            onValueChange = viewModel::onTypeChange,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            singleLine = true,
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            Lens.TYPES.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        viewModel.onTypeChange(t)
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Mount
                    var mountExpanded by remember { mutableStateOf(false) }
                    val mountOptions = listOf("Canon FD", "Nikon F", "Pentax K", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed", "Bay I", "Bay II", "Bay III", "Standard")
                    ExposedDropdownMenuBox(
                        expanded = mountExpanded,
                        onExpandedChange = { mountExpanded = !mountExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.mount,
                            onValueChange = viewModel::onMountChange,
                            label = { Text("Lens Mount") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mountExpanded) },
                            singleLine = true,
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = mountExpanded,
                            onDismissRequest = { mountExpanded = false }
                        ) {
                            mountOptions.forEach { mnt ->
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

                    // Focal Length
                    OutlinedTextField(
                        value = uiState.minFocalLength,
                        onValueChange = viewModel::onMinFocalLengthChange,
                        label = { Text("Focal Length (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Aperture Range
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.maxAperture,
                            onValueChange = viewModel::onMaxApertureChange,
                            label = { Text("Max Ap (e.g. 1.8)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.minAperture,
                            onValueChange = viewModel::onMinApertureChange,
                            label = { Text("Min Ap (e.g. 22)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Filter Size
                    OutlinedTextField(
                        value = uiState.filterSize,
                        onValueChange = viewModel::onFilterSizeChange,
                        label = { Text("Filter Size (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LensPresetsBrowser(
    initialMake: String,
    onSelectSpec: (LensSpec) -> Unit,
    onAddCustomLens: () -> Unit
) {
    val masterLensItems = remember { MasterPresetEngine.getPresetsByCategory("LENS") }
    val allBrands = remember { listOf("All") + masterLensItems.map { it.brand }.distinct().sorted() }

    var selectedBrandFilter by remember { mutableStateOf(if (initialMake.isNotBlank() && allBrands.contains(initialMake)) initialMake else "All") }
    var selectedLensMountFilter by remember { mutableStateOf("All") }
    var lensSearchQueryText by remember { mutableStateOf("") }

    var brandDropdownExpanded by remember { mutableStateOf(false) }
    var mountDropdownExpanded by remember { mutableStateOf(false) }

    var webSearchResults by remember { mutableStateOf<List<com.fotologue.core.data.preset.WebSpecResult>>(emptyList()) }
    var showWebResultsDialog by remember { mutableStateOf(false) }
    var isSearchingOnline by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showWebResultsDialog) {
        AlertDialog(
            onDismissRequest = { showWebResultsDialog = false },
            title = { Text("🌐 Lens Web Spec Search Results", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (webSearchResults.isEmpty()) {
                        Text("No matching online lens specs found for \"$lensSearchQueryText\".", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text("Select a lens model to auto-fill specs:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        webSearchResults.forEach { res ->
                            val spec = LensSpec(
                                make = res.brand,
                                model = res.name,
                                modelNumber = res.modelNumber ?: "",
                                type = res.attributes["type"] ?: "Prime",
                                mount = res.attributes["mount"] ?: "Standard",
                                filterSize = res.attributes["filterSize"]?.replace("mm", "")?.toIntOrNull() ?: 52,
                                isStabilized = false,
                                minFocusDistance = 0.45,
                                maxMagnification = 0.15,
                                minAperture = res.attributes["minAperture"]?.replace("f/", "")?.toDoubleOrNull() ?: 22.0,
                                maxAperture = res.attributes["maxAperture"]?.replace("f/", "")?.toDoubleOrNull() ?: 1.8,
                                minFocalLength = res.attributes["focalLength"]?.replace("mm", "")?.toIntOrNull() ?: 50,
                                maxFocalLength = res.attributes["focalLength"]?.replace("mm", "")?.toIntOrNull() ?: 50
                            )

                            Card(
                                onClick = {
                                    showWebResultsDialog = false
                                    onSelectSpec(spec)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(res.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("Brand: ${res.brand} • Type: ${spec.type} • Mount: ${spec.mount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Specs: ${res.attributes["focalLength"] ?: "50mm"} ${res.attributes["maxAperture"] ?: "f/1.8"} • Filter: ${spec.filterSize}mm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWebResultsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    val availableMountsForBrand = remember(selectedBrandFilter) {
        val matchingItems = if (selectedBrandFilter == "All") {
            masterLensItems
        } else {
            masterLensItems.filter { it.brand.equals(selectedBrandFilter, ignoreCase = true) }
        }
        val mounts = matchingItems.mapNotNull { it.attributes["mount"] }.distinct().sorted()
        if (mounts.size == 1) {
            selectedLensMountFilter = mounts.first()
        } else if (!mounts.contains(selectedLensMountFilter)) {
            selectedLensMountFilter = "All"
        }
        listOf("All") + mounts
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. FILTER BY LENS BRAND
        var brandQuery by remember { mutableStateOf("") }
        val filteredBrandOptions = remember(allBrands, brandQuery) {
            if (brandQuery.isBlank()) allBrands else allBrands.filter { it.contains(brandQuery, ignoreCase = true) }
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
                label = { Text("Filter by Brand") },
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

        // 2. FILTER BY LENS MOUNT
        var mountQuery by remember { mutableStateOf("") }
        val filteredMountOptions = remember(availableMountsForBrand, mountQuery) {
            if (mountQuery.isBlank()) availableMountsForBrand else availableMountsForBrand.filter { it.contains(mountQuery, ignoreCase = true) }
        }

        ExposedDropdownMenuBox(
            expanded = mountDropdownExpanded,
            onExpandedChange = { mountDropdownExpanded = !mountDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = if (mountDropdownExpanded) mountQuery else selectedLensMountFilter,
                onValueChange = {
                    mountQuery = it
                    mountDropdownExpanded = true
                },
                label = { Text("Filter by Lens Mount") },
                placeholder = { Text(selectedLensMountFilter) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mountDropdownExpanded) },
                singleLine = true,
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = mountDropdownExpanded && filteredMountOptions.isNotEmpty(),
                onDismissRequest = {
                    mountDropdownExpanded = false
                    mountQuery = ""
                }
            ) {
                filteredMountOptions.forEach { mnt ->
                    DropdownMenuItem(
                        text = { Text(mnt, fontWeight = if (mnt == selectedLensMountFilter) FontWeight.Bold else FontWeight.Normal) },
                        onClick = {
                            selectedLensMountFilter = mnt
                            mountDropdownExpanded = false
                            mountQuery = ""
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = lensSearchQueryText,
            onValueChange = { lensSearchQueryText = it },
            label = { Text("Search Lenses") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
        )

        if (lensSearchQueryText.isNotBlank()) {
            Button(
                onClick = {
                    scope.launch {
                        isSearchingOnline = true
                        webSearchResults = com.fotologue.core.data.preset.WebSpecSearchEngine.searchLensSpecs(lensSearchQueryText)
                        isSearchingOnline = false
                        showWebResultsDialog = true
                    }
                },
                enabled = !isSearchingOnline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.cloud_sync_24px), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isSearchingOnline) "Searching Online..." else "🌐 Search Online Databases for \"$lensSearchQueryText\"", fontWeight = FontWeight.Bold)
            }
        }

        // Fallback Action Button
        val fallbackButtonLabel = if (selectedBrandFilter != "All") "➕ Add New $selectedBrandFilter Lens" else "➕ Add New Custom Lens"
        OutlinedButton(
            onClick = onAddCustomLens,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(fallbackButtonLabel, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        }

        HorizontalDivider()

        val filteredPresets = remember(selectedBrandFilter, selectedLensMountFilter, lensSearchQueryText) {
            masterLensItems.filter { item ->
                val matchesBrand = selectedBrandFilter == "All" || item.brand.equals(selectedBrandFilter, ignoreCase = true)
                val mnt = item.attributes["mount"] ?: ""
                val matchesMount = selectedLensMountFilter == "All" || mnt.contains(selectedLensMountFilter, ignoreCase = true)
                val matchesQuery = lensSearchQueryText.isBlank() || (
                    item.name.contains(lensSearchQueryText, ignoreCase = true) ||
                    item.brand.contains(lensSearchQueryText, ignoreCase = true) ||
                    (item.modelNumber?.contains(lensSearchQueryText, ignoreCase = true) == true)
                )
                matchesBrand && matchesMount && matchesQuery
            }
        }

        if (filteredPresets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("No lens presets found in local catalog.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (lensSearchQueryText.isNotBlank()) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isSearchingOnline = true
                                    webSearchResults = com.fotologue.core.data.preset.WebSpecSearchEngine.searchLensSpecs(lensSearchQueryText)
                                    isSearchingOnline = false
                                    showWebResultsDialog = true
                                }
                            },
                            enabled = !isSearchingOnline
                        ) {
                            Icon(painterResource(R.drawable.cloud_sync_24px), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSearchingOnline) "Searching Online..." else "🌐 Search Online Databases for \"$lensSearchQueryText\"")
                        }
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filteredPresets.size) { idx ->
                    val presetItem = filteredPresets[idx]
                    val spec = LensSpec(
                        make = presetItem.brand,
                        model = presetItem.name,
                        modelNumber = presetItem.modelNumber ?: "",
                        type = presetItem.subCategory ?: presetItem.attributes["type"] ?: "Prime",
                        mount = presetItem.attributes["mount"] ?: "Standard",
                        filterSize = presetItem.attributes["filterSize"]?.replace("mm", "")?.toIntOrNull() ?: 52,
                        isStabilized = false,
                        minFocusDistance = 0.5,
                        maxMagnification = 0.15,
                        minAperture = presetItem.attributes["minAperture"]?.replace("f/", "")?.toDoubleOrNull() ?: 22.0,
                        maxAperture = presetItem.attributes["maxAperture"]?.replace("f/", "")?.toDoubleOrNull() ?: 1.4,
                        minFocalLength = presetItem.attributes["focalLength"]?.replace("mm", "")?.toIntOrNull() ?: 50,
                        maxFocalLength = presetItem.attributes["focalLength"]?.replace("mm", "")?.toIntOrNull() ?: 50
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(presetItem.name, fontWeight = FontWeight.Bold)
                                Text("${spec.type} • Mount: ${spec.mount} • ${presetItem.attributes["focalLength"] ?: "50mm"} ${presetItem.attributes["maxAperture"] ?: "f/1.4"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onSelectSpec(spec) },
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
}
