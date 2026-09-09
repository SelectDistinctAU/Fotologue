package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditFilterViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFilterScreen(
    viewModel: AddEditFilterViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (viewModel.filterId == null) 
                            stringResource(R.string.add_filter) 
                        else 
                            stringResource(R.string.edit_filter)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveFilter(onSuccess) }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.label_filter_name_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_filter_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    uiState.filterTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onTypeChange(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.compensationStops,
                onValueChange = viewModel::onCompensationStopsChange,
                label = { Text(stringResource(R.string.label_compensation_stops_hint)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Button(
                onClick = { viewModel.saveFilter(onSuccess) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_filter))
            }
        }
    }
}
