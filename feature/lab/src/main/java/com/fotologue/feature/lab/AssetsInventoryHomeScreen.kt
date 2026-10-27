package com.fotologue.feature.lab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.designsystem.R

enum class AssetsInventoryView {
    MAIN_MENU, INVENTORY, INVENTORY_CATEGORY_ITEMS, STORAGE_LOCATIONS, PRE_ARRIVAL_ORDERS, CUSTODY_LOANS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AssetsInventoryHomeScreen(
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onAddItemMaster: (String?) -> Unit = {},
    onEditItemMaster: (Long) -> Unit = {},
    onAddStockLot: (Long?) -> Unit = {},
    onEditStockLot: (Long) -> Unit = {},
    onAddCamera: () -> Unit = {},
    onAddLens: () -> Unit = {},
    onAddFilm: () -> Unit = {},
    onAddFilter: () -> Unit = {},
    onAddFlash: () -> Unit = {},
    onAddViewfinder: () -> Unit = {},
    onAddTeleconverter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val masterList by viewModel.masterList.collectAsState()
    val lotList by viewModel.lotList.collectAsState()
    val locationList by viewModel.locationList.collectAsState()
    val orderList by viewModel.orderList.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    var currentView by remember { mutableStateOf(AssetsInventoryView.MAIN_MENU) }
    var activeInventoryCategory by remember { mutableStateOf("Chemistry") }

    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary
    val safelightBtnBg = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary
    val safelightBtnFg = if (isSafelightActive) Color(0xFFFF0000) else Color.White
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant

    val drawerItemColors = if (isSafelightActive) {
        NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color(0xFF1F0000),
            selectedContainerColor = Color(0xFF440000),
            unselectedIconColor = Color(0xFFFF0000),
            selectedIconColor = Color(0xFFFF0000),
            unselectedTextColor = Color(0xFFFF0000),
            selectedTextColor = Color(0xFFFF0000)
        )
    } else {
        NavigationDrawerItemDefaults.colors()
    }

    val badgeBg = if (isSafelightActive) Color(0xFF440000) else MaterialTheme.colorScheme.surfaceContainerHighest
    val badgeFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant

