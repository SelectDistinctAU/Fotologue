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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.FilmPresets
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.designsystem.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInventoryMasterScreen(
    masterId: Long? = null,
    defaultCategory: String? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onSaveAndAddStock: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val masterList by viewModel.masterList.collectAsState()
    val categoriesList by viewModel.categoryList.collectAsState()
    val itemNamesList by viewModel.itemNameList.collectAsState()
    val isSafelightActive by viewModel.isSafelightActive.collectAsState()

    val initialMaster = masterList.find { it.id == masterId }

    // BLANK DEFAULTS FOR ADD MODE (initialMaster == null)
    var category by remember { mutableStateOf(initialMaster?.category ?: defaultCategory ?: "Chemistry") }
    var name by remember { mutableStateOf(initialMaster?.name ?: "") }
    var manufacturer by remember { mutableStateOf(initialMaster?.manufacturer ?: "") }
    var majorAmountStr by remember { mutableStateOf((initialMaster?.containerSizeAmount ?: 1f).toString()) }
    var majorUnit by remember { mutableStateOf(initialMaster?.containerSizeUnit ?: if (category == "Paper") "box" else if (category == "Film Stock") "box" else "L") }
    var minorYieldStr by remember { mutableStateOf((initialMaster?.innerUnitYieldTotal ?: if (category == "Paper") 25 else if (category == "Film Stock") 10 else 1000).toString()) }
    var minorUnit by remember { mutableStateOf(initialMaster?.innerItemUnit ?: if (category == "Paper") "sheets" else if (category == "Film Stock") "rolls" else "mL") }
    var notes by remember { mutableStateOf(initialMaster?.notes ?: "") }

    // DYNAMIC RE-SYNC: Ensures when editing an InventoryMaster, all fields pre-fill instantly when masterList emits!
    LaunchedEffect(initialMaster) {
        initialMaster?.let { master ->
            category = master.category
            name = master.name
            manufacturer = master.manufacturer
            majorAmountStr = master.containerSizeAmount.toString()
            majorUnit = master.containerSizeUnit
            minorYieldStr = master.innerUnitYieldTotal.toString()
            minorUnit = master.innerItemUnit
            notes = master.notes
        }
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }
    var mfgExpanded by remember { mutableStateOf(false) }
    var majorUnitExpanded by remember { mutableStateOf(false) }
    var minorUnitExpanded by remember { mutableStateOf(false) }

    // New Manufacturer Input Dialog State
    var showNewMfgDialog by remember { mutableStateOf(false) }
    var newMfgInputText by remember { mutableStateOf("") }
    val baseManufacturers = remember { mutableStateListOf("Ilford", "Kodak", "Ansell", "Adox", "Foma", "CineStill", "Paterson", "AP", "Bergger", "Arista") }

    val categories = if (categoriesList.isNotEmpty()) categoriesList.map { it.name } else listOf("Chemistry", "Paper", "Film Stock", "Safety Gear", "Supplies", "Unassigned")

    // Category-Specific Major Units
    val majorUnits = when (category) {
        "Paper" -> listOf("box", "pack", "sheets")
        "Chemistry" -> listOf("L", "mL", "bottle", "gal", "pt")
        "Film Stock" -> listOf("box", "pack", "roll")
        "Safety Gear" -> listOf("box", "pair", "pack")
        else -> listOf("pack", "box", "unit", "roll")
    }

    // Derivative Minor Units for the Selected Major Unit (Strict Diminutives - No Mixing Metric & Imperial!)
    val allowedMinorUnits = when (majorUnit) {
        "L" -> listOf("mL")
        "gal" -> listOf("fl oz")
        "pt" -> listOf("fl oz")
        "mL" -> listOf("mL")
        "box", "pack" -> when (category) {
            "Paper" -> listOf("sheets")
            "Chemistry" -> listOf("mL", "fl oz")
            "Film Stock" -> listOf("rolls")
            "Safety Gear" -> listOf("items", "pairs")
            else -> listOf("units", "items")
        }
        "bottle" -> listOf("mL", "fl oz")
        "pair" -> listOf("items")
        "roll" -> listOf("frames", "ft", "m")
        else -> listOf("units", "sheets", "mL", "rolls")
    }

    // Helper to auto-calculate derivative minor quantity & unit when major unit changes
    fun onMajorUnitChanged(newMajorUnit: String) {
        majorUnit = newMajorUnit
        val majorAmt = majorAmountStr.toFloatOrNull() ?: 1.0f
        when (newMajorUnit) {
            "L" -> {
                minorUnit = "mL"
                minorYieldStr = (majorAmt * 1000f).toInt().toString()
            }
            "gal" -> {
                minorUnit = "fl oz"
                minorYieldStr = (majorAmt * 128f).toInt().toString()
            }
            "pt" -> {
                minorUnit = "fl oz"
                minorYieldStr = (majorAmt * 16f).toInt().toString()
            }
            "box", "pack" -> {
                if (category == "Paper") {
                    minorUnit = "sheets"
                    minorYieldStr = "25"
                } else if (category == "Film Stock") {
                    minorUnit = "rolls"
                    minorYieldStr = "10"
                } else if (category == "Chemistry") {
                    minorUnit = "mL"
                    minorYieldStr = "1000"
                } else {
                    minorUnit = "items"
                    minorYieldStr = "100"
                }
            }
            "bottle" -> {
                minorUnit = "mL"
                minorYieldStr = "1000"
            }
            else -> {
                minorUnit = allowedMinorUnits.firstOrNull() ?: "units"
            }
        }
    }

    fun buildMaster(): InventoryMaster {
        val amt = majorAmountStr.toFloatOrNull() ?: 1.0f
        val yieldTotal = minorYieldStr.toIntOrNull() ?: 1
        val displaySizeStr = if (yieldTotal > 1 && majorUnit != minorUnit) {
            "${amt.toInt()} $majorUnit ($yieldTotal $minorUnit)"
        } else {
            "${amt.toInt()} $majorUnit"
        }

        return InventoryMaster(
            id = initialMaster?.id ?: 0,
            category = category,
            name = name,
            manufacturer = manufacturer,
            containerSizeAmount = amt,
            containerSizeUnit = majorUnit,
            innerUnitYieldTotal = yieldTotal,
            innerItemUnit = minorUnit,
            containerSize = displaySizeStr,
            notes = notes
        )
    }

    // Manufacturer List with FilmPresets integration
    val mfgList = remember(category, baseManufacturers) {
        if (category == "Film Stock") {
            (FilmPresets.manufacturers + baseManufacturers).distinct().sorted()
        } else {
            baseManufacturers
        }
    }

    val customNamesForCategory = itemNamesList.filter { it.category.equals(category, ignoreCase = true) }.map { it.name }
    val defaultPresets = when (category) {
        "Chemistry" -> listOf("Rapid Fixer", "Multigrade Developer", "ID-11", "D-76", "Rodinal / R09", "Indicator Stop Bath", "Ilfotol Wetting Agent")
        "Paper" -> listOf("Multigrade RC Deluxe Satin 8x10", "Multigrade RC Deluxe Pearl 8x10", "Multigrade FB Glossy 8x10", "Fomabrom Variant FB 8x10", "Adox MCC FB 8x10")
        "Film Stock" -> {
            if (manufacturer.isNotBlank()) {
                FilmPresets.getFilmsForManufacturer(manufacturer).map { it.name }
            } else {
                FilmPresets.presets.map { it.displayName }
            }
        }
        "Safety Gear" -> listOf("Nitrile Gloves XL", "Safety Goggles", "Chemical Apron", "Respirator Mask")
        else -> listOf("Negative Storage Sleeves", "Graduated Cylinder 1L", "Print Tongs", "Squeegee")
    }
    val itemPresets = (customNamesForCategory + defaultPresets).distinct()

    val filteredMfgList = remember(mfgList, manufacturer) {
        if (manufacturer.isBlank()) mfgList else mfgList.filter { it.contains(manufacturer, ignoreCase = true) }
    }

    val filteredItemPresets = remember(itemPresets, name) {
        if (name.isBlank()) itemPresets else itemPresets.filter { it.contains(name, ignoreCase = true) }
    }

    // PURE DARKROOM SAFELIGHT RED COLORS
    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary
    val safelightBtnBg = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary
    val safelightBtnFg = if (isSafelightActive) Color(0xFFFF0000) else Color.White

    val textFieldColors = if (isSafelightActive) {
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFFF0000),
            unfocusedTextColor = Color(0xFFFF0000),
            focusedBorderColor = Color(0xFFFF0000),
            unfocusedBorderColor = Color(0xFF660000),
            focusedLabelColor = Color(0xFFFF0000),
            unfocusedLabelColor = Color(0xFFCC0000)
        )
    } else {
        OutlinedTextFieldDefaults.colors()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                title = {
                    Text(
                        text = if (initialMaster == null) "Add Inventory Item" else "Edit Inventory Item",
                        style = MaterialTheme.typography.titleLarge,
                        color = safelightFg
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back to Lab",
                            tint = safelightPrimary
                        )
                    }
                },
                actions = {
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
            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    colors = textFieldColors,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    categories.forEach { catName ->
                        DropdownMenuItem(
                            text = { Text(catName, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                category = catName
                                categoryExpanded = false
                                name = "" // RESET item name on category switch so no stale paper entries linger under chemistry!
                                if (catName == "Paper" || catName == "Film Stock") {
                                    onMajorUnitChanged("box")
                                } else if (catName == "Chemistry") {
                                    onMajorUnitChanged("L")
                                }
                            }
                        )
                    }
                }
            }

            // Manufacturer Dropdown FIRST (Choose Manufacturer / Brand First!)
            ExposedDropdownMenuBox(
                expanded = mfgExpanded,
                onExpandedChange = { mfgExpanded = !mfgExpanded }
            ) {
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = {
                        manufacturer = it
                        if (category == "Film Stock") name = "" // Filter film stock options when manufacturer changes
                    },
                    label = { Text("Brand / Manufacturer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mfgExpanded) },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mfgExpanded,
                    onDismissRequest = { mfgExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    filteredMfgList.forEach { mfg ->
                        DropdownMenuItem(
                            text = { Text(mfg, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                manufacturer = mfg
                                mfgExpanded = false
                                if (category == "Film Stock") name = "" // Filter film stock options when manufacturer is selected
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("+ Add New Manufacturer...", fontWeight = FontWeight.Bold, color = safelightPrimary) },
                        onClick = {
                            mfgExpanded = false
                            newMfgInputText = ""
                            showNewMfgDialog = true
                        }
                    )
                }
            }

            // Item Name Dropdown / Input SECOND (Filtered by Selected Manufacturer!)
            ExposedDropdownMenuBox(
                expanded = nameExpanded,
                onExpandedChange = { nameExpanded = !nameExpanded }
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (category == "Film Stock") "Film Stock Name" else "Item Name") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded) },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = nameExpanded,
                    onDismissRequest = { nameExpanded = false },
                    containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                ) {
                    filteredItemPresets.forEach { preset ->
                        DropdownMenuItem(
                            text = { Text(preset, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                            onClick = {
                                name = preset
                                nameExpanded = false
                            }
                        )
                    }
                }
            }

            // Major Quantity + Major Unit Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = majorAmountStr,
                    onValueChange = {
                        majorAmountStr = it
                        onMajorUnitChanged(majorUnit)
                    },
                    label = { Text("Major Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = majorUnitExpanded,
                    onExpandedChange = { majorUnitExpanded = !majorUnitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = majorUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Major Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = majorUnitExpanded) },
                        colors = textFieldColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = majorUnitExpanded,
                        onDismissRequest = { majorUnitExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        majorUnits.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    onMajorUnitChanged(u)
                                    majorUnitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Minor Quantity + Derivative Minor Unit (Strictly Diminutive!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = minorYieldStr,
                    onValueChange = { minorYieldStr = it },
                    label = { Text("Minor Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.weight(1.2f)
                )

                ExposedDropdownMenuBox(
                    expanded = minorUnitExpanded,
                    onExpandedChange = { minorUnitExpanded = !minorUnitExpanded },
                    modifier = Modifier.weight(0.8f)
                ) {
                    OutlinedTextField(
                        value = minorUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Minor Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minorUnitExpanded) },
                        colors = textFieldColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = minorUnitExpanded,
                        onDismissRequest = { minorUnitExpanded = false },
                        containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        allowedMinorUnits.forEach { mu ->
                            DropdownMenuItem(
                                text = { Text(mu, color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified) },
                                onClick = {
                                    minorUnit = mu
                                    minorUnitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val master = buildMaster()
                    if (initialMaster != null) {
                        viewModel.updateMaster(master)
                    } else {
                        viewModel.addMaster(master)
                    }
                    onSuccess()
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = safelightBtnBg, contentColor = safelightBtnFg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = safelightBtnFg)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Inventory Item", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightBtnFg)
            }

            OutlinedButton(
                onClick = {
                    val master = buildMaster()
                    scope.launch {
                        val savedId = if (initialMaster != null) {
                            viewModel.updateMaster(master)
                            initialMaster.id
                        } else {
                            viewModel.addMasterAndGetId(master)
                        }
                        if (onSaveAndAddStock != null) {
                            onSaveAndAddStock(savedId)
                        } else {
                            onSuccess()
                        }
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = safelightPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save & Add Physical Stock", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = safelightPrimary)
            }
        }
    }

    // INLINE NEW MANUFACTURER ADD DIALOG (Case-Insensitive Deduplication)
    if (showNewMfgDialog) {
        AlertDialog(
            onDismissRequest = { showNewMfgDialog = false },
            containerColor = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceContainerHigh,
            titleContentColor = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface,
            textContentColor = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text("Add New Manufacturer", color = if (isSafelightActive) Color(0xFFFF0000) else Color.Unspecified, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter brand or manufacturer name:", style = MaterialTheme.typography.bodyMedium, color = if (isSafelightActive) Color(0xFFFF3333) else Color.Unspecified)
                    OutlinedTextField(
                        value = newMfgInputText,
                        onValueChange = { newMfgInputText = it },
                        label = { Text("Manufacturer Name") },
                        placeholder = { Text("e.g. Bergger, Arista, Bellini") },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newMfgInputText.trim()
                        if (trimmed.isNotBlank()) {
                            val existing = baseManufacturers.find { it.equals(trimmed, ignoreCase = true) }
                            if (existing != null) {
                                manufacturer = existing
                            } else {
                                baseManufacturers.add(trimmed)
                                manufacturer = trimmed
                            }
                        }
                        showNewMfgDialog = false
                    },
                    enabled = newMfgInputText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightActive) Color(0xFF660000) else MaterialTheme.colorScheme.primary)
                ) {
                    Text("Add Manufacturer", color = if (isSafelightActive) Color(0xFFFF0000) else Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewMfgDialog = false }) {
                    Text("Cancel", color = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}
