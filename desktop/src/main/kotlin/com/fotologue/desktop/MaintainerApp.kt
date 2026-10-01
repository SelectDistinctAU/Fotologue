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
        val userDir = System.getProperty("user.dir")
        File(userDir, "core/data/src/main/resources/presets")
    }

    var searchQuery by remember { mutableStateOf("") }
    var activePresets by remember { mutableStateOf<List<MasterPresetItem>>(emptyList()) }
    var feedbackMessage by remember { mutableStateOf("Ready to edit Maintainer Preset Catalogue.") }

    fun loadCategoryPresets(cat: String) {
        val file = File(presetsDir, "${cat.lowercase()}s.json")
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
        val file = File(presetsDir, "${selectedCategory.lowercase()}s.json")
        val jsonText = json.encodeToString(activePresets)
        file.writeText(jsonText)
        feedbackMessage = "✅ Saved ${activePresets.size} presets to ${file.name}"
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newBrand by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newModelNumber by remember { mutableStateOf("") }
    var newAttr1Key by remember { mutableStateOf("type") }
    var newAttr1Value by remember { mutableStateOf("") }
    var newAttr2Key by remember { mutableStateOf("mount") }
    var newAttr2Value by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("➕ Add $selectedCategory Preset", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newBrand,
                        onValueChange = { newBrand = it },
                        label = { Text("Brand / Manufacturer (e.g. Pentax, Kodak)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Preset Name (e.g. Pentax LX)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newModelNumber,
                        onValueChange = { newModelNumber = it },
                        label = { Text("Model Number (e.g. LX)") },
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
                            value = newAttr1Value,
                            onValueChange = { newAttr1Value = it },
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
                            value = newAttr2Value,
                            onValueChange = { newAttr2Value = it },
                            label = { Text("Attribute 2 Value") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newBrand.isNotBlank()) {
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
                    Text("Add Preset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
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
                    Text("Add $selectedCategory Preset")
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
