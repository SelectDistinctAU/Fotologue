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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSupplierScreen(
    supplierId: Long? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val supplierList by viewModel.supplierList.collectAsState()
    val initialSupplier = supplierList.find { it.id == supplierId }

    var name by remember { mutableStateOf(initialSupplier?.name ?: "") }
    var region by remember { mutableStateOf(initialSupplier?.region ?: "Australia") }
    var website by remember { mutableStateOf(initialSupplier?.website ?: "") }
    var email by remember { mutableStateOf(initialSupplier?.email ?: "") }
    var phone by remember { mutableStateOf(initialSupplier?.phone ?: "") }
    var address by remember { mutableStateOf(initialSupplier?.address ?: "") }
    var isUsed by remember { mutableStateOf(initialSupplier?.isUsed ?: true) }
    var notes by remember { mutableStateOf(initialSupplier?.notes ?: "") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (initialSupplier == null) "Add Supplier Contact" else "Edit Supplier Contact",
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Supplier Name") },
                placeholder = { Text("e.g. Rewind Photo Lab") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Active Supplier Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isUsed,
                    onCheckedChange = { isUsed = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "In Use (Active Supplier)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Active suppliers appear in your stock purchase dropdowns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Region") },
                placeholder = { Text("e.g. Australia, USA, Europe") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website") },
                placeholder = { Text("e.g. https://rewindphotolab.com.au") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dedicated Line for Email Address
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("e.g. orders@rewindphotolab.com.au") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dedicated Line for Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                placeholder = { Text("e.g. +61 2 9557 1234") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Physical Address / Store Location") },
                placeholder = { Text("e.g. 123 George St, Sydney NSW 2000") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Supplier Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val supplier = LabSupplier(
                        id = initialSupplier?.id ?: 0,
                        name = name.trim(),
                        region = region.trim(),
                        website = website.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        address = address.trim(),
                        isUsed = isUsed,
                        notes = notes.trim()
                    )

                    if (initialSupplier != null) {
                        viewModel.updateSupplier(supplier)
                    } else {
                        viewModel.addSupplier(supplier)
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
                Text("Save Supplier Contact", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
