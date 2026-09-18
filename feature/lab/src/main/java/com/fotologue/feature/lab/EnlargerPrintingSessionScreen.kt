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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.PerformanceLog
import com.fotologue.core.data.lab.PrintRecipe
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

    val paperMasters = masterList.filter { it.category.equals("Paper", ignoreCase = true) }

    var rollFrameIdText by remember { mutableStateOf("Roll 03, Frame 12") }
    var selectedMasterId by remember { mutableStateOf(paperMasters.firstOrNull()?.id) }
    var enlargerLensText by remember { mutableStateOf("Rodagon 80mm f/5.6") }
    var baseExposureSecText by remember { mutableStateOf("12.5") }
    var gradeFilterText by remember { mutableStateOf("Grade 2.5") }
    var dodgeBurnNotes by remember { mutableStateOf("Dodge sky -1 EV (3s), Burn shadows +1 EV (5s)") }

    // Reflection & Outcome State
    var outcomeStatus by remember { mutableStateOf("KEEPER") } // KEEPER vs REJECT
    var rejectCategory by remember { mutableStateOf("PHOTOGRAPHER_LEARNING") } // SYSTEM_ERROR vs PHOTOGRAPHER_LEARNING
    var systemErrorReason by remember { mutableStateOf("Expired Chemistry") }
    var learningTag by remember { mutableStateOf("Underexposed") }
    var outcomeNotes by remember { mutableStateOf("Fine-art exhibition print candidate") }

    var masterExpanded by remember { mutableStateOf(false) }

    // Check paper stock on hand
    val selectedPaperMaster = paperMasters.find { it.id == selectedMasterId }
    val paperLots = lotList.filter { it.masterId == selectedMasterId }
    val paperOnHandQty = paperLots.filter { it.status == "ON_HAND" || it.status == "IN_USE" }.sumOf { it.quantity }
    val isPaperOnHand = selectedPaperMaster == null || paperOnHandQty > 0 || paperLots.isEmpty()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.table_lamp_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Enlarger Printing Session",
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

            // Roll & Frame ID
            OutlinedTextField(
                value = rollFrameIdText,
                onValueChange = { rollFrameIdText = it },
                label = { Text("Roll.Frame ID (e.g. Roll 03, Frame 12)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = masterExpanded,
                    onDismissRequest = { masterExpanded = false }
                ) {
                    paperMasters.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.name} (${p.manufacturer} - ${p.displaySize})") },
                            onClick = {
                                selectedMasterId = p.id
                                masterExpanded = false
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
                    value = enlargerLensText,
                    onValueChange = { enlargerLensText = it },
                    label = { Text("Enlarger Lens & Aperture") },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f)
                )

                OutlinedTextField(
                    value = baseExposureSecText,
                    onValueChange = { baseExposureSecText = it },
                    label = { Text("Base Sec") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(0.8f)
                )
            }

            OutlinedTextField(
                value = gradeFilterText,
                onValueChange = { gradeFilterText = it },
                label = { Text("Grade Filter (e.g. Grade 2.5, Split-Grade 00+5)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dodgeBurnNotes,
                onValueChange = { dodgeBurnNotes = it },
                label = { Text("Dodge & Burn Map Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            // OUTCOME & REFLECTION (KEEPER vs REJECT)
            Text(
                text = "PRINT OUTCOME & REFLECTION",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = outcomeStatus == "KEEPER",
                    onClick = { outcomeStatus = "KEEPER" },
                    label = { Text("🟢 KEEPER (Exhibition/Edition)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = outcomeStatus == "REJECT",
                    onClick = { outcomeStatus = "REJECT" },
                    label = { Text("🔴 REJECT (Test/Error)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.error,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            if (outcomeStatus == "REJECT") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = rejectCategory == "SYSTEM_ERROR",
                        onClick = { rejectCategory = "SYSTEM_ERROR" },
                        label = { Text("System Error") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = rejectCategory == "PHOTOGRAPHER_LEARNING",
                        onClick = { rejectCategory = "PHOTOGRAPHER_LEARNING" },
                        label = { Text("Photographer Learning") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rejectCategory == "SYSTEM_ERROR") {
                    OutlinedTextField(
                        value = systemErrorReason,
                        onValueChange = { systemErrorReason = it },
                        label = { Text("System Error Cause (e.g. Expired Chemistry, Fogged Paper)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = learningTag,
                        onValueChange = { learningTag = it },
                        label = { Text("Learning Reflection Tag (e.g. Underexposed, Focus Miss)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedTextField(
                value = outcomeNotes,
                onValueChange = { outcomeNotes = it },
                label = { Text("Outcome Notes & Reflection") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addPerformanceLog(
                        PerformanceLog(
                            logEntryId = logId ?: 1L,
                            inventoryItemId = selectedMasterId ?: 1L,
                            outcomeStatus = outcomeStatus,
                            rejectCategory = if (outcomeStatus == "REJECT") rejectCategory else null,
                            systemErrorReason = if (outcomeStatus == "REJECT" && rejectCategory == "SYSTEM_ERROR") systemErrorReason else null,
                            learningTag = if (outcomeStatus == "REJECT" && rejectCategory == "PHOTOGRAPHER_LEARNING") learningTag else null,
                            notes = outcomeNotes.trim()
                        )
                    )

                    onSuccess()
                },
                enabled = rollFrameIdText.isNotBlank() && isPaperOnHand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Print Record & Reflection", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
