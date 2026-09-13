package com.fotologue.app.ui.screens

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.R
import com.fotologue.app.data.*
import com.fotologue.core.designsystem.FotologueTheme
import com.fotologue.app.ui.viewmodel.AddLogViewModel
import com.fotologue.app.util.ExposureSuggester
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddLogScreen(
    viewModel: AddLogViewModel,
    onBack: () -> Unit,
    onMeasureExposure: (Int, Long?, Long?, Long?, Long?, Long?) -> Unit,
    onAddLens: (Long?) -> Unit,
    onAddFilm: () -> Unit = {},
    onNavigateToRollCounter: () -> Unit,
) {
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val cameras by viewModel.allCameras.collectAsStateWithLifecycle()
    val lenses by viewModel.filteredLenses.collectAsStateWithLifecycle()
    val teleconverters by viewModel.filteredTeleconverters.collectAsStateWithLifecycle()
    val filters by viewModel.filteredFilters.collectAsStateWithLifecycle()
    val flashes by viewModel.filteredFlashes.collectAsStateWithLifecycle()
    val viewfinders by viewModel.filteredViewfinders.collectAsStateWithLifecycle()
    val films by viewModel.allFilms.collectAsStateWithLifecycle()
    val kits by viewModel.allCameraKitsWithGear.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.fetchLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.editingLogId != null) stringResource(R.string.edit_exposure_log) else stringResource(R.string.add_exposure_log)) },
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
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isKitVisible) {
                CameraKitSelection(
                    kits = kits,
                    selectedKitId = uiState.selectedKitId,
                    onKitSelected = { viewModel.onCameraKitSelected(it) }
                )
            }

            // Gear Selection - Order: Camera, Lens, Film
            CameraSelection(
                cameras = cameras,
                selectedCameraId = uiState.selectedCameraId,
                onCameraSelected = { viewModel.onCameraSelected(it) }
            )

            LensSelection(
                lenses = lenses,
                selectedLensId = uiState.selectedLensId,
                onLensSelected = { viewModel.onLensSelected(it) },
                onAddLens = { onAddLens(uiState.selectedCameraId) }
            )

            FilmSelection(
                films = films,
                selectedFilmId = uiState.selectedFilmId,
                onFilmSelected = { id, iso -> viewModel.onFilmSelected(id, iso) },
                onAddFilm = onAddFilm
            )

            if (uiState.isTeleconverterVisible || uiState.isFilterVisible || uiState.isFlashVisible || uiState.isViewfinderVisible) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.isTeleconverterVisible || uiState.isFilterVisible) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (uiState.isTeleconverterVisible) {
                                Box(modifier = Modifier.weight(1f)) {
                                    TeleconverterSelection(
                                        teleconverters = teleconverters,
                                        selectedTeleconverterId = uiState.selectedTeleconverterId,
                                        onTeleconverterSelected = { viewModel.onTeleconverterSelected(it) }
                                    )
                                }
                            }
                            if (uiState.isFilterVisible) {
                                Box(modifier = Modifier.weight(1f)) {
                                    FilterSelection(
                                        filters = filters,
                                        selectedFilterId = uiState.selectedFilterId,
                                        onFilterSelected = { viewModel.onFilterSelected(it) }
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.isFlashVisible || uiState.isViewfinderVisible) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (uiState.isFlashVisible) {
                                Box(modifier = Modifier.weight(1f)) {
                                    FlashSelection(
                                        flashes = flashes,
                                        selectedFlashId = uiState.selectedFlashId,
                                        onFlashSelected = { viewModel.onFlashSelected(it) }
                                    )
                                }
                            }
                            if (uiState.isViewfinderVisible) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ViewfinderSelection(
                                        viewfinders = viewfinders,
                                        selectedViewfinderId = uiState.selectedViewfinderId,
                                        onViewfinderSelected = { viewModel.onViewfinderSelected(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            FocalLengthSelection(
                selectedLens = lenses.find { it.id == uiState.selectedLensId },
                focalLength = uiState.focalLength,
                onFocalLengthChange = { viewModel.onFocalLengthChange(it) }
            )

            // Exposure Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.label_exposure),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Row 1: ISO and Film Roll.Frame
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ISOSelection(
                                selectedIso = uiState.iso,
                                onIsoChange = { viewModel.onIsoChange(it) }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = uiState.filmRoll,
                                onValueChange = { viewModel.onFilmRollChange(it) },
                                label = { Text(stringResource(R.string.label_film_roll)) },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = onNavigateToRollCounter) {
                                        Icon(painterResource(R.drawable.settings_panorama_24px), contentDescription = "Manage Counters")
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 2: Aperture and Shutter Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ApertureSelection(
                                availableApertures = uiState.availableApertures,
                                selectedAperture = uiState.aperture,
                                onApertureChange = { viewModel.onApertureChange(it) }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ShutterSpeedSelection(
                                availableShutterSpeeds = uiState.availableShutterSpeeds,
                                selectedShutterSpeed = uiState.shutterSpeed,
                                onShutterSpeedChange = { viewModel.onShutterSpeedChange(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { 
                            onMeasureExposure(
                                uiState.iso,
                                uiState.selectedCameraId,
                                uiState.selectedLensId,
                                uiState.selectedTeleconverterId,
                                uiState.selectedFilterId,
                                uiState.selectedFilmId
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(painterResource(R.drawable.ic_exposure), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.measure_exposure))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Tag Section
            TagSelection(
                allTags = uiState.allTags,
                selectedTagIds = uiState.selectedTagIds,
                onTagToggled = { viewModel.onTagToggled(it) }
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onNotesChange(it) },
                label = { Text(stringResource(R.string.label_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (!locationPermissionState.status.isGranted) {
                Button(
                    onClick = { locationPermissionState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(painterResource(R.drawable.ic_location_on), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.grant_location_save))
                }
            }

            Button(
                onClick = { viewModel.saveLog(); onBack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (viewModel.editingLogId != null) stringResource(R.string.save_changes) else stringResource(R.string.save_log))
            }
            
            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraKitSelection(
    kits: List<CameraKitWithGear>,
    selectedKitId: Long?,
    onKitSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(value = false) }
    val selectedKit = kits.find { it.cameraKit.id == selectedKitId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedKit?.cameraKit?.name ?: stringResource(R.string.quick_fill_from_kit),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.camera_kit)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none_custom)) },
                onClick = {
                    onKitSelected(null)
                    expanded = false
                }
            )
            kits.forEach { kit ->
                DropdownMenuItem(
                    text = { Text(kit.cameraKit.name) },
                    onClick = {
                        onKitSelected(kit.cameraKit.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraSelection(
    cameras: List<Camera>,
    selectedCameraId: Long?,
    onCameraSelected: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCamera = cameras.find { it.id == selectedCameraId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCamera?.name ?: stringResource(R.string.select_camera),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_camera)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cameras.forEach { camera ->
                DropdownMenuItem(
                    text = { Text(camera.name) },
                    onClick = {
                        onCameraSelected(camera.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilmSelection(
    films: List<Film>,
    selectedFilmId: Long?,
    onFilmSelected: (Long, Int) -> Unit,
    onAddFilm: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFilm = films.find { it.id == selectedFilmId }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedFilm?.name ?: if (films.isEmpty()) "No matching film in library" else stringResource(R.string.select_film),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_film)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (films.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.ic_add), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add a New Film Stock...", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        },
                        onClick = {
                            expanded = false
                            onAddFilm()
                        }
                    )
                } else {
                    films.forEach { film ->
                        DropdownMenuItem(
                            text = { Text(film.name) },
                            onClick = {
                                onFilmSelected(film.id, film.iso)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (films.isEmpty()) {
            OutlinedCard(
                onClick = onAddFilm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No film stock matching this camera format in your library. Tap here to add a film.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LensSelection(
    lenses: List<Lens>,
    selectedLensId: Long?,
    onLensSelected: (Long?) -> Unit,
    onAddLens: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLens = lenses.find { it.id == selectedLensId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedLens?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.select_lens),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_lens)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onLensSelected(null)
                    expanded = false
                }
            )
            lenses.forEach { lens ->
                DropdownMenuItem(
                    text = { Text("${lens.make} ${lens.model}") },
                    onClick = {
                        onLensSelected(lens.id)
                        expanded = false
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.add_new_lens)) },
                onClick = {
                    onAddLens()
                    expanded = false
                },
                leadingIcon = { Icon(painterResource(R.drawable.ic_add), contentDescription = null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeleconverterSelection(
    teleconverters: List<Teleconverter>,
    selectedTeleconverterId: Long?,
    onTeleconverterSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTc = teleconverters.find { it.id == selectedTeleconverterId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTc?.name ?: stringResource(R.string.none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_teleconverter)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onTeleconverterSelected(null)
                    expanded = false
                }
            )
            teleconverters.forEach { tc ->
                DropdownMenuItem(
                    text = { Text(tc.name) },
                    onClick = {
                        onTeleconverterSelected(tc.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSelection(
    filters: List<Filter>,
    selectedFilterId: Long?,
    onFilterSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFilter = filters.find { it.id == selectedFilterId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedFilter?.name ?: stringResource(R.string.none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_filter)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onFilterSelected(null)
                    expanded = false
                }
            )
            filters.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter.name) },
                    onClick = {
                        onFilterSelected(filter.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlashSelection(
    flashes: List<Flash>,
    selectedFlashId: Long?,
    onFlashSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFlash = flashes.find { it.id == selectedFlashId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedFlash?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_flash)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onFlashSelected(null)
                    expanded = false
                }
            )
            flashes.forEach { flash ->
                DropdownMenuItem(
                    text = { Text("${flash.make} ${flash.model}") },
                    onClick = {
                        onFlashSelected(flash.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewfinderSelection(
    viewfinders: List<Viewfinder>,
    selectedViewfinderId: Long?,
    onViewfinderSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedVf = viewfinders.find { it.id == selectedViewfinderId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedVf?.name ?: stringResource(R.string.none),
            onValueChange = {},
            readOnly = true,
            label = { Text("Viewfinder") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onViewfinderSelected(null)
                    expanded = false
                }
            )
            viewfinders.forEach { vf ->
                DropdownMenuItem(
                    text = { Text(vf.name) },
                    onClick = {
                        onViewfinderSelected(vf.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FocalLengthSelection(
    selectedLens: Lens?,
    focalLength: Float?,
    onFocalLengthChange: (Float?) -> Unit,
) {
    if (selectedLens == null) return

    if (selectedLens.minFocalLength == selectedLens.maxFocalLength) {
        // Prime lens
        Text(
            text = stringResource(R.string.focal_length_mm, selectedLens.minFocalLength),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    } else {
        // Zoom lens
        var showManualEntry by remember { mutableStateOf(false) }
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.label_focal_length),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        val current = focalLength ?: selectedLens.minFocalLength.toFloat()
                        onFocalLengthChange((current - 1).coerceAtLeast(selectedLens.minFocalLength.toFloat()))
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_remove), contentDescription = "Decrease")
                }
                
                Slider(
                    value = focalLength ?: selectedLens.minFocalLength.toFloat(),
                    onValueChange = onFocalLengthChange,
                    valueRange = selectedLens.minFocalLength.toFloat()..selectedLens.maxFocalLength.toFloat(),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )

                FilledIconButton(
                    onClick = {
                        val current = focalLength ?: selectedLens.minFocalLength.toFloat()
                        onFocalLengthChange((current + 1).coerceAtMost(selectedLens.maxFocalLength.toFloat()))
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_add), contentDescription = "Increase")
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Surface(
                    modifier = Modifier
                        .width(70.dp)
                        .combinedClickable(
                            onClick = { showManualEntry = true },
                            onDoubleClick = { showManualEntry = true }
                        ),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "${focalLength?.toInt() ?: selectedLens.minFocalLength}mm",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        if (showManualEntry) {
            // Dialog for manual entry
            var textValue by remember { mutableStateOf((focalLength?.toInt() ?: selectedLens.minFocalLength).toString()) }
            AlertDialog(
                onDismissRequest = { showManualEntry = false },
                title = { Text("Enter Focal Length") },
                text = {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { if (it.all { char -> char.isDigit() }) textValue = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text("mm") },
                        label = { Text("Value (${selectedLens.minFocalLength}-${selectedLens.maxFocalLength})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newVal = textValue.toFloatOrNull()
                            if (newVal != null) {
                                onFocalLengthChange(newVal.coerceIn(selectedLens.minFocalLength.toFloat(), selectedLens.maxFocalLength.toFloat()))
                            }
                            showManualEntry = false
                        }
                    ) { Text("Set") }
                },
                dismissButton = {
                    TextButton(onClick = { showManualEntry = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShutterSpeedSelection(
    availableShutterSpeeds: List<String>,
    selectedShutterSpeed: String,
    onShutterSpeedChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val finalSpeeds = if (availableShutterSpeeds.isNotEmpty()) availableShutterSpeeds else ExposureSuggester.commonShutterSpeeds

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedShutterSpeed,
            onValueChange = onShutterSpeedChange,
            label = { Text(stringResource(R.string.label_shutter_speed)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            finalSpeeds.forEach { speed ->
                DropdownMenuItem(
                    text = { Text(speed) },
                    onClick = {
                        onShutterSpeedChange(speed)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApertureSelection(
    availableApertures: List<String>,
    selectedAperture: String,
    onApertureChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAperture,
            onValueChange = onApertureChange,
            label = { Text(stringResource(R.string.label_aperture)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableApertures.forEach { aperture ->
                DropdownMenuItem(
                    text = { Text(aperture) },
                    onClick = {
                        onApertureChange(aperture)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ISOSelection(
    selectedIso: Int,
    onIsoChange: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val isos = listOf(25, 50, 64, 80, 100, 125, 160, 200, 400, 800, 1600, 3200)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedIso.toString(),
            onValueChange = { it.toIntOrNull()?.let { iso -> onIsoChange(iso) } },
            label = { Text(stringResource(R.string.label_iso)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            isos.forEach { iso ->
                DropdownMenuItem(
                    text = { Text(iso.toString()) },
                    onClick = {
                        onIsoChange(iso)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagSelection(
    allTags: List<Tag>,
    selectedTagIds: Set<Long>,
    onTagToggled: (Long) -> Unit,
) {
    val subjects = allTags.filter { it.category == Tag.CATEGORY_SUBJECT }.sortedBy { it.name.lowercase() }
    val contents = allTags.filter { it.category == Tag.CATEGORY_CONTENT }.groupBy { it.parentId }

    var selectedSubjectId by remember { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(value = false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val selectedSubject = subjects.find { it.id == selectedSubjectId }
            OutlinedTextField(
                value = selectedSubject?.name ?: "None",
                onValueChange = {},
                readOnly = true,
                label = { Text("Subject") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("None") },
                    onClick = { selectedSubjectId = null; expanded = false }
                )
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject.name) },
                        onClick = { selectedSubjectId = subject.id; expanded = false }
                    )
                }
            }
        }

        if (selectedSubjectId != null) {
            val subjectContents = contents[selectedSubjectId] ?: emptyList()
            if (subjectContents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Content",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjectContents.forEach { tag ->
                        val isSelected = selectedTagIds.contains(tag.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTagToggled(tag.id) },
                            label = { Text(tag.name) },
                            leadingIcon = if (isSelected) {
                                { Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                            } else null
                        )
                    }
                }
            }
        }

        // Intent Section - Dropdown & Sub-chips for uncluttered UI
        val topIntents = remember(allTags) { allTags.filter { it.category == Tag.CATEGORY_INTENT && it.parentId == null }.sortedBy { it.name.lowercase() } }
        val childIntentsMap = remember(allTags) { allTags.filter { it.category == Tag.CATEGORY_INTENT }.groupBy { it.parentId } }

        var selectedIntentParentId by remember { mutableStateOf<Long?>(null) }
        var intentExpanded by remember { mutableStateOf(false) }

        if (topIntents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = intentExpanded,
                onExpandedChange = { intentExpanded = !intentExpanded }
            ) {
                val selectedParent = topIntents.find { it.id == selectedIntentParentId }
                OutlinedTextField(
                    value = selectedParent?.name ?: "None",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.category_intent)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intentExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = intentExpanded,
                    onDismissRequest = { intentExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedIntentParentId = null
                            intentExpanded = false
                        }
                    )
                    topIntents.forEach { parent ->
                        DropdownMenuItem(
                            text = { Text(parent.name) },
                            onClick = {
                                selectedIntentParentId = parent.id
                                onTagToggled(parent.id)
                                intentExpanded = false
                            }
                        )
                    }
                }
            }

            if (selectedIntentParentId != null) {
                val children = childIntentsMap[selectedIntentParentId] ?: emptyList()
                if (children.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${topIntents.find { it.id == selectedIntentParentId }?.name ?: "Intent"} Sub-Tags",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        children.forEach { child ->
                            val isChildSelected = selectedTagIds.contains(child.id)
                            FilterChip(
                                selected = isChildSelected,
                                onClick = { onTagToggled(child.id) },
                                label = { Text(child.name) },
                                leadingIcon = if (isChildSelected) {
                                    { Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
        
        // Show other selected tags
        val currentSubjectContentIds = contents[selectedSubjectId]?.map { it.id }?.toSet() ?: emptySet()
        val intentTagIds = allTags.filter { it.category == Tag.CATEGORY_INTENT }.map { it.id }.toSet()
        val otherSelectedTags = allTags.filter { selectedTagIds.contains(it.id) && !currentSubjectContentIds.contains(it.id) && !intentTagIds.contains(it.id) && it.id != selectedSubjectId }
        
        if (otherSelectedTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Also Selected:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                otherSelectedTags.forEach { tag ->
                    InputChip(
                        selected = true,
                        onClick = { onTagToggled(tag.id) },
                        label = { Text(tag.name) },
                        trailingIcon = { Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                    )
                }
            }
        }
    }
}
