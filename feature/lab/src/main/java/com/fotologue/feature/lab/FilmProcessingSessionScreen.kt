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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabProcessEvent
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
    val chemicalList by viewModel.chemicalList.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()

    val availableRolls = remember(logEntriesWithGear) {
        val derived = logEntriesWithGear.mapNotNull { it.logEntry.filmRoll }.filter { it.isNotBlank() }.distinct().map { rollName ->
            val matchingEntry = logEntriesWithGear.find { it.logEntry.filmRoll == rollName }
            val filmName = matchingEntry?.film?.name ?: "Film Stock"
            val cameraName = matchingEntry?.camera?.name ?: "Camera"
            "$rollName • $filmName ($cameraName)"
        }
        if (derived.isNotEmpty()) derived else listOf("Roll 01 • Kodak Tri-X 400 (Leica M6)", "Roll 02 • Ilford HP5+ (Hasselblad)", "Roll 03 • CineStill 800T")
    }

    var filmRollIdText by remember(availableRolls) { mutableStateOf(rollId ?: availableRolls.first()) }
    var selectedChemicalId by remember { mutableStateOf(chemicalList.firstOrNull()?.id) }
    var dilutionText by remember { mutableStateOf("1+1") }
    var tempCelsiusText by remember { mutableStateOf("20.0") }
    var durationMinutesText by remember { mutableStateOf("9") }
    var durationSecondsText by remember { mutableStateOf("30") }
    var agitationSchedule by remember { mutableStateOf("Initial 30s continuous, 10s invert every 60s") }
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

    val selectedChem = chemicalList.find { it.id == selectedChemicalId }
    val isChemStockOnHand = selectedChem == null || (!selectedChem.isExhausted && selectedChem.quantityOnHand > 0 && selectedChem.capacityRollsUsed < selectedChem.capacityRollsTotal)

    LaunchedEffect(isTimerRunning, timeRemainingSeconds) {
        if (isTimerRunning && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds -= 1
        } else if (timeRemainingSeconds == 0) {
            isTimerRunning = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.experiment_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Film Processing Session",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
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
            // Out-of-Stock Warning Alert Card
            if (!isChemStockOnHand && selectedChem != null) {
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
                            Text("⚠️ Developer Out of Stock / Capacity Exhausted", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onErrorContainer)
                            Text("Selected developer (${selectedChem.name}) is exhausted or has 0 rolls capacity remaining. Please receive or open new developer stock in Lab.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // Film Roll Identifier (Exposed Editable Pick List Dropdown directly populated from database / imported JSON rolls!)
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = rollExpanded,
                    onDismissRequest = { rollExpanded = false }
                ) {
                    availableRolls.forEach { rollStr ->
                        DropdownMenuItem(
                            text = { Text(rollStr) },
                            onClick = {
                                filmRollIdText = rollStr
                                rollExpanded = false
                            }
                        )
                    }
                }
            }

            // Developer Chemistry Selector
            ExposedDropdownMenuBox(
                expanded = chemicalExpanded,
                onExpandedChange = { chemicalExpanded = !chemicalExpanded }
            ) {
                val displayChemName = selectedChem?.let { "${it.name} (${it.type} - ${it.dilution})" } ?: "Select Developer Chemistry"
                OutlinedTextField(
                    value = displayChemName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Developer Chemistry") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chemicalExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = chemicalExpanded,
                    onDismissRequest = { chemicalExpanded = false }
                ) {
                    chemicalList.forEach { chem ->
                        DropdownMenuItem(
                            text = { Text("${chem.name} (${chem.type} - ${chem.capacityRollsUsed}/${chem.capacityRollsTotal} rolls used)") },
                            onClick = {
                                selectedChemicalId = chem.id
                                dilutionText = chem.dilution
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
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = tempCelsiusText,
                    onValueChange = { tempCelsiusText = it },
                    label = { Text("Temp (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = pushPullStopsText,
                    onValueChange = { pushPullStopsText = it },
                    label = { Text("Push/Pull Stops") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
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
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = agitationSchedule,
                onValueChange = { agitationSchedule = it },
                label = { Text("Agitation Schedule") },
                modifier = Modifier.fillMaxWidth()
            )

            // LIVE INTERACTIVE DEVELOPMENT TIMER CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    val mins = timeRemainingSeconds / 60
                    val secs = timeRemainingSeconds % 60
                    val timerDisplay = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                    Text(
                        text = timerDisplay,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (timeRemainingSeconds < 30 && timeRemainingSeconds > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )

                    val progress = if (totalSeconds > 0) (totalSeconds - timeRemainingSeconds).toFloat() / totalSeconds.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            enabled = isChemStockOnHand
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTimerRunning) "Pause" else "Start Timer")
                        }

                        IconButton(onClick = {
                            isTimerRunning = false
                            timeRemainingSeconds = totalSeconds
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Timer")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Completion Button
            Button(
                onClick = {
                    viewModel.addCustomChemical(
                        name = selectedChem?.name ?: "Developer",
                        type = "Film Developer",
                        dilution = dilutionText,
                        capacityRollsTotal = 12
                    )

                    // Increment capacity used on the chemical lot if selected
                    selectedChemicalId?.let { chemId ->
                        chemicalList.find { it.id == chemId }?.let { chem ->
                            viewModel.updateChemical(chem.copy(capacityRollsUsed = chem.capacityRollsUsed + 1))
                        }
                    }

                    onSuccess()
                },
                enabled = filmRollIdText.isNotBlank() && isChemStockOnHand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finish Development & Log Event", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
