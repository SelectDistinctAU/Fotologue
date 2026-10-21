package com.fotologue.feature.lab

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.designsystem.R
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun FilmProcessingSessionScreen(
    rollId: String? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()
    val allCameras by viewModel.allCameras.collectAsState()
    val allFilms by viewModel.allFilms.collectAsState()
    val filmRollSessions by viewModel.allFilmRollSessions.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    // AUTO-TRIGGER: Turn Safelight Mode ON by default on entering Develop Roll Session!
    LaunchedEffect(Unit) {
        viewModel.setSafelightActive(true)
    }

    // Process Type Selection (B&W vs C-41 vs E-6)
    var processType by remember { mutableStateOf("BW") } // BW, C41, E6
    var enableHapticVibration by remember { mutableStateOf(true) }

    val vibrator = remember(context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    // Clean Chemistry Stock Lots Filter (Only active IN_USE or ON_HAND stock lots)
    val chemistryMasters = masterList.filter { it.category.equals("Chemistry", ignoreCase = true) }
    val activeChemistryLots = remember(lotList, chemistryMasters) {
        lotList.filter { (it.status == "IN_USE" || it.status == "ON_HAND") }
            .mapNotNull { lot ->
                val master = chemistryMasters.find { m -> m.id == lot.masterId }
                if (master != null) {
                    val statusBadge = if (lot.status == "IN_USE") "[In Use]" else "[On Hand]"
                    Pair(master.name, "${master.name} ($statusBadge - ${lot.location})")
                } else null
            }.distinctBy { it.second }
    }

    // 1. STRICT FILM DEVELOPERS FILTER
    val defaultDevPresets = when (processType) {
        "C41" -> listOf("CineStill CS41 Color Developer (38.0°C)", "Kodak Flexicolor C-41 Developer")
        "E6" -> listOf("Arista E-6 First Developer (38.0°C)", "Kodak E-6 Color Developer")
        else -> listOf("Kodak D-76 Developer", "Ilford ID-11 Developer", "Adox Rodinal / R09", "Ilford Ilfotec HC", "Ilford Microphen", "Kodak Xtol Developer", "Ilford Perceptol")
    }

    val developerOptions = remember(activeChemistryLots, defaultDevPresets) {
        val filteredActive = activeChemistryLots.filter { (name, _) ->
            name.contains("Developer", ignoreCase = true) || name.contains("Dev", ignoreCase = true) ||
            name.contains("D-76", ignoreCase = true) || name.contains("ID-11", ignoreCase = true) ||
            name.contains("Rodinal", ignoreCase = true) || name.contains("HC-110", ignoreCase = true) ||
            name.contains("Ilfotec", ignoreCase = true) || name.contains("CS41", ignoreCase = true)
        }.map { it.second }
        (filteredActive + defaultDevPresets).distinct()
    }

    // 2. STRICT STOP BATH FILTER
    val defaultStopPresets = listOf("Ilford Indicator Stop Bath", "Kodak Indicator Stop Bath", "Adox Adostop Odourless Stop", "Water Rinse Bath")
    val stopBathOptions = remember(activeChemistryLots, defaultStopPresets) {
        val filteredActive = activeChemistryLots.filter { (name, _) ->
            name.contains("Stop", ignoreCase = true) || name.contains("Rinse", ignoreCase = true) || name.contains("Water", ignoreCase = true)
        }.map { it.second }
        (filteredActive + defaultStopPresets).distinct()
    }

    // 3. STRICT FIXING CHEMISTRY FILTER (Regular Hypo vs Rapid Fixers vs Blix)
    val defaultFixingPresets = if (processType == "BW") {
        listOf(
            "Ilford Rapid Fixer (Ammonium Thiosulfate - Liquid)",
            "Kodak Rapid Fixer (Ammonium Thiosulfate - Liquid)",
            "Kodak Powdered Fixer (Sodium Thiosulfate / Hypo)",
            "Adox Adofix Plus Rapid Fixer",
            "TF-4 / TF-5 Archival Alkaline Fixer"
        )
    } else {
        listOf("CineStill CS41 Color Blix", "Unicolor C-41 Monobath Blix", "E-6 Fixer")
    }

    val fixingOptions = remember(activeChemistryLots, defaultFixingPresets) {
        val filteredActive = activeChemistryLots.filter { (name, _) ->
            name.contains("Fixer", ignoreCase = true) || name.contains("Fix", ignoreCase = true) ||
            name.contains("Hypo", ignoreCase = true) || name.contains("Blix", ignoreCase = true) ||
            name.contains("Bleach", ignoreCase = true)
        }.map { it.second }
        (filteredActive + defaultFixingPresets).distinct()
    }

    val availableRolls = remember(logEntriesWithGear, filmRollSessions, allCameras, allFilms) {
        val rollFrameCounts = mutableMapOf<String, Int>()

        // 1. Map active film roll sessions
        filmRollSessions.forEach { session ->
            val camera = allCameras.find { it.id == session.cameraId }?.name ?: "Camera"
            val film = allFilms.find { it.id == session.filmId }?.name ?: "Film Stock"
            val key = "$film ($camera) • Roll ${session.rollNumber}"
            rollFrameCounts[key] = rollFrameCounts.getOrDefault(key, 0)
        }

        // 2. Count logged exposure frames per roll
        logEntriesWithGear.forEach { entry ->
            val camera = entry.camera?.name ?: "Camera"
            val film = entry.film?.name ?: "Film Stock"
            val rollRaw = entry.logEntry.filmRoll ?: "Roll 1"
            val rollNum = if (rollRaw.contains(".")) rollRaw.substringBefore(".") else rollRaw
            val rollClean = if (rollNum.all { c -> c.isDigit() }) "Roll $rollNum" else rollNum
            val key = "$film ($camera) • $rollClean"
            rollFrameCounts[key] = (rollFrameCounts[key] ?: 0) + 1
        }

        val formatted = rollFrameCounts.map { (key, count) ->
            if (count > 0) "$key ($count frames logged)" else key
        }.distinct().sorted()

        if (formatted.isNotEmpty()) formatted else listOf("Ilford FP4 Plus 125 (Pentax LX) • Roll 1", "Kodak Tri-X 400 (Leica M6) • Roll 1")
    }

    var filmRollIdText by remember(availableRolls) { mutableStateOf(availableRolls.first()) }
    var selectedDeveloperName by remember(developerOptions) { mutableStateOf(developerOptions.first()) }
    var selectedStopBathName by remember(stopBathOptions) { mutableStateOf(stopBathOptions.first()) }
    var selectedFixerName by remember(fixingOptions) { mutableStateOf(fixingOptions.first()) }

    var dilutionText by remember { mutableStateOf("1+1") }
    var tempCelsiusText by remember(processType) { mutableStateOf(if (processType == "BW") "20.0" else "38.0") }
    
    // Auto-Recommend Development Time based on Selected Film & Developer!
    val recommendedTime = remember(filmRollIdText, selectedDeveloperName, processType) {
        when {
            processType == "C41" -> Pair(3, 15) // 3m 15s C-41 Standard
            processType == "E6" -> Pair(6, 0)   // 6m 00s E-6 Standard
            filmRollIdText.contains("HP5", ignoreCase = true) && selectedDeveloperName.contains("ID-11", ignoreCase = true) -> Pair(11, 0)
            filmRollIdText.contains("Tri-X", ignoreCase = true) && selectedDeveloperName.contains("D-76", ignoreCase = true) -> Pair(9, 45)
            filmRollIdText.contains("FP4", ignoreCase = true) -> Pair(7, 30)
            filmRollIdText.contains("Pan F", ignoreCase = true) -> Pair(11, 0)
            else -> Pair(9, 30)
        }
    }

    var durationMinutesText by remember(recommendedTime) { mutableStateOf(recommendedTime.first.toString()) }
    var durationSecondsText by remember(recommendedTime) { mutableStateOf(recommendedTime.second.toString()) }
    var pushPullStopsText by remember { mutableStateOf("0.0") }

    var rollExpanded by remember { mutableStateOf(false) }
    var chemicalExpanded by remember { mutableStateOf(false) }
    var stopExpanded by remember { mutableStateOf(false) }
    var fixerExpanded by remember { mutableStateOf(false) }

    // Multi-Stage Processing Wizard State (0 = Developer, 1 = Stop Bath, 2 = Fixer, 3 = Wetting/Wash)
    var currentStage by remember { mutableIntStateOf(0) }

    val currentStageTitle = when (currentStage) {
        0 -> "STEP 1: DEVELOPER (${durationMinutesText}m ${durationSecondsText}s)"
        1 -> "STEP 2: STOP BATH / RINSE (1m 00s)"
        2 -> "STEP 3: FIXING CHEMISTRY (5m 00s)"
        else -> "STEP 4: WETTING AGENT / WASH (1m 00s)"
    }

    val stageDurationTotalSeconds = remember(currentStage, durationMinutesText, durationSecondsText) {
        when (currentStage) {
            0 -> (durationMinutesText.toIntOrNull() ?: 9) * 60 + (durationSecondsText.toIntOrNull() ?: 30)
            1 -> 60  // 1 min Stop Bath
            2 -> 300 // 5 min Fixer
            else -> 60 // 1 min Wetting Agent / Wash
        }
    }

    // Live Stage Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember(stageDurationTotalSeconds, currentStage) { mutableIntStateOf(stageDurationTotalSeconds) }

    // LIVE AGITATION COUNTDOWN CALCULATOR
    val elapsedSeconds = stageDurationTotalSeconds - timeRemainingSeconds
    val isInitialAgitation = elapsedSeconds < 30
    val secondsInCurrentMinute = elapsedSeconds % 60
    val isAgitatingNow = isInitialAgitation || (secondsInCurrentMinute < 10)
    val agitationCountdownSeconds = if (isInitialAgitation) (30 - elapsedSeconds) else if (isAgitatingNow) (10 - secondsInCurrentMinute) else (60 - secondsInCurrentMinute)

    LaunchedEffect(isTimerRunning, timeRemainingSeconds) {
        if (isTimerRunning && timeRemainingSeconds > 0) {
            // Haptic Agitation Boundary Vibration (Vibrates 2s before boundary start/end!)
            if (enableHapticVibration && (currentStage == 0 || currentStage == 2)) {
                if (agitationCountdownSeconds == 2 || agitationCountdownSeconds == 1) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(200L, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(200L)
                        }
                    } catch (_: Exception) {
                        // Handled
                    }
                }
            }

            delay(1000L)
            timeRemainingSeconds -= 1
        } else if (timeRemainingSeconds == 0) {
            isTimerRunning = false
        }
    }

    // PURE DARKROOM SAFELIGHT RED COLORS (ZERO WHITE LIGHT!)
    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
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
                            painter = painterResource(R.drawable.theaters_24px),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Film Processing",
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
            // PROCESS TYPE SELECTOR (B&W vs C-41 vs E-6)
            Text(
                text = "DEVELOPMENT PROCESS TYPE",
                style = MaterialTheme.typography.labelSmall,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = processType == "BW",
                    onClick = { processType = "BW" },
                    label = { Text("B&W", color = if (processType == "BW" && isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = processType == "C41",
                    onClick = { processType = "C41" },
                    label = { Text("C-41 Color", color = if (processType == "C41" && isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = processType == "E6",
                    onClick = { processType = "E6" },
                    label = { Text("E-6 Slide", color = if (processType == "E6" && isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                    colors = chipColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Film Roll Identifier
            ExposedDropdownMenuBox(
                expanded = rollExpanded,
                onExpandedChange = { rollExpanded = !rollExpanded }
            ) {
                OutlinedTextField(
                    value = filmRollIdText,
                    onValueChange = { filmRollIdText = it },
                    label = { Text("Imported Film Roll / Session Identifier") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rollExpanded) },
                    singleLine = true,
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
                                filmRollIdText = rollStr
                                rollExpanded = false
                            }
                        )
                    }
                }
            }

            // FULL CHEMISTRY SEQUENCE PICKERS (Developer -> Stop Bath -> Fixing)
            Text(
                text = "FULL CHEMISTRY SEQUENCE WIZARD",
                style = MaterialTheme.typography.labelSmall,
                color = safelightPrimary,
                fontWeight = FontWeight.Bold
            )

            // 1. Developer Chemistry Selector (STRICTLY FILM DEVELOPERS ONLY!)
            ExposedDropdownMenuBox(
                expanded = chemicalExpanded,
                onExpandedChange = { chemicalExpanded = !chemicalExpanded }
            ) {
                OutlinedTextField(
                    value = selectedDeveloperName,
                    onValueChange = { selectedDeveloperName = it },
                    readOnly = false,
                    label = { Text(if (processType == "BW") "1. Developer Chemistry" else "1. Color Developer (38°C)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chemicalExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = chemicalExpanded,
                    onDismissRequest = { chemicalExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    developerOptions.forEach { devStr ->
                        DropdownMenuItem(
                            text = { Text(devStr, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedDeveloperName = devStr
                                chemicalExpanded = false
                            }
                        )
                    }
                }
            }

            // 2. Stop Bath Selector (STRICTLY STOP BATHS ONLY!)
            ExposedDropdownMenuBox(
                expanded = stopExpanded,
                onExpandedChange = { stopExpanded = !stopExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStopBathName,
                    onValueChange = { selectedStopBathName = it },
                    label = { Text(if (processType == "BW") "2. Stop Bath / Water Rinse" else "2. Bleach / Blix") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stopExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = stopExpanded,
                    onDismissRequest = { stopExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    stopBathOptions.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedStopBathName = st
                                stopExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. Fixing Chemistry Selector (STRICTLY FIXING CHEMICALS - HYPO VS RAPID FIXERS!)
            ExposedDropdownMenuBox(
                expanded = fixerExpanded,
                onExpandedChange = { fixerExpanded = !fixerExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFixerName,
                    onValueChange = { selectedFixerName = it },
                    label = { Text("3. Fixing Chemistry") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fixerExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = fixerExpanded,
                    onDismissRequest = { fixerExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    fixingOptions.forEach { fx ->
                        DropdownMenuItem(
                            text = { Text(fx, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                selectedFixerName = fx
                                fixerExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dilutionText,
                    onValueChange = { dilutionText = it },
                    label = { Text("Dilution") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = tempCelsiusText,
                    onValueChange = { tempCelsiusText = it },
                    label = { Text("Temp (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = pushPullStopsText,
                    onValueChange = { pushPullStopsText = it },
                    label = { Text("Push/Pull") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Duration Inputs (Recommended Auto-Timing Display!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = durationMinutesText,
                    onValueChange = { durationMinutesText = it },
                    label = { Text("Dev Mins (Auto)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = durationSecondsText,
                    onValueChange = { durationSecondsText = it },
                    label = { Text("Dev Secs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // GUIDED STEP TIMER & LIVE AGITATION COUNTDOWN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safelightCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = currentStageTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = safelightPrimary
                    )

                    val mins = timeRemainingSeconds / 60
                    val secs = timeRemainingSeconds % 60
                    val timerDisplay = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                    Text(
                        text = timerDisplay,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = safelightFg
                    )

                    // LIVE AGITATION COUNTDOWN BANNER!
                    if (isTimerRunning && (currentStage == 0 || currentStage == 2)) {
                        val agitationLabel = if (isAgitatingNow) "🔔 AGITATE NOW (Invert Tank): ${agitationCountdownSeconds}s" else "⏸️ REST TANK: Next agitation in ${agitationCountdownSeconds}s"
                        Text(
                            text = agitationLabel,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isAgitatingNow) Color(0xFFFF0000) else safelightFg
                        )
                    }

                    // Haptic Vibration Cue Toggle Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Checkbox(
                            checked = enableHapticVibration,
                            onCheckedChange = { enableHapticVibration = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Haptic Vibration Cues (2s before agitation boundary)",
                            style = MaterialTheme.typography.labelSmall,
                            color = safelightFg
                        )
                    }

                    val progress = if (stageDurationTotalSeconds > 0) (stageDurationTotalSeconds - timeRemainingSeconds).toFloat() / stageDurationTotalSeconds.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        color = safelightPrimary,
                        trackColor = if (isSafelightActive) Color(0xFF330000) else MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { isTimerRunning = !isTimerRunning },
                                colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = safelightBtnFg)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isTimerRunning) "Pause" else "Start", color = safelightBtnFg, fontWeight = FontWeight.Bold)
                            }

                            IconButton(onClick = {
                                isTimerRunning = false
                                timeRemainingSeconds = stageDurationTotalSeconds
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset Stage", tint = safelightPrimary)
                            }
                        }

                        if (currentStage < 3) {
                            Button(
                                onClick = {
                                    isTimerRunning = false
                                    currentStage += 1
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg)
                            ) {
                                Text("Next Step", color = safelightBtnFg, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = safelightBtnFg)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Completion Button
            Button(
                onClick = {
                    viewModel.addCustomChemical(
                        name = selectedDeveloperName,
                        type = if (processType == "BW") "B&W Developer" else "Color Developer",
                        dilution = dilutionText,
                        capacityRollsTotal = 12
                    )

                    viewModel.addProcessEvent(
                        LabProcessEvent(
                            filmRollId = filmRollIdText,
                            developerName = selectedDeveloperName,
                            temperatureCelsius = tempCelsiusText.toFloatOrNull() ?: 20.0f,
                            durationSeconds = stageDurationTotalSeconds,
                            agitationSchedule = "Initial 30s continuous, 10s invert every 60s",
                            pushPullStops = pushPullStopsText.toFloatOrNull() ?: 0.0f
                        )
                    )

                    onSuccess()
                },
                enabled = filmRollIdText.isNotBlank() && selectedDeveloperName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = safelightBtnFg)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finish Session & Log Development", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightBtnFg)
            }
        }
    }
}
