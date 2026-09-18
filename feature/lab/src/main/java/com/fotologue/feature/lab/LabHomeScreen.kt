package com.fotologue.feature.lab

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.data.lab.LabEquipment
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.StockLot
import com.fotologue.core.designsystem.R

enum class LabView {
    MAIN_MENU, SUPPLIERS, INVENTORY, INVENTORY_CATEGORY_ITEMS, EQUIPMENT
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LabHomeScreen(
    initialTab: Int = 0,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onAddItemMaster: (String?) -> Unit,
    onEditItemMaster: (Long) -> Unit,
    onAddStockLot: () -> Unit,
    onEditStockLot: (Long) -> Unit,
    onAddSupplier: () -> Unit,
    onEditSupplier: (Long) -> Unit,
    onViewSupplierIssues: (Long) -> Unit,
    onAddEquipment: () -> Unit,
    onEditEquipment: (Long) -> Unit,
    onStartFilmSession: () -> Unit = {},
    onStartPrintingSession: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()

    // Ensure initial seeding of supplier contacts directory ONLY if empty
    LaunchedEffect(Unit) {
        if (supplierList.isEmpty() || locationList.isEmpty()) {
            viewModel.seedPresets()
        }
    }

    var currentView by rememberSaveable { mutableStateOf(LabView.MAIN_MENU) }
    var activeInventoryCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSupplierFilter by remember { mutableStateOf("Active Only") }

    val supplierFilters = listOf("Active Only", "All Contacts", "Australia", "USA / Global")
    val filteredSuppliers = supplierList.filter { sup ->
        when (selectedSupplierFilter) {
            "Active Only" -> sup.isUsed
            "Australia" -> sup.region.equals("Australia", ignoreCase = true)
            "USA / Global" -> !sup.region.equals("Australia", ignoreCase = true)
            else -> true
        }
    }

    val pageTitle = when (currentView) {
        LabView.MAIN_MENU -> "Fotologue Lab"
        LabView.SUPPLIERS -> "Lab • Suppliers"
        LabView.INVENTORY -> "Lab • Inventory"
        LabView.INVENTORY_CATEGORY_ITEMS -> "Lab • ${activeInventoryCategory ?: "Inventory"}"
        LabView.EQUIPMENT -> "Lab • Darkroom Equipment"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.experiment_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pageTitle,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (currentView) {
                            LabView.MAIN_MENU -> onBack()
                            LabView.INVENTORY_CATEGORY_ITEMS -> currentView = LabView.INVENTORY
                            else -> currentView = LabView.MAIN_MENU
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMaintenance) {
                        Icon(
                            painter = painterResource(R.drawable.ic_handyman),
                            contentDescription = "Maintenance"
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
        ) {
            when (currentView) {
                LabView.MAIN_MENU -> {
                    // MAIN VERTICAL NAVIGATION MENU (Matching Gear Library Design Ethos 100%)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item(key = "section_header_lab_menu") {
                            Text(
                                text = "LAB MODULES & SESSIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }

                        // 1. SUPPLIERS (FIRST AS NEEDED FOR STOCK)
                        item(key = "nav_suppliers") {
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text("Suppliers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Vendor contacts & active supply sources", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.SUPPLIERS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.warehouse_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) {
                                        Text("${supplierList.size} contacts")
                                    }
                                }
                            )
                        }

                        // 2. INVENTORY
                        item(key = "nav_inventory") {
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text("Inventory", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Chemistry, Paper, Safety Gear, and Supplies", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.INVENTORY },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.inventory_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) {
                                        Text("${masterList.size} items")
                                    }
                                }
                            )
                        }

                        // 3. DARKROOM EQUIPMENT
                        item(key = "nav_equipment") {
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text("Darkroom Equipment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Enlargers, Tanks, Trays, Lenses, and Processing Gear", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.EQUIPMENT },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.camera_indoor_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) {
                                        Text("${equipmentList.size} items")
                                    }
                                }
                            )
                        }

