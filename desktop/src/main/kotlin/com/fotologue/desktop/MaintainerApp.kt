package com.fotologue.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var sortColumn by remember { mutableStateOf("Brand") }
    var sortAscending by remember { mutableStateOf(true) }

    fun toggleSort(col: String) {
        if (sortColumn == col) {
            sortAscending = !sortAscending
        } else {
            sortColumn = col
            sortAscending = true
        }
    }
    var filterBrand by remember { mutableStateOf("All") }
    var filterType by remember { mutableStateOf("All") }
    var filterMount by remember { mutableStateOf("All") }

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

    val cameraTypes = listOf("Film SLR", "Rangefinder", "TLR", "Medium Format SLR", "Point & Shoot", "Compact", "Large Format", "DSLR", "Mirrorless")
    val lensTypes = listOf("Prime", "Zoom", "Macro", "Anamorphic", "Tilt-Shift")
    val filmTypes = listOf("Colour Negative (C-41)", "Black & White Negative (B&W)", "Colour Slide / Reversal (E-6)", "B&W Slide", "Infrared")
    val viewfinderTypes = listOf("Eye-Level Prism", "Waist-Level Finder", "Action Finder", "Right-Angle Finder", "Coldshoe Auxiliary Finder")
    val flashTypes = listOf("Shoe Mount / Speedlight", "Built-in / Pop-up Flash", "Handle Mount / Potato Masher", "Ring Flash", "Studio / Strobe")
    val consumableTypes = listOf("Developer (Film)", "Developer (Paper)", "Stop Bath", "Fixer", "Wetting Agent", "Bleach / Toner", "Darkroom Paper", "Chemical Cleaner")
    val consumableBrands = listOf("Agfa / Adox", "Bellini", "Bergger", "Champion", "Cinestill", "Foma", "Fujifilm", "Ilford", "Jobo", "Kodak", "Paterson", "Photographers' Formulary", "Tetenal")
    val equipmentTypes = listOf("Enlarger", "Dev Tank", "Film Reel", "Developing Tray", "Measuring Cylinder / Beaker", "Film Squeegee", "Darkroom Timer / Exposure Timer", "Safelight", "Thermometer", "Film Changing Bag", "Graduated Pitcher", "Focus Finder / Grain Focus Scope")
    val equipmentBrands = listOf("Adox", "AP", "Beseler", "Durst", "Hama", "Ilford", "Jobo", "Kaiser", "Kindermann", "LPL / Saunders", "Meopta", "Omega / Saunders", "Paterson", "Peak")
    val supplierTypes = listOf("Regional Supplier", "Online Retailer", "Film Processing Lab", "Repair Technician", "Equipment Rental")
    val supplierRegions = listOf("Global / International", "North America (USA/Canada)", "Europe & UK", "Australia & Oceania", "Asia-Pacific")
    val mountOptions = listOf("Canon FD", "Nikon F", "Pentax K", "Leica M", "Leica L39", "Hasselblad V", "Contax C/Y", "Olympus OM", "Minolta SR/MD", "M42 Thread", "Fixed", "Bay I", "Bay II", "Bay III")

    val filterCompensationStops = listOf("+0.0 Stops (Clear / UV)", "+0.3 Stops", "+0.6 Stops", "+1.0 Stop (Yellow K2)", "+1.5 Stops (CPL / Polarizer)", "+2.0 Stops (Orange 16)", "+3.0 Stops (Red 25A / ND8)", "+6.0 Stops (ND64)", "+10.0 Stops (ND1000)")

    val activeCategoryBrands = remember(activePresets, selectedCategory) {
        val baseList = when (selectedCategory) {
            "CONSUMABLE" -> consumableBrands
            "EQUIPMENT" -> equipmentBrands
            else -> canonicalBrands
        }
        val jsonBrands = activePresets.map { it.brand }.distinct()
        (baseList + jsonBrands).filter { it.isNotBlank() }.distinct().sorted()
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
        filterBrand = "All"
        filterType = "All"
        filterMount = "All"
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
        var attr1DropdownExpanded by remember { mutableStateOf(false) }
        var attr2DropdownExpanded by remember { mutableStateOf(false) }

        // Tailored Attribute Visibility & Default Keys per Category
        val showAttr1 = selectedCategory != "TELECONVERTER"
        val showAttr2 = selectedCategory == "CAMERA" || selectedCategory == "LENS" || selectedCategory == "TELECONVERTER" || selectedCategory == "SUPPLIER"

        val attr1KeyName = when (selectedCategory) {
            "CAMERA", "LENS", "FILM", "VIEWFINDER", "FLASH", "CONSUMABLE", "EQUIPMENT", "SUPPLIER" -> "type"
            "FILTER" -> "compensationStops"
            else -> newAttr1Key.ifBlank { "type" }
        }

        val attr2KeyName = when (selectedCategory) {
            "CAMERA", "LENS", "TELECONVERTER" -> "mount"
            "SUPPLIER" -> "region"
            else -> newAttr2Key.ifBlank { "region" }
        }

        val attr1ValueOptions = when (selectedCategory) {
            "CAMERA" -> cameraTypes
            "LENS" -> lensTypes
            "FILM" -> filmTypes
            "VIEWFINDER" -> viewfinderTypes
            "FILTER" -> filterCompensationStops
            "FLASH" -> flashTypes
            "CONSUMABLE" -> consumableTypes
            "EQUIPMENT" -> equipmentTypes
            "SUPPLIER" -> supplierTypes
            else -> emptyList()
        }

        val attr2ValueOptions = when (selectedCategory) {
            "CAMERA", "LENS", "TELECONVERTER" -> mountOptions
            "SUPPLIER" -> supplierRegions
            else -> emptyList()
        }

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
                    // Controlled Brand Dropdown (Hidden for SUPPLIER)
                    if (selectedCategory != "SUPPLIER") {
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
                    }

                    // Attribute 3 Row: Website URL for SUPPLIER
                    if (selectedCategory == "SUPPLIER") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "website",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attribute 3 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (isEditing) (editingPresetItem?.attributes?.get("website") ?: "") else "",
                                onValueChange = { input ->
                                    if (isEditing && editingPresetItem != null) {
                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                        currentAttrs["website"] = input
                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                    }
                                },
                                label = { Text("Website URL") },
                                placeholder = { Text("https://www.bhphotovideo.com") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = if (isEditing) (editingPresetItem?.name ?: "") else newName,
                        onValueChange = { input ->
                            if (isEditing) {
                                editingPresetItem = editingPresetItem?.copy(name = input, brand = if (selectedCategory == "SUPPLIER") input else editingPresetItem!!.brand)
                            } else {
                                newName = input
                                if (selectedCategory == "SUPPLIER") newBrand = input
                            }
                        },
                        label = { Text(if (selectedCategory == "SUPPLIER") "Vendor / Contact Name (e.g. B&H Photo, Vanbar Imaging)" else "Preset Name (e.g. Pentax LX)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (selectedCategory != "SUPPLIER") {
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
                    }

                    // Attribute 1 Row (Conditional)
                    if (showAttr1) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = attr1KeyName,
                                onValueChange = { newAttr1Key = it },
                                readOnly = attr1ValueOptions.isNotEmpty(),
                                label = { Text("Attribute 1 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = if (isEditing) (editingPresetItem?.attributes?.get(attr1KeyName) ?: "") else newAttr1Value,
                                    onValueChange = { input ->
                                        if (isEditing && editingPresetItem != null) {
                                            val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                            currentAttrs[attr1KeyName] = input
                                            editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                        } else {
                                            newAttr1Value = input
                                        }
                                    },
                                    label = { Text("Attribute 1 Value") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = if (attr1ValueOptions.isNotEmpty()) {
                                        {
                                            IconButton(onClick = { attr1DropdownExpanded = !attr1DropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    } else null
                                )
                                if (attr1ValueOptions.isNotEmpty()) {
                                    DropdownMenu(
                                        expanded = attr1DropdownExpanded,
                                        onDismissRequest = { attr1DropdownExpanded = false }
                                    ) {
                                        attr1ValueOptions.forEach { vOpt ->
                                            DropdownMenuItem(
                                                text = { Text(vOpt) },
                                                onClick = {
                                                    if (isEditing && editingPresetItem != null) {
                                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                                        currentAttrs[attr1KeyName] = vOpt
                                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                                    } else {
                                                        newAttr1Value = vOpt
                                                    }
                                                    attr1DropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Attribute 3 Row: Website URL for SUPPLIER
                    if (selectedCategory == "SUPPLIER") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "website",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attribute 3 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (isEditing) (editingPresetItem?.attributes?.get("website") ?: "") else "",
                                onValueChange = { input ->
                                    if (isEditing && editingPresetItem != null) {
                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                        currentAttrs["website"] = input
                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                    }
                                },
                                label = { Text("Website URL") },
                                placeholder = { Text("https://www.bhphotovideo.com") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Attribute 2 Row (Conditional)
                    if (showAttr2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = attr2KeyName,
                                onValueChange = { newAttr2Key = it },
                                readOnly = attr2ValueOptions.isNotEmpty(),
                                label = { Text("Attribute 2 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = if (isEditing) (editingPresetItem?.attributes?.get(attr2KeyName) ?: "") else newAttr2Value,
                                    onValueChange = { input ->
                                        if (isEditing && editingPresetItem != null) {
                                            val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                            currentAttrs[attr2KeyName] = input
                                            editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                        } else {
                                            newAttr2Value = input
                                        }
                                    },
                                    label = { Text("Attribute 2 Value") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = if (attr2ValueOptions.isNotEmpty()) {
                                        {
                                            IconButton(onClick = { attr2DropdownExpanded = !attr2DropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    } else null
                                )
                                if (attr2ValueOptions.isNotEmpty()) {
                                    DropdownMenu(
                                        expanded = attr2DropdownExpanded,
                                        onDismissRequest = { attr2DropdownExpanded = false }
                                    ) {
                                        attr2ValueOptions.forEach { vOpt ->
                                            DropdownMenuItem(
                                                text = { Text(vOpt) },
                                                onClick = {
                                                    if (isEditing && editingPresetItem != null) {
                                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                                        currentAttrs[attr2KeyName] = vOpt
                                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                                    } else {
                                                        newAttr2Value = vOpt
                                                    }
                                                    attr2DropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Attribute 3 Row: Website URL for SUPPLIER
                    if (selectedCategory == "SUPPLIER") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "website",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attribute 3 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (isEditing) (editingPresetItem?.attributes?.get("website") ?: "") else "",
                                onValueChange = { input ->
                                    if (isEditing && editingPresetItem != null) {
                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                        currentAttrs["website"] = input
                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                    }
                                },
                                label = { Text("Website URL") },
                                placeholder = { Text("https://www.bhphotovideo.com") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Attribute 3 Row: Fixed Lens Reference for Fixed-Lens Cameras
                    val currentTypeVal = if (isEditing) (editingPresetItem?.attributes?.get("type") ?: "") else newAttr1Value
                    val currentMountVal = if (isEditing) (editingPresetItem?.attributes?.get("mount") ?: "") else newAttr2Value
                    val isFixedLensCamera = selectedCategory == "CAMERA" && (currentMountVal == "Fixed" || currentTypeVal == "TLR" || currentTypeVal == "Compact" || currentTypeVal == "Point & Shoot")

                    if (isFixedLensCamera) {
                        var attr3DropdownExpanded by remember { mutableStateOf(false) }
                        val lensOptions = remember {
                            val lensFile = File(presetsDir, "lenses.json")
                            val loaded = if (lensFile.exists()) {
                                try { 
                                    json.decodeFromString<List<MasterPresetItem>>(lensFile.readText())
                                        .filter { it.attributes["mount"].equals("Fixed", ignoreCase = true) }
                                        .map { it.name } 
                                } catch (_: Exception) { emptyList() }
                            } else emptyList()
                            listOf("None") + loaded
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "fixedLens",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attribute 3 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = if (isEditing) (editingPresetItem?.attributes?.get("fixedLens") ?: "") else "",
                                    onValueChange = { input ->
                                        if (isEditing && editingPresetItem != null) {
                                            val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                            currentAttrs["fixedLens"] = input
                                            editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                        }
                                    },
                                    label = { Text("Attribute 3 Value (Referenced Lens)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { attr3DropdownExpanded = !attr3DropdownExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = attr3DropdownExpanded,
                                    onDismissRequest = { attr3DropdownExpanded = false }
                                ) {
                                    lensOptions.forEach { lOpt ->
                                        DropdownMenuItem(
                                            text = { Text(lOpt) },
                                            onClick = {
                                                if (isEditing && editingPresetItem != null) {
                                                    val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                                    if (lOpt == "None") currentAttrs.remove("fixedLens") else currentAttrs["fixedLens"] = lOpt
                                                    editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                                }
                                                attr3DropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Attribute 3 Row: Website URL for SUPPLIER
                    if (selectedCategory == "SUPPLIER") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "website",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Attribute 3 Key") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (isEditing) (editingPresetItem?.attributes?.get("website") ?: "") else "",
                                onValueChange = { input ->
                                    if (isEditing && editingPresetItem != null) {
                                        val currentAttrs = editingPresetItem!!.attributes.toMutableMap()
                                        currentAttrs["website"] = input
                                        editingPresetItem = editingPresetItem!!.copy(attributes = currentAttrs)
                                    }
                                },
                                label = { Text("Website URL") },
                                placeholder = { Text("https://www.bhphotovideo.com") },
                                modifier = Modifier.weight(1f)
                            )
                        }
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
                            if (showAttr1 && attr1KeyName.isNotBlank() && newAttr1Value.isNotBlank()) attrs[attr1KeyName] = newAttr1Value
                            if (showAttr2 && attr2KeyName.isNotBlank() && newAttr2Value.isNotBlank()) attrs[attr2KeyName] = newAttr2Value

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
            Text(
                "📂 Active Preset File Path: ${getCategoryFile(selectedCategory).absolutePath}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            // Streamlined Category Bar & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New $selectedCategory Preset Entry")
                    }
                    Button(
                        onClick = { saveCategoryPresets() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save to JSON File")
                    }
                }
            }

            val showBrandFilter = true
            val showTypeFilter = selectedCategory == "CAMERA" || selectedCategory == "LENS" || selectedCategory == "FILM" || selectedCategory == "VIEWFINDER" || selectedCategory == "CONSUMABLE" || selectedCategory == "FLASH"
            val showMountFilter = selectedCategory == "CAMERA" || selectedCategory == "LENS" || selectedCategory == "TELECONVERTER"

            val brandFilterOptions = remember(activePresets) {
                listOf("All") + activePresets.map { it.brand }.distinct().filter { it.isNotBlank() }.sorted()
            }

            val typeFilterOptions = remember(activePresets, selectedCategory) {
                val types = activePresets.mapNotNull { it.subCategory ?: it.attributes["type"] }.distinct().filter { it.isNotBlank() }.sorted()
                listOf("All") + types
            }

            val mountFilterOptions = remember(activePresets, selectedCategory) {
                val mounts = activePresets.mapNotNull { it.attributes["mount"] }.distinct().filter { it.isNotBlank() }.sorted()
                listOf("All") + mounts
            }

            val regionFilterOptions = remember(activePresets, selectedCategory) {
                val regions = activePresets.mapNotNull { it.attributes["region"] }.distinct().filter { it.isNotBlank() }.sorted()
                listOf("All") + regions
            }

            Text(feedbackMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            HorizontalDivider()

            val filteredList = activePresets.filter { preset ->
                val matchesBrand = filterBrand == "All" || preset.brand.equals(filterBrand, ignoreCase = true)
                val typeVal = preset.subCategory ?: preset.attributes["type"] ?: ""
                val matchesType = filterType == "All" || typeVal.equals(filterType, ignoreCase = true)
                val mountVal = preset.attributes["mount"] ?: ""
                val matchesMount = filterMount == "All" || mountVal.equals(filterMount, ignoreCase = true)
                val matchesSearch = searchQuery.isBlank() || preset.name.contains(searchQuery, true) || preset.brand.contains(searchQuery, true) || (preset.modelNumber?.contains(searchQuery, true) == true)

                matchesBrand && matchesType && matchesMount && matchesSearch
            }

            val sortedList = remember(filteredList, sortColumn, sortAscending) {
                val comp = when (sortColumn) {
                    "Brand" -> compareBy<MasterPresetItem> { it.brand }
                    "Name" -> compareBy { it.name }
                    "Type" -> compareBy { it.subCategory ?: it.attributes["type"] ?: "" }
                    "Mount" -> compareBy { it.attributes["mount"] ?: "" }
                    "ID" -> compareBy { it.id }
                    else -> compareBy { it.brand }
                }
                if (sortAscending) filteredList.sortedWith(comp) else filteredList.sortedWith(comp.reversed())
            }

            if (sortedList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No $selectedCategory presets found in JSON file.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Data Grid Header Row with Integrated Filter + Sort Dropdowns
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedCategory == "SUPPLIER") {
                                GridHeaderCell(
                                    title = "Vendor Name",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = emptyList(),
                                    activeFilter = "All",
                                    modifier = Modifier.weight(2.2f),
                                    onSelectSort = { toggleSort("Vendor Name") },
                                    onSelectFilter = {}
                                )
                                GridHeaderCell(
                                    title = "Supplier Type",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = typeFilterOptions,
                                    activeFilter = filterType,
                                    modifier = Modifier.weight(1.5f),
                                    onSelectSort = { toggleSort("Supplier Type") },
                                    onSelectFilter = { filterType = it }
                                )
                                GridHeaderCell(
                                    title = "Region",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = regionFilterOptions,
                                    activeFilter = filterMount,
                                    modifier = Modifier.weight(1.8f),
                                    onSelectSort = { toggleSort("Region") },
                                    onSelectFilter = { filterMount = it }
                                )
                                GridHeaderCell(
                                    title = "Website",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = emptyList(),
                                    activeFilter = "All",
                                    modifier = Modifier.weight(2.2f),
                                    onSelectSort = { toggleSort("Website") },
                                    onSelectFilter = {}
                                )
                                GridHeaderCell(
                                    title = "Preset ID",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = emptyList(),
                                    activeFilter = "All",
                                    modifier = Modifier.weight(1.5f),
                                    onSelectSort = { toggleSort("ID") },
                                    onSelectFilter = {}
                                )
                            } else {
                                GridHeaderCell(
                                    title = "Brand",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = brandFilterOptions,
                                    activeFilter = filterBrand,
                                    modifier = Modifier.weight(if (showMountFilter) 1.2f else 1.5f),
                                    onSelectSort = { toggleSort("Brand") },
                                    onSelectFilter = { filterBrand = it }
                                )
                                GridHeaderCell(
                                    title = "Preset Name",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = emptyList(),
                                    activeFilter = "All",
                                    modifier = Modifier.weight(if (showMountFilter) 2.5f else 3.2f),
                                    onSelectSort = { toggleSort("Name") },
                                    onSelectFilter = {}
                                )
                                GridHeaderCell(
                                    title = "Type",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = if (showTypeFilter) typeFilterOptions else emptyList(),
                                    activeFilter = filterType,
                                    modifier = Modifier.weight(if (showMountFilter) 1.5f else 1.8f),
                                    onSelectSort = { toggleSort("Type") },
                                    onSelectFilter = { filterType = it }
                                )
                                if (showMountFilter) {
                                    GridHeaderCell(
                                        title = "Mount / Spec",
                                        activeColumn = sortColumn,
                                        ascending = sortAscending,
                                        filterOptions = mountFilterOptions,
                                        activeFilter = filterMount,
                                        modifier = Modifier.weight(1.5f),
                                        onSelectSort = { toggleSort("Mount") },
                                        onSelectFilter = { filterMount = it }
                                    )
                                }
                                GridHeaderCell(
                                    title = "Preset ID",
                                    activeColumn = sortColumn,
                                    ascending = sortAscending,
                                    filterOptions = emptyList(),
                                    activeFilter = "All",
                                    modifier = Modifier.weight(1.8f),
                                    onSelectSort = { toggleSort("ID") },
                                    onSelectFilter = {}
                                )
                            }
                            Text(
                                "Actions",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(80.dp)
                            )
                        }

                        HorizontalDivider()

                        // Data Grid Content Rows
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(sortedList.size) { idx ->
                                val preset = sortedList[idx]
                                if (selectedCategory == "SUPPLIER") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { editingPresetItem = preset }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            preset.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(2.2f)
                                        )
                                        Text(
                                            preset.attributes["type"] ?: "—",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Text(
                                            preset.attributes["region"] ?: "—",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1.8f)
                                        )
                                        Text(
                                            preset.attributes["website"] ?: "—",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.weight(2.2f)
                                        )
                                        Text(
                                            preset.id,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.width(80.dp)
                                        ) {
                                            IconButton(
                                                onClick = { editingPresetItem = preset },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit Preset",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    activePresets = activePresets.filter { it.id != preset.id }
                                                    saveCategoryPresets()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Preset",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    val cleanName = if (preset.name.startsWith(preset.brand, ignoreCase = true)) {
                                        preset.name.substring(preset.brand.length).trim()
                                    } else {
                                        preset.name
                                    }
                                    val typeVal = preset.subCategory ?: preset.attributes["type"] ?: "—"
                                    val mountVal = preset.attributes["mount"] ?: preset.attributes["fixedLens"] ?: preset.attributes["compensationStops"] ?: "—"

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { editingPresetItem = preset }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            preset.brand,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(if (showMountFilter) 1.2f else 1.5f)
                                        )
                                        Text(
                                            cleanName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(if (showMountFilter) 2.5f else 3.2f)
                                        )
                                        Text(
                                            typeVal,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(if (showMountFilter) 1.5f else 1.8f)
                                        )
                                        if (showMountFilter) {
                                            Text(
                                                mountVal,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.weight(1.5f)
                                            )
                                        }
                                        Text(
                                            preset.id,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.weight(1.8f)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.width(80.dp)
                                        ) {
                                            IconButton(
                                                onClick = { editingPresetItem = preset },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit Preset",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    activePresets = activePresets.filter { it.id != preset.id }
                                                    saveCategoryPresets()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Preset",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridHeaderCell(
    title: String,
    activeColumn: String,
    ascending: Boolean,
    filterOptions: List<String>,
    activeFilter: String,
    modifier: Modifier = Modifier,
    onSelectSort: () -> Unit,
    onSelectFilter: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isSortActive = activeColumn.equals(title.substringBefore(" ").substringBefore("/"), ignoreCase = true)
    val isFilterActive = activeFilter != "All"

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.clickable { menuExpanded = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                if (isFilterActive) "$title: $activeFilter" else title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSortActive || isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSortActive || isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSortActive) {
                Text(
                    if (ascending) "▲" else "▼",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sort Ascending (A-Z)", fontWeight = if (isSortActive && ascending) FontWeight.Bold else FontWeight.Normal) },
                onClick = {
                    if (!isSortActive || !ascending) onSelectSort()
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Sort Descending (Z-A)", fontWeight = if (isSortActive && !ascending) FontWeight.Bold else FontWeight.Normal) },
                onClick = {
                    if (!isSortActive || ascending) onSelectSort()
                    menuExpanded = false
                }
            )

            if (filterOptions.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                filterOptions.forEach { fOpt ->
                    DropdownMenuItem(
                        text = { Text(if (fOpt == "All") "All ${title}s" else fOpt, fontWeight = if (fOpt == activeFilter) FontWeight.Bold else FontWeight.Normal) },
                        onClick = {
                            onSelectFilter(fOpt)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}
