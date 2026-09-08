package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.fotologue.app.data.Filter
import com.fotologue.app.data.Lens
import com.fotologue.app.ui.viewmodel.AddEditCameraKitViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCameraKitScreen(
    viewModel: AddEditCameraKitViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.kitId == null) stringResource(R.string.add_kit) else stringResource(R.string.edit_kit)) },
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
                label = { Text(stringResource(R.string.kit_name)) },
                placeholder = { Text(stringResource(R.string.kit_name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isNameValid,
                supportingText = { if (!uiState.isNameValid) Text(stringResource(R.string.error_name_empty)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GearDropdown(
                label = stringResource(R.string.label_camera),
                items = uiState.allCameras,
                selectedId = uiState.cameraId,
                onItemSelected = { viewModel.onCameraSelected(it.id) },
                itemLabel = { it.name },
                getItemId = { it.id },
                isError = !uiState.isCameraValid,
                supportingText = { if (!uiState.isCameraValid) Text(stringResource(R.string.error_select_camera)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GearDropdown(
                label = stringResource(R.string.label_lens),
                items = uiState.filteredLenses,
                selectedId = uiState.lensId,
                onItemSelected = { viewModel.onLensSelected(it.id) },
                itemLabel = { "${it.make} ${it.model}" },
                getItemId = { it.id },
                isError = !uiState.isLensValid,
                supportingText = { if (!uiState.isLensValid) Text(stringResource(R.string.error_select_lens)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GearDropdown(
                label = stringResource(R.string.label_film),
                items = uiState.allFilms,
                selectedId = uiState.filmId,
                onItemSelected = { viewModel.onFilmSelected(it.id) },
                itemLabel = { "${it.name} (${it.expectedExposures} exp)" },
                getItemId = { it.id },
                isError = !uiState.isFilmValid,
                supportingText = { if (!uiState.isFilmValid) Text(stringResource(R.string.error_select_film)) }
            )

            if (uiState.isTeleconverterVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                GearDropdown(
                    label = stringResource(R.string.label_teleconverter_optional),
                    items = uiState.allTeleconverters,
                    selectedId = uiState.teleconverterId,
                    onItemSelected = { viewModel.onTeleconverterSelected(it.id) },
                    itemLabel = { it.name },
                    getItemId = { it.id },
                    isOptional = true,
                    onClear = { viewModel.onTeleconverterSelected(null) }
                )
            }

            if (uiState.isFilterVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                GearDropdown(
                    label = stringResource(R.string.label_filter_optional),
                    items = uiState.allFilters,
                    selectedId = uiState.filterId,
                    onItemSelected = { viewModel.onFilterSelected(it.id) },
                    itemLabel = { it.name },
                    getItemId = { it.id },
                    isOptional = true,
                    onClear = { viewModel.onFilterSelected(null) }
                )
            }

            if (uiState.isFlashVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                GearDropdown(
                    label = stringResource(R.string.label_flash_optional),
                    items = uiState.allFlashes,
                    selectedId = uiState.flashId,
                    onItemSelected = { viewModel.onFlashSelected(it.id) },
                    itemLabel = { "${it.make} ${it.model}" },
                    getItemId = { it.id },
                    isOptional = true,
                    onClear = { viewModel.onFlashSelected(null) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.saveKit(onBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_kit))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GearDropdown(
    label: String,
    items: List<T>,
    selectedId: Long?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    getItemId: (T) -> Long,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    isOptional: Boolean = false,
    onClear: (() -> Unit)? = null
) where T : Any {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = remember(selectedId, items) {
        items.find { getItemId(it) == selectedId }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem?.let(itemLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Row {
                    if (isOptional && selectedId != null && onClear != null) {
                        IconButton(onClick = onClear) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.action_clear))
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
            isError = isError,
            supportingText = supportingText
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
