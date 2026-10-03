package com.fotologue.feature.lab

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
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
    MAIN_MENU, SUPPLIERS, INVENTORY, INVENTORY_CATEGORY_ITEMS, EQUIPMENT, STORAGE_LOCATIONS, CUSTODY_LOANS, PRE_ARRIVAL_ORDERS
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
    onAddStockLot: (Long?) -> Unit,
    onEditStockLot: (Long) -> Unit,
    onAddSupplier: () -> Unit,
    onEditSupplier: (Long) -> Unit,
    onViewSupplierIssues: (Long) -> Unit,
    onAddEquipment: () -> Unit,
    onEditEquipment: (Long) -> Unit,
    onStartFilmSession: () -> Unit = {},
    onStartPrintingSession: () -> Unit = {},
    onViewLogDetail: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val supplierList by viewModel.supplierList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val orderList by viewModel.orderList.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val performanceLogs by viewModel.performanceLogs.collectAsState()
    val processEvents by viewModel.processEvents.collectAsState()
    val logEntriesWithGear by viewModel.allLogEntriesWithGear.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    // Ensure initial seeding of supplier contacts directory ONLY if empty
    LaunchedEffect(Unit) {
        if (supplierList.isEmpty() || locationList.isEmpty()) {
            viewModel.seedPresets()
        }
    }

    var currentView by rememberSaveable {
        mutableStateOf(
            when (initialTab) {
                1 -> LabView.SUPPLIERS
                2 -> LabView.INVENTORY
                3 -> LabView.EQUIPMENT
                4 -> LabView.STORAGE_LOCATIONS
                else -> LabView.MAIN_MENU
            }
        )
    }
    var activeInventoryCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSupplierFilter by remember { mutableStateOf("Active Only") }
    var showSafelightExitDialog by remember { mutableStateOf(false) }

    fun handleBackNavigation() {
        when (currentView) {
            LabView.MAIN_MENU -> {
                if (isSafelightActive) {
                    showSafelightExitDialog = true
                } else {
                    onBack()
                }
            }
            LabView.SUPPLIERS -> {
                if (initialTab == 1) {
                    onBack()
                } else {
                    currentView = LabView.MAIN_MENU
                }
            }
            LabView.INVENTORY_CATEGORY_ITEMS -> currentView = LabView.INVENTORY
            else -> currentView = LabView.MAIN_MENU
        }
    }

    val supplierFilters = listOf("Active Contacts", "All Contacts", "Australia", "USA / Global")
    val filteredSuppliers = supplierList.filter { sup ->
        when (selectedSupplierFilter) {
            "Active Contacts" -> sup.isUsed
            "Australia" -> sup.region.equals("Australia", ignoreCase = true)
            "USA / Global" -> !sup.region.equals("Australia", ignoreCase = true)
            else -> true
        }
    }

    val pageTitle = when (currentView) {
        LabView.MAIN_MENU -> "Fotologue Lab"
        LabView.SUPPLIERS -> "Contacts Directory"
        LabView.INVENTORY -> "Lab • Consumables"
        LabView.INVENTORY_CATEGORY_ITEMS -> "Lab • ${activeInventoryCategory ?: "Consumables"}"
        LabView.EQUIPMENT -> "Lab • Lab Equipment"
        LabView.STORAGE_LOCATIONS -> "Lab • Storage Locations"
        LabView.CUSTODY_LOANS -> "Lab • Custody & Loans"
        LabView.PRE_ARRIVAL_ORDERS -> "Lab • Pre-Arrival Orders"
    }

    // PURE DARKROOM SAFELIGHT RED COLORS (ZERO WHITE LIGHT!)
    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    val drawerItemColors = if (isSafelightActive) {
        NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color(0xFF1A0000),
            unselectedIconColor = Color(0xFFFF0000),
            unselectedTextColor = Color(0xFFFF0000)
        )
    } else {
        NavigationDrawerItemDefaults.colors()
    }

    val badgeBg = if (isSafelightActive) Color(0xFF440000) else MaterialTheme.colorScheme.surfaceContainerHighest
    val badgeFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val headerIconRes = when (currentView) {
                            LabView.SUPPLIERS, LabView.STORAGE_LOCATIONS -> R.drawable.warehouse_24px
                            LabView.EQUIPMENT -> R.drawable.camera_indoor_24px
                            LabView.INVENTORY, LabView.INVENTORY_CATEGORY_ITEMS -> R.drawable.inventory_24px
                            else -> R.drawable.experiment_24px
                        }
                        Icon(
                            painter = painterResource(headerIconRes),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pageTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = safelightFg
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackNavigation() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = safelightPrimary
                        )
                    }
                },
                actions = {
                    // UNAMBIGUOUS SAFELIGHT TOGGLE SWITCH
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = "Safelight",
                            style = MaterialTheme.typography.labelSmall,
                            color = safelightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isSafelightActive,
                            onCheckedChange = { viewModel.setSafelightActive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF0000),
                                checkedTrackColor = Color(0xFF660000),
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        )
                    }
                    IconButton(onClick = onNavigateToMaintenance) {
                        Icon(
                            painter = painterResource(R.drawable.ic_handyman),
                            contentDescription = "Maintenance",
                            tint = safelightPrimary
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
                                color = safelightPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }

                        // 2. CONSUMABLES
                        item(key = "nav_inventory") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Consumables", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Chemistry, Paper, Safety Gear, and Supplies", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.INVENTORY },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.inventory_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text("${masterList.size} items", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 3. LAB EQUIPMENT
                        item(key = "nav_equipment") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Lab Equipment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Enlargers, Tanks, Trays, Lenses, and Processing Gear", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.EQUIPMENT },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.camera_indoor_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text("${equipmentList.size} items", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 4. STORAGE LOCATIONS
                        item(key = "nav_locations") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Storage Locations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Dry Cabinets, Studio Shelves, Camera Bags, Paper Safes", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.STORAGE_LOCATIONS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.warehouse_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text("${locationList.size} locations", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 5. PRE-ARRIVAL ORDERS
                        item(key = "nav_pre_arrival_orders") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Pre-Arrival Orders", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Inbound Packages, Waitlist & Shipping Lifecycle", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.PRE_ARRIVAL_ORDERS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.warehouse_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    val inTransitCount = orderList.count { it.status == "IN_TRANSIT" || it.status == "WAITLISTED" }
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text("$inTransitCount orders", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 6. CUSTODY & LOANS LEDGER
                        item(key = "nav_custody_loans") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Custody & Loans Ledger", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Active Loans, Hired Gear, CLA & Camera Repairs", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = LabView.CUSTODY_LOANS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_call_log),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    val activeCount = orderList.count { it.status in listOf("LOAN_OUT", "RENTAL_IN", "SERVICE_REPAIR") }
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text("$activeCount active", color = badgeFg)
                                    }
                                }
                            )
                        }

                        item(key = "section_header_darkroom_sessions") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = "DARKROOM EXECUTION SESSIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = safelightPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                            )
                        }

                        // 4. DEVELOP ROLL
                        item(key = "nav_develop_roll") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Develop Roll", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Film roll processing session with live timer & chemical tracking", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = onStartFilmSession,
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.theaters_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }

                        // 5. ENLARGER SESSION
                        item(key = "nav_enlarger_session") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Enlarger Session", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Log enlarger printing, recipes, and learning reflection logs", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = onStartPrintingSession,
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.table_lamp_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
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
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Contact", style = MaterialTheme.typography.labelMedium, color = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
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
                                        color = safelightSubFg
                                    )
                                }
                            } else {
                                items(filteredSuppliers, key = { "sup_${it.id}_${it.name}" }) { supplier ->
                                    SupplierCard(
                                        supplier = supplier,
                                        isSafelightActive = isSafelightActive,
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
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium, color = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
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
                                    color = safelightPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }

                            val categories = listOf(
                                Triple("Chemistry", R.drawable.lab_panel_24px, masterList.count { it.category.equals("Chemistry", ignoreCase = true) }),
                                Triple("Paper", R.drawable.stacks_24px, masterList.count { it.category.equals("Paper", ignoreCase = true) }),
                                Triple("Film Stock", R.drawable.camera_roll_24px, masterList.count { it.category.equals("Film Stock", ignoreCase = true) }),
                                Triple("Safety Gear", R.drawable.health_and_safety_24px, masterList.count { it.category.equals("Safety Gear", ignoreCase = true) }),
                                Triple("Supplies", R.drawable.shopping_cart_24px, masterList.count { it.category.equals("Supplies", ignoreCase = true) })
                            )

                            items(categories, key = { "cat_${it.first}" }) { (catName, iconRes, count) ->
                                NavigationDrawerItem(
                                    colors = drawerItemColors,
                                    label = { Text(catName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg) },
                                    selected = false,
                                    onClick = {
                                        activeInventoryCategory = catName
                                        currentView = LabView.INVENTORY_CATEGORY_ITEMS
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(iconRes),
                                            contentDescription = null,
                                            tint = safelightPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    badge = {
                                        Badge(
                                            containerColor = badgeBg,
                                            contentColor = badgeFg
                                        ) {
                                            Text("$count items", color = badgeFg)
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
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium, color = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
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
                                        color = safelightSubFg
                                    )
                                }
                            } else {
                                items(categoryMasters, key = { "m_${it.id}_${it.name}" }) { master ->
                                    val masterLots = lotList.filter { it.masterId == master.id }

                                    MasterItemCard(
                                        master = master,
                                        lots = masterLots,
                                        isSafelightActive = isSafelightActive,
                                        onEditMaster = { onEditItemMaster(master.id) },
                                        onDeleteMaster = { viewModel.deleteMaster(master) },
                                        onAddStockLot = { onAddStockLot(master.id) },
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
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Equipment", style = MaterialTheme.typography.labelMedium, color = if (isSafelightActive) Color(0xFFFF0000) else Color.White)
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
                                        color = safelightSubFg
                                    )
                                }
                            } else {
                                val groupedEquipment = equipmentList.groupBy { it.category }
                                groupedEquipment.forEach { (category, items) ->
                                    item(key = "header_eq_$category") {
                                        Text(
                                            text = category.uppercase(),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = safelightPrimary,
                                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                        )
                                    }
                                    items(items, key = { "equip_${it.id}_${it.name}" }) { equipment ->
                                        EquipmentCard(
                                            equipment = equipment,
                                            isSafelightActive = isSafelightActive,
                                            onEdit = { onEditEquipment(equipment.id) },
                                            onDelete = { viewModel.deleteEquipment(equipment) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                LabView.STORAGE_LOCATIONS -> {
                    StorageLocationsScreen(
                        viewModel = viewModel,
                        onBack = { currentView = LabView.MAIN_MENU }
                    )
                }

                LabView.CUSTODY_LOANS -> {
                    CustodyLedgerScreen(
                        viewModel = viewModel,
                        onBack = { currentView = LabView.MAIN_MENU }
                    )
                }

                LabView.PRE_ARRIVAL_ORDERS -> {
                    PreArrivalOrdersScreen(
                        viewModel = viewModel,
                        onBack = { currentView = LabView.MAIN_MENU }
                    )
                }
            }

            if (showSafelightExitDialog) {
                AlertDialog(
                    onDismissRequest = { showSafelightExitDialog = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
                    textContentColor = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant,
                    title = { Text("Turn Off Darkroom Safelight?", color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified, fontWeight = FontWeight.Bold) },
                    text = { Text("You are leaving the Lab area with Darkroom Safelight Mode active. Would you like to turn off Safelight Mode or keep it running?", color = if (isSafelightActive) Color(0xFFFF3333) else Color.Unspecified) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.setSafelightActive(false)
                                showSafelightExitDialog = false
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary,
                                contentColor = if (isSafelightActive) Color(0xFFFF0000) else Color.White
                            )
                        ) {
                            Text("Turn Off Safelight", color = if (isSafelightActive) Color(0xFFFF0000) else Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showSafelightExitDialog = false
                            onBack()
                        }) {
                            Text("Keep Active", color = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MasterItemCard(
    master: InventoryMaster,
    lots: List<StockLot>,
    isSafelightActive: Boolean = false,
    onEditMaster: () -> Unit,
    onDeleteMaster: () -> Unit,
    onAddStockLot: () -> Unit,
    onEditStockLot: (StockLot) -> Unit,
    onShiftStatus: (StockLot, String) -> Unit
) {
    val onHandQty = lots.filter { it.status == "ON_HAND" }.sumOf { it.quantity }
    val inUseQty = lots.filter { it.status == "IN_USE" }.sumOf { it.quantity }
    val inTransitQty = lots.filter { it.status == "IN_TRANSIT" }.sumOf { it.quantity }

    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = safelightCardBg)
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
                    Text(text = master.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                    val sub = buildString {
                        if (master.manufacturer.isNotBlank()) append("${master.manufacturer} • ")
                        append("Size: ${master.displaySize}")
                    }
                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = safelightSubFg)

                    // Calculated Summary
                    val stockReport = if (isSafelightActive) {
                        buildString {
                            append("On Hand: $onHandQty")
                            if (inUseQty > 0) append(" • In Use: $inUseQty")
                            if (inTransitQty > 0) append(" • In Transit: $inTransitQty")
                        }
                    } else {
                        buildString {
                            append("🟢 On Hand: $onHandQty")
                            if (inUseQty > 0) append(" • 🟡 In Use: $inUseQty")
                            if (inTransitQty > 0) append(" • 🚚 In Transit: $inTransitQty")
                        }
                    }
                    Text(
                        text = stockReport,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = safelightPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditMaster) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Inventory Item", tint = safelightPrimary)
                    }
                    IconButton(onClick = onDeleteMaster) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Inventory Item", tint = safelightPrimary)
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
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = safelightPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Stock Lot", style = MaterialTheme.typography.labelSmall, color = safelightPrimary)
            }

            // Physical Stock Lots (In-Flow & Actions)
            if (lots.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.outlineVariant)
                Text("Stock Lots & In-Flow:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = safelightPrimary)

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
                            Text(statusLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                            Text(
                                text = "Location: ${lot.location}${if (lot.batchNumber.isNotBlank()) " • Lot: " + lot.batchNumber else ""}${if (lot.cost.isNotBlank()) " • Cost: " + lot.cost else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = safelightSubFg
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quick Action Buttons
                            if (lot.status == "IN_TRANSIT") {
                                TextButton(onClick = { onShiftStatus(lot, "ON_HAND") }) {
                                    Text("Receive", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = safelightPrimary)
                                }
                            } else if (lot.status == "ON_HAND") {
                                TextButton(onClick = { onShiftStatus(lot, "IN_USE") }) {
                                    Text("Open/Use", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = safelightPrimary)
                                }
                            }

                            IconButton(onClick = { onEditStockLot(lot) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Stock Lot", modifier = Modifier.size(18.dp), tint = safelightPrimary)
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
    isSafelightActive: Boolean = false,
    onToggleIsUsed: () -> Unit,
    onEdit: () -> Unit,
    onViewIssues: () -> Unit,
    onDelete: () -> Unit
) {
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = safelightCardBg)
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
                    Text(text = supplier.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                    val sub = buildString {
                        append(supplier.region)
                        val rating = supplier.reliabilityRating
                        if (rating != null && rating > 0f) append(" • $rating★ Reliability")
                        if (supplier.website.isNotBlank()) append(" • ${supplier.website}")
                    }
                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                }
            }

            Row {
                IconButton(onClick = onViewIssues) {
                    Icon(
                        painter = painterResource(R.drawable.ic_call_log),
                        contentDescription = "Vendor Issue & Call Tracker",
                        tint = safelightPrimary
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
                        } catch (_: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                        }
                    }) {
                        Icon(Icons.Default.Place, contentDescription = "View Store Location on Google Maps", tint = safelightPrimary)
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Supplier", tint = safelightPrimary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Supplier", tint = safelightPrimary)
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: LabEquipment,
    isSafelightActive: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = safelightCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipment.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                Text(
                    text = "${equipment.category}${if (equipment.notes.isNotBlank()) " • " + equipment.notes else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = safelightSubFg
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Equipment", tint = safelightPrimary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Equipment", tint = safelightPrimary)
                }
            }
        }
    }
}
