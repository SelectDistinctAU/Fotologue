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
                    IconButton(onClick = { viewModel.saveFilm(onBack) }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
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
            if (viewModel.filmId == null) {
                Button(
                    onClick = { showPresetsDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.settings_panorama_24px), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Select from Presets")
                }
                HorizontalDivider()
            }

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