    fun handleBackNavigation() {
        when (currentView) {
            AssetsInventoryView.INVENTORY_CATEGORY_ITEMS -> currentView = AssetsInventoryView.INVENTORY
            AssetsInventoryView.INVENTORY,
            AssetsInventoryView.STORAGE_LOCATIONS,
            AssetsInventoryView.PRE_ARRIVAL_ORDERS,
            AssetsInventoryView.CUSTODY_LOANS -> currentView = AssetsInventoryView.MAIN_MENU
            AssetsInventoryView.MAIN_MENU -> onBack()
        }
    }

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
                            AssetsInventoryView.MAIN_MENU -> R.drawable.inventory_24px
                            AssetsInventoryView.STORAGE_LOCATIONS -> R.drawable.shelves_24px
                            AssetsInventoryView.PRE_ARRIVAL_ORDERS -> R.drawable.delivery_truck_speed_24px
                            AssetsInventoryView.CUSTODY_LOANS -> R.drawable.ic_call_log
                            AssetsInventoryView.INVENTORY -> R.drawable.inventory_24px
                            AssetsInventoryView.INVENTORY_CATEGORY_ITEMS -> when (activeInventoryCategory) {
                                "Chemistry" -> R.drawable.lab_panel_24px
                                "Paper" -> R.drawable.stacks_24px
                                "Film Stock" -> R.drawable.camera_roll_24px
                                "Safety Gear" -> R.drawable.health_and_safety_24px
                                "Supplies" -> R.drawable.shopping_cart_24px
                                else -> R.drawable.inventory_24px
                            }
                        }
                        Icon(
                            painter = painterResource(headerIconRes),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentView) {
                                AssetsInventoryView.MAIN_MENU -> "Assets & Inventory"
                                AssetsInventoryView.INVENTORY -> "Consumables"
                                AssetsInventoryView.INVENTORY_CATEGORY_ITEMS -> activeInventoryCategory
                                AssetsInventoryView.STORAGE_LOCATIONS -> "Storage Locations"
                                AssetsInventoryView.PRE_ARRIVAL_ORDERS -> "Pre-Arrival Orders"
                                AssetsInventoryView.CUSTODY_LOANS -> "Custody & Loans"
                            },
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
                    IconButton(
                        onClick = { viewModel.toggleSafelight() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.night_sight_max_24px),
                            contentDescription = if (isSafelightActive) "Disable Red Shift" else "Enable Red Shift",
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
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
                AssetsInventoryView.MAIN_MENU -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item(key = "section_header_inventory_menu") {
                            Text(
                                text = "PHYSICAL INVENTORY & ORDERS",
                                style = MaterialTheme.typography.labelSmall,
                                color = safelightPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }

                        // 1. CONSUMABLES
                        item(key = "nav_consumables") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Consumables", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Chemistry, Paper, Safety Gear, and Supplies", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = AssetsInventoryView.INVENTORY },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.inventory_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(containerColor = badgeBg, contentColor = badgeFg) {
                                        Text("${masterList.size} items", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 2. STORAGE LOCATIONS
                        item(key = "nav_storage_locations") {
                            NavigationDrawerItem(
                                colors = drawerItemColors,
                                label = {
                                    Column {
                                        Text("Storage Locations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                        Text("Dry Cabinets, Studio Shelves, Camera Bags, Paper Safes", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                    }
                                },
                                selected = false,
                                onClick = { currentView = AssetsInventoryView.STORAGE_LOCATIONS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.shelves_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    Badge(containerColor = badgeBg, contentColor = badgeFg) {
                                        Text("${locationList.size} locations", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 3. PRE-ARRIVAL ORDERS
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
                                onClick = { currentView = AssetsInventoryView.PRE_ARRIVAL_ORDERS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.delivery_truck_speed_24px),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    val inTransitCount = orderList.count { order ->
                                        val st = order.status.uppercase()
                                        st != "DELIVERED" && st != "RECEIVED" && (
                                            st.contains("TRANSIT") || st.contains("WAIT") || st.contains("PACK") || order.assetCategory in listOf("Camera", "Lens", "Film Stock", "Consumables", "Lab Equipment")
                                        )
                                    }
                                    Badge(containerColor = badgeBg, contentColor = badgeFg) {
                                        Text("$inTransitCount orders", color = badgeFg)
                                    }
                                }
                            )
                        }

                        // 4. CUSTODY & LOANS LEDGER
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
                                onClick = { currentView = AssetsInventoryView.CUSTODY_LOANS },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_call_log),
                                        contentDescription = null,
                                        tint = safelightPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                badge = {
                                    val activeCount = orderList.count { order ->
                                        val st = order.status.uppercase()
                                        st != "COMPLETED" && st != "RETURNED" && (
                                            st.contains("LOAN") || st.contains("RENTAL") || st.contains("REPAIR") || st.contains("SERVICE") || order.notes.contains("Borrower:", ignoreCase = true)
                                        )
                                    }
                                    Badge(containerColor = badgeBg, contentColor = badgeFg) {
                                        Text("$activeCount active", color = badgeFg)
                                    }
                                }
                            )
                        }
                    }
                }

                AssetsInventoryView.INVENTORY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { onAddItemMaster(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = safelightBtnFg)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium, color = safelightBtnFg)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val inventoryCategories = listOf(
                            Triple("Chemistry", R.drawable.lab_panel_24px, masterList.count { it.category.equals("Chemistry", ignoreCase = true) }),
                            Triple("Paper", R.drawable.stacks_24px, masterList.count { it.category.equals("Paper", ignoreCase = true) }),
                            Triple("Film Stock", R.drawable.camera_roll_24px, masterList.count { it.category.equals("Film Stock", ignoreCase = true) }),
                            Triple("Safety Gear", R.drawable.health_and_safety_24px, masterList.count { it.category.equals("Safety Gear", ignoreCase = true) }),
                            Triple("Supplies", R.drawable.shopping_cart_24px, masterList.count { it.category.equals("Supplies", ignoreCase = true) })
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    text = "INVENTORY CATEGORIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = safelightPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                                )
                            }
                            items(inventoryCategories) { (catName, iconRes, itemCount) ->
                                NavigationDrawerItem(
                                    colors = drawerItemColors,
                                    label = {
                                        Text(catName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                    },
                                    selected = false,
                                    onClick = {
                                        activeInventoryCategory = catName
                                        currentView = AssetsInventoryView.INVENTORY_CATEGORY_ITEMS
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
                                        Badge(containerColor = badgeBg, contentColor = badgeFg) {
                                            Text("$itemCount items", color = badgeFg)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                AssetsInventoryView.INVENTORY_CATEGORY_ITEMS -> {
                    val filteredMasters = masterList.filter { it.category.equals(activeInventoryCategory, ignoreCase = true) }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { onAddItemMaster(activeInventoryCategory) },
                            colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = safelightBtnFg)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inventory Item", style = MaterialTheme.typography.labelMedium, color = safelightBtnFg)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (filteredMasters.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No $activeInventoryCategory items added yet. Tap '+ Inventory Item' above to create one.", color = safelightSubFg)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredMasters, key = { "master_${it.id}_${it.name}" }) { master ->
                                    val masterLots = lotList.filter { it.masterId == master.id }
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = safelightCardBg)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = master.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightFg)
                                                    val sub = listOfNotNull(
                                                        master.manufacturer.ifBlank { null },
                                                        master.containerSize.ifBlank { null },
                                                        "Default Location: ${master.defaultLocation}"
                                                    ).joinToString(" • ")
                                                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                                }
                                                IconButton(onClick = { onEditItemMaster(master.id) }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Item Specification", tint = safelightPrimary)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Stock Lots & In-Flow:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = safelightPrimary)
                                                TextButton(onClick = { onAddStockLot(master.id) }) {
                                                    Text("Add Stock Lot", style = MaterialTheme.typography.labelSmall, color = safelightPrimary)
                                                }
                                            }

                                            if (masterLots.isEmpty()) {
                                                Text("No physical stock lots logged yet. Tap 'Add Stock Lot' to record physical bottles/boxes.", style = MaterialTheme.typography.bodySmall, color = safelightSubFg)
                                            } else {
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    masterLots.forEach { lot ->
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                val lotTitle = "Qty: ${lot.quantity} • Stored: ${lot.location}${if (lot.batchNumber.isNotBlank()) " • Batch: " + lot.batchNumber else ""}"
                                                                Text(lotTitle, style = MaterialTheme.typography.bodySmall, color = safelightFg)
                                                            }
                                                            IconButton(onClick = { onEditStockLot(lot.id) }, modifier = Modifier.size(24.dp)) {
                                                                Icon(Icons.Default.Edit, contentDescription = "Edit Stock Lot", tint = safelightPrimary, modifier = Modifier.size(16.dp))
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
                }

                AssetsInventoryView.STORAGE_LOCATIONS -> {
                    StorageLocationsScreen(
                        viewModel = viewModel,
                        onBack = { currentView = AssetsInventoryView.MAIN_MENU }
                    )
                }

                AssetsInventoryView.PRE_ARRIVAL_ORDERS -> {
                    PreArrivalOrdersScreen(
                        viewModel = viewModel,
                        onBack = { currentView = AssetsInventoryView.MAIN_MENU },
                        onAddCamera = onAddCamera,
                        onAddLens = onAddLens,
                        onAddFilm = onAddFilm,
                        onAddFilter = onAddFilter,
                        onAddFlash = onAddFlash,
                        onAddViewfinder = onAddViewfinder,
                        onAddTeleconverter = onAddTeleconverter,
                        onAddItemMaster = { cat -> onAddItemMaster(cat) }
                    )
                }

                AssetsInventoryView.CUSTODY_LOANS -> {
                    CustodyLedgerScreen(
                        viewModel = viewModel,
                        onBack = { currentView = AssetsInventoryView.MAIN_MENU }
                    )
                }
            }
        }
    }
}
