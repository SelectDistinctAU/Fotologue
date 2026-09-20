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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.StockLot
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockLotScreen(
    lotId: Long? = null,
    masterId: Long? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onAddNewSupplier: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()

    val initialLot = lotList.find { it.id == lotId }

    // Active Suppliers Filter (ONLY suppliers marked as isUsed == true!)
    val activeSuppliersOnly = remember(supplierList, initialLot) {
        val used = supplierList.filter { it.isUsed }
        val assigned = initialLot?.supplierId?.let { id -> supplierList.find { it.id == id } }
        if (assigned != null && !used.contains(assigned)) {
            (used + assigned).distinct()
        } else if (used.isNotEmpty()) {
            used
        } else {
            supplierList
        }
    }

    var selectedMasterId by remember { mutableStateOf(masterId ?: initialLot?.masterId ?: masterList.firstOrNull()?.id) }
    var selectedSupplierId by remember { mutableStateOf(initialLot?.supplierId ?: activeSuppliersOnly.firstOrNull()?.id) }
    var previousSupplierCount by remember { mutableStateOf(supplierList.size) }

    LaunchedEffect(supplierList.size) {
        if (supplierList.size > previousSupplierCount) {
            // A new supplier was added! Auto-select the newly created supplier
            supplierList.lastOrNull()?.let { newSup ->
                selectedSupplierId = newSup.id
            }
        }
        previousSupplierCount = supplierList.size
    }
    var batchNumber by remember { mutableStateOf(initialLot?.batchNumber ?: "") }
    var location by remember { mutableStateOf(initialLot?.location ?: locationList.firstOrNull()?.name ?: "Chemical Shelf A") }
    var cost by remember { mutableStateOf(initialLot?.cost?.replace("$", "") ?: "") }
    var quantityStr by remember { mutableStateOf((initialLot?.quantity ?: 1).toString()) }
    var status by remember { mutableStateOf(initialLot?.status ?: "ON_HAND") } // ON_HAND vs IN_TRANSIT
    var notes by remember { mutableStateOf(initialLot?.notes ?: "") }

    // DYNAMIC RE-SYNC: Ensures when editing a StockLot or adding for a specific masterId, all fields pre-fill instantly!
    LaunchedEffect(initialLot, masterList, supplierList, masterId) {
        if (masterId != null) {
            selectedMasterId = masterId
        }
        initialLot?.let { lot ->
            selectedMasterId = lot.masterId
            if (lot.supplierId != null) {
                selectedSupplierId = lot.supplierId
            } else if (selectedSupplierId == null) {
                selectedSupplierId = activeSuppliersOnly.firstOrNull()?.id
            }
            batchNumber = lot.batchNumber
            location = lot.location
            cost = lot.cost.replace("$", "")
            quantityStr = lot.quantity.toString()
            status = lot.status
            notes = lot.notes
        } ?: run {
            if (selectedMasterId == null) selectedMasterId = masterId ?: masterList.firstOrNull()?.id
            if (selectedSupplierId == null) selectedSupplierId = activeSuppliersOnly.firstOrNull()?.id
        }
    }

    var masterExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statuses = listOf("ON_HAND", "IN_TRANSIT", "IN_USE", "EXHAUSTED")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (initialLot == null) "Add Physical Stock" else "Edit Stock Lot",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back to Lab"
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
            // Stock Item Specification Dropdown (Natural Language Label)
            ExposedDropdownMenuBox(
                expanded = masterExpanded,
                onExpandedChange = { masterExpanded = !masterExpanded }
            ) {
                val selectedMaster = masterList.find { it.id == selectedMasterId }
                val displayMasterName = if (selectedMaster != null) "${selectedMaster.name} (${selectedMaster.manufacturer})" else "Select Stock Item"
                OutlinedTextField(
                    value = displayMasterName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Stock Item Specification") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = masterExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = masterExpanded,
                    onDismissRequest = { masterExpanded = false }
                ) {
                    masterList.forEach { m ->
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

            // Supplier Contact Dropdown (STRICTLY LIMITED TO ACTIVE SUPPLIERS MARKED IN USE!)
            ExposedDropdownMenuBox(
                expanded = supplierExpanded,
                onExpandedChange = { supplierExpanded = !supplierExpanded }
            ) {
                val selectedSupplier = activeSuppliersOnly.find { it.id == selectedSupplierId }
                val displaySupplierName = selectedSupplier?.name ?: "Select Supplier"
                OutlinedTextField(
                    value = displaySupplierName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Supplier Contact") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = supplierExpanded,
                    onDismissRequest = { supplierExpanded = false }
                ) {
                    activeSuppliersOnly.forEach { sup ->
                        DropdownMenuItem(
                            text = { Text("${sup.name} (${sup.region})") },
                            onClick = {
                                selectedSupplierId = sup.id
                                supplierExpanded = false
                            }
                        )
                    }
                    if (onAddNewSupplier != null) {
                        DropdownMenuItem(
                            text = { Text("+ Add New Supplier Contact...", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                supplierExpanded = false
                                onAddNewSupplier()
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
                    value = batchNumber,
                    onValueChange = { batchNumber = it },
                    label = { Text("Batch / Lot Number") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Currency Field Validation: Numbers and decimal point ONLY, max 2 decimal places!
                OutlinedTextField(
                    value = cost,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                            cost = input
                        }
                    },
                    label = { Text("Purchase Cost ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = quantityStr,
                onValueChange = { quantityStr = it },
                label = { Text("Quantity Purchased") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Storage Location Dropdown (DEDICATED FULL-WIDTH LINE TO PREVENT WRAPPING!)
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = !locationExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Storage Location") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    locationList.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc.name) },
                            onClick = {
                                location = loc.name
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            // Initial Status Dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                val statusLabel = when (status) {
                    "ON_HAND" -> "🟢 On Hand (Received)"
                    "IN_USE" -> "🟡 In Use (Opened/Mixed)"
                    "IN_TRANSIT" -> "🚚 In Transit (Ordered)"
                    else -> "🔴 Exhausted (Empty)"
                }
                OutlinedTextField(
                    value = statusLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Initial Stock Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statuses.forEach { st ->
                        val label = when (st) {
                            "ON_HAND" -> "🟢 On Hand (Received)"
                            "IN_USE" -> "🟡 In Use (Opened/Mixed)"
                            "IN_TRANSIT" -> "🚚 In Transit (Ordered)"
                            else -> "🔴 Exhausted (Empty)"
                        }
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                status = st
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Stock Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedMasterId?.let { masterId ->
                        val formattedCost = if (cost.isNotBlank() && !cost.startsWith("$")) "$$cost" else cost
                        val lot = StockLot(
                            id = initialLot?.id ?: 0,
                            masterId = masterId,
                            supplierId = selectedSupplierId,
                            batchNumber = batchNumber,
                            location = location,
                            cost = formattedCost,
                            quantity = quantityStr.toIntOrNull() ?: 1,
                            status = status,
                            openedDate = if (status == "IN_USE" && initialLot?.openedDate == null) System.currentTimeMillis() else initialLot?.openedDate,
                            notes = notes
                        )

                        if (initialLot != null) {
                            viewModel.updateStockLot(lot)
                        } else {
                            viewModel.addStockLot(lot)
                        }
                        onSuccess()
                    }
                },
                enabled = selectedMasterId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Physical Stock", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
