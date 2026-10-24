package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.core.data.Lens
import com.fotologue.core.data.LensSpec
import com.fotologue.core.data.preset.MasterPresetEngine
import com.fotologue.app.ui.viewmodel.AddEditLensViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditLensScreen(
    viewModel: AddEditLensViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val masterBrands = remember {
        listOf(
            "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
            "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
            "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
            "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "Other..."
        )
    }
    var makeExpanded by remember { mutableStateOf(false) }
    var showLensPresetsDialog by remember { mutableStateOf(false) }

    if (showLensPresetsDialog) {
        val masterLensItems = remember { MasterPresetEngine.getPresetsByCategory("LENS") }
        val allBrands = remember { listOf("All") + masterLensItems.map { it.brand }.distinct().sorted() }

        var selectedBrandFilter by remember { mutableStateOf(if (uiState.make.isNotBlank() && allBrands.contains(uiState.make)) uiState.make else "All") }
        var selectedLensMountFilter by remember { mutableStateOf("All") }
        var lensSearchQueryText by remember { mutableStateOf("") }

        var brandDropdownExpanded by remember { mutableStateOf(false) }
        var mountDropdownExpanded by remember { mutableStateOf(false) }

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

        AlertDialog(
            onDismissRequest = { showLensPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.lensd), contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Lens from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select brand and mount filters to search lens presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
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

                    // Fallback Action Button
                    val fallbackButtonLabel = if (selectedBrandFilter != "All") "➕ Add New $selectedBrandFilter Lens" else "➕ Add New Custom Lens"
                    OutlinedButton(
                        onClick = {
                            if (selectedBrandFilter != "All") {
                                viewModel.onMakeChange(selectedBrandFilter)
                            }
                            if (selectedLensMountFilter != "All") {
                                viewModel.onMountChange(selectedLensMountFilter)
                            }
                            showLensPresetsDialog = false
                        },
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
                            Text("No lens presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                            onClick = {
                                                viewModel.onSelectSpec(spec)
                                                showLensPresetsDialog = false
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
                Button(onClick = { showLensPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.lensd),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.lens_details))
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
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.onSearch("")
                    showLensPresetsDialog = true
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Icon(painterResource(R.drawable.lensd), contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
            Text(
                stringResource(R.string.associated_cameras),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "Note: Only cameras with interchangeable lens mounts (or fixed-lens bodies missing their taking lens record) are listed.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.cameras.isEmpty()) {
                Text(
                    stringResource(R.string.error_no_cameras),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                if (!uiState.isCameraSelectionValid) {
                    Text(
                        stringResource(R.string.error_select_at_least_one_camera),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
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

            if (uiState.selectedCameraIds.isEmpty() && uiState.cameras.isNotEmpty()) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_add), contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
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
                onClick = { viewModel.saveLens(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save_lens), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.manual_entry),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Make (Dropdown)
            ExposedDropdownMenuBox(
                expanded = makeExpanded,
                onExpandedChange = { makeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.make,
                    onValueChange = viewModel::onMakeChange,
                    label = { Text(stringResource(R.string.label_make)) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = makeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = makeExpanded,
                    onDismissRequest = { makeExpanded = false }
                ) {
                    masterBrands.forEach { make ->
                        DropdownMenuItem(
                            text = { Text(make) },
                            onClick = {
                                if (make != "Other...") {
                                    viewModel.onMakeChange(make)
                                }
                                makeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = { Text(stringResource(R.string.label_model_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    Lens.TYPES.forEach { typeOption ->
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
            
            if (uiState.isInterchangeable) {
                var lensMountExpanded by remember { mutableStateOf(false) }
                val defaultLensMounts = remember { listOf("Pentax K", "Canon FD", "Nikon F", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed") }

                ExposedDropdownMenuBox(
                    expanded = lensMountExpanded,
                    onExpandedChange = { lensMountExpanded = !lensMountExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.mount,
                        onValueChange = viewModel::onMountChange,
                        label = { Text("Lens Mount") },
                        placeholder = { Text("e.g. Pentax K, Canon FD, Leica M") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lensMountExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = lensMountExpanded,
                        onDismissRequest = { lensMountExpanded = false }
                    ) {
                        defaultLensMounts.forEach { mnt ->
                            DropdownMenuItem(
                                text = { Text(mnt) },
                                onClick = {
                                    viewModel.onMountChange(mnt)
                                    lensMountExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.type == "Prime") {
                OutlinedTextField(
                    value = uiState.minFocalLength,
                    onValueChange = viewModel::onMinFocalLengthChange,
                    label = { Text(stringResource(R.string.label_focal_length_mm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uiState.minFocalLength,
                        onValueChange = viewModel::onMinFocalLengthChange,
                        label = { Text("Min FL (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.maxFocalLength,
                        onValueChange = viewModel::onMaxFocalLengthChange,
                        label = { Text("Max FL (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.minAperture,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("""^\d{0,2}(\.\d{0,2})?$"""))) {
                            viewModel.onMinApertureChange(newValue)
                        }
                    },
                    label = { Text("Max Ap (e.g. 1.8)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("1.8") }
                )
                OutlinedTextField(
                    value = uiState.maxAperture,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("""^\d{0,2}(\.\d{0,2})?$"""))) {
                            viewModel.onMaxApertureChange(newValue)
                        }
                    },
                    label = { Text("Min Ap (e.g. 22)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("22") }
                )
            }

            OutlinedTextField(
                value = uiState.filterSize,
                onValueChange = viewModel::onFilterSizeChange,
                label = { Text(stringResource(R.string.label_filter_size_mm)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.isStabilized,
                    onCheckedChange = viewModel::onIsStabilizedChange
                )
                Text(stringResource(R.string.label_image_stabilized))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.minFocusDistance,
                    onValueChange = viewModel::onMinFocusDistanceChange,
                    label = { Text("Min Focus (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.maxMagnification,
                    onValueChange = viewModel::onMaxMagnificationChange,
                    label = { Text("Max Mag (x)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveLens(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_lens))
            }
        }
    }
}
