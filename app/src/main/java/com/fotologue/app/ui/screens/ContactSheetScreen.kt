package com.fotologue.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.data.lab.PerformanceLog
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fotologue.app.R
import com.fotologue.core.data.MacroCopyNamingOptions
import com.fotologue.core.data.MacroCopyPreset
import com.fotologue.app.ui.viewmodel.ContactSheetFrame
import com.fotologue.app.ui.viewmodel.ContactSheetUiState
import com.fotologue.app.ui.viewmodel.ContactSheetViewModel
import com.fotologue.app.ui.viewmodel.ExportType
import com.fotologue.app.ui.viewmodel.RollKey

data class ContactSheetGridCell(
    val frameNumber: Int,
    val frame: ContactSheetFrame?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSheetScreen(
    viewModel: ContactSheetViewModel,
    isSafelightActive: Boolean = false,
    onToggleSafelight: (() -> Unit)? = null,
    onBack: () -> Unit,
    onLogClick: (Long) -> Unit,
    onAddLogForFrame: ((Int) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val performanceLogs by viewModel.performanceLogs.collectAsStateWithLifecycle()
    val processEvents by viewModel.processEvents.collectAsStateWithLifecycle()
    val allLogEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsStateWithLifecycle()

    ContactSheetScreenContent(
        uiState = uiState,
        performanceLogs = performanceLogs,
        processEvents = processEvents,
        allLogEntriesWithGear = allLogEntriesWithGear,
        isSafelightActive = isSafelightActive,
        onToggleSafelight = onToggleSafelight,
        onBack = onBack,
        onLogClick = onLogClick,
        onAddLogForFrame = onAddLogForFrame,
        onRollSelected = viewModel::selectRoll,
        onToggleFrameSelection = viewModel::toggleFrameSelection,
        onSelectAll = viewModel::selectAllFrames,
        onClearSelection = viewModel::clearSelection,
        onDeleteSelected = viewModel::deleteSelected,
        onExportSelected = viewModel::exportSelected,
        onShiftFrameNumbers = viewModel::shiftFrameNumbers
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSheetScreenContent(
    uiState: ContactSheetUiState,
    performanceLogs: List<PerformanceLog> = emptyList(),
    processEvents: List<LabProcessEvent> = emptyList(),
    allLogEntriesWithGear: List<LogEntryWithGear> = emptyList(),
    isSafelightActive: Boolean = false,
    onToggleSafelight: (() -> Unit)? = null,
    onBack: () -> Unit,
    onLogClick: (Long) -> Unit,
    onAddLogForFrame: ((Int) -> Unit)? = null,
    onRollSelected: (RollKey) -> Unit,
    onToggleFrameSelection: (Long) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onExportSelected: (ExportType, Uri, MacroCopyNamingOptions) -> Unit,
    onShiftFrameNumbers: (Int, Int) -> Unit
) {
    var showExportOptions by remember { mutableStateOf(false) }
    var actionFrameTarget by remember { mutableStateOf<ContactSheetFrame?>(null) }
    var actionGapFrameNumber by remember { mutableStateOf<Int?>(null) }

    var enableMacroCopyNaming by remember { mutableStateOf(false) }
    var macroPrefix by remember { mutableStateOf("_DSF") }
    var macroStartNumber by remember { mutableStateOf("1001") }
    var macroDigitCount by remember { mutableStateOf("4") }
    var macroIsReverse by remember { mutableStateOf(false) }

    var selectedStationTab by remember { mutableIntStateOf(0) }

    fun getMacroOptions() = MacroCopyNamingOptions(
        enabled = enableMacroCopyNaming,
        prefix = macroPrefix.trim(),
        startNumber = macroStartNumber.toIntOrNull() ?: 1001,
        digitCount = macroDigitCount.toIntOrNull() ?: 4,
        isReverse = macroIsReverse
    )

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> uri?.let { onExportSelected(ExportType.CSV, it, getMacroOptions()) } }

    val jsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { onExportSelected(ExportType.JSON, it, getMacroOptions()) } }

    val zipExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> uri?.let { onExportSelected(ExportType.ZIP, it, getMacroOptions()) } }

    val xmpExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri -> uri?.let { onExportSelected(ExportType.XMP, it, getMacroOptions()) } }

    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri -> uri?.let { onExportSelected(ExportType.PDF, it, getMacroOptions()) } }

    if (actionFrameTarget != null) {
        val target = actionFrameTarget!!
        AlertDialog(
            onDismissRequest = { actionFrameTarget = null },
            title = { Text("Frame #${target.frameNumber} Actions") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            actionFrameTarget = null
                            onShiftFrameNumbers(target.frameNumber, 1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Insert Frame Gap (+1 Offset from Frame #${target.frameNumber})")
                    }
                    TextButton(
                        onClick = {
                            actionFrameTarget = null
                            onShiftFrameNumbers(target.frameNumber, -1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pull Back Frame Numbers (-1 Offset from Frame #${target.frameNumber})")
                    }
                    TextButton(
                        onClick = {
                            actionFrameTarget = null
                            onLogClick(target.logWithGear.logEntry.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Log Details")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { actionFrameTarget = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (actionGapFrameNumber != null) {
        val gapFrameNo = actionGapFrameNumber!!
        AlertDialog(
            onDismissRequest = { actionGapFrameNumber = null },
            title = { Text("Frame #$gapFrameNo (Gap / Unlogged)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            actionGapFrameNumber = null
                            onShiftFrameNumbers(gapFrameNo, 1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Insert Additional Gap (+1 Offset from Frame #$gapFrameNo)")
                    }
                    TextButton(
                        onClick = {
                            actionGapFrameNumber = null
                            onShiftFrameNumbers(gapFrameNo + 1, -1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove Gap / Pull Frames Back (-1 Offset)")
                    }
                    if (onAddLogForFrame != null) {
                        TextButton(
                            onClick = {
                                actionGapFrameNumber = null
                                onAddLogForFrame(gapFrameNo)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log Exposure for Frame #$gapFrameNo")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { actionGapFrameNumber = null }) {
                    Text(stringResource(R.string.action_cancel))
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
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.export_options),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.export_as_csv)) },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        val fileName = "contact_sheet_${System.currentTimeMillis()}.csv"
                        csvExportLauncher.launch(fileName)
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.export_as_json)) },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        val fileName = "contact_sheet_${System.currentTimeMillis()}.json"
                        jsonExportLauncher.launch(fileName)
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.export_as_zip)) },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        val fileName = "contact_sheet_${System.currentTimeMillis()}.zip"
                        zipExportLauncher.launch(fileName)
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.export_as_xmp)) },
                    supportingContent = { Text("Individual .xmp sidecars per frame for Lightroom/Capture One") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        val fileName = "sidecars_${System.currentTimeMillis()}.zip"
                        xmpExportLauncher.launch(fileName)
                    }
                )
                ListItem(
                    headlineContent = { Text("Export as Printable PDF Proof Sheet") },
                    supportingContent = { Text("High-res A4 contact sheet for printing or digital binders") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showExportOptions = false
                        val fileName = "proof_sheet_${System.currentTimeMillis()}.pdf"
                        pdfExportLauncher.launch(fileName)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Macro Copy / Scanner Naming Options
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { enableMacroCopyNaming = !enableMacroCopyNaming }
                        .padding(vertical = 4.dp)
                ) {
                    Switch(
                        checked = enableMacroCopyNaming,
                        onCheckedChange = { enableMacroCopyNaming = it }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Macro Copy / Scanner Filename Matching", style = MaterialTheme.typography.titleMedium)
                        Text("Name exported XMP sidecars to match your camera RAW/TIF scan files (e.g. _DSF1024.xmp)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (enableMacroCopyNaming) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var presetMenuExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = presetMenuExpanded,
                                onExpandedChange = { presetMenuExpanded = !presetMenuExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = "Load Preset...",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Load Preset") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = presetMenuExpanded) },
                                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = presetMenuExpanded,
                                    onDismissRequest = { presetMenuExpanded = false }
                                ) {
                                    MacroCopyPreset.DEFAULT_PRESETS.forEach { preset ->
                                        DropdownMenuItem(
                                            text = { Text(preset.name) },
                                            onClick = {
                                                macroPrefix = preset.options.prefix
                                                macroStartNumber = preset.options.startNumber.toString()
                                                macroDigitCount = preset.options.digitCount.toString()
                                                macroIsReverse = preset.options.isReverse
                                                presetMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = macroPrefix,
                                    onValueChange = { macroPrefix = it },
                                    label = { Text("Prefix (e.g. _DSF, IMG_)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = macroStartNumber,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) macroStartNumber = it },
                                    label = { Text("Start No.") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = macroDigitCount,
                                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 1) macroDigitCount = it },
                                    label = { Text("Digits (3-5)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(100.dp),
                                    singleLine = true
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = macroIsReverse,
                                        onCheckedChange = { macroIsReverse = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reverse Order", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            if (uiState.isSelectionMode) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                    title = { Text(stringResource(R.string.selected_count, uiState.selectedFrameIds.size), color = safelightFg) },
                    navigationIcon = {
                        IconButton(onClick = onClearSelection) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.action_clear_selection), tint = safelightPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = onSelectAll) {
                            Icon(painterResource(R.drawable.ic_select_all), contentDescription = stringResource(R.string.action_select_all), tint = safelightPrimary)
                        }
                        IconButton(onClick = { showExportOptions = true }) {
                            Icon(painterResource(R.drawable.ic_share), contentDescription = "Export", tint = safelightPrimary)
                        }
                        IconButton(onClick = onDeleteSelected) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete), tint = safelightPrimary)
                        }
                    }
                )
            } else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.hub_24px),
                                contentDescription = null,
                                tint = safelightPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Station", style = MaterialTheme.typography.titleLarge, color = safelightFg)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Return to Application Hub", tint = safelightPrimary)
                        }
                    },
                    actions = {
                        if (onToggleSafelight != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = "Red Shift",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = safelightPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = isSafelightActive,
                                    onCheckedChange = { onToggleSafelight() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFFFF0000),
                                        checkedTrackColor = Color(0xFF660000),
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                    )
                                )
                            }
                        }
                        if (uiState.frames.isNotEmpty()) {
                            IconButton(onClick = onSelectAll) {
                                Icon(painterResource(R.drawable.ic_select_all), contentDescription = stringResource(R.string.action_select_all), tint = safelightPrimary)
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedStationTab,
                containerColor = safelightBg,
                contentColor = safelightPrimary
            ) {
                Tab(
                    selected = selectedStationTab == 0,
                    onClick = { selectedStationTab = 0 },
                    text = { Text("Contact Sheets", fontWeight = FontWeight.Bold, color = if (selectedStationTab == 0) safelightPrimary else safelightFg) }
                )
                Tab(
                    selected = selectedStationTab == 1,
                    onClick = { selectedStationTab = 1 },
                    text = { Text("Darkroom Reflections", fontWeight = FontWeight.Bold, color = if (selectedStationTab == 1) safelightPrimary else safelightFg) }
                )
            }

            if (selectedStationTab == 0) {
                // Roll Selector Dropdown Header
                if (uiState.availableRolls.isNotEmpty()) {
                var rollExpanded by remember { mutableStateOf(false) }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = rollExpanded,
                            onExpandedChange = { rollExpanded = !rollExpanded }
                        ) {
                            val activeRoll = uiState.selectedRollKey
                            val labelText = if (activeRoll != null) {
                                "${activeRoll.cameraName} • ${activeRoll.filmName} (Roll ${activeRoll.rollNumber})"
                            } else {
                                "Select Film Roll"
                            }

                            OutlinedTextField(
                                value = labelText,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Film Roll") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rollExpanded) },
                                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = rollExpanded,
                                onDismissRequest = { rollExpanded = false }
                            ) {
                                uiState.availableRolls.forEach { rollKey ->
                                    DropdownMenuItem(
                                        text = { Text("${rollKey.cameraName} • ${rollKey.filmName} (Roll ${rollKey.rollNumber})") },
                                        onClick = {
                                            onRollSelected(rollKey)
                                            rollExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.frames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No film roll logs available to display as a contact sheet.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val maxFrameNo = maxOf(
                    uiState.frames.maxOfOrNull { it.frameNumber } ?: 0,
                    uiState.expectedExposures
                )
                val frameMap = remember(uiState.frames) { uiState.frames.associateBy { it.frameNumber } }
                val gridCells = remember(maxFrameNo, frameMap) {
                    (1..maxFrameNo).map { frameNo ->
                        ContactSheetGridCell(frameNumber = frameNo, frame = frameMap[frameNo])
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(uiState.gridColumns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 80.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gridCells, key = { "cell_${it.frameNumber}_${it.frame?.logWithGear?.logEntry?.id ?: 0}" }) { cell ->
                        val frameItem = cell.frame
                        if (frameItem != null) {
                            val isSelected = uiState.selectedFrameIds.contains(frameItem.logWithGear.logEntry.id)
                            ContactSheetFrameCard(
                                frame = frameItem,
                                isSelected = isSelected,
                                showCheckbox = uiState.isSelectionMode,
                                onClick = {
                                    if (uiState.isSelectionMode) {
                                        onToggleFrameSelection(frameItem.logWithGear.logEntry.id)
                                    } else {
                                        onLogClick(frameItem.logWithGear.logEntry.id)
                                    }
                                },
                                onLongClick = {
                                    actionFrameTarget = frameItem
                                }
                            )
                        } else {
                            SkippedFrameCard(
                                frameNumber = cell.frameNumber,
                                onGapClick = { actionGapFrameNumber = cell.frameNumber }
                            )
                        }
                    }
                }
            }
        } else {
            // DARKROOM REFLECTIONS TAB (Moved to Station as requested!)
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "header_reflections_title") {
                    Text(
                        text = "DARKROOM REFLECTIONS & SESSION LOGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                item(key = "reflections_info_card") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🧪 Studio & Darkroom Reflections", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Review completed development events, enlarger print recipes, keeper ratings, and photographer learning reflections directly alongside your digital contact sheets.", style = MaterialTheme.typography.bodySmall, color = safelightFg)
                        }
                    }
                }

                item(key = "header_dev_events") {
                    Text(
                        text = "COMPLETED FILM DEVELOPMENT SESSIONS (${processEvents.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (processEvents.isEmpty()) {
                    item(key = "empty_dev_events") {
                        Text(
                            text = "No film development sessions logged yet. Process film in Lab to log events.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = safelightFg
                        )
                    }
                } else {
                    items(processEvents, key = { "dev_evt_${it.id}" }) { evt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = evt.filmRollId, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                Text(text = "Developer: ${evt.developerName} • Temp: ${evt.temperatureCelsius}°C • Duration: ${evt.durationSeconds / 60}m ${evt.durationSeconds % 60}s", style = MaterialTheme.typography.bodySmall, color = safelightPrimary)
                            }
                        }
                    }
                }

                item(key = "header_print_logs") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = "ENLARGER PRINT REFLECTION LOGS (${performanceLogs.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }

                if (performanceLogs.isEmpty()) {
                    item(key = "empty_print_logs") {
                        Text(
                            text = "No print reflection logs recorded yet. Log prints in Lab Enlarger Session.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = safelightFg
                        )
                    }
                } else {
                    items(performanceLogs, key = { "perf_log_${it.id}" }) { perf ->
                        val matchingEntry = allLogEntriesWithGear.find { entry -> entry.logEntry.id == perf.logEntryId }
                        val cameraName = matchingEntry?.camera?.name ?: "Camera"
                        val filmName = matchingEntry?.film?.name ?: "Film Stock"
                        val rollName = matchingEntry?.logEntry?.filmRoll ?: "Roll 01"
                        val frameNumStr = matchingEntry?.logEntry?.notes?.substringAfter("Frame #", "")?.takeWhile { it.isDigit() }
                        val frameNum = if (!frameNumStr.isNullOrBlank()) frameNumStr else "${perf.logEntryId}"
                        val rollFrameTitle = "$filmName ($cameraName) • $rollName, Frame #$frameNum"

                        val statusStr = if (perf.outcomeStatus == "KEEPER") {
                            if (isSafelightActive) "🔴 KEEPER (Exhibition)" else "🟢 KEEPER (Exhibition)"
                        } else {
                            "🔴 REJECT (${perf.rejectCategory ?: "Test"})"
                        }

                        val statusColor = if (perf.outcomeStatus == "KEEPER") {
                            if (isSafelightActive) Color(0xFFFF0000) else Color(0xFF2E7D32)
                        } else {
                            if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.error
                        }

                        Card(
                            onClick = { onLogClick(perf.logEntryId) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = rollFrameTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = statusStr, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = statusColor)
                                if (!perf.learningTag.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Learning Reflection: ${perf.learningTag}", style = MaterialTheme.typography.bodySmall, color = safelightFg)
                                }
                                if (perf.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Notes: ${perf.notes}", style = MaterialTheme.typography.bodySmall, color = safelightPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SkippedFrameCard(
    frameNumber: Int,
    onGapClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGapClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .height(150.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "#$frameNumber",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = "Skipped / Gap",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactSheetFrameCard(
    frame: ContactSheetFrame,
    isSelected: Boolean,
    showCheckbox: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val log = frame.logWithGear.logEntry

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        },
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header: Frame Badge
                Text(
                    text = "#${frame.frameNumber}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Thumbnail / Aspect Square Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (log.exposureSettings.thumbnailUri != null) {
                        AsyncImage(
                            model = log.exposureSettings.thumbnailUri,
                            contentDescription = "Thumbnail frame #${frame.frameNumber}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_photo_camera),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                // Exposure Tag
                Text(
                    text = "${log.exposureSettings.shutterSpeed} @ ${log.exposureSettings.aperture}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Export Badges
                if (log.isExportedZip || log.isExportedCsv || log.isExportedJson || log.isExportedXmp) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        if (log.isExportedZip) ContactSheetExportBadge("Z")
                        if (log.isExportedCsv) ContactSheetExportBadge("C")
                        if (log.isExportedJson) ContactSheetExportBadge("J")
                        if (log.isExportedXmp) ContactSheetExportBadge("X")
                    }
                }
            }

            if (showCheckbox) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun ContactSheetExportBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        color = Color.Transparent,
        modifier = Modifier.size(14.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
