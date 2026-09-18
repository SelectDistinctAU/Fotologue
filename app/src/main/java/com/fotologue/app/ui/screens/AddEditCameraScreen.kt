package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.core.data.CameraType
import com.fotologue.core.data.Film
import com.fotologue.app.ui.viewmodel.AddEditCameraViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCameraScreen(
    viewModel: AddEditCameraViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.cameraId == null) stringResource(R.string.add_camera) else stringResource(R.string.edit_camera)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveCamera(onBack) }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.camera_name)) },
                    modifier = Modifier.weight(1f),
                    isError = !uiState.isNameValid,
                    supportingText = if (!uiState.isNameValid) {
                        { Text(stringResource(R.string.error_name_empty)) }
                    } else null
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { 
                        val query = if (uiState.name.isNotBlank()) uiState.name else uiState.modelNumber
                        viewModel.searchDatabase(query) 
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_search), contentDescription = stringResource(R.string.action_lookup))
                }
            }

            if (uiState.isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (uiState.searchResults.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(stringResource(R.string.online_lookup), style = MaterialTheme.typography.labelLarge)
                        uiState.searchResults.take(3).forEach { spec ->
                            ListItem(
                                headlineContent = { Text(spec.name) },
                                supportingContent = { Text("${spec.type.displayName} • ${spec.mount}") },
                                modifier = Modifier.clickable { viewModel.applySpec(spec) }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = uiState.modelNumber,
                onValueChange = viewModel::onModelNumberChange,
                label = { Text(stringResource(R.string.label_model_number_optional)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.name.length >= 2 && !uiState.isSearching && uiState.searchResults.isEmpty()) {
                TextButton(
                    onClick = { uriHandler.openUri("https://www.google.com/search?q=${uiState.name}+camera+specs") }
                ) {
                    Icon(painterResource(R.drawable.ic_search), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.search_web_for_specs))
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.type.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_camera_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CameraType.entries.forEach { typeOption ->
                        DropdownMenuItem(
                            text = { Text(typeOption.displayName) },
                            onClick = {
                                viewModel.onTypeChange(typeOption)
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.type.isInterchangeable) {
                OutlinedTextField(
                    value = uiState.mount,
                    onValueChange = viewModel::onMountChange,
                    label = { Text(stringResource(R.string.label_lens_mount)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            var formatExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = formatExpanded,
                onExpandedChange = { formatExpanded = !formatExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.format,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Film Format") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = formatExpanded,
                    onDismissRequest = { formatExpanded = false }
                ) {
                    Film.ALL_FORMATS.forEach { format ->
                        DropdownMenuItem(
                            text = { Text(format) },
                            onClick = {
                                viewModel.onFormatChange(format)
                                formatExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = "Hint: If your camera uses multiple film formats (e.g. interchangeable backs), add it once for each format.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            OutlinedTextField(
                value = uiState.shutterSpeeds,
                onValueChange = viewModel::onShutterSpeedsChange,
                label = { Text(stringResource(R.string.label_shutter_speeds_comma)) },
                placeholder = { Text(stringResource(R.string.shutter_speeds_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.saveCamera(onBack) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.save_camera))
            }
        }
    }
}
