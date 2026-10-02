package com.fotologue.feature.lab

import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditSupplierScreen(
    supplierId: Long? = null,
    viewModel: LabHomeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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

    var selectedRoles by remember {
        mutableStateOf(
            if (initialSupplier == null) setOf("#Supplier")
            else setOf("#Supplier")
        )
    }
    val roleTags = listOf("#Supplier", "#Lab", "#RepairTech", "#Borrower", "#Friend")

    // Privacy-First Native Android Contact Picker (Zero Address Book Harvesting)
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIdx = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    if (nameIdx >= 0) {
                        val contactName = it.getString(nameIdx) ?: ""
                        if (contactName.isNotBlank()) {
                            name = contactName
                            selectedRoles = selectedRoles + "#Borrower" + "#Friend"
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (initialSupplier == null) "Add Contact" else "Edit Contact",
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
            // Privacy-First Import Button
            OutlinedButton(
                onClick = { contactPickerLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Import from Phone Contacts", fontWeight = FontWeight.Bold)
            }

            // Field 1: Contact Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Contact Name") },
                placeholder = { Text("e.g. Rewind Photo Lab, Dave Smith") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Field 2: Role Tags Chips
            Text("Contact Roles", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                roleTags.forEach { role ->
                    val isSel = selectedRoles.contains(role)
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedRoles = if (isSel) selectedRoles - role else selectedRoles + role
                        },
                        label = { Text(role, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

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
                        text = "In Use (Active Contact)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Active contacts appear in stock purchase, loan, and repair dropdowns.",
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
                label = { Text("Contact Notes") },
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
                Text("Save Contact", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
