package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.designsystem.R
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmProcessingSessionScreen(
    rollId: String? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val chemicalList by viewModel.chemicalList.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    // AUTO-TRIGGER: Turn Safelight Mode ON by default on entering Develop Roll Session!
    LaunchedEffect(Unit) {
        viewModel.setSafelightActive(true)
    }

    // Process Type Selection (B&W vs C-41 vs E-6)
    var processType by remember { mutableStateOf("BW") } // BW, C41, E6

    // Clean Chemistry Stock Lots Filter (Only active IN_USE or ON_HAND stock lots)
    val chemistryMasters = masterList.filter { it.category.equals("Chemistry", ignoreCase = true) }
    val activeChemistryLots = remember(lotList, chemistryMasters) {
        lotList.filter { (it.status == "IN_USE" || it.status == "ON_HAND") }
            .mapNotNull { lot ->
                val master = chemistryMasters.find { m -> m.id == lot.masterId }
                if (master != null) {
                    val statusBadge = if (lot.status == "IN_USE") "[In Use]" else "[On Hand]"
                    "${master.name} ($statusBadge - ${lot.location})"
                } else null
            }.distinct()
    }

    val defaultDevPresets = when (processType) {
        "C41" -> listOf("CineStill CS41 Color Developer (38.0°C)", "Kodak Flexicolor C-41 Developer", "Unicolor C-41 Blix")
        "E6" -> listOf("Arista E-6 First Developer (38.0°C)", "Kodak E-6 Color Developer", "E-6 Fogging Developer")
        else -> listOf("Kodak D-76 Developer", "Ilford ID-11 Developer", "Adox Rodinal / R09", "Ilford Ilfotec HC", "Ilford Microphen")
    }

    val developerOptions = remember(activeChemistryLots, defaultDevPresets) {
        (activeChemistryLots + defaultDevPresets).distinct()
    }

    val allCameras by viewModel.allCameras.collectAsState()
    val allFilms by viewModel.allFilms.collectAsState()
    val filmRollSessions by viewModel.allFilmRollSessions.collectAsState()

    val availableRolls = remember(logEntriesWithGear, filmRollSessions, allCameras, allFilms) {
        val rollsFromLogs = logEntriesWithGear
            .groupBy { 
                val rollRaw = it.logEntry.filmRoll ?: "Roll 1"
                val rollNum = if (rollRaw.contains(".")) rollRaw.substringBefore(".") else rollRaw
                val rollClean = if (rollNum.all { c -> c.isDigit() }) "Roll $rollNum" else rollNum
                "${it.film?.name ?: "Film Stock"} (${it.camera?.name ?: "Camera"}) • $rollClean"
            }
            .map { (groupKey, entries) ->
                val frameCount = entries.size
                "$groupKey ($frameCount frames logged)"
            }

        val rollsFromSessions = filmRollSessions.mapNotNull { session ->
            val camera = allCameras.find { it.id == session.cameraId }?.name ?: "Camera"
            val film = allFilms.find { it.id == session.filmId }?.name ?: "Film Stock"
            "$film ($camera) • Roll ${session.rollNumber}"
        }

        val combined = (rollsFromSessions + rollsFromLogs).distinct().sorted()
        if (combined.isNotEmpty()) combined else listOf("Ilford FP4 Plus 125 (Pentax LX) • Roll 1", "Kodak Tri-X 400 (Leica M6) • Roll 1")
    }

    var filmRollIdText by remember(availableRolls) { mutableStateOf(rollId ?: availableRolls.first()) }
    var selectedDeveloperName by remember(developerOptions) { mutableStateOf(developerOptions.first()) }
    var selectedStopFixerName by remember { mutableStateOf("Ilford Rapid Fixer / Stop Bath") }
    var dilutionText by remember { mutableStateOf("1+1") }
    var tempCelsiusText by remember(processType) { mutableStateOf(if (processType == "BW") "20.0" else "38.0") }
    var durationMinutesText by remember(processType) { mutableStateOf(if (processType == "BW") "9" else "3") }
    var durationSecondsText by remember(processType) { mutableStateOf(if (processType == "BW") "30" else "15") }
    var agitationSchedule by remember(processType) { mutableStateOf(if (processType == "BW") "Initial 30s continuous, 10s invert every 60s" else "Continuous agitation for first 30s, 4 inversions every 30s") }
    var pushPullStopsText by remember { mutableStateOf("0.0") }

    var rollExpanded by remember { mutableStateOf(false) }
    var chemicalExpanded by remember { mutableStateOf(false) }

    // Live Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember { mutableIntStateOf(570) } // 9:30 default
    val totalSeconds = remember(durationMinutesText, durationSecondsText) {
        val m = durationMinutesText.toIntOrNull() ?: 9
        val s = durationSecondsText.toIntOrNull() ?: 30
        m * 60 + s
    }

    LaunchedEffect(isTimerRunning, timeRemainingSeconds) {
        if (isTimerRunning && timeRemainingSeconds > 0) {
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
                    // UNAMBIGUOUS SAFELIGHT TOGGLE SWITCH
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = "Safelight",
                            style = MaterialTheme.typography.labelSmall,
                            color = safelightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isSafelightActive,
                            onCheckedChange = { viewModel.setSafelightActive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF0000),
                                checkedTrackColor = Color(0xFF660000),
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
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

            // Film Roll Identifier (Exposed Dropdown directly populated from database / imported JSON rolls!)
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
                    onDismissRequest = { rollExpanded = false }
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

            // Developer Chemistry Selector (Filtered for Active Stock Lots!)
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
                    onDismissRequest = { chemicalExpanded = false }
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
                    label = { Text(if (processType == "BW") "Temp (°C)" else "Temp (38°C Strict)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = pushPullStopsText,
                    onValueChange = { pushPullStopsText = it },
                    label = { Text("Push/Pull Stops") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Duration Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = durationMinutesText,
                    onValueChange = {
                        durationMinutesText = it
                        timeRemainingSeconds = totalSeconds
                    },
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = durationSecondsText,
                    onValueChange = {
                        durationSecondsText = it
                        timeRemainingSeconds = totalSeconds
                    },
                    label = { Text("Seconds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = agitationSchedule,
                onValueChange = { agitationSchedule = it },
                label = { Text("Agitation Schedule") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // LIVE INTERACTIVE DEVELOPMENT TIMER CARD
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
                        text = "DEVELOPMENT COUNTDOWN TIMER",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    val mins = timeRemainingSeconds / 60
                    val secs = timeRemainingSeconds % 60
                    val timerDisplay = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                    Text(
                        text = timerDisplay,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = safelightFg
                    )

                    val progress = if (totalSeconds > 0) (totalSeconds - timeRemainingSeconds).toFloat() / totalSeconds.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        color = safelightPrimary,
                        trackColor = if (isSafelightActive) Color(0xFF330000) else MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = safelightBtnFg)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTimerRunning) "Pause" else "Start Timer", color = safelightBtnFg, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = {
                            isTimerRunning = false
                            timeRemainingSeconds = totalSeconds
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Timer", tint = safelightPrimary)
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
                Text("Finish Development & Log Event", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightBtnFg)
            }
        }
    }
}
