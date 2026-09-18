package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.InventoryItem
import com.fotologue.core.data.lab.PerformanceLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPerformanceDialog(
    logEntryId: Long, // Roll.Frame ID
    inventoryItems: List<InventoryItem>,
    isSafelightMode: Boolean,
    textColor: Color,
    cardColor: Color,
    onDismiss: () -> Unit,
    onSave: (PerformanceLog) -> Unit
) {
    var selectedItemId by remember { mutableStateOf(inventoryItems.firstOrNull()?.id) }
    var outcomeStatus by remember { mutableStateOf("KEEPER") } // KEEPER vs REJECT
    var rejectCategory by remember { mutableStateOf<String?>("PHOTOGRAPHER_LEARNING") } // SYSTEM_ERROR vs PHOTOGRAPHER_LEARNING
    var systemErrorReason by remember { mutableStateOf<String?>("EXPIRED_CHEMISTRY") }
    var learningTag by remember { mutableStateOf<String?>("MISSED_MOMENT") }
    var ratingStr by remember { mutableStateOf("5.0") }
    var notes by remember { mutableStateOf("") }

    var itemExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var reasonExpanded by remember { mutableStateOf(false) }
    var learningExpanded by remember { mutableStateOf(false) }

    val outcomeStatuses = listOf("KEEPER", "REJECT")
    val rejectCategories = listOf("PHOTOGRAPHER_LEARNING", "SYSTEM_ERROR")
    val systemReasons = listOf("EXPIRED_CHEMISTRY", "PAPER_FOGGED", "LIGHT_LEAK", "HARDWARE_FAILURE")
    val learningTags = listOf("MISSED_MOMENT", "BAD_FRAMING", "UNDEREXPOSED", "OVEREXPOSED", "FOCUS_MISSED", "LOW_LIGHT_SHAKE")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = { Text("Log Frame Outcome & Reflection", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Item Selector
                ExposedDropdownMenuBox(
                    expanded = itemExpanded,
                    onExpandedChange = { itemExpanded = !itemExpanded }
                ) {
                    val selectedName = inventoryItems.find { it.id == selectedItemId }?.name ?: "Select Consumable Used"
                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Consumable Used", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = itemExpanded,
                        onDismissRequest = { itemExpanded = false }
                    ) {
                        inventoryItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.name} (${item.category})") },
                                onClick = {
                                    selectedItemId = item.id
                                    itemExpanded = false
                                }
                            )
                        }
                    }
                }

                // Outcome Status: Keeper vs Reject
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = outcomeStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Outcome", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        outcomeStatuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(if (st == "KEEPER") "🟢 Keeper" else "🔴 Reject") },
                                onClick = {
                                    outcomeStatus = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                if (outcomeStatus == "REJECT") {
                    // Reject Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (rejectCategory == "SYSTEM_ERROR") "System Error (Supply/Gear Flaw)" else "Photographer Learning (Growth Opportunity)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reject Category", color = textColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            rejectCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(if (cat == "SYSTEM_ERROR") "System Error (Hardware/Chem)" else "Photographer Learning (Skill)") },
                                    onClick = {
                                        rejectCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (rejectCategory == "SYSTEM_ERROR") {
                        ExposedDropdownMenuBox(
                            expanded = reasonExpanded,
                            onExpandedChange = { reasonExpanded = !reasonExpanded }
                        ) {
                            OutlinedTextField(
                                value = systemErrorReason ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("System Failure Reason", color = textColor) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reasonExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = reasonExpanded,
                                onDismissRequest = { reasonExpanded = false }
                            ) {
                                systemReasons.forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r) },
                                        onClick = {
                                            systemErrorReason = r
                                            reasonExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Photographer Learning Tag Dropdown
                        ExposedDropdownMenuBox(
                            expanded = learningExpanded,
                            onExpandedChange = { learningExpanded = !learningExpanded }
                        ) {
                            OutlinedTextField(
                                value = learningTag ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Learning Opportunity Tag", color = textColor) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = learningExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = learningExpanded,
                                onDismissRequest = { learningExpanded = false }
                            ) {
                                learningTags.forEach { tag ->
                                    DropdownMenuItem(
                                        text = { Text(tag) },
                                        onClick = {
                                            learningTag = tag
                                            learningExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = ratingStr,
                        onValueChange = { ratingStr = it },
                        label = { Text("Quality Rating (1.0 - 5.0★)", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reflection Notes", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val log = PerformanceLog(
                        logEntryId = logEntryId,
                        inventoryItemId = selectedItemId,
                        outcomeStatus = outcomeStatus,
                        rejectCategory = if (outcomeStatus == "REJECT") rejectCategory else null,
                        systemErrorReason = if (outcomeStatus == "REJECT" && rejectCategory == "SYSTEM_ERROR") systemErrorReason else null,
                        learningTag = if (outcomeStatus == "REJECT" && rejectCategory == "PHOTOGRAPHER_LEARNING") learningTag else null,
                        outcomeRating = ratingStr.toFloatOrNull() ?: 5.0f,
                        notes = notes
                    )
                    onSave(log)
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightMode) Color(0xFF880000) else MaterialTheme.colorScheme.primary)
            ) {
                Text("Log Outcome", color = if (isSafelightMode) Color(0xFFFF0000) else Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
