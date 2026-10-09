package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.R
import com.fotologue.app.ui.viewmodel.TaxonomyViewModel
import com.fotologue.core.data.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreen(
    viewModel: TaxonomyViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val filteredTags = remember(uiState.allTags, uiState.selectedCategoryTab) {
        when (uiState.selectedCategoryTab) {
            "Subjects" -> uiState.allTags.filter { it.category == Tag.CATEGORY_SUBJECT }
            "Content" -> uiState.allTags.filter { it.category == Tag.CATEGORY_CONTENT }
            "Intent" -> uiState.allTags.filter { it.category == Tag.CATEGORY_INTENT }
            else -> uiState.allTags
        }
    }

    // Sub-Group Mapping for Mobile Hierarchical Cards
    val subGroupMapping = remember {
        mapOf(
            "Architecture" to "Urban/built environment",
            "Street" to "Urban/built environment",
            "Cityscape" to "Urban/built environment",
            "Building" to "Urban/built environment",
            "Bridge" to "Urban/built environment",
            "Interior" to "Urban/built environment",
            "Skyline" to "Urban/built environment",
            "Wedding" to "Events",
            "Concert" to "Events",
            "Sport" to "Events",
            "Festival" to "Events",
            "Protest" to "Events",
            "Celebration" to "Events",
            "Sunset/sunrise" to "Nature",
            "Landscape" to "Nature",
            "Animals" to "Nature",
            "Flora" to "Nature",
            "Seascape" to "Nature",
            "Portrait" to "People & Portraits",
            "Group" to "People & Portraits",
            "Candid" to "People & Portraits",
            "Light & Shadow Study" to "Study",
            "Material & Film Test" to "Study",
            "Composition Study" to "Study"
        )
    }

    val groupedTags = remember(filteredTags) {
        filteredTags.groupBy { tag -> subGroupMapping[tag.name] ?: "General" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_label),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Taxonomy & Tags", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTagDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Tag")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Summary Badges
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Subjects", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${uiState.subjectsCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Content", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${uiState.contentCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Intent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${uiState.intentCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Subjects", "Content", "Intent").forEach { tab ->
                    FilterChip(
                        selected = uiState.selectedCategoryTab == tab,
                        onClick = { viewModel.selectCategoryTab(tab) },
                        label = { Text(tab) }
                    )
                }
            }

            HorizontalDivider()

            // Hierarchical Sub-Group Tag List
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedTags.forEach { (subGroupName, tagsList) ->
                    item(key = "header_$subGroupName") {
                        Text(
                            text = subGroupName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(tagsList, key = { "tag_${it.id}_${it.name}" }) { tag ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.showEditTagDialog(tag) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_label),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { viewModel.showEditTagDialog(tag) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.showDeleteTagDialog(tag) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD TAG DIALOG
    if (uiState.showAddDialog) {
        var nameText by remember { mutableStateOf("") }
        var categoryText by remember { mutableStateOf(Tag.CATEGORY_CONTENT) }

        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text("Add Taxonomy Tag", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Tag Name") },
                        placeholder = { Text("e.g. Sunset, Concert") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Category:", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(Tag.CATEGORY_SUBJECT, Tag.CATEGORY_CONTENT, Tag.CATEGORY_INTENT).forEach { cat ->
                            FilterChip(
                                selected = categoryText == cat,
                                onClick = { categoryText = cat },
                                label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTag(nameText, categoryText)
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) { Text("Cancel") }
            }
        )
    }

    // EDIT TAG DIALOG
    uiState.editingTag?.let { tagToEdit ->
        var nameText by remember(tagToEdit) { mutableStateOf(tagToEdit.name) }
        var categoryText by remember(tagToEdit) { mutableStateOf(tagToEdit.category) }

        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text("Edit ${tagToEdit.category.replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Tag Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Category:", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(Tag.CATEGORY_SUBJECT, Tag.CATEGORY_CONTENT, Tag.CATEGORY_INTENT).forEach { cat ->
                            FilterChip(
                                selected = categoryText == cat,
                                onClick = { categoryText = cat },
                                label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTag(tagToEdit.copy(name = nameText, category = categoryText))
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) { Text("Cancel") }
            }
        )
    }

    // DELETE CONFIRMATION DIALOG
    uiState.deletingTag?.let { tagToDelete ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text("Delete Tag: #${tagToDelete.name}", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete #${tagToDelete.name}? This tag will be removed from your catalog.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = { viewModel.deleteTag(tagToDelete) }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) { Text("Cancel") }
            }
        )
    }
}
