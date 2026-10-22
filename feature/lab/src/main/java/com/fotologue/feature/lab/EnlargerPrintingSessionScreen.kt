package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.fotologue.core.data.lab.ExposureMode
import com.fotologue.core.data.lab.ExposureStep
import com.fotologue.core.data.lab.StepType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import com.fotologue.core.data.DarkroomLightGuard
import com.fotologue.core.data.lab.PerformanceLog
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnlargerPrintingSessionScreen(
    logId: Long? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()
    val allCameras by viewModel.allCameras.collectAsState()
    val allFilms by viewModel.allFilms.collectAsState()
    val filmRollSessions by viewModel.allFilmRollSessions.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    // AUTO-TRIGGER: Turn Safelight Mode ON by default on entering Enlarger Printing Session!
    LaunchedEffect(Unit) {
        viewModel.setSafelightActive(true)
    }

    val paperMasters = masterList.filter { it.category.equals("Paper", ignoreCase = true) }

    // 1. TOP-LEVEL FILM ROLL SELECTION
    val availableRolls = remember(logEntriesWithGear, filmRollSessions, allCameras, allFilms) {
        val rollFrameCounts = mutableMapOf<String, Int>()

        // 1. Map active film roll sessions
        filmRollSessions.forEach { session ->
            val camera = allCameras.find { it.id == session.cameraId }?.name ?: "Camera"
            val film = allFilms.find { it.id == session.filmId }?.name ?: "Film Stock"
            val key = "$camera • $film • Roll ${session.rollNumber}"
            rollFrameCounts[key] = rollFrameCounts.getOrDefault(key, 0)
        }

        // 2. Count logged exposure frames per roll
        logEntriesWithGear.forEach { entry ->
            val camera = entry.camera?.name ?: "Camera"
            val film = entry.film?.name ?: "Film Stock"
            val rollRaw = entry.logEntry.filmRoll ?: "Roll 1"
            val rollNum = if (rollRaw.contains(".")) rollRaw.substringBefore(".") else rollRaw
            val rollClean = if (rollNum.all { c -> c.isDigit() }) "Roll $rollNum" else rollNum
            val key = "$camera • $film • $rollClean"
            rollFrameCounts[key] = (rollFrameCounts[key] ?: 0) + 1
        }

        val formatted = rollFrameCounts.map { (key, count) ->
            if (count > 0) "$key ($count frames)" else key
        }.distinct().sorted()

        if (formatted.isNotEmpty()) formatted else listOf("Pentax LX • Ilford FP4 Plus 125 • Roll 1", "Canon AE-1 Program • Ilford FP4 Plus 125 • Roll 1", "Leica M6 • Kodak Tri-X 400 • Roll 1")
    }

    var selectedRollText by remember(availableRolls) { mutableStateOf(availableRolls.first()) }
    var rollExpanded by remember { mutableStateOf(false) }

    // 2. DETAILED FRAME LAYER SELECTION FOR THE SELECTED ROLL
    val availableFramesForSelectedRoll = remember(selectedRollText, logEntriesWithGear) {
        val matchingEntries = logEntriesWithGear.filter { entry ->
            val camera = entry.camera?.name ?: "Camera"
            val film = entry.film?.name ?: "Film Stock"
            selectedRollText.contains(camera) && selectedRollText.contains(film)
        }

        val frames = matchingEntries.map { entry ->
            val frameNumStr = entry.logEntry.notes.substringAfter("Frame #", "").takeWhile { it.isDigit() }
            val frameNum = if (frameNumStr.isNotBlank()) frameNumStr else "${entry.logEntry.id}"
            val shutter = entry.logEntry.exposureSettings.shutterSpeed
            val aperture = entry.logEntry.exposureSettings.aperture
            Pair(entry.logEntry.id, "Frame #$frameNum • $shutter @ $aperture")
        }.distinctBy { it.first }

        if (frames.isNotEmpty()) frames else listOf(Pair(logEntriesWithGear.firstOrNull()?.logEntry?.id ?: 1L, "Frame #01 • 1/125 @ f/8"))
    }

    var selectedFramePair by remember(availableFramesForSelectedRoll) { mutableStateOf(availableFramesForSelectedRoll.first()) }
    var frameExpanded by remember { mutableStateOf(false) }

    // User Enlarger Bodies & Lenses from Equipment DB
    val userEnlargers = equipmentList.filter { it.category.equals("Enlarger", ignoreCase = true) }.map { it.name }
    val enlargerOptions = if (userEnlargers.isNotEmpty()) userEnlargers else listOf("Intrepid Compact 4x5 Enlarger", "Beseler 23C II", "Omega D2 4x5")

    val dbEnlargerLenses = equipmentList.filter { it.category.equals("Enlarger Lens", ignoreCase = true) }.map { it.name }
    val enlargerLensOptions = if (dbEnlargerLenses.isNotEmpty()) dbEnlargerLenses else listOf("Rodagon 80mm f/5.6", "El-Nikkor 50mm f/2.8", "Schneider Componon-S 100mm f/5.6")

    val standardPrintDimensions = listOf("4x5\"", "5x7\"", "8x10\"", "11x14\"", "12x16\"", "16x20\"", "20x24\"", "30x40 cm", "40x50 cm")
    val standardGradeFilters = listOf("Grade 00", "Grade 0", "Grade 0.5", "Grade 1", "Grade 1.5", "Grade 2", "Grade 2.5", "Grade 3", "Grade 3.5", "Grade 4", "Grade 4.5", "Grade 5", "Split-Grade (00 + 5)")
    val darkroomDefectsLOV = remember {
        listOf(
            "Dust / Scratch on Negative",
            "Exhausted Chemistry",
            "Incorrect Exposure Time",
            "Wrong Grade Filter",
            "Paper Fogged / Light Leak",
            "Dodge & Burn Error",
            "Uneven Development / Agitation",
            "Enlarger Focus Drift"
        )
    }

    val availableLearningTags = remember {
        mutableStateListOf(
            "Fine Art Exhibition Candidate",
            "Grade 3 Contrast Test",
            "Burn Sky +1 Stop",
            "Dodge Shadows 2s",
            "Test Strip Calibration",
            "Paper Tonal Comparison",
            "Split-Grade Filter Test"
        )
    }
    val selectedLearningTags = remember { mutableStateListOf("Fine Art Exhibition Candidate") }

    var selectedMasterId by remember { mutableStateOf(paperMasters.firstOrNull()?.id) }
    var selectedEnlargerBody by remember(enlargerOptions) { mutableStateOf(enlargerOptions.first()) }
    var printSizeText by remember { mutableStateOf("8x10\"") }
    var enlargerLensText by remember(enlargerLensOptions) { mutableStateOf(enlargerLensOptions.first()) }
    var baseExposureSecText by remember { mutableStateOf("12.5") }
    var gradeFilterText by remember { mutableStateOf("Grade 2.5") }
    var dodgeBurnNotes by remember { mutableStateOf("Dodge sky -1 EV (3s), Burn shadows +1 EV (5s)") }

    // v1.3 Expanded Darkroom Parameters
    var lightHeadTypeText by remember { mutableStateOf("Condenser") }
    var lightHeadExpanded by remember { mutableStateOf(false) }
    val lightHeadOptions = listOf("Condenser", "Cold Light (Grid)", "Color / Dichroic Diffusion", "LED Modulated")

    var takingApertureText by remember { mutableStateOf("f/11") }
    var apertureExpanded by remember { mutableStateOf(false) }
    val apertureOptions = listOf("f/4", "f/5.6", "f/8", "f/11", "f/16", "f/22")

    var enlargerHeightCmText by remember { mutableStateOf("34.5") }
    var magnificationRatioText by remember { mutableStateOf("3.2") }

    var developerNameText by remember { mutableStateOf("Ilford Multigrade Developer") }
    var developerExpanded by remember { mutableStateOf(false) }
    val onHandDevelopers = remember(masterList) {
        masterList.filter { it.category.contains("Chemistry", ignoreCase = true) || it.name.contains("Developer", ignoreCase = true) }
            .map { "📦 ${it.name} (${it.manufacturer})" }
    }
    val masterDeveloperOptions = listOf(
        "Ilford Multigrade Developer",
        "Kodak Dektol",
        "Ansco 130 Paper Developer",
        "Selectol-Soft Paper Developer",
        "Kodak XTOL",
        "🏠 Guest / Community Darkroom Developer..."
    )
    val developerDropdownGroups = remember(onHandDevelopers) {
        if (onHandDevelopers.isNotEmpty()) {
            (onHandDevelopers + "───────────────" + masterDeveloperOptions).distinct()
        } else {
            masterDeveloperOptions
        }
    }

    var dilutionText by remember { mutableStateOf("1+9") }
    var dilutionExpanded by remember { mutableStateOf(false) }
    val dilutionOptions = listOf(
        "1+9 (Ilford / Kodak Standard)",
        "1+4 (Concentrated / Fast Dev)",
        "1+2 (Dektol Base)",
        "1+1 (Ansco 130 Base)",
        "Stock (Undiluted)",
        "1+19 (High-Economy / Soft)"
    )

    var fixerAgeCapacityText by remember { mutableStateOf("Batch #1 (12 prints processed)") }
    var washTimeMinutesText by remember { mutableStateOf("20") }
    var dryDownNoteText by remember { mutableStateOf("Baryta/FB paper darkens by ~8%–10% upon drying") }
    var testStripRangeText by remember { mutableStateOf("2s, 4s, 8s, 16s, 32s") }

    // EXPOSURE STEP LEDGER STATE (v1.3 Final Spec)
    var exposureModeState by remember { mutableStateOf(ExposureMode.SINGLE_GRADE) }
    val exposureStepsList = remember {
        mutableStateListOf(
            ExposureStep(
                id = "step_base_1",
                sessionId = "temp",
                stepType = StepType.BASE,
                grade = 2.5f,
                timeSeconds = 12.5f,
                region = "Full Frame",
                tool = null,
                sortOrder = 0
            )
        )
    }

    // Always-visible Test Strip Diagnostic Ranges
    var testStripBaseRangeText by remember { mutableStateOf("2s, 4s, 8s, 16s, 32s") }
    var testStripSoftRangeText by remember { mutableStateOf("4s, 8s, 12s, 16s") }
    var testStripHardRangeText by remember { mutableStateOf("2s, 3s, 4s, 5s") }

    // Option B: Legacy Unstructured Notes Card State
    var legacyNotesText by remember { mutableStateOf("Dodge sky -1 EV (3s), Burn shadows +1 EV (5s)") }
    var showLegacyNotesCard by remember { mutableStateOf(true) }

    // SMART AUTO-PREFILL 1: Enlarger Head Type from Enlarger Body Selection
    LaunchedEffect(selectedEnlargerBody) {
        val bodyLower = selectedEnlargerBody.lowercase()
        lightHeadTypeText = when {
            bodyLower.contains("intrepid") || bodyLower.contains("led") -> "LED Modulated"
            bodyLower.contains("color") || bodyLower.contains("dichro") || bodyLower.contains("lpl") -> "Color / Dichroic Diffusion"
            bodyLower.contains("cold") || bodyLower.contains("grid") -> "Cold Light (Grid)"
            else -> "Condenser"
        }
    }

    // SMART AUTO-PREFILL 2: Base Dilution from Developer Selection
    LaunchedEffect(developerNameText) {
        val devLower = developerNameText.lowercase()
        dilutionText = when {
            devLower.contains("ansco") -> "1+1"
            devLower.contains("dektol") -> "1+2"
            devLower.contains("selectol") -> "1+1"
            devLower.contains("xtol") -> "1+1"
            else -> "1+9" // Standard Ilford Multigrade / Kodak Liquid Paper Dev
        }
    }

    // SMART AUTO-PREFILL 3: Dry-Down Advisory Note from Paper Stock Profile
    val selectedPaperMaster = paperMasters.find { it.id == selectedMasterId }
    LaunchedEffect(selectedPaperMaster) {
        if (selectedPaperMaster != null) {
            val pName = selectedPaperMaster.name.lowercase()
            dryDownNoteText = if (pName.contains("fb") || pName.contains("baryta") || pName.contains("fiber") || pName.contains("foma")) {
                "Baryta/FB paper darkens by ~8%–10% upon drying"
            } else {
                "RC paper has minimal dry-down (~2%–3%)"
            }
        }
    }

    // SMART AUTO-CALCULATOR 4: Magnification Ratio (M = d2 / d1) & Lens-to-Paper Distance (v = f * (M + 1))
    LaunchedEffect(selectedRollText, printSizeText, enlargerLensText) {
        // 1. Negative Format Diagonal (d1)
        val rLower = selectedRollText.lowercase()
        val d1Mm = when {
            rLower.contains("4x5") || rLower.contains("large format") -> 162.89 // 4x5 sheet
            rLower.contains("120") || rLower.contains("medium format") || rLower.contains("6x6") || rLower.contains("6x7") -> 79.20 // 120 medium format
            else -> 43.27 // 35mm (24x36mm)
        }

        // 2. Print Size Diagonal (d2)
        val pLower = printSizeText.lowercase()
        val d2Mm = when {
            pLower.contains("4x5") -> 162.89
            pLower.contains("5x7") -> 218.49
            pLower.contains("8x10") -> 325.27
            pLower.contains("11x14") -> 452.23
            pLower.contains("12x16") -> 508.00
            pLower.contains("16x20") -> 650.56
            pLower.contains("20x24") -> 793.62
            pLower.contains("30x40") -> 500.00
            pLower.contains("40x50") -> 640.31
            else -> 325.27 // 8x10" default
        }

        // 3. Linear Magnification Ratio M = d2 / d1
        val calculatedM = (d2Mm / d1Mm)
        magnificationRatioText = String.format("%.1fx", calculatedM)

        // 4. Lens Focal Length (f in cm)
        val lLower = enlargerLensText.lowercase()
        val focalMm = when {
            lLower.contains("150mm") -> 150.0
            lLower.contains("135mm") -> 135.0
            lLower.contains("105mm") -> 105.0
            lLower.contains("80mm") -> 80.0
            lLower.contains("75mm") -> 75.0
            lLower.contains("50mm") -> 50.0
            else -> 80.0 // Default 80mm
        }
        val focalCm = focalMm / 10.0

        // 5. Lens-to-Paper Distance v = f * (M + 1) in cm
        val calculatedVCm = focalCm * (calculatedM + 1.0)
        enlargerHeightCmText = String.format("%.1f", calculatedVCm)
    }

    // Reflection & Outcome State
    var outcomeStatus by remember { mutableStateOf("KEEPER") } // KEEPER vs REJECT
    var darkroomDefectReason by remember { mutableStateOf(darkroomDefectsLOV.first()) }
    var defectExpanded by remember { mutableStateOf(false) }
    var outcomeNotes by remember { mutableStateOf("Fine-art exhibition print candidate") }
    var showAddCustomTagDialog by remember { mutableStateOf(false) }
    var newCustomTagInput by remember { mutableStateOf("") }

    var masterExpanded by remember { mutableStateOf(false) }
    var enlargerBodyExpanded by remember { mutableStateOf(false) }
    var printSizeExpanded by remember { mutableStateOf(false) }
    var lensExpanded by remember { mutableStateOf(false) }
    var gradeExpanded by remember { mutableStateOf(false) }
    var learningExpanded by remember { mutableStateOf(false) }

    // Check paper stock on hand
    val paperLots = lotList.filter { it.masterId == selectedMasterId }
    val paperOnHandQty = paperLots.filter { it.status == "ON_HAND" || it.status == "IN_USE" }.sumOf { it.quantity }
    val isPaperOnHand = selectedPaperMaster == null || paperOnHandQty > 0 || paperLots.isEmpty()

    // PURE DARKROOM SAFELIGHT RED COLORS (ZERO WHITE OR GREY LIGHT!)
    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary
    val safelightBtnBg = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary
    val safelightBtnFg = if (isSafelightActive) Color(0xFFFF0000) else Color.White

    val textFieldColors = if (isSafelightActive) {
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFFF0000),
            unfocusedTextColor = Color(0xFFFF0000),
            focusedBorderColor = Color(0xFFFF0000),
            unfocusedBorderColor = Color(0xFF660000),
            focusedLabelColor = Color(0xFFFF0000),
            unfocusedLabelColor = Color(0xFFCC0000)
        )
    } else {
        OutlinedTextFieldDefaults.colors()
    }

    val chipColors = if (isSafelightActive) {
        FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF8B0000),
            selectedLabelColor = Color(0xFFFF0000),
            containerColor = Color(0xFF1F0000),
            labelColor = Color(0xFFFF3333)
        )
    } else {
        FilterChipDefaults.filterChipColors()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.table_lamp_24px),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Enlarger Session",
                            style = MaterialTheme.typography.titleLarge,
                            color = safelightFg
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = safelightPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleSafelight() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(com.fotologue.core.designsystem.R.drawable.night_sight_max_24px),
                            contentDescription = if (isSafelightActive) "Disable Red Shift" else "Enable Red Shift",
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
                        )
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paper Out-of-Stock Warning Alert
            if (!isPaperOnHand && selectedPaperMaster != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Stock Alert", tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("⚠️ Paper Out of Stock", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onErrorContainer)
                            Text("Selected paper (${selectedPaperMaster.name}) has 0 boxes on hand! Please add or receive paper stock in Lab before printing.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // 1. TOP-LEVEL ROLL FILTER DROPDOWN
            ExposedDropdownMenuBox(
                expanded = rollExpanded,
                onExpandedChange = { rollExpanded = !rollExpanded }
            ) {
                OutlinedTextField(
                    value = selectedRollText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("1. Select Developed Film Roll (Top-Level Filter)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rollExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = rollExpanded,
                    onDismissRequest = { rollExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    availableRolls.forEach { rollStr ->
                        DropdownMenuItem(
                            text = { Text(rollStr, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedRollText = rollStr
                                rollExpanded = false
                            }
                        )
                    }
                }
            }

            // 2. DETAILED FRAME LAYER FILTER DROPDOWN
            ExposedDropdownMenuBox(
                expanded = frameExpanded,
                onExpandedChange = { frameExpanded = !frameExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFramePair.second,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("2. Select Frame to Print (Detailed Layer)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frameExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = frameExpanded,
                    onDismissRequest = { frameExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    availableFramesForSelectedRoll.forEach { framePair ->
                        DropdownMenuItem(
                            text = { Text(framePair.second, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedFramePair = framePair
                                frameExpanded = false
                            }
                        )
                    }
                }
            }

            // Paper Stock Selection
            ExposedDropdownMenuBox(
                expanded = masterExpanded,
                onExpandedChange = { masterExpanded = !masterExpanded }
            ) {
                val displayPaperName = selectedPaperMaster?.let { "${it.name} (${it.manufacturer} - ${it.displaySize})" } ?: "Select Paper Stock Specification"
                OutlinedTextField(
                    value = displayPaperName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Paper Stock Specification") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = masterExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = masterExpanded,
                    onDismissRequest = { masterExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    paperMasters.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.name} (${p.manufacturer} - ${p.displaySize})", color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedMasterId = p.id
                                masterExpanded = false
                            }
                        )
                    }
                }
            }

            // Enlarger Body Selection
            ExposedDropdownMenuBox(
                expanded = enlargerBodyExpanded,
                onExpandedChange = { enlargerBodyExpanded = !enlargerBodyExpanded }
            ) {
                OutlinedTextField(
                    value = selectedEnlargerBody,
                    onValueChange = { selectedEnlargerBody = it },
                    label = { Text("Enlarger Machine / Body") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = enlargerBodyExpanded) },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = enlargerBodyExpanded,
                    onDismissRequest = { enlargerBodyExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    enlargerOptions.forEach { en ->
                        DropdownMenuItem(
                            text = { Text(en, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedEnlargerBody = en
                                enlargerBodyExpanded = false
                            }
                        )
                    }
                }
            }

            // Enlarger Lens & Print Size Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enlarger Lens Dropdown (STRICTLY FROM USER EQUIPMENT DB)
                ExposedDropdownMenuBox(
                    expanded = lensExpanded,
                    onExpandedChange = { lensExpanded = !lensExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    OutlinedTextField(
                        value = enlargerLensText,
                        onValueChange = { enlargerLensText = it },
                        label = { Text("Enlarger Lens") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lensExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = lensExpanded,
                        onDismissRequest = { lensExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        enlargerLensOptions.forEach { lensOpt ->
                            DropdownMenuItem(
                                text = { Text(lensOpt, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    enlargerLensText = lensOpt
                                    lensExpanded = false
                                }
                            )
                        }
                    }
                }

                // Print Dimensions Dropdown
                ExposedDropdownMenuBox(
                    expanded = printSizeExpanded,
                    onExpandedChange = { printSizeExpanded = !printSizeExpanded },
                    modifier = Modifier.weight(0.8f)
                ) {
                    OutlinedTextField(
                        value = printSizeText,
                        onValueChange = { printSizeText = it },
                        label = { Text("Print Size") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = printSizeExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = printSizeExpanded,
                        onDismissRequest = { printSizeExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        standardPrintDimensions.forEach { dim ->
                            DropdownMenuItem(
                                text = { Text(dim, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    printSizeText = dim
                                    printSizeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Enlarger Geometry: Height (cm) & Magnification Ratio (M)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = enlargerHeightCmText,
                    onValueChange = { enlargerHeightCmText = it },
                    label = { Text("Lens-to-Paper Distance (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = magnificationRatioText,
                    onValueChange = { magnificationRatioText = it },
                    label = { Text("Magnification (M)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // EXPOSURE STEP LEDGER
            Text(
                text = "EXPOSURE STEP LEDGER",
                style = MaterialTheme.typography.labelSmall,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            // Mode Selector: Single-Grade vs Split-Grade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = exposureModeState == ExposureMode.SINGLE_GRADE,
                    onClick = {
                        exposureModeState = ExposureMode.SINGLE_GRADE
                        if (exposureStepsList.count { it.stepType == StepType.BASE } > 1) {
                            val firstBase = exposureStepsList.firstOrNull { it.stepType == StepType.BASE }
                            exposureStepsList.removeAll { it.stepType == StepType.BASE }
                            if (firstBase != null) exposureStepsList.add(0, firstBase)
                        }
                    },
                    label = { Text("Single-Grade Mode", fontWeight = FontWeight.Bold) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = exposureModeState == ExposureMode.SPLIT_GRADE,
                    onClick = {
                        exposureModeState = ExposureMode.SPLIT_GRADE
                        if (exposureStepsList.count { it.stepType == StepType.BASE } < 2) {
                            exposureStepsList.removeAll { it.stepType == StepType.BASE }
                            exposureStepsList.add(
                                0,
                                ExposureStep(
                                    id = "step_base_soft_${System.currentTimeMillis()}",
                                    sessionId = "temp",
                                    stepType = StepType.BASE,
                                    grade = 0.0f,
                                    timeSeconds = 8.0f,
                                    region = "Soft Base (Grade 00)",
                                    tool = "Multigrade Soft Filter",
                                    sortOrder = 0
                                )
                            )
                            exposureStepsList.add(
                                1,
                                ExposureStep(
                                    id = "step_base_hard_${System.currentTimeMillis() + 1}",
                                    sessionId = "temp",
                                    stepType = StepType.BASE,
                                    grade = 5.0f,
                                    timeSeconds = 4.5f,
                                    region = "Hard Base (Grade 5.0)",
                                    tool = "Multigrade Hard Filter",
                                    sortOrder = 1
                                )
                            )
                        }
                    },
                    label = { Text("Split-Grade Mode", fontWeight = FontWeight.Bold) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Render Exposure Steps Ledger Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    exposureStepsList.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safelightPrimary
                            )

                            // Step Type Badge
                            FilterChip(
                                selected = true,
                                onClick = {},
                                label = { Text(step.stepType.name) },
                                colors = chipColors
                            )

                            // Grade Input (Mandatory when SPLIT_GRADE)
                            OutlinedTextField(
                                value = step.grade?.toString() ?: "",
                                onValueChange = { valStr ->
                                    val newGrade = valStr.toFloatOrNull()
                                    exposureStepsList[index] = step.copy(grade = newGrade)
                                },
                                label = { Text(if (exposureModeState == ExposureMode.SPLIT_GRADE) "Grade *" else "Grade") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                isError = exposureModeState == ExposureMode.SPLIT_GRADE && step.grade == null,
                                colors = textFieldColors,
                                modifier = Modifier.weight(1f)
                            )

                            // Time Seconds
                            OutlinedTextField(
                                value = if (step.timeSeconds > 0) step.timeSeconds.toString() else "",
                                onValueChange = { valStr ->
                                    val newTime = valStr.toFloatOrNull() ?: 0.0f
                                    exposureStepsList[index] = step.copy(timeSeconds = newTime)
                                },
                                label = { Text("Sec") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = textFieldColors,
                                modifier = Modifier.weight(1f)
                            )

                            // Delete Button (Protected for minimum BASE steps)
                            val canDelete = when (step.stepType) {
                                StepType.BASE -> (exposureModeState == ExposureMode.SINGLE_GRADE && exposureStepsList.count { it.stepType == StepType.BASE } > 1) ||
                                                 (exposureModeState == ExposureMode.SPLIT_GRADE && exposureStepsList.count { it.stepType == StepType.BASE } > 2)
                                else -> true
                            }

                            if (canDelete) {
                                IconButton(
                                    onClick = { exposureStepsList.removeAt(index) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Delete Step", tint = safelightPrimary)
                                }
                            }
                        }

                        // Region & Tool Input Row for Dodge/Burn
                        if (step.stepType != StepType.BASE) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = step.region ?: "",
                                    onValueChange = { reg -> exposureStepsList[index] = step.copy(region = reg) },
                                    label = { Text("Region (e.g. Sky)") },
                                    singleLine = true,
                                    colors = textFieldColors,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = step.tool ?: "",
                                    onValueChange = { tool -> exposureStepsList[index] = step.copy(tool = tool) },
                                    label = { Text("Tool (e.g. Wand)") },
                                    singleLine = true,
                                    colors = textFieldColors,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Add Dodge / Burn Step Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                exposureStepsList.add(
                                    ExposureStep(
                                        id = "step_dodge_${System.currentTimeMillis()}",
                                        sessionId = "temp",
                                        stepType = StepType.DODGE,
                                        grade = if (exposureModeState == ExposureMode.SPLIT_GRADE) 0.0f else null,
                                        timeSeconds = 2.0f,
                                        region = "Shadows",
                                        tool = "Wand",
                                        sortOrder = exposureStepsList.size
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Dodge", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                exposureStepsList.add(
                                    ExposureStep(
                                        id = "step_burn_${System.currentTimeMillis()}",
                                        sessionId = "temp",
                                        stepType = StepType.BURN,
                                        grade = if (exposureModeState == ExposureMode.SPLIT_GRADE) 5.0f else null,
                                        timeSeconds = 4.0f,
                                        region = "Sky",
                                        tool = "Hole Card",
                                        sortOrder = exposureStepsList.size
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Burn", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Light Head Type & Taking Aperture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = lightHeadExpanded,
                    onExpandedChange = { lightHeadExpanded = !lightHeadExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    OutlinedTextField(
                        value = lightHeadTypeText,
                        onValueChange = { lightHeadTypeText = it },
                        label = { Text("Light Head Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lightHeadExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = lightHeadExpanded,
                        onDismissRequest = { lightHeadExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        lightHeadOptions.forEach { headOpt ->
                            DropdownMenuItem(
                                text = { Text(headOpt, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    lightHeadTypeText = headOpt
                                    lightHeadExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = apertureExpanded,
                    onExpandedChange = { apertureExpanded = !apertureExpanded },
                    modifier = Modifier.weight(0.8f)
                ) {
                    OutlinedTextField(
                        value = takingApertureText,
                        onValueChange = { takingApertureText = it },
                        label = { Text("Aperture") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = apertureExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = apertureExpanded,
                        onDismissRequest = { apertureExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        apertureOptions.forEach { apOpt ->
                            DropdownMenuItem(
                                text = { Text(apOpt, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    takingApertureText = apOpt
                                    apertureExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Developer Chemistry & Dilution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = developerExpanded,
                    onExpandedChange = { developerExpanded = !developerExpanded },
                    modifier = Modifier.weight(1.3f)
                ) {
                    OutlinedTextField(
                        value = developerNameText,
                        onValueChange = { developerNameText = it },
                        label = { Text("Paper Developer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = developerExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = developerExpanded,
                        onDismissRequest = { developerExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        developerDropdownGroups.forEach { devOpt ->
                            val cleanVal = devOpt.removePrefix("📦 ").substringBefore(" (").removePrefix("🏠 ")
                            val isSeparator = devOpt.startsWith("──────")
                            DropdownMenuItem(
                                enabled = !isSeparator,
                                text = { Text(devOpt, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified, fontWeight = if (devOpt.startsWith("📦")) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    if (!isSeparator) {
                                        developerNameText = cleanVal
                                        developerExpanded = false
                                    }
                                }
                            )
                        }
                    }
                }

                // Dilution Controlled Dropdown + Input
                ExposedDropdownMenuBox(
                    expanded = dilutionExpanded,
                    onExpandedChange = { dilutionExpanded = !dilutionExpanded },
                    modifier = Modifier.weight(0.9f)
                ) {
                    OutlinedTextField(
                        value = dilutionText,
                        onValueChange = { dilutionText = it },
                        label = { Text("Dilution") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dilutionExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dilutionExpanded,
                        onDismissRequest = { dilutionExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        dilutionOptions.forEach { dilOpt ->
                            val cleanVal = dilOpt.substringBefore(" ")
                            DropdownMenuItem(
                                text = { Text(dilOpt, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    dilutionText = cleanVal
                                    dilutionExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Archival Evidence & Test Strip Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fixerAgeCapacityText,
                    onValueChange = { fixerAgeCapacityText = it },
                    label = { Text("Fixer Capacity / Age") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = washTimeMinutesText,
                    onValueChange = { washTimeMinutesText = it },
                    label = { Text("Wash (Mins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(0.7f)
                )
            }

            OutlinedTextField(
                value = dryDownNoteText,
                onValueChange = { dryDownNoteText = it },
                label = { Text("Dry-Down Advisory Note") },
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // Always-Visible Test Strip Diagnostic Ranges (Stacked Full-Width Rows)
            Text(
                text = "TEST STRIP DIAGNOSTIC RANGES",
                style = MaterialTheme.typography.labelSmall,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = testStripBaseRangeText,
                onValueChange = { testStripBaseRangeText = it },
                label = { Text("Base Test Strip Sequence (e.g. 2s, 4s, 8s, 16s, 32s)") },
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = testStripSoftRangeText,
                    onValueChange = { testStripSoftRangeText = it },
                    label = { Text("Soft Strip (Grade 00)") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = testStripHardRangeText,
                    onValueChange = { testStripHardRangeText = it },
                    label = { Text("Hard Strip (Grade 5.0)") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // OUTCOME & REFLECTION (KEEPER vs REJECT)
            Text(
                text = "PRINT OUTCOME & REFLECTION",
                style = MaterialTheme.typography.labelSmall,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = outcomeStatus == "KEEPER",
                    onClick = { outcomeStatus = "KEEPER" },
                    label = { Text("✓ KEEPER", fontWeight = FontWeight.Bold, color = if (outcomeStatus == "KEEPER" && isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = outcomeStatus == "REJECT",
                    onClick = { outcomeStatus = "REJECT" },
                    label = { Text("✕ REJECT", fontWeight = FontWeight.Bold, color = if (outcomeStatus == "REJECT" && isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )
            }

            if (outcomeStatus == "REJECT") {
                // CONTROLLED DARKROOM DEFECT CAUSE DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = defectExpanded,
                    onExpandedChange = { defectExpanded = !defectExpanded }
                ) {
                    OutlinedTextField(
                        value = darkroomDefectReason,
                        onValueChange = { darkroomDefectReason = it },
                        readOnly = true,
                        label = { Text("Darkroom Defect Cause") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = defectExpanded) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = defectExpanded,
                        onDismissRequest = { defectExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        darkroomDefectsLOV.forEach { defect ->
                            DropdownMenuItem(
                                text = { Text(defect, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    darkroomDefectReason = defect
                                    defectExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // MULTI-SELECT PHOTOGRAPHER LEARNING TAGS
            Text(
                text = "Photographer Learning & Reflection Tags (Multi-Select):",
                style = MaterialTheme.typography.labelMedium,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableLearningTags.forEach { tagStr ->
                    val isSel = selectedLearningTags.contains(tagStr)
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            if (isSel) {
                                selectedLearningTags.remove(tagStr)
                            } else {
                                selectedLearningTags.add(tagStr)
                            }
                        },
                        label = { Text(tagStr) },
                        colors = chipColors
                    )
                }

                FilterChip(
                    selected = false,
                    onClick = { showAddCustomTagDialog = true },
                    label = { Text("➕ Add Custom Tag", fontWeight = FontWeight.Bold) },
                    colors = chipColors
                )
            }

            if (showAddCustomTagDialog) {
                AlertDialog(
                    onDismissRequest = { showAddCustomTagDialog = false },
                    title = { Text("Add Custom Learning Tag") },
                    text = {
                        OutlinedTextField(
                            value = newCustomTagInput,
                            onValueChange = { newCustomTagInput = it },
                            label = { Text("Custom Reflection Tag Name") },
                            singleLine = true,
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val clean = newCustomTagInput.trim()
                                if (clean.isNotBlank() && !availableLearningTags.contains(clean)) {
                                    availableLearningTags.add(clean)
                                    selectedLearningTags.add(clean)
                                }
                                newCustomTagInput = ""
                                showAddCustomTagDialog = false
                            }
                        ) {
                            Text("Add & Select Tag")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showAddCustomTagDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            OutlinedTextField(
                value = outcomeNotes,
                onValueChange = { outcomeNotes = it },
                label = { Text("Outcome Notes & Reflection") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addPerformanceLog(
                        PerformanceLog(
                            logEntryId = selectedFramePair.first,
                            inventoryItemId = selectedMasterId ?: 1L,
                            outcomeStatus = outcomeStatus,
                            rejectCategory = if (outcomeStatus == "REJECT") "DARKROOM_DEFECT" else null,
                            systemErrorReason = if (outcomeStatus == "REJECT") darkroomDefectReason else null,
                            learningTag = if (selectedLearningTags.isNotEmpty()) selectedLearningTags.joinToString(", ") else null,
                            notes = outcomeNotes.trim()
                        )
                    )

                    onSuccess()
                },
                enabled = selectedRollText.isNotBlank() && isPaperOnHand,
                colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = safelightBtnFg)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Print Record & Reflection", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = safelightBtnFg, maxLines = 1)
            }
        }
    }
}
