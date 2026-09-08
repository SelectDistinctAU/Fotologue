package com.fotologue.app.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import com.fotologue.app.data.Tag
import com.fotologue.app.data.Lens
import com.fotologue.app.ui.theme.FotologueTheme
import com.fotologue.app.ui.viewmodel.AddLogViewModel
import com.fotologue.app.R
import com.fotologue.app.data.Camera
import com.fotologue.app.data.CameraKitWithGear
import com.fotologue.app.data.Film
import com.fotologue.app.data.Filter
import com.fotologue.app.data.Flash
import com.fotologue.app.data.Teleconverter
import com.fotologue.app.util.ExposureSuggester
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddLogScreen(
    viewModel: AddLogViewModel,
    onBack: () -> Unit,
    onMeasureExposure: (Int, Long?, Long?, Long?, Long?, Long?) -> Unit,
    onAddLens: (Long?) -> Unit,
    onNavigateToRollCounter: () -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isWideScreen = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(840) || isLandscape

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val cameras by viewModel.allCameras.collectAsStateWithLifecycle()
    val lenses by viewModel.filteredLenses.collectAsStateWithLifecycle()
    val teleconverters by viewModel.allTeleconverters.collectAsStateWithLifecycle()
    val filters by viewModel.allFilters.collectAsStateWithLifecycle()
    val flashes by viewModel.allFlashes.collectAsStateWithLifecycle()
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
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp)
        ) {
            if (uiState.isKitVisible) {
                CameraKitSelection(
                    kits = kits,
                    selectedKitId = uiState.selectedKitId,
                    onKitSelected = { viewModel.onCameraKitSelected(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        CameraSelection(
                            cameras = cameras,
                            selectedCameraId = uiState.selectedCameraId,
                            onCameraSelected = { viewModel.onCameraSelected(it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LensSelection(
                            lenses = lenses,
                            selectedLensId = uiState.selectedLensId,
                            onLensSelected = { viewModel.onLensSelected(it) },
                            onAddLens = { onAddLens(uiState.selectedCameraId) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FocalLengthSelection(
                            selectedLens = lenses.find { it.id == uiState.selectedLensId },
                            focalLength = uiState.focalLength,
                            onFocalLengthChange = { viewModel.onFocalLengthChange(it) }
                        )
                        
                        if (uiState.isTeleconverterVisible) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TeleconverterSelection(
                                teleconverters = teleconverters,
                                selectedTeleconverterId = uiState.selectedTeleconverterId,
                                onTeleconverterSelected = { viewModel.onTeleconverterSelected(it) }
                            )
                        }

                        if (uiState.isFilterVisible) {
                            Spacer(modifier = Modifier.height(16.dp))
                            FilterSelection(
                                filters = filters,
                                selectedFilterId = uiState.selectedFilterId,
                                onFilterSelected = { viewModel.onFilterSelected(it) }
                            )
                        }

                        if (uiState.isFlashVisible) {
                            Spacer(modifier = Modifier.height(16.dp))
                            FlashSelection(
                                flashes = flashes,
                                selectedFlashId = uiState.selectedFlashId,
                                onFlashSelected = { viewModel.onFlashSelected(it) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        FilmSelection(
                            films = films,
                            selectedFilmId = uiState.selectedFilmId,
                            onFilmSelected = { id, filmIso -> 
                                viewModel.onFilmSelected(id, filmIso)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilmRollAndIsoSelection(
                            filmRoll = uiState.filmRoll,
                            onFilmRollChange = { viewModel.onFilmRollChange(it) },
                            iso = uiState.iso,
                            onIsoChange = { viewModel.onIsoChange(it) },
                            expectedExposures = uiState.expectedExposures,
                            onCounterClick = onNavigateToRollCounter
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FilledTonalButton(
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
                    Spacer(modifier = Modifier.width(32.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ExposureSettingsFields(
                            shutterSpeed = uiState.shutterSpeed,
                            onShutterSpeedChange = { viewModel.onShutterSpeedChange(it) },
                            availableShutterSpeeds = uiState.availableShutterSpeeds,
                            aperture = uiState.aperture,
                            onApertureChange = { viewModel.onApertureChange(it) },
                            availableApertures = uiState.availableApertures
                        )
                    }
                }
            } else {
                CameraSelection(
                    cameras = cameras,
                    selectedCameraId = uiState.selectedCameraId,
                    onCameraSelected = { viewModel.onCameraSelected(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LensSelection(
                    lenses = lenses,
                    selectedLensId = uiState.selectedLensId,
                    onLensSelected = { viewModel.onLensSelected(it) },
                    onAddLens = { onAddLens(uiState.selectedCameraId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                FocalLengthSelection(
                    selectedLens = lenses.find { it.id == uiState.selectedLensId },
                    focalLength = uiState.focalLength,
                    onFocalLengthChange = { viewModel.onFocalLengthChange(it) }
                )
                
                if (uiState.isTeleconverterVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TeleconverterSelection(
                        teleconverters = teleconverters,
                        selectedTeleconverterId = uiState.selectedTeleconverterId,
                        onTeleconverterSelected = { viewModel.onTeleconverterSelected(it) }
                    )
                }

                if (uiState.isFilterVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FilterSelection(
                        filters = filters,
                        selectedFilterId = uiState.selectedFilterId,
                        onFilterSelected = { viewModel.onFilterSelected(it) }
                    )
                }

                if (uiState.isFlashVisible) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FlashSelection(
                        flashes = flashes,
                        selectedFlashId = uiState.selectedFlashId,
                        onFlashSelected = { viewModel.onFlashSelected(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                FilmSelection(
                    films = films,
                    selectedFilmId = uiState.selectedFilmId,
                    onFilmSelected = { id, filmIso -> 
                        viewModel.onFilmSelected(id, filmIso)
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                FilmRollAndIsoSelection(
                    filmRoll = uiState.filmRoll,
                    onFilmRollChange = { viewModel.onFilmRollChange(it) },
                    iso = uiState.iso,
                    onIsoChange = { viewModel.onIsoChange(it) },
                    expectedExposures = uiState.expectedExposures,
                    onCounterClick = onNavigateToRollCounter
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FilledTonalButton(
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

                Spacer(modifier = Modifier.height(16.dp))
                
                ExposureSettingsFields(
                    shutterSpeed = uiState.shutterSpeed,
                    onShutterSpeedChange = { viewModel.onShutterSpeedChange(it) },
                    availableShutterSpeeds = uiState.availableShutterSpeeds,
                    aperture = uiState.aperture,
                    onApertureChange = { viewModel.onApertureChange(it) },
                    availableApertures = uiState.availableApertures
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.faceDetected) {
                val peopleTag = uiState.allTags.find { it.category == Tag.CATEGORY_SUBJECT && it.name == "People" }
                val whoTags = uiState.allTags.filter { it.parentId == peopleTag?.id }.sortedBy { it.name.lowercase() }
                if (whoTags.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.face_detected_suggestion),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        whoTags.forEach { tag ->
                            FilterChip(
                                selected = uiState.selectedTagIds.contains(tag.id),
                                onClick = { viewModel.onTagToggled(tag.id) },
                                label = { Text(tag.name) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Text(stringResource(R.string.label_tags), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            TagSelection(
                tags = uiState.allTags,
                selectedTagIds = uiState.selectedTagIds,
                selectedSubjectId = uiState.selectedSubjectId,
                onSubjectSelected = { viewModel.onSubjectSelected(it) },
                onTagToggled = { viewModel.onTagToggled(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onNotesChange(it) },
                label = { Text(stringResource(R.string.label_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                prefix = { Box(modifier = Modifier.width(0.dp)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (uiState.selectedCameraId != null && uiState.selectedFilmId != null) {
                        if (locationPermissionState.status.isGranted || viewModel.editingLogId != null) {
                            viewModel.saveLog()
                            onBack()
                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.selectedCameraId != null && uiState.selectedFilmId != null
            ) {
                val buttonText = when {
                    viewModel.editingLogId != null -> stringResource(R.string.save_changes)
                    locationPermissionState.status.isGranted -> stringResource(R.string.save_log)
                    else -> stringResource(R.string.grant_location_save)
                }
                Text(buttonText)
            }
        }
    }

    if (uiState.showRollFinishedDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onRollFinishedDismissed,
            title = { Text("Film Roll Finished") },
            text = { Text("You have reached (or exceeded) the expected exposures for this roll. Would you like to start a new roll now?") },
            confirmButton = {
                Button(onClick = viewModel::finishRollAndStartNew) {
                    Text("Start New Roll")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onRollFinishedDismissed) {
                    Text("Not Yet")
                }
            }
        )
    }
}

@Composable
private fun FilmRollAndIsoSelection(
    filmRoll: String,
    onFilmRollChange: (String) -> Unit,
    iso: Int,
    onIsoChange: (Int) -> Unit,
    expectedExposures: Int? = null,
    onCounterClick: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val label = if (expectedExposures != null) {
            stringResource(R.string.label_film_roll) + " (of $expectedExposures)"
        } else {
            stringResource(R.string.label_film_roll)
        }
        
        OutlinedTextField(
            value = filmRoll,
            onValueChange = onFilmRollChange,
            label = { Text(label) },
            modifier = Modifier.weight(1.2f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            prefix = { Box(modifier = Modifier.width(0.dp)) },
            trailingIcon = {
                IconButton(onClick = onCounterClick) {
                    Icon(painterResource(R.drawable.ic_replay), contentDescription = "Manage Roll Counter")
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            value = iso.toString(),
            onValueChange = { 
                if (it.length <= 5) {
                    onIsoChange(it.toIntOrNull() ?: 0)
                }
            },
            label = { Text(stringResource(R.string.label_iso)) },
            modifier = Modifier.weight(0.8f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Box(modifier = Modifier.width(0.dp)) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TagSelection(
    tags: List<Tag>,
    selectedTagIds: Set<Long>,
    selectedSubjectId: Long?,
    onSubjectSelected: (Long?) -> Unit,
    onTagToggled: (Long) -> Unit
) {
    val subjects = remember(tags) { tags.filter { it.category == Tag.CATEGORY_SUBJECT }.sortedBy { it.name.lowercase() } }
    val contentTags = remember(tags) { tags.filter { it.category == Tag.CATEGORY_CONTENT }.sortedBy { it.name.lowercase() } }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Subject Chips
        Text(
            text = stringResource(R.string.label_select_subject),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjects.forEach { subject ->
                FilterChip(
                    selected = selectedSubjectId == subject.id,
                    onClick = { 
                        onSubjectSelected(if (selectedSubjectId == subject.id) null else subject.id) 
                    },
                    label = { Text(subject.name) }
                )
            }
        }

        // 2. Filtered Content Chips
        if (selectedSubjectId != null) {
            val filteredContent = contentTags.filter { it.parentId == selectedSubjectId }
            if (filteredContent.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.label_related_content),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredContent.forEach { tag ->
                        FilterChip(
                            selected = selectedTagIds.contains(tag.id),
                            onClick = { onTagToggled(tag.id) },
                            label = { Text(tag.name) }
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.msg_no_content_for_subject),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            Text(
                text = stringResource(R.string.msg_choose_subject_hint),
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 3. Show currently selected tags even if subject isn't active
        val currentlySelected = contentTags.filter { selectedTagIds.contains(it.id) && it.parentId != selectedSubjectId }
        if (currentlySelected.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.label_also_selected),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentlySelected.forEach { tag ->
                    FilterChip(
                        selected = true,
                        onClick = { onTagToggled(tag.id) },
                        label = { Text(tag.name) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraKitSelection(
    kits: List<CameraKitWithGear>,
    selectedKitId: Long?,
    onKitSelected: (Long?) -> Unit
) {
    if (kits.isEmpty()) return

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
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
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

@Composable
private fun FocalLengthSelection(
    selectedLens: Lens?,
    focalLength: Float?,
    onFocalLengthChange: (Float?) -> Unit
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
        Column {
            Text(
                text = stringResource(R.string.focal_length_mm, focalLength?.toInt() ?: selectedLens.minFocalLength),
                style = MaterialTheme.typography.labelMedium
            )
            Slider(
                value = focalLength ?: selectedLens.minFocalLength.toFloat(),
                onValueChange = { onFocalLengthChange(it) },
                valueRange = selectedLens.minFocalLength.toFloat()..selectedLens.maxFocalLength.toFloat(),
                steps = if (selectedLens.maxFocalLength > selectedLens.minFocalLength) selectedLens.maxFocalLength - selectedLens.minFocalLength - 1 else 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeleconverterSelection(
    teleconverters: List<Teleconverter>,
    selectedTeleconverterId: Long?,
    onTeleconverterSelected: (Long?) -> Unit
) {
    var tcExpanded by remember { mutableStateOf(false) }
    val selectedTc = teleconverters.find { it.id == selectedTeleconverterId }
    ExposedDropdownMenuBox(
        expanded = tcExpanded,
        onExpandedChange = { tcExpanded = !tcExpanded }
    ) {
        OutlinedTextField(
            value = selectedTc?.name ?: stringResource(R.string.select_teleconverter),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_teleconverter_optional)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tcExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = tcExpanded,
            onDismissRequest = { tcExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onTeleconverterSelected(null)
                    tcExpanded = false
                }
            )
            teleconverters.forEach { tc ->
                DropdownMenuItem(
                    text = { Text(tc.name) },
                    onClick = {
                        onTeleconverterSelected(tc.id)
                        tcExpanded = false
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
    onFilterSelected: (Long?) -> Unit
) {
    var filterExpanded by remember { mutableStateOf(false) }
    val selectedFilter = filters.find { it.id == selectedFilterId }
    ExposedDropdownMenuBox(
        expanded = filterExpanded,
        onExpandedChange = { filterExpanded = !filterExpanded }
    ) {
        OutlinedTextField(
            value = selectedFilter?.name ?: stringResource(R.string.select_filter),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_filter_optional)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = filterExpanded,
            onDismissRequest = { filterExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onFilterSelected(null)
                    filterExpanded = false
                }
            )
            filters.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter.name) },
                    onClick = {
                        onFilterSelected(filter.id)
                        filterExpanded = false
                    }
                )
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
    onAddLens: () -> Unit
) {
    var lensExpanded by remember { mutableStateOf(false) }
    val selectedLens = lenses.find { it.id == selectedLensId }
    ExposedDropdownMenuBox(
        expanded = lensExpanded,
        onExpandedChange = { lensExpanded = !lensExpanded }
    ) {
        OutlinedTextField(
            value = selectedLens?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.select_lens),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_lens)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lensExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = lensExpanded,
            onDismissRequest = { lensExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onLensSelected(null)
                    lensExpanded = false
                }
            )
            lenses.forEach { lens ->
                DropdownMenuItem(
                    text = { Text("${lens.make} ${lens.model}") },
                    onClick = {
                        onLensSelected(lens.id)
                        lensExpanded = false
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.add_new_lens)) },
                onClick = {
                    onAddLens()
                    lensExpanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraSelection(
    cameras: List<Camera>,
    selectedCameraId: Long?,
    onCameraSelected: (Long) -> Unit
) {
    var cameraExpanded by remember { mutableStateOf(false) }
    val selectedCamera = cameras.find { it.id == selectedCameraId }
    ExposedDropdownMenuBox(
        expanded = cameraExpanded,
        onExpandedChange = { cameraExpanded = !cameraExpanded }
    ) {
        OutlinedTextField(
            value = selectedCamera?.name ?: stringResource(R.string.select_camera),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_camera)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cameraExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = cameraExpanded,
            onDismissRequest = { cameraExpanded = false }
        ) {
            cameras.forEach { camera ->
                DropdownMenuItem(
                    text = { Text(camera.name) },
                    onClick = {
                        onCameraSelected(camera.id)
                        cameraExpanded = false
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
    onFilmSelected: (Long, Int) -> Unit
) {
    var filmExpanded by remember { mutableStateOf(false) }
    val selectedFilm = films.find { it.id == selectedFilmId }
    ExposedDropdownMenuBox(
        expanded = filmExpanded,
        onExpandedChange = { filmExpanded = !filmExpanded }
    ) {
        OutlinedTextField(
            value = selectedFilm?.let { "${it.name} (${it.expectedExposures} exp)" } ?: stringResource(R.string.select_film),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_film)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filmExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = filmExpanded,
            onDismissRequest = { filmExpanded = false }
        ) {
            films.forEach { film ->
                DropdownMenuItem(
                    text = { Text("${film.name} (ISO ${film.iso}, ${film.expectedExposures} exp)") },
                    onClick = {
                        onFilmSelected(film.id, film.iso)
                        filmExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposureSettingsFields(
    shutterSpeed: String,
    onShutterSpeedChange: (String) -> Unit,
    availableShutterSpeeds: List<String>,
    aperture: String,
    onApertureChange: (String) -> Unit,
    availableApertures: List<String>
) {
    val finalSpeeds = if (availableShutterSpeeds.isNotEmpty()) availableShutterSpeeds else ExposureSuggester.commonShutterSpeeds
    val finalApertures = if (availableApertures.isNotEmpty()) availableApertures else ExposureSuggester.commonApertures.map { "f/${if (it % 1.0 == 0.0) it.toInt() else it}" }

    var shutterExpanded by remember { mutableStateOf(false) }
    var apertureExpanded by remember { mutableStateOf(false) }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = shutterExpanded,
                onExpandedChange = { shutterExpanded = !shutterExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = shutterSpeed,
                    onValueChange = onShutterSpeedChange,
                    label = { Text(stringResource(R.string.label_shutter_speed)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shutterExpanded) },
                    modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = shutterExpanded,
                    onDismissRequest = { shutterExpanded = false }
                ) {
                    finalSpeeds.forEach { speed ->
                        DropdownMenuItem(
                            text = { Text(speed) },
                            onClick = {
                                onShutterSpeedChange(speed)
                                shutterExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            ExposedDropdownMenuBox(
                expanded = apertureExpanded,
                onExpandedChange = { apertureExpanded = !apertureExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = aperture,
                    onValueChange = onApertureChange,
                    label = { Text(stringResource(R.string.label_aperture)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = apertureExpanded) },
                    modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = apertureExpanded,
                    onDismissRequest = { apertureExpanded = false }
                ) {
                    finalApertures.forEach { ap ->
                        DropdownMenuItem(
                            text = { Text(ap) },
                            onClick = {
                                onApertureChange(ap)
                                apertureExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlashSelection(
    flashes: List<Flash>,
    selectedFlashId: Long?,
    onFlashSelected: (Long?) -> Unit
) {
    var flashExpanded by remember { mutableStateOf(false) }
    val selectedFlash = flashes.find { it.id == selectedFlashId }
    ExposedDropdownMenuBox(
        expanded = flashExpanded,
        onExpandedChange = { flashExpanded = !flashExpanded }
    ) {
        OutlinedTextField(
            value = selectedFlash?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.select_flash),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_flash_optional)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = flashExpanded) },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = flashExpanded,
            onDismissRequest = { flashExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onFlashSelected(null)
                    flashExpanded = false
                }
            )
            flashes.forEach { flash ->
                DropdownMenuItem(
                    text = { Text("${flash.make} ${flash.model}") },
                    onClick = {
                        onFlashSelected(flash.id)
                        flashExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddLogScreenPreview() {
    FotologueTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Exposure Log") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = "Leica M6",
                    onValueChange = {},
                    label = { Text("Camera") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "35mm f/2 Summicron-M ASPH",
                    onValueChange = {},
                    label = { Text("Lens") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "Kodak Portra 400",
                    onValueChange = {},
                    label = { Text("Film") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(painterResource(R.drawable.ic_exposure), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Measure Exposure")
                }
                Spacer(modifier = Modifier.height(16.dp))
                ExposureSettingsFields(
                    shutterSpeed = "1/125",
                    onShutterSpeedChange = {},
                    availableShutterSpeeds = emptyList(),
                    aperture = "f/8",
                    onApertureChange = {},
                    availableApertures = emptyList()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Save Log")
                }
            }
        }
    }
}