                        item(key = "section_header_darkroom_sessions") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "DARKROOM EXECUTION SESSIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                            )
                        }

                        // 4. DEVELOP ROLL
                        item(key = "nav_develop_roll") {
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text("Develop Roll", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Film roll processing session with live timer & chemical tracking", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                selected = false,
                                onClick = onStartFilmSession,
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.theaters_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }

                        // 5. ENLARGER SESSION
                        item(key = "nav_enlarger_session") {
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text("Enlarger Session", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Log enlarger printing, recipes, and learning reflection logs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                selected = false,
                                onClick = onStartPrintingSession,
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.table_lamp_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                LabView.SUPPLIERS -> {
                    // PAGE 1: SUPPLIERS DIRECTORY
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = onAddSupplier,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supplier Contact", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Supplier Filter Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            supplierFilters.forEach { filter ->
                                FilterChip(
                                    selected = selectedSupplierFilter == filter,
                                    onClick = { selectedSupplierFilter = filter },
                                    label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (filteredSuppliers.isEmpty()) {
                                item {
                                    Text(
                                        text = "No suppliers found for filter '$selectedSupplierFilter'. Tap 'Supplier Contact' to add vendors.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(filteredSuppliers, key = { "sup_${it.id}_${it.name}" }) { supplier ->
                                    SupplierCard(
                                        supplier = supplier,
                                        onToggleIsUsed = {
                                            viewModel.updateSupplier(supplier.copy(isUsed = !supplier.isUsed))
                                        },
                                        onEdit = { onEditSupplier(supplier.id) },
                                        onViewIssues = { onViewSupplierIssues(supplier.id) },
                                        onDelete = { viewModel.deleteSupplier(supplier) }
                                    )
                                }
                            }
                        }
                    }
                }

                LabView.INVENTORY -> {
                    // PAGE 2: INVENTORY CATEGORIES MENU
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { onAddItemMaster(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(key = "section_header_inventory_cats") {
                                Text(
                                    text = "INVENTORY CATEGORIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }

                            val categories = listOf(
                                Triple("Chemistry", R.drawable.lab_panel_24px, masterList.count { it.category.equals("Chemistry", ignoreCase = true) }),
                                Triple("Paper", R.drawable.stacks_24px, masterList.count { it.category.equals("Paper", ignoreCase = true) }),
                                Triple("Safety Gear", R.drawable.health_and_safety_24px, masterList.count { it.category.equals("Safety Gear", ignoreCase = true) }),
                                Triple("Supplies", R.drawable.shopping_cart_24px, masterList.count { it.category.equals("Supplies", ignoreCase = true) })
                            )

                            items(categories, key = { "cat_${it.first}" }) { (catName, iconRes, count) ->
                                NavigationDrawerItem(
                                    label = { Text(catName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                                    selected = false,
                                    onClick = {
                                        activeInventoryCategory = catName
                                        currentView = LabView.INVENTORY_CATEGORY_ITEMS
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(iconRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    badge = {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ) {
                                            Text("$count items")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                LabView.INVENTORY_CATEGORY_ITEMS -> {
                    // PAGE 2.1: CATEGORY ITEMS DETAIL LIST
                    val categoryMasters = masterList.filter { it.category.equals(activeInventoryCategory, ignoreCase = true) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { onAddItemMaster(activeInventoryCategory) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (categoryMasters.isEmpty()) {
                                item {
                                    Text(
                                        text = "No $activeInventoryCategory items added yet. Tap '+ Inventory Item' above to create one.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(categoryMasters, key = { "m_${it.id}_${it.name}" }) { master ->
                                    val masterLots = lotList.filter { it.masterId == master.id }

                                    MasterItemCard(
                                        master = master,
                                        lots = masterLots,
                                        onEditMaster = { onEditItemMaster(master.id) },
                                        onDeleteMaster = { viewModel.deleteMaster(master) },
                                        onAddStockLot = onAddStockLot,
                                        onEditStockLot = { lot -> onEditStockLot(lot.id) },
                                        onShiftStatus = { lot, newStatus ->
                                            viewModel.updateStockLot(
                                                lot.copy(
                                                    status = newStatus,
                                                    openedDate = if (newStatus == "IN_USE" && lot.openedDate == null) System.currentTimeMillis() else lot.openedDate
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                LabView.EQUIPMENT -> {
                    // PAGE 3: DARKROOM EQUIPMENT
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = onAddEquipment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Equipment", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (equipmentList.isEmpty()) {
                                item {
                                    Text(
                                        text = "No equipment added yet. Tap 'Equipment' to search or create custom items.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                val groupedEquipment = equipmentList.groupBy { it.category }
                                groupedEquipment.forEach { (category, items) ->
                                    item(key = "header_eq_$category") {
                                        Text(
                                            text = category.uppercase(),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                        )
                                    }
                                    items(items, key = { "equip_${it.id}_${it.name}" }) { equipment ->
                                        EquipmentCard(
                                            equipment = equipment,
                                            onEdit = { onEditEquipment(equipment.id) },
                                            onDelete = { viewModel.deleteEquipment(equipment) }
                                        )
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

@Composable
fun MasterItemCard(
    master: InventoryMaster,
    lots: List<StockLot>,
    onEditMaster: () -> Unit,
    onDeleteMaster: () -> Unit,
    onAddStockLot: () -> Unit,
    onEditStockLot: (StockLot) -> Unit,
    onShiftStatus: (StockLot, String) -> Unit
) {
    val onHandQty = lots.filter { it.status == "ON_HAND" }.sumOf { it.quantity }
    val inUseQty = lots.filter { it.status == "IN_USE" }.sumOf { it.quantity }
    val inTransitQty = lots.filter { it.status == "IN_TRANSIT" }.sumOf { it.quantity }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = master.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    val sub = buildString {
                        if (master.manufacturer.isNotBlank()) append("${master.manufacturer} • ")
                        append("Size: ${master.displaySize}")
                    }
                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Calculated Summary
                    val stockReport = buildString {
                        append("🟢 On Hand: $onHandQty")
                        if (inUseQty > 0) append(" • 🟡 In Use: $inUseQty")
                        if (inTransitQty > 0) append(" • 🚚 In Transit: $inTransitQty")
                    }
                    Text(
                        text = stockReport,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditMaster) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Inventory Item")
                    }
                    IconButton(onClick = onDeleteMaster) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Inventory Item")
                    }
                }
            }

            // ATTACHED + ADD STOCK BUTTON DIRECTLY ON ITEM CARD
            OutlinedButton(
                onClick = onAddStockLot,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Stock Lot", style = MaterialTheme.typography.labelSmall)
            }

            // Physical Stock Lots (In-Flow & Actions)
            if (lots.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Stock Lots & In-Flow:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

                lots.forEach { lot ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val statusLabel = when (lot.status) {
                                "ON_HAND" -> "🟢 On Hand (${lot.quantity} ${master.containerSizeUnit})"
                                "IN_USE" -> "🟡 In Use (${lot.quantity} ${master.containerSizeUnit})"
                                "IN_TRANSIT" -> "🚚 In Transit (${lot.quantity} ${master.containerSizeUnit})"
                                else -> "🔴 Exhausted"
                            }
                            Text(statusLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "Location: ${lot.location}${if (lot.batchNumber.isNotBlank()) " • Lot: " + lot.batchNumber else ""}${if (lot.cost.isNotBlank()) " • Cost: " + lot.cost else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quick Action Buttons
                            if (lot.status == "IN_TRANSIT") {
                                TextButton(onClick = { onShiftStatus(lot, "ON_HAND") }) {
                                    Text("Receive", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            } else if (lot.status == "ON_HAND") {
                                TextButton(onClick = { onShiftStatus(lot, "IN_USE") }) {
                                    Text("Open/Use", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            IconButton(onClick = { onEditStockLot(lot) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Stock Lot", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierCard(
    supplier: LabSupplier,
    onToggleIsUsed: () -> Unit,
    onEdit: () -> Unit,
    onViewIssues: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = supplier.isUsed,
                    onCheckedChange = { onToggleIsUsed() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = supplier.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    val sub = buildString {
                        append(supplier.region)
                        val rating = supplier.reliabilityRating
                        if (rating != null && rating > 0f) append(" • $rating★ Reliability")
                        if (supplier.website.isNotBlank()) append(" • ${supplier.website}")
                    }
                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row {
                IconButton(onClick = onViewIssues) {
                    Icon(
                        painter = painterResource(R.drawable.ic_handyman),
                        contentDescription = "View Order Issues & Interaction Logs",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (supplier.address.isNotBlank()) {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(supplier.address)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                        }
                    }) {
                        Icon(Icons.Default.Place, contentDescription = "View Store Location on Google Maps", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Supplier")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Supplier")
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: LabEquipment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipment.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "${equipment.category}${if (equipment.notes.isNotBlank()) " • " + equipment.notes else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Equipment")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Equipment")
                }
            }
        }
    }
}
