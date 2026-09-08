package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.data.Lens
import com.fotologue.app.ui.viewmodel.AddEditLensViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditLensScreen(
    viewModel: AddEditLensViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.lensd),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.lens_details))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 16.dp)
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
                if (!uiState.isCameraSelectionValid) {
                    Text(
                        stringResource(R.string.error_select_at_least_one_camera),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
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

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.online_lookup),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.modelNumber,
                    onValueChange = viewModel::onModelNumberChange,
                    label = { Text(stringResource(R.string.label_lens_model_hint)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.onSearch(uiState.modelNumber) },
                    colors = IconButtonDefaults.filledIconButtonColors()
                ) {
                    Icon(painterResource(R.drawable.ic_search), contentDescription = stringResource(R.string.action_lookup))
                }
            }

            if (uiState.isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            }

            if (uiState.showSearchResults) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            stringResource(R.string.online_lookup),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        uiState.searchResults.take(5).forEach { spec ->
                            ListItem(
                                headlineContent = { Text("${spec.make} ${spec.model}") },
                                supportingContent = { Text("${spec.modelNumber} • ${spec.type} • ${spec.mount}") },
                                modifier = Modifier.clickable { viewModel.onSelectSpec(spec) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            } else if (uiState.modelNumber.length >= 2 && !uiState.isSearching) {
                val uriHandler = LocalUriHandler.current
                TextButton(
                    onClick = { uriHandler.openUri("https://www.google.com/search?q=${uiState.modelNumber}+lens+specs") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_search), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.search_web_for_specs))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.manual_entry),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.make,
                    onValueChange = viewModel::onMakeChange,
                    label = { Text(stringResource(R.string.label_make)) },
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
                OutlinedTextField(
                    value = uiState.model,
                    onValueChange = viewModel::onModelChange,
                    label = { Text(stringResource(R.string.label_model_name)) },
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        Lens.TYPES.forEach { typeOption ->
                            DropdownMenuItem(
                                text = { Text(typeOption) },
                                onClick = {
                                    viewModel.onTypeChange(typeOption)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                if (uiState.isInterchangeable) {
                    OutlinedTextField(
                        value = uiState.mount,
                        onValueChange = viewModel::onMountChange,
                        label = { Text(stringResource(R.string.label_mount)) },
                        modifier = Modifier
                            .widthIn(min = 150.dp)
                            .weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.type == "Prime") {
                OutlinedTextField(
                    value = uiState.minFocalLength,
                    onValueChange = viewModel::onMinFocalLengthChange,
                    label = { Text(stringResource(R.string.label_focal_length_mm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.minFocalLength,
                        onValueChange = viewModel::onMinFocalLengthChange,
                        label = { Text(stringResource(R.string.label_min_focal_length_mm)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.maxFocalLength,
                        onValueChange = viewModel::onMaxFocalLengthChange,
                        label = { Text(stringResource(R.string.label_max_focal_length_mm)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.minAperture,
                    onValueChange = viewModel::onMinApertureChange,
                    label = { Text(stringResource(R.string.label_max_aperture_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
                OutlinedTextField(
                    value = uiState.maxAperture,
                    onValueChange = viewModel::onMaxApertureChange,
                    label = { Text(stringResource(R.string.label_min_aperture_hint)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = uiState.filterSize,
                    onValueChange = viewModel::onFilterSizeChange,
                    label = { Text(stringResource(R.string.label_filter_size_mm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.widthIn(min = 100.dp).weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = uiState.isStabilized,
                        onCheckedChange = viewModel::onIsStabilizedChange
                    )
                    Text(stringResource(R.string.label_image_stabilized))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.minFocusDistance,
                    onValueChange = viewModel::onMinFocusDistanceChange,
                    label = { Text(stringResource(R.string.label_min_focus_m)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
                OutlinedTextField(
                    value = uiState.maxMagnification,
                    onValueChange = viewModel::onMaxMagnificationChange,
                    label = { Text(stringResource(R.string.label_max_magnification_x)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 150.dp).weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.saveLens(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_lens))
            }
        }
    }
}
