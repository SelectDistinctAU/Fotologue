package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fotologue.core.data.lab.LabStorageLocation

@Composable
fun LovMaintenanceDialog(
    locations: List<LabStorageLocation>,
    textColor: Color,
    cardColor: Color,
    onDismiss: () -> Unit,
    onAddLocation: (String) -> Unit,
    onUpdateLocation: (LabStorageLocation) -> Unit,
    onDeleteLocation: (LabStorageLocation) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Locations, 1 = Manufacturers, 2 = Categories
    var newLocationName by remember { mutableStateOf("") }
    var editingLocationId by remember { mutableStateOf<Long?>(null) }
    var editingLocationText by remember { mutableStateOf("") }

    // Dynamic Lists for Manufacturers and Categories
    val manufacturers = remember { mutableStateListOf("Ilford", "Kodak", "Ansell", "Adox", "Foma", "CineStill", "Paterson", "AP") }
    var newMfgName by remember { mutableStateOf("") }

    val categories = remember { mutableStateListOf("Chemistry", "Paper", "Safety Gear", "Supplies") }
    var newCatName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = {
            Text(
                text = "List of Values (LOV) Maintenance",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = cardColor,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Locations", style = MaterialTheme.typography.labelMedium) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Manufacturers", style = MaterialTheme.typography.labelMedium) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Categories", style = MaterialTheme.typography.labelMedium) })
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // Storage Locations Tab (Full CRUD: Add, Edit, Delete)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newLocationName,
                            onValueChange = { newLocationName = it },
                            label = { Text("Add New Storage Location", color = textColor) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newLocationName.isNotBlank()) {
                                    onAddLocation(newLocationName.trim())
                                    newLocationName = ""
                                }
                            },
                            enabled = newLocationName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Location", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        if (locations.isEmpty()) {
                            item {
                                Text("No locations defined. Enter a location above to add.", style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.7f))
                            }
                        } else {
                            items(locations, key = { "loc_${it.id}_${it.name}" }) { loc ->
                                if (editingLocationId == loc.id) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = editingLocationText,
                                            onValueChange = { editingLocationText = it },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            if (editingLocationText.isNotBlank()) {
                                                onUpdateLocation(loc.copy(name = editingLocationText.trim()))
                                                editingLocationId = null
                                            }
                                        }) {
                                            Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(loc.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textColor)
                                        Row {
                                            IconButton(onClick = {
                                                editingLocationId = loc.id
                                                editingLocationText = loc.name
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Location", tint = textColor.copy(alpha = 0.7f))
                                            }
                                            IconButton(onClick = { onDeleteLocation(loc) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Location", tint = textColor.copy(alpha = 0.7f))
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = textColor.copy(alpha = 0.15f))
                            }
                        }
                    }
                } else if (selectedTab == 1) {
                    // Manufacturers Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newMfgName,
                            onValueChange = { newMfgName = it },
                            label = { Text("Add Manufacturer", color = textColor) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newMfgName.isNotBlank() && !manufacturers.contains(newMfgName.trim())) {
                                    manufacturers.add(newMfgName.trim())
                                    newMfgName = ""
                                }
                            },
                            enabled = newMfgName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Manufacturer", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(manufacturers, key = { "mfg_$it" }) { mfg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mfg, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textColor)
                                IconButton(onClick = { manufacturers.remove(mfg) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Manufacturer", tint = textColor.copy(alpha = 0.7f))
                                }
                            }
                            HorizontalDivider(color = textColor.copy(alpha = 0.15f))
                        }
                    }
                } else {
                    // Categories Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCatName,
                            onValueChange = { newCatName = it },
                            label = { Text("Add Category", color = textColor) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newCatName.isNotBlank() && !categories.contains(newCatName.trim())) {
                                    categories.add(newCatName.trim())
                                    newCatName = ""
                                }
                            },
                            enabled = newCatName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Category", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(categories, key = { "cat_$it" }) { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textColor)
                                IconButton(onClick = { categories.remove(cat) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Category", tint = textColor.copy(alpha = 0.7f))
                                }
                            }
                            HorizontalDivider(color = textColor.copy(alpha = 0.15f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = textColor)
            }
        }
    )
}
