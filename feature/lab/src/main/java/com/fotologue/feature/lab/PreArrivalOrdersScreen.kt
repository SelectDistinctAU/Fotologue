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
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    // Mark Delivered Dialog State
    var deliveryDialogOrder by remember { mutableStateOf<OrderHistory?>(null) }
    var selectedDeliveryLocation by remember { mutableStateOf("") }
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
        if (showPresetSelectorModal) {
            val presetCategoryKey = when (assetCategory) {
                "Camera" -> "CAMERA"
                "Lens" -> "LENS"
                "Film Stock" -> "FILM"
                "Filter" -> "FILTER"
                "Flash" -> "FLASH"
                "Viewfinder" -> "VIEWFINDER"
                "Teleconverter" -> "TELECONVERTER"
                "Consumable" -> "CONSUMABLE"
                "Lab Equipment" -> "EQUIPMENT"
                else -> "CAMERA"
            }

            val masterPresets = remember(assetCategory) {
                val enginePresets = MasterPresetEngine.getPresetsByCategory(presetCategoryKey)
                if (enginePresets.isNotEmpty()) {
                    enginePresets.map { p ->
                        Pair(p.name, "${p.brand} • ${p.attributes.entries.joinToString { "${it.key}: ${it.value}" }}".trim(' ', '•'))
                    }
                } else {
                    masterList.map { m ->
                        Pair(m.name, "${m.manufacturer} • ${m.displaySize}")
                    }
                }
            }

            var presetSearchQuery by remember { mutableStateOf("") }
            val filteredPresets = masterPresets.filter {
                presetSearchQuery.isBlank() || it.first.contains(presetSearchQuery, ignoreCase = true) || it.second.contains(presetSearchQuery, ignoreCase = true)
            }

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
                    IconButton(onClick = { showPresetSelectorModal = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Return to Order Form")
                    }
                    Icon(painterResource(com.fotologue.core.designsystem.R.drawable.settings_photo_camera_24px), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select $assetCategory Preset", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                    OutlinedTextField(
                        value = presetSearchQuery,
                        onValueChange = { presetSearchQuery = it },
                        label = { Text("Search $assetCategory Master Catalog") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No presets found for $assetCategory", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredPresets.forEach { (pName, pSpecs) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            assetName = pName
                                            assetSpecs = pSpecs
                                            showPresetSelectorModal = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(pName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(pSpecs, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                assetName = pName
                                                assetSpecs = pSpecs
                                                showPresetSelectorModal = false
                                            }
                                        ) {
                                            Text("Select")
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        } else {
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
                            text = if (editingOrder == null) "Log Pre-Arrival Asset Order" else "Edit Pre-Arrival Order",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Step $stepperPage of 3: ${when(stepperPage) { 1 -> "Asset Classification"; 2 -> "Asset Specifications"; else -> "Order & Shipping Details" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                when (stepperPage) {
                    1 -> { // STEP 1: ASSET CLASSIFICATION
                        Text("Define the incoming asset classification category:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
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
                    }
                    2 -> { // STEP 2: ASSET SPECIFICATIONS
                        Text("Specify details for the arriving $assetCategory:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        if (assetCategory != "Other / General") {
                            OutlinedButton(
                                onClick = { showPresetSelectorModal = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(painterResource(com.fotologue.core.designsystem.R.drawable.settings_photo_camera_24px), contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select $assetCategory from Master Catalog Presets", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = assetName,
                            onValueChange = { assetName = it },
                            label = { Text("$assetCategory Name / Model *") },
                            placeholder = { Text("e.g. Voigtländer 35mm f/1.4, Ilford ID-11") },
                            isError = assetName.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = assetSpecs,
                            onValueChange = { assetSpecs = it },
                            label = { Text("Technical Specifications") },
                            placeholder = { Text("e.g. VM Mount, 43mm Filter Thread, 1L Powder") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    3 -> { // STEP 3: ORDER LIFECYCLE & VENDOR
                        Text("Vendor, tracking, and storage location details:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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

                        // Target Storage Location Dropdown
                        ExposedDropdownMenuBox(
                            expanded = locationDropdownExpanded,
                            onExpandedChange = { locationDropdownExpanded = !locationDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = targetStorageLocation.ifBlank { "Select Target Storage Location" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Target Storage Location Upon Arrival") },
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
                                            targetStorageLocation = loc.name
                                            locationDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

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
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (stepperPage > 1) {
                        OutlinedButton(
                            onClick = { stepperPage-- },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    }
                    if (stepperPage < 3) {
                        Button(
                            onClick = { stepperPage++ },
                            enabled = if (stepperPage == 2) assetName.isNotBlank() else true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                val fullNotes = "$assetName • Category: $assetCategory${if (assetSpecs.isNotBlank()) " • $assetSpecs" else ""}${if (targetStorageLocation.isNotBlank()) " • Target: $targetStorageLocation" else ""}".trim(' ', '•')
                                val order = OrderHistory(
                                    id = editingOrder?.id ?: 0,
                                    supplierId = selectedSupplierId,
                                    trackingNumber = trackingNumber.trim(),
                                    totalCost = totalCost.trim(),
                                    notes = fullNotes,
                                    status = status,
                                    assetCategory = assetCategory
                                )
                                if (editingOrder == null) {
                                    viewModel.addOrder(order)
                                } else {
                                    viewModel.updateOrder(order)
                                }
                                showAddEditDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Order", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 1-Tap Delivery Activation Dialog
    if (deliveryDialogOrder != null) {
        val targetOrder = deliveryDialogOrder!!
        AlertDialog(
            onDismissRequest = { deliveryDialogOrder = null },
            title = { Text("📦 Activate Asset on Delivery", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Confirm arrival and activate this asset into your active catalog at the designated storage location:")
                    
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
                            DropdownMenuItem(
                                text = { Text("📦 Unassigned / Pending Storage") },
                                onClick = {
                                    selectedDeliveryLocation = "Unassigned / Pending Storage"
                                    locationDropdownExpanded = false
                                }
                            )
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
                        val updatedNotes = "${targetOrder.notes}${if (selectedDeliveryLocation.isNotBlank()) " • Stored at: $selectedDeliveryLocation" else ""}".trim(' ', '•')
                        viewModel.updateOrder(targetOrder.copy(status = "Delivered", notes = updatedNotes))
                        
                        // Auto-Register Item to Active Catalog (Camera Gear or Lab Inventory)
                        val assetNameExtracted = targetOrder.notes.substringBefore(" • ")
                        val assetSpecsExtracted = targetOrder.notes.substringAfter(" • ", "")
                        val vendorName = supplierList.find { it.id == targetOrder.supplierId }?.name ?: ""
                        
                        viewModel.activatePreArrivalAsset(
                            assetCategory = targetOrder.assetCategory,
                            assetName = assetNameExtracted,
                            assetSpecs = assetSpecsExtracted,
                            storageLocation = selectedDeliveryLocation.ifBlank { "Unassigned Storage" },
                            supplierName = vendorName
                        )

                        deliveryDialogOrder = null
                    }
                ) {
                    Text("Activate Asset")
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
                                        text = order.notes.ifBlank { "Inbound Asset Order" },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Badge(containerColor = if (isDelivered) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer) {
                                    Text(formatOrderStatus(order.status), style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Category: ${order.assetCategory} • Vendor: $supplierName${if (order.totalCost.isNotBlank()) " • Cost: $" + order.totalCost else ""}",
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
