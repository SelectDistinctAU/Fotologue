package com.fotologue.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fotologue.core.data.Camera
import com.fotologue.core.data.Film
import com.fotologue.core.data.LogEntry
import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.core.designsystem.FotologueTheme
import com.fotologue.app.ui.viewmodel.TimelineViewModel
import com.fotologue.app.ui.viewmodel.ExportType
import com.fotologue.app.R
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.ExposureSettings
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onBack: () -> Unit = {},
    onAddLogClick: () -> Unit,
    onLogClick: (Long) -> Unit,
    onGearLibraryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onContactSheetClick: () -> Unit = {},
    onLabClick: () -> Unit = {},
    selectedLogId: Long? = null,
) {
    val logs by viewModel.logEntries.collectAsStateWithLifecycle()
    val allLogs by viewModel.allLogEntries.collectAsStateWithLifecycle()
    val cameras by viewModel.allCameras.collectAsStateWithLifecycle()
    val selectedCameraId by viewModel.selectedCameraId.collectAsStateWithLifecycle()
    val showOnlyUnexported by viewModel.showOnlyUnexported.collectAsStateWithLifecycle()
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val localChangesPending by viewModel.localFoundationChangesPending.collectAsStateWithLifecycle()

    TimelineScreenContent(
        onBack = onBack,
        logs = logs,
        allLogsCount = allLogs.size,
        cameras = cameras,
        selectedCameraId = selectedCameraId,
        showOnlyUnexported = showOnlyUnexported,
        isOnboardingComplete = isOnboardingComplete,
        selectedIds = selectedIds,
        selectedLogId = selectedLogId,
        onAddLogClick = onAddLogClick,
        onLogClick = onLogClick,
        onGearLibraryClick = onGearLibraryClick,
        onSettingsClick = onSettingsClick,
        onContactSheetClick = onContactSheetClick,
        onLabClick = onLabClick,
        onCameraFilterClick = viewModel::setCameraFilter,
        onToggleUnexportedFilter = viewModel::toggleUnexportedFilter,
        onToggleSelection = viewModel::toggleSelection,
        onClearSelection = viewModel::clearSelection,
        onSelectAll = viewModel::selectAll,
        onDeleteSelected = viewModel::deleteSelected,
        onExportLogs = viewModel::exportLogs,
        localChangesPending = localChangesPending,
        onSetIncludeFoundationData = viewModel::setIncludeFoundationData
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimelineScreenContent(
    logs: List<LogEntryWithGear>,
    allLogsCount: Int,
    cameras: List<Camera>,
    selectedCameraId: Long?,
    showOnlyUnexported: Boolean,
    isOnboardingComplete: Boolean,
    selectedIds: Set<Long>,
    selectedLogId: Long?,
    onAddLogClick: () -> Unit,
    onLogClick: (Long) -> Unit,
    onGearLibraryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onContactSheetClick: () -> Unit = {},
    onLabClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onCameraFilterClick: (Long?) -> Unit,
    onToggleUnexportedFilter: () -> Unit,
    onToggleSelection: (Long) -> Unit,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onExportLogs: (ExportType, Uri) -> Unit,
    localChangesPending: Boolean,
    onSetIncludeFoundationData: (Boolean) -> Unit
) {
    val isSelectionMode = selectedIds.isNotEmpty()
    var showExportOptions by remember { mutableStateOf(value = false) }
    val currentConfiguration = LocalConfiguration.current

    val zipExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> uri?.let { onExportLogs(ExportType.ZIP, it) } }

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri -> uri?.let { onExportLogs(ExportType.PDF, it) } }

    var showFoundationSyncDialog by remember { mutableStateOf(false) }

    if (showFoundationSyncDialog) {
        AlertDialog(
            onDismissRequest = { showFoundationSyncDialog = false },
            title = { Text("Share Foundation Additions?") },
            text = { Text("You have added new gear or materials locally. Would you like to submit these additions to the Base Camp for review?\n\nSelecting 'Export Logs Only' will exclude your new items.") },
            confirmButton = {
                Button(onClick = {
                    showFoundationSyncDialog = false
                    val locale = currentConfiguration.locales[0]
                    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", locale)
                    // We need to pass the intention to include foundation data
                    onSetIncludeFoundationData(true)
                    zipExportLauncher.launch("fotologue_export_${dateFormat.format(Date())}.zip")
                }) {
                    Text("Share with Base")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        showFoundationSyncDialog = false
                    }) {
                        Text("Revert")
                    }
                    TextButton(onClick = {
                        showFoundationSyncDialog = false
                        val locale = currentConfiguration.locales[0]
                        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", locale)
                        onSetIncludeFoundationData(false)
                        zipExportLauncher.launch("fotologue_export_${dateFormat.format(Date())}.zip")
                    }) {
                        Text("Logs Only")
                    }
                }
            }
        )
    }

    if (showExportOptions) {
        ModalBottomSheet(
            onDismissRequest = { showExportOptions = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.export_options),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // The canonical ZIP payload required for the Desktop CCC
                ListItem(
                    headlineContent = { Text("Export Master Archive (ZIP)") },
                    supportingContent = { Text("Contains metadata, thumbnails, and sidecars for Desktop Vault ingestion.") },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        if (localChangesPending) {
                            showFoundationSyncDialog = true
                        } else {
                            val locale = currentConfiguration.locales[0]
                            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", locale)
                            onSetIncludeFoundationData(false)
                            zipExportLauncher.launch("fotologue_export_${dateFormat.format(Date())}.zip")
                        }
                    }
                )

                // Optional PDF contact sheet for visual review
                ListItem(
                    headlineContent = { Text("Export Contact Sheet (PDF)") },
                    supportingContent = { Text("A printable darkroom proof sheet of your field notes.") },
                    modifier = Modifier.clickable {
                        val locale = currentConfiguration.locales[0]
                        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", locale)
                        pdfExportLauncher.launch("contact_sheet_${dateFormat.format(Date())}.pdf")
                        showExportOptions = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text(stringResource(R.string.selected_count, selectedIds.size)) },
                    navigationIcon = {
                        IconButton(onClick = onClearSelection) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.action_clear_selection))
                        }
                    },
                    actions = {
                        IconButton(onClick = onSelectAll) {
                            Icon(painterResource(R.drawable.ic_select_all), contentDescription = stringResource(R.string.action_select_all))
                        }
                        IconButton(onClick = { showExportOptions = true }) {
                            Icon(painterResource(R.drawable.ic_share), contentDescription = "Export")
                        }
                        IconButton(onClick = onDeleteSelected) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.hiking_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Field", style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = "Back to Application Hub"
                            )
                        }
                    },
                    actions = {
                        if (logs.isNotEmpty()) {
                            IconButton(onClick = onSelectAll) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_select_all), 
                                    contentDescription = stringResource(R.string.action_select_all)
                                )
                            }
                        }
                        IconButton(onClick = onGearLibraryClick) {
                            Icon(painterResource(R.drawable.ic_handyman), contentDescription = stringResource(R.string.gear_library_title))
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode && isOnboardingComplete) {
                FloatingActionButton(
                    onClick = onAddLogClick,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(painterResource(R.drawable.list_alt_add_24px), contentDescription = stringResource(R.string.add_log))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
        ) {
            if (allLogsCount > 0) {
                Text(
                    text = stringResource(R.string.label_filter_by_camera),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    FilterChip(
                        selected = selectedCameraId == null,
                        onClick = { onCameraFilterClick(null) },
                        label = { Text(stringResource(R.string.filter_all_cameras)) }
                    )
                    FilterChip(
                        selected = showOnlyUnexported,
                        onClick = onToggleUnexportedFilter,
                        label = { Text("To Do (Unexported)") }
                    )
                    cameras.forEach { camera ->
                        FilterChip(
                            selected = selectedCameraId == camera.id,
                            onClick = { onCameraFilterClick(camera.id) },
                            label = { Text(camera.name) }
                        )
                    }
                }
            }

            if (logs.isEmpty()) {
                val emptyMessage = if (allLogsCount > 0) {
                    "No new logs exist yet."
                } else {
                    stringResource(R.string.timeline_empty)
                }
                Text(
                    emptyMessage,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding() + 80.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                ) {
                    items(logs) { logWithGear ->
                        val isSelected = selectedIds.contains(logWithGear.logEntry.id)
                        LogEntryItem(
                            logWithGear = logWithGear,
                            onClick = {
                                if (isSelectionMode) {
                                    onToggleSelection(logWithGear.logEntry.id)
                                } else {
                                    onLogClick(logWithGear.logEntry.id)
                                }
                            },
                            onLongClick = {
                                onToggleSelection(logWithGear.logEntry.id)
                            },
                            isSelected = isSelected || (logWithGear.logEntry.id == selectedLogId),
                            showCheckbox = isSelectionMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        color = Color.Transparent,
        modifier = Modifier.size(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogEntryItem(
    logWithGear: LogEntryWithGear,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean,
    showCheckbox: Boolean = false
) {
    val log = logWithGear.logEntry
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                log.exposureSettings.thumbnailUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Thumbnail",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    val locale = LocalConfiguration.current.locales[0]
                    val dateFormat = remember(locale) { SimpleDateFormat("MMM dd, yyyy HH:mm", locale) }
                    Text(
                        text = dateFormat.format(Date(log.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    val gearString = buildString {
                        append(logWithGear.camera?.name ?: stringResource(R.string.unknown_camera))
                        logWithGear.lens?.let { lens ->
                            append(" + ${lens.make} ${lens.model}")
                        }
                        logWithGear.teleconverter?.let { tc ->
                            append(" + ${tc.name}")
                        }
                        logWithGear.filter?.let { filter ->
                            append(" + ${filter.name}")
                        }
                        logWithGear.flash?.let { flash ->
                            append(" + ${flash.make} ${flash.model}")
                        }
                    }
                    Text(
                        text = gearString,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = logWithGear.film?.let { "${it.name} (ISO ${it.iso}, ${it.expectedExposures} exp)" } ?: stringResource(R.string.unknown_film),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!log.filmRoll.isNullOrBlank()) {
                        val expected = logWithGear.film?.expectedExposures
                        val isHalf = logWithGear.camera?.format == Film.FORMAT_35MM_HALF || logWithGear.camera?.isHalfFrame == true
                        val total = if (isHalf && (expected != null)) expected * 2 else expected
                        
                        val displayText = if (total != null) {
                            "${log.filmRoll}/$total"
                        } else {
                            log.filmRoll
                        }
                        
                        Text(
                            text = stringResource(R.string.label_film_roll) + " $displayText",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${log.exposureSettings.shutterSpeed} @ ${log.exposureSettings.aperture}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (log.latitude != null && log.longitude != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_location_on),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val locale = LocalConfiguration.current.locales[0]
                            Text(
                                text = String.format(locale, "%.4f, %.4f", log.latitude, log.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (logWithGear.tags.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            logWithGear.tags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tag.name, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }
                    if (log.notes.isNotEmpty()) {
                        Text(
                            text = log.notes,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Export Status Badges (Bottom Right)
                    if (log.isExportedZip || log.isExportedCsv || log.isExportedJson || log.isExportedXmp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (log.isExportedZip) ExportBadge("Z")
                            if (log.isExportedCsv) {
                                Spacer(modifier = Modifier.width(4.dp))
                                ExportBadge("C")
                            }
                            if (log.isExportedJson) {
                                Spacer(modifier = Modifier.width(4.dp))
                                ExportBadge("J")
                            }
                            if (log.isExportedXmp) {
                                Spacer(modifier = Modifier.width(4.dp))
                                ExportBadge("X")
                            }
                        }
                    }
                }
            }
            if (showCheckbox) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    FotologueTheme {
        // Mock data for preview
        val logWithGear = LogEntryWithGear(
            logEntry = LogEntry(
                timestamp = System.currentTimeMillis(),
                notes = "Test note",
                environmentalConditions = "Sunny",
                cameraId = 1,
                filmId = 1,
                exposureSettings = ExposureSettings("1/125", "f/8", 400),
                latitude = 37.7749,
                longitude = -122.4194
            ),
            camera = Camera(1, "Leica M3", CameraType.FILM_SLR),
            film = Film(1, "Kodak Portra 400", 400, Film.TYPE_COLOR_NEGATIVE),
            lens = null,
            teleconverter = null,
            filter = null,
            flash = null,
            kit = null
        )
        TimelineScreenContent(
            logs = listOf(logWithGear),
            allLogsCount = 1,
            cameras = listOf(Camera(1, "Leica M3", CameraType.FILM_SLR)),
            selectedCameraId = null,
            showOnlyUnexported = false,
            isOnboardingComplete = true,
            selectedIds = emptySet(),
            selectedLogId = null,
            onAddLogClick = {},
            onLogClick = {},
            onGearLibraryClick = {},
            onSettingsClick = {},
            onCameraFilterClick = {},
            onToggleUnexportedFilter = {},
            onToggleSelection = {},
            onClearSelection = {},
            onSelectAll = {},
            onDeleteSelected = {},
            onExportLogs = { _, _ -> },
            localChangesPending = false,
            onSetIncludeFoundationData = {}
        )
    }
}
