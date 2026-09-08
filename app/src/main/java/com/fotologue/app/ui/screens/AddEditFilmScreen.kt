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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.fotologue.app.data.Film
import com.fotologue.app.data.FilmPreset
import com.fotologue.app.data.FilmPresets
import com.fotologue.app.ui.viewmodel.AddEditFilmViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFilmScreen(
    viewModel: AddEditFilmViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showPresetsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.filmId == null) stringResource(R.string.add_film) else stringResource(R.string.edit_film)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (viewModel.filmId == null) {
                        TextButton(onClick = { showPresetsDialog = true }) {
                            Text(stringResource(R.string.presets))
                        }
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
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp)
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
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.abbreviation,
                onValueChange = viewModel::onAbbreviationChange,
                label = { Text("Abbreviation (for file naming, e.g. P400)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("E.g. TX400, G200") }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
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

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.abbreviation,
                onValueChange = viewModel::onAbbreviationChange,
                label = { Text("Abbreviation (for file naming, e.g. P400)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("E.g. TX400, G200") }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.iso,
                onValueChange = viewModel::onIsoChange,
                label = { Text(stringResource(R.string.label_iso)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.expectedExposures,
                onValueChange = viewModel::onExpectedExposuresChange,
                label = { Text("Expected Exposures (e.g. 12, 24, 36)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val filmTypes = Film.ALL_TYPES
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.film_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    isError = !uiState.isTypeValid,
                    supportingText = {
                        if (!uiState.isTypeValid) {
                            Text(stringResource(R.string.error_invalid_film_type))
                        }
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filmTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onTypeChange(type)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.saveFilm(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_film))
            }
        }
    }

    if (showPresetsDialog) {
        FilmPresetsDialog(
            onDismiss = { showPresetsDialog = false },
            onPresetSelected = { preset ->
                viewModel.onPresetSelected(preset)
                showPresetsDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmPresetsDialog(
    onDismiss: () -> Unit,
    onPresetSelected: (FilmPreset) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.common_films)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                var selectedManufacturer by remember { mutableStateOf<String?>(null) }
                
                if (selectedManufacturer == null) {
                    Text(stringResource(R.string.select_manufacturer), style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(FilmPresets.manufacturers) { manufacturer ->
                            ListItem(
                                headlineContent = { Text(manufacturer) },
                                modifier = Modifier.clickable { 
                                    selectedManufacturer = manufacturer 
                                }
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { selectedManufacturer = null }) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                        }
                        Text(selectedManufacturer!!, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(FilmPresets.getFilmsForManufacturer(selectedManufacturer!!)) { preset ->
                            ListItem(
                                headlineContent = { Text(preset.name) },
                                supportingContent = { Text("ISO ${preset.iso} • ${preset.type}") },
                                modifier = Modifier.clickable { 
                                    onPresetSelected(preset)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
