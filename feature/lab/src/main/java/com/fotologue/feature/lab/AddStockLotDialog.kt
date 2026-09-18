package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.StockLot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockLotDialog(
    masters: List<InventoryMaster>,
    suppliers: List<LabSupplier>,
    isSafelightMode: Boolean = false,
    textColor: Color,
    cardColor: Color,
    onDismiss: () -> Unit,
    onSave: (StockLot) -> Unit
) {
    var selectedMasterId by remember { mutableStateOf(masters.firstOrNull()?.id) }
    var selectedSupplierId by remember { mutableStateOf(suppliers.firstOrNull()?.id) }
    var batchNumber by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("1") }
    var status by remember { mutableStateOf("ON_HAND") } // ON_HAND vs IN_TRANSIT
    var notes by remember { mutableStateOf("") }

    var masterExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statuses = listOf("ON_HAND", "IN_TRANSIT")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = { Text("Add Physical Stock to Inventory", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Item Master Dropdown
                ExposedDropdownMenuBox(
                    expanded = masterExpanded,
                    onExpandedChange = { masterExpanded = !masterExpanded }
                ) {
                    val selectedMaster = masters.find { it.id == selectedMasterId }
                    val displayMasterName = if (selectedMaster != null) "${selectedMaster.name} (${selectedMaster.manufacturer})" else "Select Item Master"
                    OutlinedTextField(
                        value = displayMasterName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Item Master Specification", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = masterExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = masterExpanded,
                        onDismissRequest = { masterExpanded = false }
                    ) {
                        masters.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.name} (${m.manufacturer} - ${m.containerSize})") },
                                onClick = {
                                    selectedMasterId = m.id
                                    masterExpanded = false
                                }
                            )
                        }
                    }
                }

                // Supplier Dropdown
                ExposedDropdownMenuBox(
                    expanded = supplierExpanded,
                    onExpandedChange = { supplierExpanded = !supplierExpanded }
                ) {
                    val selectedSupplier = suppliers.find { it.id == selectedSupplierId }
                    val displaySupplierName = selectedSupplier?.name ?: "Select Supplier Contact"
                    OutlinedTextField(
                        value = displaySupplierName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Supplier Contact", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = supplierExpanded,
                        onDismissRequest = { supplierExpanded = false }
                    ) {
                        suppliers.forEach { sup ->
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = batchNumber,
                        onValueChange = { batchNumber = it },
                        label = { Text("Batch / Lot Number", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Purchase Cost", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity Purchased", color = textColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Initial Status Dropdown
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = if (status == "ON_HAND") "🟢 On Hand" else "🚚 In Transit",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status", color = textColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            statuses.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(if (st == "ON_HAND") "🟢 On Hand (Received)" else "🚚 In Transit (Ordered)") },
                                    onClick = {
                                        status = st
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Stock Notes", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedMasterId?.let { masterId ->
                        val lot = StockLot(
                            masterId = masterId,
                            supplierId = selectedSupplierId,
                            batchNumber = batchNumber,
                            cost = cost,
                            quantity = quantityStr.toIntOrNull() ?: 1,
                            status = status,
                            notes = notes
                        )
                        onSave(lot)
                    }
                },
                enabled = selectedMasterId != null,
                colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightMode) Color(0xFF880000) else MaterialTheme.colorScheme.primary)
            ) {
                Text("Add to Stock", color = if (isSafelightMode) Color(0xFFFF0000) else Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
