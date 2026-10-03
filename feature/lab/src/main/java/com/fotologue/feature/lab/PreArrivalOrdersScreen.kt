package com.fotologue.feature.lab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.designsystem.R

private fun formatOrderStatus(status: String): String {
    return when (status.uppercase()) {
        "WAITLISTED" -> "Waitlisted"
        "PACKAGING" -> "Packaging"
        "IN_TRANSIT" -> "In Transit"
        "DELIVERED", "RECEIVED" -> "Delivered"
        else -> status.replace('_', ' ').lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

private fun parseOrderStatusToEnum(display: String): String {
    return when (display) {
        "Waitlisted" -> "Waitlisted"
        "Packaging" -> "Packaging"
        "In Transit" -> "In Transit"
        "Delivered" -> "Delivered"
        else -> display.replace('_', ' ').lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreArrivalOrdersScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orderList by viewModel.orderList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf("All") }

    // Dialog state
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingOrder by remember { mutableStateOf<OrderHistory?>(null) }
    var selectedSupplierId by remember { mutableStateOf<Long?>(null) }
    var trackingNumber by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("IN_TRANSIT") }

    var supplierDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    // Mark Delivered Dialog State
    var deliveryDialogOrder by remember { mutableStateOf<OrderHistory?>(null) }
    var selectedDeliveryLocation by remember { mutableStateOf("") }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Waitlisted", "Packaging", "In Transit", "Delivered")

    fun openAddEditDialog(order: OrderHistory? = null) {
        editingOrder = order
        selectedSupplierId = order?.supplierId ?: supplierList.firstOrNull()?.id
        trackingNumber = order?.trackingNumber ?: ""
        totalCost = order?.totalCost ?: ""
        notes = order?.notes ?: ""
        status = order?.status ?: "IN_TRANSIT"
        showAddEditDialog = true
    }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = {
                Text(
                    text = if (editingOrder == null) "Log Pre-Arrival Order" else "Edit Pre-Arrival Order",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Vendor Contact Dropdown
                    ExposedDropdownMenuBox(
                        expanded = supplierDropdownExpanded,
                        onExpandedChange = { supplierDropdownExpanded = !supplierDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val currentSupplierName = supplierList.find { it.id == selectedSupplierId }?.name ?: "Select Vendor Contact"
                        OutlinedTextField(
                            value = currentSupplierName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Vendor / Contact") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierDropdownExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = supplierDropdownExpanded,
                            onDismissRequest = { supplierDropdownExpanded = false }
                        ) {
                            supplierList.forEach { sup ->
                                DropdownMenuItem(
                                    text = { Text("${sup.name} (${sup.region})") },
                                    onClick = {
                                        selectedSupplierId = sup.id
                                        supplierDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Order Lifecycle Status
                    ExposedDropdownMenuBox(
                        expanded = statusDropdownExpanded,
                        onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = formatOrderStatus(status),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Order Lifecycle Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false }
                        ) {
                            statusOptions.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        status = parseOrderStatusToEnum(st)
                                        statusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Asset Description / Items Ordered") },
                        placeholder = { Text("e.g. Leica M3, Pentax 50mm f/1.4 Lens") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = trackingNumber,
                            onValueChange = { trackingNumber = it },
                            label = { Text("Tracking Number") },
                            placeholder = { Text("e.g. AusPost 12345") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = totalCost,
                            onValueChange = { totalCost = it },
                            label = { Text("Total Cost ($)") },
                            placeholder = { Text("e.g. 450.00") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val order = OrderHistory(
                            id = editingOrder?.id ?: 0,
                            supplierId = selectedSupplierId,
                            trackingNumber = trackingNumber.trim(),
                            totalCost = totalCost.trim(),
                            notes = notes.trim(),
                            status = status
                        )
                        if (editingOrder == null) {
                            viewModel.addOrder(order)
                        } else {
                            viewModel.updateOrder(order)
                        }
                        showAddEditDialog = false
                    }
                ) {
                    Text("Save Order")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 1-Tap Delivery Storage Location Assignment Dialog
    if (deliveryDialogOrder != null) {
        val targetOrder = deliveryDialogOrder!!
        AlertDialog(
            onDismissRequest = { deliveryDialogOrder = null },
            title = { Text("📦 Assign Storage Location on Delivery", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select the canonical physical storage location where this inbound item will be stored upon delivery:")
                    
                    ExposedDropdownMenuBox(
                        expanded = locationDropdownExpanded,
                        onExpandedChange = { locationDropdownExpanded = !locationDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDeliveryLocation.ifBlank { "Select Storage Location" },
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
                            locationList.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text("📦 ${loc.name} (${loc.locationType})") },
                                    onClick = {
                                        selectedDeliveryLocation = loc.name
                                        locationDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedNotes = if (selectedDeliveryLocation.isNotBlank()) {
                            "${targetOrder.notes} • Stored at: $selectedDeliveryLocation".trim(' ', '•')
                        } else targetOrder.notes
                        viewModel.updateOrder(targetOrder.copy(status = "DELIVERED", notes = updatedNotes))
                        deliveryDialogOrder = null
                    }
                ) {
                    Text("Confirm Delivery")
                }
            },
            dismissButton = {
                TextButton(onClick = { deliveryDialogOrder = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val filteredOrders = orderList.filter {
        when (selectedStatusFilter) {
            "In Transit" -> it.status == "IN_TRANSIT"
            "Waitlisted / Packaging" -> it.status == "WAITLISTED" || it.status == "PACKAGING"
            "Delivered" -> it.status == "DELIVERED" || it.status == "RECEIVED"
            else -> true
        }
    }

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
            Text("Log Pre-Arrival Order", fontWeight = FontWeight.Bold)
        }

        // Status Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "In Transit", "Waitlisted / Packaging", "Delivered").forEach { filter ->
                val isSelected = selectedStatusFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatusFilter = filter },
                    label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pre-arrival orders logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredOrders) { order ->
                    val supplierName = supplierList.find { it.id == order.supplierId }?.name ?: "Vendor Contact"
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.warehouse_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = order.notes.ifBlank { "Inbound Gear Order" },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Badge(containerColor = if (order.status == "DELIVERED" || order.status == "Delivered") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer) {
                                    Text(formatOrderStatus(order.status), style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vendor: $supplierName${if (order.totalCost.isNotBlank()) " • Cost: $" + order.totalCost else ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (order.trackingNumber.isNotBlank()) {
                                Text(
                                    text = "Tracking #: ${order.trackingNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (order.status != "DELIVERED" && order.status != "RECEIVED") {
                                    Button(
                                        onClick = {
                                            selectedDeliveryLocation = locationList.firstOrNull()?.name ?: ""
                                            deliveryDialogOrder = order
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Delivered", style = MaterialTheme.typography.labelSmall)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { openAddEditDialog(order) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { viewModel.deleteOrder(order) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
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
