package com.fotologue.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@kotlinx.serialization.Serializable
data class MasterPresetItem(
    val id: String,
    val category: String,
    val subCategory: String? = null,
    val brand: String,
    val name: String,
    val modelNumber: String? = null,
    val attributes: Map<String, String> = emptyMap(),
    val isDeveloperPreset: Boolean = true
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fotologue Master Preset Authoring Tool (Maintainer Edition)"
    ) {
        MaintainerAppScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MaintainerAppScreen() {
    val json = remember { Json { prettyPrint = true; ignoreUnknownKeys = true } }
    var selectedCategory by remember { mutableStateOf("CAMERA") }
    val categories = listOf("CAMERA", "LENS", "FILM", "TELECONVERTER", "FILTER", "FLASH", "VIEWFINDER", "CONSUMABLE", "EQUIPMENT", "SUPPLIER")

    val presetsDir = remember {
        var currentDir = File(System.getProperty("user.dir"))
        while (currentDir.exists() && !File(currentDir, "settings.gradle.kts").exists()) {
            val parent = currentDir.parentFile ?: break
            currentDir = parent
        }
        File(currentDir, "core/data/src/main/resources/presets")
    }

    var searchQuery by remember { mutableStateOf("") }
    var activePresets by remember { mutableStateOf<List<MasterPresetItem>>(emptyList()) }
    var feedbackMessage by remember { mutableStateOf("Ready to edit Maintainer Preset Catalogue.") }

    val canonicalBrands = remember {
        listOf(
            "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
            "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
            "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
            "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica"
        )
    }

    val activeCategoryBrands = remember(activePresets) {
        val jsonBrands = activePresets.map { it.brand }.distinct()
        (canonicalBrands + jsonBrands).filter { it.isNotBlank() }.distinct().sorted()
    }

    val categoryFiles = mapOf(
        "CAMERA" to "cameras.json",
        "LENS" to "lenses.json",
        "FILM" to "films.json",
        "TELECONVERTER" to "teleconverters.json",
        "FILTER" to "filters.json",
        "FLASH" to "flashes.json",
        "VIEWFINDER" to "viewfinders.json",
        "CONSUMABLE" to "consumables.json",
        "EQUIPMENT" to "lab_equipment.json",
        "SUPPLIER" to "suppliers.json"
    )

    fun getCategoryFile(cat: String): File {
        val fileName = categoryFiles[cat] ?: "${cat.lowercase()}s.json"
        return File(presetsDir, fileName)
    }

    fun loadCategoryPresets(cat: String) {
        val file = getCategoryFile(cat)
        if (file.exists()) {
            try {
                activePresets = json.decodeFromString(file.readText())
            } catch (_: Exception) {
                activePresets = emptyList()
            }
        } else {
            activePresets = emptyList()
        }
    }

    LaunchedEffect(selectedCategory) {
        loadCategoryPresets(selectedCategory)
    }

    fun saveCategoryPresets() {
        if (!presetsDir.exists()) presetsDir.mkdirs()
        val file = getCategoryFile(selectedCategory)
        val jsonText = json.encodeToString(activePresets)
        file.writeText(jsonText)
        feedbackMessage = "✅ Saved ${activePresets.size} presets to ${file.name}"
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPresetItem by remember { mutableStateOf<MasterPresetItem?>(null) }

    var newBrand by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newModelNumber by remember { mutableStateOf("") }
    var newAttr1Key by remember { mutableStateOf("type") }
    var newAttr1Value by remember { mutableStateOf("") }
    var newAttr2Key by remember { mutableStateOf("mount") }
    var newAttr2Value by remember { mutableStateOf("") }

    // Dialog for ADDING or EDITING a Preset Entry
    val isEditing = editingPresetItem != null
    if (showAddDialog || isEditing) {
        var brandDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingPresetItem = null
            },
            title = { Text(if (isEditing) "✏️ Edit $selectedCategory Preset Entry" else "➕ Add New $selectedCategory Preset Entry", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Controlled Brand Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (isEditing) (editingPresetItem?.brand ?: "") else newBrand,
                            onValueChange = { input ->
                                if (isEditing) {
                                    editingPresetItem = editingPresetItem?.copy(brand = input)
                                } else {
                                    newBrand = input
                                }
                            },
                            label = { Text("Brand") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { brandDropdownExpanded = !brandDropdownExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = brandDropdownExpanded,
                            onDismissRequest = { brandDropdownExpanded = false }
                        ) {
                            activeCategoryBrands.forEach { bOpt ->
                                DropdownMenuItem(
                                    text = { Text(bOpt) },
                                    onClick = {
                                        if (isEditing) {
                                            editingPresetItem = editingPresetItem?.copy(brand = bOpt)
                                        } else {
                                            newBrand = bOpt
                                        }
                                        brandDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = if (isEditing) (editingPresetItem?.name ?: "") else newName,
                        onValueChange = { input ->
                            if (isEditing) {
                                editingPresetItem = editingPresetItem?.copy(name = input)
                            } else {
                                newName = input
                            }
                        },
                        label = { Text("Preset Name (e.g. Pentax LX)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = if (isEditing) (editingPresetItem?.modelNumber ?: "") else newModelNumber,
                        onValueChange = { input ->
                            if (isEditing) {
                                editingPresetItem = editingPresetItem?.copy(modelNumber = input)
                            } else {
                                newModelNumber = input
                            }
                        },
                        label = { Text("Model Number (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAttr1Key,
                            onValueChange = { newAttr1Key = it },
                            label = { Text("Attribute 1 Key") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = if (isEditing) (editingPresetItem?.attributes?.get(newAttr1Key) ?: "") else newAttr1Value,
                            onValueChange = { input ->
                                if (isEditing && editingPresetItem != null) {
                                    val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                    currentAttrs[newAttr1Key] = input
                                    editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                } else {
                                    newAttr1Value = input
                                }
                            },
                            label = { Text("Attribute 1 Value") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAttr2Key,
                            onValueChange = { newAttr2Key = it },
                            label = { Text("Attribute 2 Key") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = if (isEditing) (editingPresetItem?.attributes?.get(newAttr2Key) ?: "") else newAttr2Value,
                            onValueChange = { input ->
                                if (isEditing && editingPresetItem != null) {
                                    val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                    currentAttrs[newAttr2Key] = input
                                    editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                } else {
                                    newAttr2Value = input
                                }
                            },
                            label = { Text("Attribute 2 Value") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isEditing && editingPresetItem != null) {
                            val updated = editingPresetItem!!
                            activePresets = activePresets.map { if (it.id == updated.id) updated else it }
                            saveCategoryPresets()
                            editingPresetItem = null
                        } else if (newName.isNotBlank() && newBrand.isNotBlank()) {
                            val generatedId = "${selectedCategory.lowercase().take(3)}_${newBrand.lowercase().replace(" ", "_")}_${newName.lowercase().replace(" ", "_").replace("/", "_")}"
                            val attrs = mutableMapOf<String, String>()
                            if (newAttr1Key.isNotBlank() && newAttr1Value.isNotBlank()) attrs[newAttr1Key] = newAttr1Value
                            if (newAttr2Key.isNotBlank() && newAttr2Value.isNotBlank()) attrs[newAttr2Key] = newAttr2Value

                            val item = MasterPresetItem(
                                id = generatedId,
                                category = selectedCategory,
                                brand = newBrand.trim(),
                                name = newName.trim(),
                                modelNumber = newModelNumber.trim().ifBlank { null },
                                attributes = attrs
                            )
                            activePresets = activePresets + item
                            saveCategoryPresets()
                            showAddDialog = false
                            newBrand = ""
                            newName = ""
                            newModelNumber = ""
                            newAttr1Value = ""
                            newAttr2Value = ""
                        }
                    }
                ) {
                    Text(if (isEditing) "Save Changes" else "Add Preset Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingPresetItem = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "🛠️ FOTOLOGUE MASTER PRESET AUTHORING TOOL",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Maintainer-grade authoring tool to create, edit, validate, and format master JSON preset files.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Category Filter Bar
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Filter Presets") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New $selectedCategory Preset Entry")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { saveCategoryPresets() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save to JSON File")
                }
            }

            Text(feedbackMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            HorizontalDivider()

            val filteredList = activePresets.filter {
                searchQuery.isBlank() || it.name.contains(searchQuery, true) || it.brand.contains(searchQuery, true)
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No $selectedCategory presets found in JSON file.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList.size) { idx ->
                        val preset = filteredList[idx]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${preset.brand} ${preset.name}", fontWeight = FontWeight.Bold)
                                    Text("ID: ${preset.id} • Attrs: ${preset.attributes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = {
                                        editingPresetItem = preset
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Preset", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        activePresets = activePresets.filter { it.id != preset.id }
                                        saveCategoryPresets()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Preset", tint = MaterialTheme.colorScheme.error)
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
