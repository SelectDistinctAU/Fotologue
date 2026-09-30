package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.AddEditTeleconverterViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTeleconverterScreen(
    viewModel: AddEditTeleconverterViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.frame_inspect_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (viewModel.teleconverterId == null) 
                                "Add Teleconverter / Extension Ring" 
                            else 
                                "Edit Teleconverter / Extension Ring"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveTeleconverter(onSuccess) }) {
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
            Text(
                stringResource(R.string.associated_cameras),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.cameras.isEmpty()) {
                Text(
                    stringResource(R.string.error_no_cameras),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.cameras.forEach { camera ->
                        val isSelected = uiState.selectedCameraIds.contains(camera.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCameraToggled(camera.id) },
                            label = { Text(camera.name) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            Text(
                "Note: Teleconverters and Extension Rings attach behind interchangeable lenses (SLRs / Rangefinders). For TLRs and fixed-lens cameras, front aux lenses should be logged under Filters / Front Attachments.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            var converterTypeExpanded by remember { mutableStateOf(false) }
            val converterTypes = listOf("Teleconverter", "Extension Ring / Macro Tube", "Speed Booster / Focal Reducer")
            var selectedConverterType by remember { mutableStateOf(converterTypes[0]) }

            ExposedDropdownMenuBox(
                expanded = converterTypeExpanded,
                onExpandedChange = { converterTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedConverterType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Converter Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = converterTypeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = converterTypeExpanded,
                    onDismissRequest = { converterTypeExpanded = false }
                ) {
                    converterTypes.forEach { typeOpt ->
                        DropdownMenuItem(
                            text = { Text(typeOpt) },
                            onClick = {
                                selectedConverterType = typeOpt
                                converterTypeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Converter / Extension Ring Name (e.g. Canon FD 2x-A)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.magnification,
                onValueChange = viewModel::onMagnificationChange,
                label = { Text("Magnification / Extension (e.g. 1.4x, 2x, 25mm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            var lightLossExpanded by remember { mutableStateOf(false) }
            val lightLossOptions = listOf("0.0", "0.5", "1.0", "1.5", "2.0")

            ExposedDropdownMenuBox(
                expanded = lightLossExpanded,
                onExpandedChange = { lightLossExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "${uiState.lightLossStops} Stops",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Light Loss (Stops)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lightLossExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = lightLossExpanded,
                    onDismissRequest = { lightLossExpanded = false }
                ) {
                    lightLossOptions.forEach { lossOpt ->
                        DropdownMenuItem(
                            text = { Text("$lossOpt Stops") },
                            onClick = {
                                viewModel.onLightLossStopsChange(lossOpt)
                                lightLossExpanded = false
                            }
                        )
                    }
                }
            }
            
            Button(
                onClick = { viewModel.saveTeleconverter(onSuccess) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_teleconverter))
            }
        }
    }
}
