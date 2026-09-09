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
    val commonMakes = listOf("Canon", "Nikon", "Sony", "Fujifilm", "Olympus", "Panasonic", "Leica", "Pentax", "Sigma", "Tamron", "Tokina", "Zeiss", "Voigtländer", "Other...")
    var makeExpanded by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.manual_entry),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Make (Dropdown)
            ExposedDropdownMenuBox(
                expanded = makeExpanded,
                onExpandedChange = { makeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.make,
                    onValueChange = viewModel::onMakeChange,
                    label = { Text(stringResource(R.string.label_make)) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = makeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = makeExpanded,
                    onDismissRequest = { makeExpanded = false }
                ) {
                    commonMakes.forEach { make ->
                        DropdownMenuItem(
                            text = { Text(make) },
                            onClick = {
                                if (make != "Other...") {
                                    viewModel.onMakeChange(make)
                                }
                                makeExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = { Text(stringResource(R.string.label_model_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.type == "Prime") {
                OutlinedTextField(
                    value = uiState.minFocalLength,
                    onValueChange = viewModel::onMinFocalLengthChange,
                    label = { Text(stringResource(R.string.label_focal_length_mm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = uiState.minFocalLength,
                        onValueChange = viewModel::onMinFocalLengthChange,
                        label = { Text("Min FL (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.maxFocalLength,
                        onValueChange = viewModel::onMaxFocalLengthChange,
                        label = { Text("Max FL (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.minAperture,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("""^\d{0,2}(\.\d{0,2})?$"""))) {
                            viewModel.onMinApertureChange(newValue)
                        }
                    },
                    label = { Text("Max Ap (e.g. 1.8)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("1.8") }
                )
                OutlinedTextField(
                    value = uiState.maxAperture,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("""^\d{0,2}(\.\d{0,2})?$"""))) {
                            viewModel.onMaxApertureChange(newValue)
                        }
                    },
                    label = { Text("Min Ap (e.g. 22)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("22") }
                )
            }

            OutlinedTextField(
                value = uiState.filterSize,
                onValueChange = viewModel::onFilterSizeChange,
                label = { Text(stringResource(R.string.label_filter_size_mm)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.isStabilized,
                    onCheckedChange = viewModel::onIsStabilizedChange
                )
                Text(stringResource(R.string.label_image_stabilized))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.minFocusDistance,
                    onValueChange = viewModel::onMinFocusDistanceChange,
                    label = { Text("Min Focus (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.maxMagnification,
                    onValueChange = viewModel::onMaxMagnificationChange,
                    label = { Text("Max Mag (x)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveLens(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_lens))
            }
        }
    }
}
