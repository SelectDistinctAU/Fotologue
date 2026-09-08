package com.fotologue.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.fotologue.app.data.CameraType
import com.fotologue.app.ui.viewmodel.AddEditCameraViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCameraScreen(
    viewModel: AddEditCameraViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.cameraId == null) stringResource(R.string.add_camera) else stringResource(R.string.edit_camera)) },
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
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.camera_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isNameValid,
                supportingText = { if (!uiState.isNameValid) Text(stringResource(R.string.error_name_empty)) }
            )

            OutlinedTextField(
                value = uiState.modelNumber,
                onValueChange = viewModel::onModelNumberChange,
                label = { Text(stringResource(R.string.label_model_number_optional)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (uiState.isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            }

            if (uiState.searchResults.isNotEmpty()) {
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
                                headlineContent = { Text(spec.name) },
                                supportingContent = { Text("${spec.type.displayName} • ${spec.mount}") },
                                modifier = Modifier.clickable { viewModel.applySpec(spec) },
                                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                            )
                        }
                    }
                }
            } else if (uiState.name.length >= 2 && !uiState.isSearching) {
                val uriHandler = LocalUriHandler.current
                TextButton(
                    onClick = { uriHandler.openUri("https://www.google.com/search?q=${uiState.name}+camera+specs") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.search_web_for_specs))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.type.isInterchangeable) {
                OutlinedTextField(
                    value = uiState.mount,
                    onValueChange = viewModel::onMountChange,
                    label = { Text(stringResource(R.string.label_lens_mount)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                    modifier = Modifier
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
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
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.isHalfFrame,
                    onCheckedChange = viewModel::onIsHalfFrameChange
                )
                Text(
                    text = "Is Half-Frame Camera? (Double exposures per roll)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.shutterSpeeds,
                onValueChange = viewModel::onShutterSpeedsChange,
                label = { Text(stringResource(R.string.label_shutter_speeds_comma)) },
                placeholder = { Text(stringResource(R.string.shutter_speeds_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.saveCamera(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_camera))
            }
        }
    }
}
