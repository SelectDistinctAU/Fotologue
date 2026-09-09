package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditCameraKitViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCameraKitScreen(
    viewModel: AddEditCameraKitViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (viewModel.kitId == null) 
                            stringResource(R.string.add_kit) 
                        else 
                            stringResource(R.string.edit_kit)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveKit(onSuccess) }) {
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
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.kit_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isNameValid,
                placeholder = { Text(stringResource(R.string.kit_name_placeholder)) }
            )

            // Camera Selection
            var cameraExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = cameraExpanded,
                onExpandedChange = { cameraExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCamera = uiState.allCameras.find { it.id == uiState.cameraId }
                OutlinedTextField(
                    value = selectedCamera?.name ?: stringResource(R.string.select_camera),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_camera)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cameraExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                    isError = !uiState.isCameraValid
                )
                ExposedDropdownMenu(
                    expanded = cameraExpanded,
                    onDismissRequest = { cameraExpanded = false }
                ) {
                    uiState.allCameras.forEach { camera ->
                        DropdownMenuItem(
                            text = { Text(camera.name) },
                            onClick = {
                                viewModel.onCameraSelected(camera.id)
                                cameraExpanded = false
                            }
                        )
                    }
                }
            }

            // Lens Selection
            var lensExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = lensExpanded,
                onExpandedChange = { lensExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedLens = uiState.filteredLenses.find { it.id == uiState.lensId }
                OutlinedTextField(
                    value = selectedLens?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.select_lens),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_lens)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lensExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                    isError = !uiState.isLensValid
                )
                ExposedDropdownMenu(
                    expanded = lensExpanded,
                    onDismissRequest = { lensExpanded = false }
                ) {
                    uiState.filteredLenses.forEach { lens ->
                        DropdownMenuItem(
                            text = { Text("${lens.make} ${lens.model}") },
                            onClick = {
                                viewModel.onLensSelected(lens.id)
                                lensExpanded = false
                            }
                        )
                    }
                }
            }

            // Film Selection
            var filmExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = filmExpanded,
                onExpandedChange = { filmExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedFilm = uiState.allFilms.find { it.id == uiState.filmId }
                OutlinedTextField(
                    value = selectedFilm?.let { "${it.name} (ISO ${it.iso})" } ?: stringResource(R.string.select_film),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_film)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filmExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                    isError = !uiState.isFilmValid
                )
                ExposedDropdownMenu(
                    expanded = filmExpanded,
                    onDismissRequest = { filmExpanded = false }
                ) {
                    uiState.allFilms.forEach { film ->
                        DropdownMenuItem(
                            text = { Text("${film.name} (ISO ${film.iso})") },
                            onClick = {
                                viewModel.onFilmSelected(film.id)
                                filmExpanded = false
                            }
                        )
                    }
                }
            }

            // Optional Gear
            if (uiState.isTeleconverterVisible) {
                var tcExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = tcExpanded,
                    onExpandedChange = { tcExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedTc = uiState.allTeleconverters.find { it.id == uiState.teleconverterId }
                    OutlinedTextField(
                        value = selectedTc?.name ?: stringResource(R.string.none),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_teleconverter)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tcExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = tcExpanded,
                        onDismissRequest = { tcExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = { viewModel.onTeleconverterSelected(null); tcExpanded = false })
                        uiState.allTeleconverters.forEach { tc ->
                            DropdownMenuItem(text = { Text(tc.name) }, onClick = { viewModel.onTeleconverterSelected(tc.id); tcExpanded = false })
                        }
                    }
                }
            }

            if (uiState.isFilterVisible) {
                var filterExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = filterExpanded,
                    onExpandedChange = { filterExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedFilter = uiState.allFilters.find { it.id == uiState.filterId }
                    OutlinedTextField(
                        value = selectedFilter?.name ?: stringResource(R.string.none),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_filter)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = { viewModel.onFilterSelected(null); filterExpanded = false })
                        uiState.allFilters.forEach { filter ->
                            DropdownMenuItem(text = { Text(filter.name) }, onClick = { viewModel.onFilterSelected(filter.id); filterExpanded = false })
                        }
                    }
                }
            }

            if (uiState.isFlashVisible) {
                var flashExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = flashExpanded,
                    onExpandedChange = { flashExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedFlash = uiState.allFlashes.find { it.id == uiState.flashId }
                    OutlinedTextField(
                        value = selectedFlash?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.none),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_flash)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = flashExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = flashExpanded,
                        onDismissRequest = { flashExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = { viewModel.onFlashSelected(null); flashExpanded = false })
                        uiState.allFlashes.forEach { flash ->
                            DropdownMenuItem(text = { Text("${flash.make} ${flash.model}") }, onClick = { viewModel.onFlashSelected(flash.id); flashExpanded = false })
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.saveKit(onSuccess) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_kit))
            }
        }
    }
}
