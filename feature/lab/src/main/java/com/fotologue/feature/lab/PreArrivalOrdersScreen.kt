package com.fotologue.feature.lab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.fotologue.core.data.lab.LabChemical
import com.fotologue.core.data.lab.LabEquipment
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.data.preset.MasterPresetEngine
import com.fotologue.core.designsystem.R
import com.fotologue.feature.lab.R as LabR

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreArrivalOrdersScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onAddCamera: () -> Unit = {},
    onAddLens: () -> Unit = {},
    onAddFilm: () -> Unit = {},
    onAddFilter: () -> Unit = {},
    onAddFlash: () -> Unit = {},
    onAddViewfinder: () -> Unit = {},
    onAddTeleconverter: () -> Unit = {},
    onAddItemMaster: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val orderList by viewModel.orderList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val masterList by viewModel.masterList.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf("All") }

    // Dialog & Stepper State
    var showAddEditDialog by remember { mutableStateOf(false) }
    var stepperPage by remember { mutableIntStateOf(1) } // 1 = Classification, 2 = Asset Spec, 3 = Order Details
    var editingOrder by remember { mutableStateOf<OrderHistory?>(null) }

    var assetCategory by remember { mutableStateOf("Camera") }
    var assetName by remember { mutableStateOf("") }
    var assetSpecs by remember { mutableStateOf("") }
    var selectedSupplierId by remember { mutableStateOf<Long?>(null) }
    var trackingNumber by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("In Transit") }
    var targetStorageLocation by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var supplierDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    var showPresetSelectorModal by remember { mutableStateOf(false) }

    val categoryOptions = listOf(
        "Camera", "Lens", "Film Stock",
        "Teleconverter", "Filter", "Flash", "Viewfinder",
        "Consumable", "Lab Equipment", "Other / General"
    )
    val statusOptions = listOf("Waitlisted", "Packaging", "In Transit", "Delivered")

    fun openAddEditDialog(order: OrderHistory? = null) {
        editingOrder = order
        stepperPage = 1
        assetCategory = order?.assetCategory?.ifBlank { "Camera" } ?: "Camera"
        assetName = order?.notes?.substringBefore(" • ") ?: ""
        assetSpecs = order?.notes?.substringAfter(" • ", "") ?: ""
        selectedSupplierId = order?.supplierId ?: supplierList.firstOrNull()?.id
        trackingNumber = order?.trackingNumber ?: ""
        totalCost = order?.totalCost ?: ""
        notes = order?.notes ?: ""
        status = order?.status ?: "In Transit"
        targetStorageLocation = locationList.firstOrNull()?.name ?: ""
        showAddEditDialog = true
    }

    if (showAddEditDialog) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAddEditDialog = false }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Close Form")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Log Pre-Arrival Asset Order",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Step 1 of 1: Asset Classification",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text("Define the incoming asset classification category to open its canonical preset catalog and form:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = assetCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asset Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categoryOptions.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontWeight = if (cat == assetCategory) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                assetCategory = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    showAddEditDialog = false
                    when (assetCategory) {
                        "Camera" -> onAddCamera()
                        "Lens" -> onAddLens()
                        "Film Stock" -> onAddFilm()
                        "Filter" -> onAddFilter()
                        "Flash" -> onAddFlash()
                        "Viewfinder" -> onAddViewfinder()
                        "Teleconverter" -> onAddTeleconverter()
                        "Consumable", "Chemical", "Paper" -> onAddItemMaster("Consumable")
                        "Lab Equipment" -> onAddItemMaster("Lab Equipment")
                        else -> onAddCamera()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Next", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }

    val filteredOrders = orderList.filter {
        when (selectedStatusFilter) {
            "In Transit" -> it.status == "IN_TRANSIT" || it.status == "In Transit"
            "Waitlisted / Packaging" -> it.status in listOf("WAITLISTED", "Waitlisted", "PACKAGING", "Packaging")
            "Delivered" -> it.status in listOf("DELIVERED", "Delivered", "RECEIVED", "Received")
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
            Text("Log Pre-Arrival Asset Order", fontWeight = FontWeight.Bold)
        }

        // Status Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                    val isDelivered = order.status in listOf("DELIVERED", "Delivered", "RECEIVED", "Received")
                    val orderTitle = order.notes.substringBefore(" • ").removePrefix("Category: Lens ").removePrefix("Category: Camera ").ifBlank { "Inbound Asset Order" }
                    val orderSpecs = order.notes.substringAfter(" • ", "").ifBlank { "Category: ${order.assetCategory}" }

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
                                        painter = painterResource(com.fotologue.core.designsystem.R.drawable.delivery_truck_speed_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = orderTitle,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                var statusMenuExpanded by remember { mutableStateOf(false) }
                                Box {
                                    AssistChip(
                                        onClick = { statusMenuExpanded = true },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(com.fotologue.core.designsystem.R.drawable.delivery_truck_speed_24px),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        label = { Text(formatOrderStatus(order.status), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (isDelivered) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                    DropdownMenu(
                                        expanded = statusMenuExpanded,
                                        onDismissRequest = { statusMenuExpanded = false }
                                    ) {
                                        listOf("In Transit", "Waitlisted", "Packaging", "Delivered").forEach { st ->
                                            DropdownMenuItem(
                                                text = { Text(st, fontWeight = if (st.equals(order.status, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal) },
                                                onClick = {
                                                    statusMenuExpanded = false
                                                    viewModel.updateOrder(order.copy(status = st))
                                                    if (st == "Delivered") {
                                                        val assetNameExtracted = order.notes.substringBefore(" • ").removePrefix("Category: Lens ").removePrefix("Category: Camera ")
                                                        val assetSpecsExtracted = order.notes.substringAfter(" • ", "")
                                                        viewModel.activatePreArrivalAsset(
                                                            assetCategory = order.assetCategory,
                                                            assetName = assetNameExtracted,
                                                            assetSpecs = assetSpecsExtracted,
                                                            storageLocation = "Unassigned Storage",
                                                            supplierName = supplierName
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$orderSpecs • Vendor: $supplierName${if (order.totalCost.isNotBlank()) " • $" + order.totalCost else ""}",
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
                                if (!isDelivered) {
                                    Button(
                                        onClick = {
                                            viewModel.updateOrder(order.copy(status = "Delivered"))
                                            val assetNameExtracted = order.notes.substringBefore(" • ").removePrefix("Category: Lens ").removePrefix("Category: Camera ")
                                            val assetSpecsExtracted = order.notes.substringAfter(" • ", "")
                                            viewModel.activatePreArrivalAsset(
                                                assetCategory = order.assetCategory,
                                                assetName = assetNameExtracted,
                                                assetSpecs = assetSpecsExtracted,
                                                storageLocation = "Unassigned Storage",
                                                supplierName = supplierName
                                            )
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
                                    IconButton(onClick = { openAddEditDialog(order) }, modifier = Modifier.size(48.dp)) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit pre-arrival order", modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = { viewModel.deleteOrder(order) }, modifier = Modifier.size(48.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete pre-arrival order", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
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
