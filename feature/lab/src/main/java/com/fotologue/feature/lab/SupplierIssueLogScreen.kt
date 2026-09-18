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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.SupplierIssueLog
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierIssueLogScreen(
    supplierId: Long? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val supplierList by viewModel.supplierList.collectAsState()
    val issueList by viewModel.supplierIssueList.collectAsState()

    var selectedSupplierId by remember { mutableStateOf(supplierId ?: supplierList.firstOrNull()?.id) }
    var supplierExpanded by remember { mutableStateOf(false) }

    // New Issue Input State
    var showAddForm by remember { mutableStateOf(false) }
    var newSummary by remember { mutableStateOf("") }
    var newContactPerson by remember { mutableStateOf("") }
    var newNotes by remember { mutableStateOf("") }

    val filteredIssues = issueList.filter { selectedSupplierId == null || it.supplierId == selectedSupplierId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vendor Issue & Call Tracker",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            // Supplier Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = supplierExpanded,
                onExpandedChange = { supplierExpanded = !supplierExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val currentSupplier = supplierList.find { it.id == selectedSupplierId }
                val displayTitle = currentSupplier?.name ?: "All Supplier Contacts"
                OutlinedTextField(
                    value = displayTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter Supplier Contact") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = supplierExpanded,
                    onDismissRequest = { supplierExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Supplier Contacts") },
                        onClick = {
                            selectedSupplierId = null
                            supplierExpanded = false
                        }
                    )
                    supplierList.forEach { sup ->
                        DropdownMenuItem(
                            text = { Text(sup.name) },
                            onClick = {
                                selectedSupplierId = sup.id
                                supplierExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button to Toggle New Issue Form
            Button(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (showAddForm) "Close Form" else "+ Log Vendor Issue / Interaction", style = MaterialTheme.typography.labelMedium)
            }

            if (showAddForm) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Log Vendor Interaction / Issue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                        OutlinedTextField(
                            value = newSummary,
                            onValueChange = { newSummary = it },
                            label = { Text("Issue Summary") },
                            placeholder = { Text("e.g. Missing 2x boxes of paper in order #104") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newContactPerson,
                            onValueChange = { newContactPerson = it },
                            label = { Text("Contact Person") },
                            placeholder = { Text("e.g. Called Sarah in dispatch") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newNotes,
                            onValueChange = { newNotes = it },
                            label = { Text("Notes / Resolution Plan") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                selectedSupplierId?.let { supId ->
                                    viewModel.addSupplierIssueLog(supId, newSummary, newContactPerson, newNotes)
                                    newSummary = ""
                                    newContactPerson = ""
                                    newNotes = ""
                                    showAddForm = false
                                }
                            },
                            enabled = newSummary.isNotBlank() && selectedSupplierId != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Interaction Log")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Issue Logs List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredIssues.isEmpty()) {
                    item {
                        Text(
                            text = "No vendor issues or call interactions logged yet. Tap '+ Log Vendor Issue' above to create one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(filteredIssues, key = { "issue_${it.id}_${it.date}" }) { issue ->
                        val supplierName = supplierList.find { it.id == issue.supplierId }?.name ?: "Supplier"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                        Text(text = supplierName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        Text(text = issue.issueSummary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }

                                    // Status Badge
                                    val (badgeBg, badgeFg, statusText) = when (issue.status) {
                                        "OPEN" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "🔴 OPEN")
                                        "FOLLOWED_UP" -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "🟡 FOLLOWED UP")
                                        "RESOLVED" -> Triple(Color(0xFF2E7D32), Color.White, "🟢 RESOLVED")
                                        else -> Triple(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.onSurfaceVariant, "⚪ CLOSED")
                                    }
                                    Badge(
                                        containerColor = badgeBg,
                                        contentColor = badgeFg
                                    ) {
                                        Text(text = statusText, style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                if (issue.contactPerson.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Contact: ${issue.contactPerson}", style = MaterialTheme.typography.bodySmall)
                                }

                                if (issue.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = issue.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Status Action Shift Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (issue.status == "OPEN") {
                                        TextButton(onClick = { viewModel.updateSupplierIssueLog(issue.copy(status = "FOLLOWED_UP")) }) {
                                            Text("Followed Up", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    if (issue.status != "RESOLVED" && issue.status != "CLOSED") {
                                        TextButton(onClick = { viewModel.updateSupplierIssueLog(issue.copy(status = "RESOLVED")) }) {
                                            Text("Mark Resolved", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    IconButton(onClick = { viewModel.deleteSupplierIssueLog(issue) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Log")
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
