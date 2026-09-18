package com.fotologue.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.data.lab.LabCategory
import com.fotologue.core.data.lab.LabItemName
import com.fotologue.core.data.lab.LabUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInventoryMasterDialog(
    initialMaster: InventoryMaster? = null,
    categoriesList: List<LabCategory> = emptyList(),
    unitsList: List<LabUnit> = emptyList(),
    itemNamesList: List<LabItemName> = emptyList(),
    isSafelightMode: Boolean = false,
    textColor: Color,
    cardColor: Color,
    onDismiss: () -> Unit,
    onSave: (InventoryMaster) -> Unit
) {
    var category by remember { mutableStateOf(initialMaster?.category ?: "Chemistry") }
    var name by remember { mutableStateOf(initialMaster?.name ?: "") }
    var manufacturer by remember { mutableStateOf(initialMaster?.manufacturer ?: "Ilford") }
    var containerAmountStr by remember { mutableStateOf((initialMaster?.containerSizeAmount ?: if (category == "Paper") 25f else 1f).toString()) }
    var containerUnit by remember { mutableStateOf(initialMaster?.containerSizeUnit ?: if (category == "Paper") "sheets" else "L") }
    var notes by remember { mutableStateOf(initialMaster?.notes ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var nameExpanded by remember { mutableStateOf(false) }
    var mfgExpanded by remember { mutableStateOf(false) }

    val categories = if (categoriesList.isNotEmpty()) categoriesList.map { it.name } else listOf("Chemistry", "Paper", "Safety Gear", "Supplies")
    
    val units = when (category) {
        "Paper" -> (listOf("sheets", "box", "pack") + unitsList.map { it.name }).distinct()
        "Chemistry" -> (listOf("L", "mL", "fl oz", "gal") + unitsList.map { it.name }).distinct()
        "Safety Gear" -> (listOf("box", "pair", "pack") + unitsList.map { it.name }).distinct()
        else -> (listOf("unit", "pack", "box") + unitsList.map { it.name }).distinct()
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = { Text(if (initialMaster == null) "Add Item Specification" else "Edit Item Specification", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category Exposed Dropdown Menu
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor),
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
                                    val matchedCat = categoriesList.find { it.name == catName }
                                    if (matchedCat != null) {
                                        containerUnit = matchedCat.defaultUnit
                                    } else if (catName == "Paper") {
                                        containerAmountStr = "25"
                                        containerUnit = "sheets"
                                    } else if (catName == "Chemistry") {
                                        containerAmountStr = "1"
                                        containerUnit = "L"
                                    }
                                }
                            )
                        }
                    }
                }

                // Item Name (Editable Pick List Dropdown)
                ExposedDropdownMenuBox(
                    expanded = nameExpanded,
                    onExpandedChange = { nameExpanded = !nameExpanded }
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
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

                // Manufacturer (Editable Pick List Dropdown)
                ExposedDropdownMenuBox(
                    expanded = mfgExpanded,
                    onExpandedChange = { mfgExpanded = !mfgExpanded }
                ) {
                    OutlinedTextField(
                        value = manufacturer,
                        onValueChange = { manufacturer = it },
                        label = { Text("Manufacturer", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mfgExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
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

                // Container Numeric Amount + Unit Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = containerAmountStr,
                        onValueChange = { containerAmountStr = it },
                        label = { Text("Container Size / Quantity", color = textColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Unit Choice Chips
                Text("Unit:", fontSize = 12.sp, color = textColor)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    units.take(6).forEach { u ->
                        FilterChip(
                            selected = containerUnit == u,
                            onClick = { containerUnit = u },
                            label = { Text(u, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isSafelightMode) Color(0xFF880000) else MaterialTheme.colorScheme.primary,
                                selectedLabelColor = if (isSafelightMode) Color(0xFFFF0000) else Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = textColor, unfocusedBorderColor = textColor.copy(alpha = 0.5f), focusedLabelColor = textColor, cursorColor = textColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = containerAmountStr.toFloatOrNull() ?: 1.0f
                    val displaySizeStr = if (amt % 1.0f == 0.0f) "${amt.toInt()} $containerUnit" else "$amt $containerUnit"
                    val master = InventoryMaster(
                        id = initialMaster?.id ?: 0,
                        category = category,
                        name = name,
                        manufacturer = manufacturer,
                        containerSizeAmount = amt,
                        containerSizeUnit = containerUnit,
                        containerSize = displaySizeStr,
                        notes = notes
                    )
                    onSave(master)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = if (isSafelightMode) Color(0xFF880000) else MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Item Specification", color = if (isSafelightMode) Color(0xFFFF0000) else Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor)
            }
        }
    )
}
