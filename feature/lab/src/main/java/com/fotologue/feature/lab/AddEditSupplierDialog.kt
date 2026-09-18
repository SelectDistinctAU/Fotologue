package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.fotologue.core.data.lab.LabSupplier

@Composable
fun AddEditSupplierDialog(
    initialSupplier: LabSupplier? = null,
    isSafelightMode: Boolean,
    textColor: Color,
    cardColor: Color,
    onDismiss: () -> Unit,
    onSave: (LabSupplier) -> Unit
) {
    var name by remember { mutableStateOf(initialSupplier?.name ?: "") }
    var region by remember { mutableStateOf(initialSupplier?.region ?: "Australia") }
    var website by remember { mutableStateOf(initialSupplier?.website ?: "") }
    var email by remember { mutableStateOf(initialSupplier?.email ?: "") }
    var phone by remember { mutableStateOf(initialSupplier?.phone ?: "") }
    var address by remember { mutableStateOf(initialSupplier?.address ?: "") }
    var ratingStr by remember { mutableStateOf((initialSupplier?.reliabilityRating ?: 5.0f).toString()) }
    var notes by remember { mutableStateOf(initialSupplier?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = { Text(if (initialSupplier == null) "Add Supplier Contact" else "Edit Supplier Details", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Supplier Name (e.g. Rewind Photo Lab)", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("Region", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ratingStr,
                        onValueChange = { ratingStr = it },
                        label = { Text("Reliability (1-5★)", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone", color = textColor) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Vendor Notes", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val supplier = LabSupplier(
                        id = initialSupplier?.id ?: 0,
                        name = name,
                        region = region,
                        website = website,
                        email = email,
                        phone = phone,
                        address = address,
                        reliabilityRating = ratingStr.toFloatOrNull() ?: 5.0f,
                        notes = notes
                    )
                    onSave(supplier)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightMode) Color(0xFF880000) else MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Supplier", color = if (isSafelightMode) Color(0xFFFF0000) else Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
