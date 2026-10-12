package com.fotologue.feature.lab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabStorageLocation
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageLocationsScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locationList by viewModel.locationList.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingLocation by remember { mutableStateOf<LabStorageLocation?>(null) }
    var locationName by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("Cabinet") }
    var locationNotes by remember { mutableStateOf("") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    val storageTypes = listOf("Cabinet", "Shelf", "Bag", "Studio", "Drawer", "Paper Safe", "Fridge / Cold Storage")

    fun openDialog(loc: LabStorageLocation? = null) {
        editingLocation = loc
        locationName = loc?.name ?: ""
        locationType = loc?.locationType ?: "Cabinet"
        locationNotes = loc?.sleeveNumber ?: ""
        showAddEditDialog = true
    }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = {
                Text(
                    text = if (editingLocation == null) "Add Storage Location" else "Edit Storage Location",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Location Name") },
                        placeholder = { Text("e.g. Dry Cabinet A, Studio Shelf 2") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = locationType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Storage Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            storageTypes.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        locationType = st
                                        typeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = locationNotes,
                        onValueChange = { locationNotes = it },
                        label = { Text("Sleeve / Unit # or Details") },
                        placeholder = { Text("e.g. Sleeve #1, Humidity 40%") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (locationName.isNotBlank()) {
                            val loc = LabStorageLocation(
                                id = editingLocation?.id ?: 0,
                                name = locationName.trim(),
                                locationType = locationType,
                                sleeveNumber = locationNotes.ifBlank { "Sleeve #1" }
                            )
                            if (editingLocation == null) {
                                viewModel.addStorageLocation(loc)
                            } else {
                                viewModel.updateStorageLocation(loc)
                            }
                            showAddEditDialog = false
                        }
                    }
                ) {
                    Text("Save Location")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { openDialog() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Storage Location", fontWeight = FontWeight.Bold)
        }

        Text(
            "Canonical physical storage spaces across your studio, darkroom, and field bags:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (locationList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No storage locations logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(locationList) { loc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(loc.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("${loc.locationType} • ${loc.sleeveNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { openDialog(loc) }, modifier = Modifier.size(48.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit storage location", modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { viewModel.deleteStorageLocation(loc) }, modifier = Modifier.size(48.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete storage location", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
