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
import androidx.compose.material3.Button
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

    val initialMaster = masterList.find { it.id == masterId }

    // BLANK DEFAULTS FOR ADD MODE (initialMaster == null)
    var category by remember { mutableStateOf(initialMaster?.category ?: defaultCategory ?: "Chemistry") }
    var name by remember { mutableStateOf(initialMaster?.name ?: "") }
    var manufacturer by remember { mutableStateOf(initialMaster?.manufacturer ?: "") }
    var majorAmountStr by remember { mutableStateOf((initialMaster?.containerSizeAmount ?: 1f).toString()) }
    var majorUnit by remember { mutableStateOf(initialMaster?.containerSizeUnit ?: if (category == "Paper") "box" else "L") }
    var minorYieldStr by remember { mutableStateOf((initialMaster?.innerUnitYieldTotal ?: if (category == "Paper") 25 else 1000).toString()) }
    var minorUnit by remember { mutableStateOf(initialMaster?.innerItemUnit ?: if (category == "Paper") "sheets" else "mL") }
    var notes by remember { mutableStateOf(initialMaster?.notes ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }
    var mfgExpanded by remember { mutableStateOf(false) }
    var majorUnitExpanded by remember { mutableStateOf(false) }
    var minorUnitExpanded by remember { mutableStateOf(false) }

    val categories = if (categoriesList.isNotEmpty()) categoriesList.map { it.name } else listOf("Chemistry", "Paper", "Safety Gear", "Supplies", "Unassigned")

    // Category-Specific Major Units
    val majorUnits = when (category) {
        "Paper" -> listOf("box", "pack", "sheets")
        "Chemistry" -> listOf("L", "mL", "bottle", "gal", "pt")
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
            "Safety Gear" -> listOf("items", "pairs")
            else -> listOf("units", "items")
        }
        "bottle" -> listOf("mL", "fl oz")
        "pair" -> listOf("items")
        "roll" -> listOf("frames", "ft", "m")
        else -> listOf("units", "sheets", "mL")
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

    val customNamesForCategory = itemNamesList.filter { it.category.equals(category, ignoreCase = true) }.map { it.name }
    val defaultPresets = when (category) {
        "Chemistry" -> listOf("Rapid Fixer", "Multigrade Developer", "ID-11", "D-76", "Rodinal / R09", "Indicator Stop Bath", "Ilfotol Wetting Agent")
        "Paper" -> listOf("Multigrade RC Deluxe Satin 8x10", "Multigrade RC Deluxe Pearl 8x10", "Multigrade FB Glossy 8x10", "Fomabrom Variant FB 8x10", "Adox MCC FB 8x10")
        "Safety Gear" -> listOf("Nitrile Gloves XL", "Safety Goggles", "Chemical Apron", "Respirator Mask")
        else -> listOf("Negative Storage Sleeves", "Graduated Cylinder 1L", "Print Tongs", "Squeegee")
    }
    val itemPresets = (customNamesForCategory + defaultPresets).distinct()
    val manufacturers = listOf("Ilford", "Kodak", "Ansell", "Adox", "Foma", "CineStill", "Paterson", "AP")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (initialMaster == null) "Add Inventory Item" else "Edit Inventory Item",
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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { catName ->
                        DropdownMenuItem(
                            text = { Text(catName) },
                            onClick = {
                                category = catName
                                categoryExpanded = false
                                name = "" // RESET item name on category switch so no stale paper entries linger under chemistry!
                                if (catName == "Paper") {
                                    onMajorUnitChanged("box")
                                } else if (catName == "Chemistry") {
                                    onMajorUnitChanged("L")
                                }
                            }
                        )
                    }
                }
            }

            // Item Name Dropdown / Input
            ExposedDropdownMenuBox(
                expanded = nameExpanded,
                onExpandedChange = { nameExpanded = !nameExpanded }
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g. Rapid Fixer or Multigrade RC Deluxe 8x10") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded) },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = nameExpanded,
                    onDismissRequest = { nameExpanded = false }
                ) {
                    itemPresets.forEach { preset ->
                        DropdownMenuItem(
                            text = { Text(preset) },
                            onClick = {
                                name = preset
                                nameExpanded = false
                            }
                        )
                    }
                }
            }

            // Manufacturer Dropdown
            ExposedDropdownMenuBox(
                expanded = mfgExpanded,
                onExpandedChange = { mfgExpanded = !mfgExpanded }
            ) {
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it },
                    label = { Text("Manufacturer") },
                    placeholder = { Text("e.g. Ilford, Kodak, Ansell") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mfgExpanded) },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mfgExpanded,
                    onDismissRequest = { mfgExpanded = false }
                ) {
                    manufacturers.forEach { mfg ->
                        DropdownMenuItem(
                            text = { Text(mfg) },
                            onClick = {
                                manufacturer = mfg
                                mfgExpanded = false
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = majorUnitExpanded,
                        onDismissRequest = { majorUnitExpanded = false }
                    ) {
                        majorUnits.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = minorUnitExpanded,
                        onDismissRequest = { minorUnitExpanded = false }
                    ) {
                        allowedMinorUnits.forEach { mu ->
                            DropdownMenuItem(
                                text = { Text(mu) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Inventory Item", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save & Add Physical Stock", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
