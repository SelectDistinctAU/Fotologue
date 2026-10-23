package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabOrder
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommercialLabOrderScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val suppliers by viewModel.supplierList.collectAsState()
    val filmRollSessions by viewModel.allFilmRollSessions.collectAsState()
    val allCameras by viewModel.allCameras.collectAsState()
    val allFilms by viewModel.allFilms.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    val labSuppliers = suppliers.filter { it.isUsed }
    val availableRolls = remember(logEntriesWithGear, filmRollSessions, allCameras, allFilms) {
        val rollFrameCounts = mutableMapOf<String, Int>()
        filmRollSessions.forEach { session ->
            val camera = allCameras.find { it.id == session.cameraId }?.name ?: "Camera"
            val film = allFilms.find { it.id == session.filmId }?.name ?: "Film Stock"
            val key = "$camera • $film • Roll ${session.rollNumber}"
            rollFrameCounts[key] = rollFrameCounts.getOrDefault(key, 0)
        }
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
        if (formatted.isNotEmpty()) formatted else listOf("Pentax LX • Ilford FP4 Plus 125 • Roll 1", "Leica M6 • Kodak Tri-X 400 • Roll 1")
    }

    var selectedSupplierId by remember { mutableStateOf(labSuppliers.firstOrNull()?.id ?: 1L) }
    var selectedRollText by remember(availableRolls) { mutableStateOf(availableRolls.first()) }
    var serviceType by remember { mutableStateOf("DEV_AND_SCAN") } // DEV_ONLY, DEV_AND_SCAN, PRINT_ONLY, DEV_SCAN_PRINT
    var pushPullInstruction by remember { mutableStateOf("BOX_SPEED") } // BOX_SPEED, PUSH_1, PUSH_2, PULL_1
    var trackingNumber by remember { mutableStateOf("") }
    var costAmount by remember { mutableStateOf("") }
    var labNotes by remember { mutableStateOf("High-resolution TIFF scans requested") }

    var supplierExpanded by remember { mutableStateOf(false) }
    var rollExpanded by remember { mutableStateOf(false) }

    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant

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
                            painter = painterResource(R.drawable.delivery_truck_speed_24px),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Commercial Lab Order", style = MaterialTheme.typography.titleLarge, color = safelightFg)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back", tint = safelightPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSafelight() }, modifier = Modifier.size(48.dp)) {
                        Icon(painterResource(R.drawable.night_sight_max_24px), contentDescription = "Toggle Red Shift", tint = safelightPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safelightCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚚 Commercial Lab Drop-Off & Order Tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = safelightFg)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Submit film rolls directly to professional commercial labs (Rewind, Bellerive Photo, Hillvale) bypassing home chemistry processing while maintaining full custody tracking.", style = MaterialTheme.typography.bodySmall, color = safelightFg)
                }
            }

            // 1. SELECT COMMERCIAL LAB SUPPLIER
            ExposedDropdownMenuBox(
                expanded = supplierExpanded,
                onExpandedChange = { supplierExpanded = !supplierExpanded }
            ) {
                val selectedSupplier = labSuppliers.find { it.id == selectedSupplierId }
                val displaySupplier = selectedSupplier?.let { "${it.name} (${it.region})" } ?: "Select Commercial Lab"
                OutlinedTextField(
                    value = displaySupplier,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("1. Select Commercial Processing Lab") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = supplierExpanded,
                    onDismissRequest = { supplierExpanded = false }
                ) {
                    labSuppliers.forEach { sup ->
                        DropdownMenuItem(
                            text = { Text("${sup.name} (${sup.region})") },
                            onClick = {
                                selectedSupplierId = sup.id
                                supplierExpanded = false
                            }
                        )
                    }
                }
            }

            // 2. SELECT FILM ROLL
            ExposedDropdownMenuBox(
                expanded = rollExpanded,
                onExpandedChange = { rollExpanded = !rollExpanded }
            ) {
                OutlinedTextField(
                    value = selectedRollText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("2. Select Exposed Film Roll") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rollExpanded) },
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
                                selectedRollText = rollStr
                                rollExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. SERVICE TYPE SELECTION
            Text("3. Select Lab Service Requested:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = safelightPrimary)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = serviceType == "DEV_AND_SCAN", onClick = { serviceType = "DEV_AND_SCAN" }, label = { Text("Develop + Scan") })
                FilterChip(selected = serviceType == "DEV_ONLY", onClick = { serviceType = "DEV_ONLY" }, label = { Text("Develop Only") })
                FilterChip(selected = serviceType == "PRINT_ONLY", onClick = { serviceType = "PRINT_ONLY" }, label = { Text("Darkroom Print Only") })
                FilterChip(selected = serviceType == "DEV_SCAN_PRINT", onClick = { serviceType = "DEV_SCAN_PRINT" }, label = { Text("Dev + Scan + Print") })
            }

            // 4. PUSH / PULL ISO INSTRUCTIONS
            Text("4. Push / Pull Processing Instruction:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = safelightPrimary)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = pushPullInstruction == "BOX_SPEED", onClick = { pushPullInstruction = "BOX_SPEED" }, label = { Text("Box Speed (EI)") })
                FilterChip(selected = pushPullInstruction == "PUSH_1", onClick = { pushPullInstruction = "PUSH_1" }, label = { Text("Push +1 Stop") })
                FilterChip(selected = pushPullInstruction == "PUSH_2", onClick = { pushPullInstruction = "PUSH_2" }, label = { Text("Push +2 Stops") })
                FilterChip(selected = pushPullInstruction == "PULL_1", onClick = { pushPullInstruction = "PULL_1" }, label = { Text("Pull -1 Stop") })
            }

            // TRACKING NUMBER & COST
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = trackingNumber,
                    onValueChange = { trackingNumber = it },
                    label = { Text("Tracking / Order #") },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f)
                )

                OutlinedTextField(
                    value = costAmount,
                    onValueChange = { costAmount = it },
                    label = { Text("Cost ($)") },
                    singleLine = true,
                    modifier = Modifier.weight(0.8f)
                )
            }

            OutlinedTextField(
                value = labNotes,
                onValueChange = { labNotes = it },
                label = { Text("Special Lab Instructions & Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addLabOrder(
                        LabOrder(
                            supplierId = selectedSupplierId,
                            filmRollId = selectedRollText,
                            serviceType = serviceType,
                            pushPullInstruction = pushPullInstruction,
                            orderStatus = "IN_PROCESSING",
                            trackingNumber = trackingNumber,
                            costAmount = costAmount,
                            labNotes = labNotes.trim()
                        )
                    )
                    onSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Order to Commercial Lab", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
