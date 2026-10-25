package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.fotologue.core.data.Film
import com.fotologue.core.data.FilmPreset
import com.fotologue.core.data.FilmPresets
import com.fotologue.app.ui.viewmodel.AddEditFilmViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFilmScreen(
    viewModel: AddEditFilmViewModel,
    onBack: () -> Unit,
    isPreArrivalMode: Boolean = false,
    onSavePreArrivalOrder: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var isCustomFormView by remember(uiState.name, viewModel.filmId) { mutableStateOf(viewModel.filmId != null || uiState.name.isNotBlank()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (!isCustomFormView && viewModel.filmId == null)
                            "Select Film Stock from Presets"
                        else if (viewModel.filmId == null)
                            stringResource(R.string.add_film)
                        else
                            stringResource(R.string.edit_film)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isCustomFormView && viewModel.filmId == null) {
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
                        IconButton(onClick = { viewModel.saveFilm(onBack) }) {
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
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isCustomFormView && viewModel.filmId == null) {
                // PRIMARY TARGET: FULL-SCREEN PRESET CATALOG BROWSER
                FilmPresetsBrowser(
                    initialFormat = uiState.format,
                    onPresetSelected = { preset, chosenFormat ->
                        if (isPreArrivalMode) {
                            val specs = "${preset.manufacturer} • type: ${preset.type}, iso: ${preset.iso}, format: $chosenFormat, abbreviation: ${preset.abbreviation}"
                            onSavePreArrivalOrder(preset.name, specs, preset.manufacturer)
                            onBack()
                        } else {
                            viewModel.onPresetSelected(preset, chosenFormat)
                            isCustomFormView = true
                        }
                    },
                    onAddCustomFilm = {
                        isCustomFormView = true
                    }
                )
            } else {
                // MANUAL DETAIL FORM VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var nameExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = nameExpanded && uiState.filteredPresets.isNotEmpty(),
                        onExpandedChange = { nameExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = {
                                viewModel.onNameChange(it)
                                nameExpanded = true
                            },
                            label = { Text(stringResource(R.string.film_name)) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth(),
                            isError = !uiState.isNameValid,
                            supportingText = { if (!uiState.isNameValid) Text(stringResource(R.string.error_name_empty)) }
                        )

                        ExposedDropdownMenu(
                            expanded = nameExpanded && uiState.filteredPresets.isNotEmpty(),
                            onDismissRequest = { nameExpanded = false }
                        ) {
                            uiState.filteredPresets.forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset.displayName) },
                                    onClick = {
                                        viewModel.onPresetSelected(preset)
                                        nameExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.abbreviation,
                        onValueChange = viewModel::onAbbreviationChange,
                        label = { Text("Abbreviation (e.g. TX400, G200)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Used for concise display") }
                    )

                    val filmFormats = Film.ALL_FORMATS
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
                            filmFormats.forEach { format ->
                                DropdownMenuItem(
                                    text = { Text(format) },
                                    onClick = {
                                        viewModel.onFormatChange(format)
                                        formatExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.iso,
                        onValueChange = viewModel::onIsoChange,
                        label = { Text(stringResource(R.string.label_iso)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = uiState.expectedExposures,
                        onValueChange = viewModel::onExpectedExposuresChange,
                        label = { Text("Expected Exposures (e.g. 12, 24, 36)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    val filmTypes = Film.ALL_TYPES
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
                            label = { Text(stringResource(R.string.film_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                            isError = !uiState.isTypeValid,
                            supportingText = {
                                if (!uiState.isTypeValid) {
                                    Text(stringResource(R.string.error_invalid_film_type))
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            filmTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.onTypeChange(type)
                                        typeExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isPreArrivalMode) {
                                val specs = "type: ${uiState.type}, iso: ${uiState.iso}, format: ${uiState.format}"
                                onSavePreArrivalOrder(uiState.name, specs, "Custom")
                                onBack()
                            } else {
                                viewModel.saveFilm(onComplete = onBack)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (isPreArrivalMode) "📦 Add to Pre-Arrival Inbound Register" else stringResource(R.string.save_film), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.FilmPresetsBrowser(
    initialFormat: String = Film.FORMAT_35MM,
    onPresetSelected: (FilmPreset, String) -> Unit,
    onAddCustomFilm: () -> Unit
) {
    val masterFilmBrands = remember {
        listOf("All") + FilmPresets.manufacturers.sorted()
    }
    val formatOptions = listOf("All", Film.FORMAT_35MM, Film.FORMAT_120, Film.FORMAT_LARGE)

    var selectedBrandFilter by remember { mutableStateOf("All") }
    var selectedFormatFilter by remember(initialFormat) { mutableStateOf(if (initialFormat in formatOptions) initialFormat else "All") }
    var brandDropdownExpanded by remember { mutableStateOf(false) }
    var formatDropdownExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredPresets = remember(selectedBrandFilter, selectedFormatFilter, searchQuery) {
        FilmPresets.presets.filter { preset ->
            val matchesBrand = selectedBrandFilter == "All" || preset.manufacturer.equals(selectedBrandFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() || (
                preset.name.contains(searchQuery, ignoreCase = true) ||
                preset.manufacturer.contains(searchQuery, ignoreCase = true) ||
                preset.abbreviation.contains(searchQuery, ignoreCase = true)
            )
            matchesBrand && matchesQuery
        }
    }

    Text(
        text = "Select a brand and format to view curated film presets:",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    // Filter by Film Brand Dropdown
    var brandQuery by remember { mutableStateOf("") }
    val filteredBrandOptions = remember(masterFilmBrands, brandQuery) {
        if (brandQuery.isBlank()) masterFilmBrands else masterFilmBrands.filter { it.contains(brandQuery, ignoreCase = true) }
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
            label = { Text("Filter by Film Brand") },
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

    // Filter by Film Format Dropdown
    var formatQuery by remember { mutableStateOf("") }
    val filteredFormatOptions = remember(formatOptions, formatQuery) {
        if (formatQuery.isBlank()) formatOptions else formatOptions.filter { it.contains(formatQuery, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = formatDropdownExpanded,
        onExpandedChange = { formatDropdownExpanded = !formatDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (formatDropdownExpanded) formatQuery else selectedFormatFilter,
            onValueChange = {
                formatQuery = it
                formatDropdownExpanded = true
            },
            label = { Text("Filter by Film Format") },
            placeholder = { Text(selectedFormatFilter) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatDropdownExpanded) },
            singleLine = true,
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = formatDropdownExpanded && filteredFormatOptions.isNotEmpty(),
            onDismissRequest = {
                formatDropdownExpanded = false
                formatQuery = ""
            }
        ) {
            filteredFormatOptions.forEach { fOpt ->
                DropdownMenuItem(
                    text = { Text(fOpt, fontWeight = if (fOpt == selectedFormatFilter) FontWeight.Bold else FontWeight.Normal) },
                    onClick = {
                        selectedFormatFilter = fOpt
                        formatDropdownExpanded = false
                        formatQuery = ""
                    }
                )
            }
        }
    }

    // Search Bar
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("Search Film Stocks") },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
    )

    // Fallback Action Button
    OutlinedButton(
        onClick = onAddCustomFilm,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(painterResource(R.drawable.ic_add), contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (selectedBrandFilter != "All") "Can't find stock? Add Custom $selectedBrandFilter Film" else "Add Custom / Unlisted Film Stock", fontWeight = FontWeight.Bold)
    }

    HorizontalDivider()

    if (filteredPresets.isEmpty()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("No film presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPresets, key = { "preset_film_${it.manufacturer}_${it.name}" }) { preset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(preset.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            val expDisplay = if (selectedFormatFilter.contains("120")) 12 else if (selectedFormatFilter.contains("Large")) 1 else preset.expectedExposures
                            Text(
                                text = "${preset.type} • ISO ${preset.iso} • $expDisplay exp • Abbr: ${preset.abbreviation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { onPresetSelected(preset, selectedFormatFilter) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Select", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmPresetsDialog(
    initialFormat: String = Film.FORMAT_35MM,
    onDismiss: () -> Unit,
    onPresetSelected: (FilmPreset, String) -> Unit
) {
    val masterFilmBrands = remember {
        listOf("All") + FilmPresets.manufacturers.sorted()
    }
    val formatOptions = listOf("All", Film.FORMAT_35MM, Film.FORMAT_120, Film.FORMAT_LARGE)

    var selectedBrandFilter by remember { mutableStateOf("All") }
    var selectedFormatFilter by remember(initialFormat) { mutableStateOf(if (initialFormat in formatOptions) initialFormat else "All") }
    var brandDropdownExpanded by remember { mutableStateOf(false) }
    var formatDropdownExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredPresets = remember(selectedBrandFilter, selectedFormatFilter, searchQuery) {
        FilmPresets.presets.filter { preset ->
            val matchesBrand = selectedBrandFilter == "All" || preset.manufacturer.equals(selectedBrandFilter, ignoreCase = true)
            val matchesFormat = true // Film emulsions apply to all format selections!
            val matchesQuery = searchQuery.isBlank() || (
                preset.name.contains(searchQuery, ignoreCase = true) ||
                preset.manufacturer.contains(searchQuery, ignoreCase = true) ||
                preset.abbreviation.contains(searchQuery, ignoreCase = true)
            )
            matchesBrand && matchesFormat && matchesQuery
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.frame_inspect_24px),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Film Stock from Presets",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select a brand to view curated film presets:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Filter by Film Brand Dropdown
                var brandQuery by remember { mutableStateOf("") }
                val filteredBrandOptions = remember(masterFilmBrands, brandQuery) {
                    if (brandQuery.isBlank()) masterFilmBrands else masterFilmBrands.filter { it.contains(brandQuery, ignoreCase = true) }
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
                        label = { Text("Filter by Film Brand") },
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

                // Filter by Film Format Dropdown
                var formatQuery by remember { mutableStateOf("") }
                val filteredFormatOptions = remember(formatOptions, formatQuery) {
                    if (formatQuery.isBlank()) formatOptions else formatOptions.filter { it.contains(formatQuery, ignoreCase = true) }
                }

                ExposedDropdownMenuBox(
                    expanded = formatDropdownExpanded,
                    onExpandedChange = { formatDropdownExpanded = !formatDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (formatDropdownExpanded) formatQuery else selectedFormatFilter,
                        onValueChange = {
                            formatQuery = it
                            formatDropdownExpanded = true
                        },
                        label = { Text("Filter by Film Format") },
                        placeholder = { Text(selectedFormatFilter) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatDropdownExpanded) },
                        singleLine = true,
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = formatDropdownExpanded && filteredFormatOptions.isNotEmpty(),
                        onDismissRequest = {
                            formatDropdownExpanded = false
                            formatQuery = ""
                        }
                    ) {
                        filteredFormatOptions.forEach { fOpt ->
                            DropdownMenuItem(
                                text = { Text(fOpt, fontWeight = if (fOpt == selectedFormatFilter) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedFormatFilter = fOpt
                                    formatDropdownExpanded = false
                                    formatQuery = ""
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Film Stocks") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
                )

                HorizontalDivider()

                if (filteredPresets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No film presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredPresets, key = { "preset_film_${it.manufacturer}_${it.name}" }) { preset ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(preset.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        val expDisplay = if (selectedFormatFilter.contains("120")) 12 else if (selectedFormatFilter.contains("Large")) 1 else preset.expectedExposures
                                        Text(
                                            text = "${preset.type} • ISO ${preset.iso} • $expDisplay exp • Abbr: ${preset.abbreviation}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { onPresetSelected(preset, selectedFormatFilter) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Select", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
