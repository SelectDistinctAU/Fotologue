package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.designsystem.R

enum class MaintenanceArea {
    NONE, ITEM_NAMES, LOCATIONS, CATEGORIES, UNITS, MANUFACTURERS, EQUIPMENT_SUBCATEGORIES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabMaintenanceScreen(
    viewModel: LabHomeViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val locationList by viewModel.locationList.collectAsState()
    val categoryList by viewModel.categoryList.collectAsState()
    val unitList by viewModel.unitList.collectAsState()
    val itemNameList by viewModel.itemNameList.collectAsState()
    val equipmentSubCategoryList by viewModel.equipmentSubCategoryList.collectAsState()

    var activeArea by remember { mutableStateOf(MaintenanceArea.NONE) }

    // Equipment Sub-Category State
    var newEqSubCatName by remember { mutableStateOf("") }
    var newEqCategory by remember { mutableStateOf("Dev Tank") }
    var editingEqSubCatId by remember { mutableStateOf<Long?>(null) }
    var editingEqSubCatNameText by remember { mutableStateOf("") }
    var editingEqCategory by remember { mutableStateOf("Dev Tank") }
    var eqCatExpanded by remember { mutableStateOf(false) }
    var editEqCatExpanded by remember { mutableStateOf(false) }

    // Item Name Edit State
    var newItemName by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Paper") }
    var editingItemNameId by remember { mutableStateOf<Long?>(null) }
    var editingItemNameText by remember { mutableStateOf("") }
    var editingItemNameCategory by remember { mutableStateOf("Paper") }
    var itemCatExpanded by remember { mutableStateOf(false) }
    var editItemCatExpanded by remember { mutableStateOf(false) }

    // Storage Location Edit State
    var newLocationName by remember { mutableStateOf("") }
    var editingLocationId by remember { mutableStateOf<Long?>(null) }
    var editingLocationText by remember { mutableStateOf("") }

    // Category Edit State
    var newCatName by remember { mutableStateOf("") }
    var newCatDefaultUnit by remember { mutableStateOf("L") }
    var editingCatId by remember { mutableStateOf<Long?>(null) }
    var editingCatNameText by remember { mutableStateOf("") }
    var editingCatUnitText by remember { mutableStateOf("") }
    var catUnitExpanded by remember { mutableStateOf(false) }
    var editCatUnitExpanded by remember { mutableStateOf(false) }

    // Unit Edit State
    var newUnitName by remember { mutableStateOf("") }
    var editingUnitId by remember { mutableStateOf<Long?>(null) }
    var editingUnitText by remember { mutableStateOf("") }

    // Manufacturers List & Edit State
    val manufacturers = remember { mutableStateListOf("Ilford", "Kodak", "Ansell", "Adox", "Foma", "CineStill", "Paterson", "AP") }
    var newMfgName by remember { mutableStateOf("") }
    var editingMfgIndex by remember { mutableStateOf<Int?>(null) }
    var editingMfgText by remember { mutableStateOf("") }

    val categoriesList = listOf("Paper", "Chemistry", "Safety Gear", "Supplies", "Unassigned")
    val equipmentCategoriesList = listOf("Dev Tank", "Enlarger", "Enlarger Lens", "Tray", "Light Table", "Squeegee & Drying", "General")

    val pageTitle = when (activeArea) {
        MaintenanceArea.NONE -> "Maintenance"
        MaintenanceArea.ITEM_NAMES -> "Maintenance • Item Names"
        MaintenanceArea.LOCATIONS -> "Maintenance • Storage Locations"
        MaintenanceArea.CATEGORIES -> "Maintenance • Categories"
        MaintenanceArea.UNITS -> "Maintenance • Measurement Units"
        MaintenanceArea.MANUFACTURERS -> "Maintenance • Brands"
        MaintenanceArea.EQUIPMENT_SUBCATEGORIES -> "Maintenance • Equipment Sub-Categories"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pageTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeArea != MaintenanceArea.NONE) {
                            activeArea = MaintenanceArea.NONE
                        } else {
                            onBack?.invoke()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
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
            if (activeArea == MaintenanceArea.NONE) {
                // MAIN NAVIGATION DRAWER ITEMS (Matching Gear Library Design Ethos 100%)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "section_header_dimensions") {
                        Text(
                            text = "DARKROOM & INVENTORY DIMENSIONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }

                    item(key = "nav_item_names") {
                        NavigationDrawerItem(
                            label = { Text("Item Names") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.ITEM_NAMES },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_label),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text("${itemNameList.size}")
                                }
                            }
                        )
                    }

                    item(key = "nav_locations") {
                        NavigationDrawerItem(
                            label = { Text("Storage Locations") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.LOCATIONS },
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
                                    Text("${locationList.size}")
                                }
                            }
                        )
                    }

                    item(key = "nav_categories") {
                        NavigationDrawerItem(
                            label = { Text("Categories & Default Units") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.CATEGORIES },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_label),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text("${categoryList.size}")
                                }
                            }
                        )
                    }

                    item(key = "nav_units") {
                        NavigationDrawerItem(
                            label = { Text("Measurement Units") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.UNITS },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.scale_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text("${unitList.size}")
                                }
                            }
                        )
                    }

                    item(key = "nav_manufacturers") {
                        NavigationDrawerItem(
                            label = { Text("Brands") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.MANUFACTURERS },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.factory_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text("${manufacturers.size}")
                                }
                            }
                        )
                    }

                    item(key = "section_header_equipment") {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "EQUIPMENT TAXONOMY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }

                    item(key = "nav_eq_subcategories") {
                        NavigationDrawerItem(
                            label = { Text("Equipment Sub-Categories") },
                            selected = false,
                            onClick = { activeArea = MaintenanceArea.EQUIPMENT_SUBCATEGORIES },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_handyman),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) {
                                    Text("${equipmentSubCategoryList.size}")
                                }
                            }
                        )
                    }
                }
            } else if (activeArea == MaintenanceArea.EQUIPMENT_SUBCATEGORIES) {
                // DEDICATED AREA VIEW: EQUIPMENT SUB-CATEGORIES
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = eqCatExpanded,
                            onExpandedChange = { eqCatExpanded = !eqCatExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newEqCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Parent Equipment Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eqCatExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = eqCatExpanded,
                                onDismissRequest = { eqCatExpanded = false }
                            ) {
                                equipmentCategoriesList.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            newEqCategory = cat
                                            eqCatExpanded = false
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
                                value = newEqSubCatName,
                                onValueChange = { newEqSubCatName = it },
                                label = { Text("New Sub-Category Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (newEqSubCatName.isNotBlank()) {
                                        viewModel.addEquipmentSubCategory(newEqCategory, newEqSubCatName.trim())
                                        newEqSubCatName = ""
                                    }
                                },
                                enabled = newEqSubCatName.isNotBlank(),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Sub-Category")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (equipmentSubCategoryList.isEmpty()) {
                            item {
                                Text(
                                    text = "No equipment sub-categories defined yet. Enter a sub-category above to create one.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            itemsIndexed(equipmentSubCategoryList, key = { _, sub -> "eq_sub_${sub.id}_${sub.name}" }) { _, sub ->
                                if (editingEqSubCatId == sub.id) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = editingEqSubCatNameText,
                                            onValueChange = { editingEqSubCatNameText = it },
                                            label = { Text("Sub-Category Name") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            ExposedDropdownMenuBox(
                                                expanded = editEqCatExpanded,
                                                onExpandedChange = { editEqCatExpanded = !editEqCatExpanded },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                OutlinedTextField(
                                                    value = editingEqCategory,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("Category") },
                                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editEqCatExpanded) },
                                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = editEqCatExpanded,
                                                    onDismissRequest = { editEqCatExpanded = false }
                                                ) {
                                                    equipmentCategoriesList.forEach { cat ->
                                                        DropdownMenuItem(
                                                            text = { Text(cat) },
                                                            onClick = {
                                                                editingEqCategory = cat
                                                                editEqCatExpanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    if (editingEqSubCatNameText.isNotBlank()) {
                                                        viewModel.updateEquipmentSubCategory(sub.copy(name = editingEqSubCatNameText.trim(), category = editingEqCategory))
                                                        editingEqSubCatId = null
                                                    }
                                                },
                                                modifier = Modifier.height(52.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Save")
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Save")
                                            }
                                        }
                                    }
                                } else {
                                    NavigationDrawerItem(
                                        label = {
                                            Column {
                                                Text(text = sub.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                                Text(text = "Category: ${sub.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        },
                                        selected = false,
                                        onClick = {
                                            editingEqSubCatId = sub.id
                                            editingEqSubCatNameText = sub.name
                                            editingEqCategory = sub.category
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_handyman),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        badge = {
                                            Row {
                                                IconButton(onClick = {
                                                    editingEqSubCatId = sub.id
                                                    editingEqSubCatNameText = sub.name
                                                    editingEqCategory = sub.category
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Sub-Category")
                                                }
                                                IconButton(onClick = { viewModel.deleteEquipmentSubCategory(sub) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Sub-Category")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (activeArea == MaintenanceArea.ITEM_NAMES) {
                // DEDICATED AREA VIEW: ITEM NAMES (TWO-ROW SPACIOUS LAYOUT)
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Parent Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = itemCatExpanded,
                            onExpandedChange = { itemCatExpanded = !itemCatExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newItemCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Parent Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemCatExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = itemCatExpanded,
                                onDismissRequest = { itemCatExpanded = false }
                            ) {
                                categoriesList.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            newItemCategory = cat
                                            itemCatExpanded = false
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
                                value = newItemName,
                                onValueChange = { newItemName = it },
                                label = { Text("New Item Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (newItemName.isNotBlank()) {
                                        viewModel.addItemName(newItemName.trim(), newItemCategory)
                                        newItemName = ""
                                    }
                                },
                                enabled = newItemName.isNotBlank(),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Item Name")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (itemNameList.isEmpty()) {
                            item {
                                Text(
                                    text = "No custom item names defined yet. Type a name above to create one.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            itemsIndexed(itemNameList, key = { _, item -> "item_name_${item.id}_${item.name}" }) { _, item ->
                                if (editingItemNameId == item.id) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = editingItemNameText,
                                            onValueChange = { editingItemNameText = it },
                                            label = { Text("Item Name") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            ExposedDropdownMenuBox(
                                                expanded = editItemCatExpanded,
                                                onExpandedChange = { editItemCatExpanded = !editItemCatExpanded },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                OutlinedTextField(
                                                    value = editingItemNameCategory,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("Category") },
                                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editItemCatExpanded) },
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = editItemCatExpanded,
                                                    onDismissRequest = { editItemCatExpanded = false }
                                                ) {
                                                    categoriesList.forEach { cat ->
                                                        DropdownMenuItem(
                                                            text = { Text(cat) },
                                                            onClick = {
                                                                editingItemNameCategory = cat
                                                                editItemCatExpanded = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    if (editingItemNameText.isNotBlank()) {
                                                        viewModel.updateItemName(item.copy(name = editingItemNameText.trim(), category = editingItemNameCategory))
                                                        editingItemNameId = null
                                                    }
                                                },
                                                modifier = Modifier.height(52.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Save")
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Save")
                                            }
                                        }
                                    }
                                } else {
                                    NavigationDrawerItem(
                                        label = {
                                            Column {
                                                Text(text = item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                                Text(text = "Category: ${item.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        },
                                        selected = false,
                                        onClick = {
                                            editingItemNameId = item.id
                                            editingItemNameText = item.name
                                            editingItemNameCategory = item.category
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_label),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        badge = {
                                            Row {
                                                IconButton(onClick = {
                                                    editingItemNameId = item.id
                                                    editingItemNameText = item.name
                                                    editingItemNameCategory = item.category
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Item Name")
                                                }
                                                IconButton(onClick = { viewModel.deleteItemName(item) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Item Name")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (activeArea == MaintenanceArea.LOCATIONS) {
                // DEDICATED AREA VIEW: STORAGE LOCATIONS
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newLocationName,
                            onValueChange = { newLocationName = it },
                            label = { Text("New Storage Location") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newLocationName.isNotBlank()) {
                                    viewModel.addStorageLocation(newLocationName.trim())
                                    newLocationName = ""
                                }
                            },
                            enabled = newLocationName.isNotBlank(),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Location")
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (locationList.isEmpty()) {
                            item {
                                Text(
                                    text = "No storage locations defined yet. Type a name above to create one.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            itemsIndexed(locationList, key = { _, loc -> "loc_${loc.id}_${loc.name}" }) { _, loc ->
                                if (editingLocationId == loc.id) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = editingLocationText,
                                            onValueChange = { editingLocationText = it },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            if (editingLocationText.isNotBlank()) {
                                                viewModel.updateStorageLocation(loc.copy(name = editingLocationText.trim()))
                                                editingLocationId = null
                                            }
                                        }) {
                                            Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                } else {
                                    NavigationDrawerItem(
                                        label = { Text(loc.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                                        selected = false,
                                        onClick = {
                                            editingLocationId = loc.id
                                            editingLocationText = loc.name
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.warehouse_24px),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        badge = {
                                            Row {
                                                IconButton(onClick = {
                                                    editingLocationId = loc.id
                                                    editingLocationText = loc.name
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Location")
                                                }
                                                IconButton(onClick = { viewModel.deleteStorageLocation(loc) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Location")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (activeArea == MaintenanceArea.CATEGORIES) {
                // DEDICATED AREA VIEW: CATEGORIES WITH EXPOSED DROPDOWN FOR UNITS
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCatName,
                            onValueChange = { newCatName = it },
                            label = { Text("Category Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )

                        // Exposed Unit Dropdown
                        ExposedDropdownMenuBox(
                            expanded = catUnitExpanded,
                            onExpandedChange = { catUnitExpanded = !catUnitExpanded },
                            modifier = Modifier.weight(0.8f)
                        ) {
                            OutlinedTextField(
                                value = newCatDefaultUnit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unit") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catUnitExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = catUnitExpanded,
                                onDismissRequest = { catUnitExpanded = false }
                            ) {
                                unitList.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u.name) },
                                        onClick = {
                                            newCatDefaultUnit = u.name
                                            catUnitExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (newCatName.isNotBlank()) {
                                    viewModel.addCategory(newCatName.trim(), newCatDefaultUnit.trim())
                                    newCatName = ""
                                }
                            },
                            enabled = newCatName.isNotBlank(),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Category")
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(categoryList, key = { _, cat -> "cat_${cat.id}_${cat.name}" }) { _, cat ->
                            if (editingCatId == cat.id) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = editingCatNameText,
                                        onValueChange = { editingCatNameText = it },
                                        label = { Text("Category") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f)
                                    )

                                    // Exposed Unit Dropdown during edit
                                    ExposedDropdownMenuBox(
                                        expanded = editCatUnitExpanded,
                                        onExpandedChange = { editCatUnitExpanded = !editCatUnitExpanded },
                                        modifier = Modifier.weight(0.8f)
                                    ) {
                                        OutlinedTextField(
                                            value = editingCatUnitText,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Unit") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editCatUnitExpanded) },
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = editCatUnitExpanded,
                                            onDismissRequest = { editCatUnitExpanded = false }
                                        ) {
                                            unitList.forEach { u ->
                                                DropdownMenuItem(
                                                    text = { Text(u.name) },
                                                    onClick = {
                                                        editingCatUnitText = u.name
                                                        editCatUnitExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    IconButton(onClick = {
                                        if (editingCatNameText.isNotBlank()) {
                                            viewModel.updateCategory(cat.copy(name = editingCatNameText.trim(), defaultUnit = editingCatUnitText.trim()))
                                            editingCatId = null
                                        }
                                    }) {
                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            } else {
                                NavigationDrawerItem(
                                    label = {
                                        Column {
                                            Text(text = cat.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Text(
                                                text = "Default Unit: ${cat.defaultUnit}${if (cat.isSystemCore) " • System Core" else ""}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    selected = false,
                                    onClick = {
                                        editingCatId = cat.id
                                        editingCatNameText = cat.name
                                        editingCatUnitText = cat.defaultUnit
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_label),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    badge = {
                                        Row {
                                            IconButton(onClick = {
                                                editingCatId = cat.id
                                                editingCatNameText = cat.name
                                                editingCatUnitText = cat.defaultUnit
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Category")
                                            }
                                            if (!cat.isSystemCore) {
                                                IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else if (activeArea == MaintenanceArea.UNITS) {
                // DEDICATED AREA VIEW: MEASUREMENT UNITS (Inherits `scale_24px` Icon)
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newUnitName,
                            onValueChange = { newUnitName = it },
                            label = { Text("New Unit (e.g. sheets, L, box, roll)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newUnitName.isNotBlank()) {
                                    viewModel.addUnit(newUnitName.trim())
                                    newUnitName = ""
                                }
                            },
                            enabled = newUnitName.isNotBlank(),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Unit")
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(unitList, key = { _, u -> "u_${u.id}_${u.name}" }) { _, u ->
                            if (editingUnitId == u.id) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = editingUnitText,
                                        onValueChange = { editingUnitText = it },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = {
                                        if (editingUnitText.isNotBlank()) {
                                            viewModel.updateUnit(u.copy(name = editingUnitText.trim()))
                                            editingUnitId = null
                                        }
                                    }) {
                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            } else {
                                NavigationDrawerItem(
                                    label = {
                                        Column {
                                            Text(text = u.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            if (u.isSystemCore) {
                                                Text(text = "System Core Unit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    },
                                    selected = false,
                                    onClick = {
                                        editingUnitId = u.id
                                        editingUnitText = u.name
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.scale_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    badge = {
                                        Row {
                                            IconButton(onClick = {
                                                editingUnitId = u.id
                                                editingUnitText = u.name
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Unit")
                                            }
                                            if (!u.isSystemCore) {
                                                IconButton(onClick = { viewModel.deleteUnit(u) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Unit")
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // DEDICATED AREA VIEW: MANUFACTURERS (Inherits `factory_24px` Icon)
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newMfgName,
                            onValueChange = { newMfgName = it },
                            label = { Text("New Brand Name") },
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
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Manufacturer")
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(manufacturers, key = { index, _ -> "mfg_$index" }) { index, mfg ->
                            if (editingMfgIndex == index) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = editingMfgText,
                                        onValueChange = { editingMfgText = it },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = {
                                        if (editingMfgText.isNotBlank()) {
                                            manufacturers[index] = editingMfgText.trim()
                                            editingMfgIndex = null
                                        }
                                    }) {
                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            } else {
                                NavigationDrawerItem(
                                    label = { Text(text = mfg, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                                    selected = false,
                                    onClick = {
                                        editingMfgIndex = index
                                        editingMfgText = mfg
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.factory_24px),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    badge = {
                                        Row {
                                            IconButton(onClick = {
                                                editingMfgIndex = index
                                                editingMfgText = mfg
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Manufacturer")
                                            }
                                            IconButton(onClick = { manufacturers.removeAt(index) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove Manufacturer")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
