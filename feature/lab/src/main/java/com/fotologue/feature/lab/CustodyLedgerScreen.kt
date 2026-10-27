package com.fotologue.feature.lab

import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.designsystem.R

private fun formatInteractionType(type: String): String {
    return when (type.uppercase()) {
        "LOAN_OUT", "LOAN" -> "Loan Out"
        "RENTAL_IN", "RENTAL" -> "Rental In"
        "SERVICE_REPAIR", "REPAIR" -> "Service / Repair"
        "PURCHASE_ORDER" -> "Purchase Order"
        else -> type.replace('_', ' ').lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

private fun parseInteractionTypeToEnum(display: String): String {
    return when (display) {
        "Loan Out" -> "LOAN_OUT"
        "Rental In" -> "RENTAL_IN"
        "Service / Repair" -> "SERVICE_REPAIR"
        else -> display.uppercase().replace(' ', '_')
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustodyLedgerScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orderList by viewModel.orderList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val chemicalList by viewModel.chemicalList.collectAsState()
    val cameraList by viewModel.allCameras.collectAsState(emptyList())
    val lensList by viewModel.allLenses.collectAsState(emptyList())
    val context = LocalContext.current

    var selectedTypeFilter by remember { mutableStateOf("All") }

    // Dialog State
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingOrder by remember { mutableStateOf<OrderHistory?>(null) }
    var assetName by remember { mutableStateOf("") }
    var interactionType by remember { mutableStateOf("LOAN_OUT") }
    var borrowerContactName by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var assetDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var contactDropdownExpanded by remember { mutableStateOf(false) }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    // Native Phone Contact Picker for Borrower Selection
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            val cursor = context.contentResolver.query(uri, arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        borrowerContactName = it.getString(nameIndex) ?: ""
                    }
                }
            }
        }
    }

    val typeOptions = listOf("Loan Out", "Rental In", "Service / Repair")

    val filteredInteractions = orderList.filter {
        when (selectedTypeFilter) {
            "Loans Out" -> it.status == "LOAN_OUT"
            "Rentals In" -> it.status == "RENTAL_IN"
            "Repairs & CLA" -> it.status == "SERVICE_REPAIR"
            else -> it.status in typeOptions || it.notes.contains("Borrower:", ignoreCase = true)
        }
    }

    fun openAddEditDialog(order: OrderHistory? = null) {
        editingOrder = order
        assetName = order?.notes?.substringBefore(" • ") ?: ""
        interactionType = order?.status?.ifBlank { "LOAN_OUT" } ?: "LOAN_OUT"
        borrowerContactName = supplierList.find { it.id == order?.supplierId }?.name ?: ""
        storageLocation = locationList.firstOrNull()?.name ?: ""
        notes = order?.notes ?: ""
        showAddEditDialog = true
    }

    if (showAddEditDialog) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAddEditDialog = false }) {
                    Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Close Form")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (editingOrder == null) "Log Custody Interaction" else "Edit Custody Interaction",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Registered Catalog Asset Picker Dropdown
            ExposedDropdownMenuBox(
                expanded = assetDropdownExpanded,
                onExpandedChange = { assetDropdownExpanded = !assetDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = assetName,
                    onValueChange = { assetName = it },
                    label = { Text("Registered Catalog Asset / Gear *") },
                    placeholder = { Text("Select or type asset name") },
                    isError = assetName.isBlank(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assetDropdownExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = assetDropdownExpanded,
                    onDismissRequest = { assetDropdownExpanded = false }
                ) {
                    cameraList.forEach { cam ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(com.fotologue.core.designsystem.R.drawable.settings_photo_camera_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text("${cam.name} (${cam.type.displayName})") },
                            onClick = {
                                assetName = cam.name
                                assetDropdownExpanded = false
                            }
                        )
                    }
                    lensList.forEach { lens ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(com.fotologue.core.designsystem.R.drawable.lensd),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text("${lens.make} ${lens.model}".trim()) },
                            onClick = {
                                assetName = "${lens.make} ${lens.model}".trim()
                                assetDropdownExpanded = false
                            }
                        )
                    }
                    equipmentList.forEach { eq ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.camera_indoor_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text("${eq.name} (${eq.category})") },
                            onClick = {
                                assetName = eq.name
                                assetDropdownExpanded = false
                            }
                        )
                    }
                    chemicalList.forEach { chem ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.lab_panel_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text("${chem.name} (${chem.type})") },
                            onClick = {
                                assetName = chem.name
                                assetDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Interaction Type
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = formatInteractionType(interactionType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Interaction Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    typeOptions.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = {
                                interactionType = parseInteractionTypeToEnum(t)
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Borrower / Contact Selection + Native Picker Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = contactDropdownExpanded,
                    onExpandedChange = { contactDropdownExpanded = !contactDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = borrowerContactName,
                        onValueChange = { borrowerContactName = it },
                        label = { Text("Borrower / Vendor Contact") },
                        placeholder = { Text("Select or type contact name") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = contactDropdownExpanded) },
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = contactDropdownExpanded && supplierList.isNotEmpty(),
                        onDismissRequest = { contactDropdownExpanded = false }
                    ) {
                        supplierList.forEach { sup ->
                            DropdownMenuItem(
                                text = { Text("${sup.name} (${sup.region})") },
                                onClick = {
                                    borrowerContactName = sup.name
                                    contactDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { contactPickerLauncher.launch(null) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Pick Phone Contact", tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Storage Location Dropdown
            ExposedDropdownMenuBox(
                expanded = locationDropdownExpanded,
                onExpandedChange = { locationDropdownExpanded = !locationDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = storageLocation.ifBlank { "Unassigned / Pending Location" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Storage Location") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationDropdownExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = locationDropdownExpanded,
                    onDismissRequest = { locationDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.shelves_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        text = { Text("Unassigned / Pending Location") },
                        onClick = {
                            storageLocation = "Unassigned / Pending Location"
                            locationDropdownExpanded = false
                        }
                    )
                    locationList.forEach { loc ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.shelves_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text("${loc.name} (${loc.locationType})") },
                            onClick = {
                                storageLocation = loc.name
                                locationDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Interaction Notes / Terms") },
                placeholder = { Text("e.g. Loaned for weekend shoot, expected return Oct 15") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (assetName.isNotBlank()) {
                        val matchedSupplier = supplierList.find { it.name.equals(borrowerContactName.trim(), ignoreCase = true) }
                        val fullNotes = "$assetName • Borrower: $borrowerContactName • Location: $storageLocation • ${notes.trim()}".trim(' ', '•')
                        val order = OrderHistory(
                            id = editingOrder?.id ?: 0,
                            supplierId = matchedSupplier?.id,
                            status = interactionType,
                            notes = fullNotes
                        )
                        if (editingOrder == null) {
                            viewModel.addOrder(order)
                        } else {
                            viewModel.updateOrder(order)
                        }
                        showAddEditDialog = false
                    }
                },
                enabled = assetName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save Interaction", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = { openAddEditDialog() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Custody / Loan Interaction", fontWeight = FontWeight.Bold)
            }

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Loans Out", "Rentals In", "Repairs & CLA").forEach { filter ->
                    val isSelected = selectedTypeFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTypeFilter = filter },
                        label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (filteredInteractions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active custody or loan interactions logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredInteractions) { item ->
                        val isCompleted = item.status == "COMPLETED" || item.status == "RETURNED"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_call_log),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.notes.substringBefore(" • ").ifBlank { "Custody Interaction" },
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    AssistChip(
                                        onClick = {},
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(com.fotologue.core.designsystem.R.drawable.patient_list_24px),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        label = { Text(if (isCompleted) "Returned" else formatInteractionType(item.status), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!isCompleted) {
                                        Button(
                                            onClick = {
                                                viewModel.updateOrder(item.copy(status = "RETURNED"))
                                                val refId = item.assetRefId
                                                if (refId != null) {
                                                    viewModel.markPreArrivalAssetDelivered(item.assetCategory, refId)
                                                }
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Mark Returned", style = MaterialTheme.typography.labelSmall)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(1.dp))
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { openAddEditDialog(item) }, modifier = Modifier.size(48.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit custody interaction", modifier = Modifier.size(20.dp))
                                        }
                                        IconButton(onClick = { viewModel.deleteOrder(item) }, modifier = Modifier.size(48.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete custody interaction", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
