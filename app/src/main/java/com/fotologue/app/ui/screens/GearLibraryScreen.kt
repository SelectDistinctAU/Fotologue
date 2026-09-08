package com.fotologue.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.data.*
import com.fotologue.app.ui.viewmodel.GearLibraryViewModel
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun GearLibraryScreen(
    viewModel: GearLibraryViewModel,
    onBack: () -> Unit,
    onAddCamera: () -> Unit,
    onEditCamera: (Long) -> Unit,
    onAddFilm: () -> Unit,
    onEditFilm: (Long) -> Unit,
    onAddLens: () -> Unit,
    onEditLens: (Long) -> Unit,
    onAddTeleconverter: () -> Unit,
    onEditTeleconverter: (Long) -> Unit,
    onAddFilter: () -> Unit,
    onEditFilter: (Long) -> Unit,
    onAddFlash: () -> Unit,
    onEditFlash: (Long) -> Unit,
    onAddKit: () -> Unit,
    onEditKit: (Long) -> Unit,
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = calculatePaneScaffoldDirective(adaptiveInfo)
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>(
        scaffoldDirective = directive
    )
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedAccessoriesTabIndex by remember { mutableIntStateOf(0) }
    
    // Requested order: Kits, Cameras, Lenses, Films, Accessories, Subjects, Content
    val categories = listOf(
        GearCategory(stringResource(R.string.category_kits), iconRes = R.drawable.ic_inventory_2),
        GearCategory(stringResource(R.string.category_cameras), iconRes = R.drawable.ic_photo_camera),
        GearCategory(stringResource(R.string.category_lenses), iconRes = R.drawable.lensd),
        GearCategory(stringResource(R.string.category_films), iconRes = R.drawable.ic_camera_roll),
        GearCategory(stringResource(R.string.category_accessories), iconRes = R.drawable.extension_24px),
        GearCategory(stringResource(R.string.category_subjects), iconRes = R.drawable.ic_label),
        GearCategory(stringResource(R.string.category_content), iconRes = R.drawable.ic_label)
    )
    
    val cameras by viewModel.allCameras.collectAsStateWithLifecycle()
    val lensesWithCameras by viewModel.allLensesWithCameras.collectAsStateWithLifecycle()
    val teleconverters by viewModel.allTeleconverters.collectAsStateWithLifecycle()
    val filters by viewModel.allFilters.collectAsStateWithLifecycle()
    val flashes by viewModel.allFlashes.collectAsStateWithLifecycle()
    val films by viewModel.allFilms.collectAsStateWithLifecycle()
    val kits by viewModel.allCameraKits.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val contentTags by viewModel.allContent.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.gear_library_title)) },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back_to_categories))
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding() + 16.dp,
                            bottom = innerPadding.calculateBottomPadding() + 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(categories) { index, category ->
                            NavigationDrawerItem(
                                label = { Text(category.name) },
                                selected = uiState.selectedCategoryIndex == index,
                                onClick = { 
                                    viewModel.onCategorySelected(index)
                                    scope.launch {
                                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, index)
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(category.iconRes), 
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val content = @Composable { paddingValues: PaddingValues ->
                    when (uiState.selectedCategoryIndex) {
                        0 -> KitList(
                            kits = kits,
                            selectedIds = uiState.selectedKitIds,
                            isReorderMode = uiState.isReorderMode,
                            onKitClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleKitSelection(id) else onEditKit(id)
                            },
                            onKitLongClick = { viewModel.toggleKitSelection(it) },
                            onMove = viewModel::moveKit,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        1 -> CameraList(
                            cameras = cameras,
                            selectedIds = uiState.selectedCameraIds,
                            isReorderMode = uiState.isReorderMode,
                            onCameraClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleCameraSelection(id) else onEditCamera(id)
                            },
                            onCameraLongClick = { viewModel.toggleCameraSelection(it) },
                            onMove = viewModel::moveCamera,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        2 -> LensList(
                            lenses = lensesWithCameras,
                            selectedIds = uiState.selectedLensIds,
                            isReorderMode = uiState.isReorderMode,
                            onLensClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleLensSelection(id) else onEditLens(id)
                            },
                            onLensLongClick = { viewModel.toggleLensSelection(it) },
                            onMove = viewModel::moveLens,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        3 -> FilmList(
                            films = films,
                            selectedIds = uiState.selectedFilmIds,
                            isReorderMode = uiState.isReorderMode,
                            onFilmClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleFilmSelection(id) else onEditFilm(id)
                            },
                            onFilmLongClick = { viewModel.toggleFilmSelection(it) },
                            onMove = viewModel::moveFilm,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        4 -> AccessoriesGearScreen(
                            teleconverters = teleconverters,
                            filters = filters,
                            flashes = flashes,
                            selectedTeleconverterIds = uiState.selectedTeleconverterIds,
                            selectedFilterIds = uiState.selectedFilterIds,
                            selectedFlashIds = uiState.selectedFlashIds,
                            selectedTabIndex = selectedAccessoriesTabIndex,
                            isReorderMode = uiState.isReorderMode,
                            onTabSelected = { selectedAccessoriesTabIndex = it },
                            onTeleconverterClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleTeleconverterSelection(id) else onEditTeleconverter(id)
                            },
                            onTeleconverterLongClick = { viewModel.toggleTeleconverterSelection(it) },
                            onFilterClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleFilterSelection(id) else onEditFilter(id)
                            },
                            onFilterLongClick = { viewModel.toggleFilterSelection(it) },
                            onFlashClick = { id -> 
                                if (uiState.isSelectionMode) viewModel.toggleFlashSelection(id) else onEditFlash(id)
                            },
                            onFlashLongClick = { viewModel.toggleFlashSelection(it) },
                            onMoveTeleconverter = viewModel::moveTeleconverter,
                            onMoveFilter = viewModel::moveFilter,
                            onMoveFlash = viewModel::moveFlash,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        5 -> SubjectList(
                            subjects = subjects,
                            selectedIds = uiState.selectedSubjectIds,
                            isReorderMode = uiState.isReorderMode,
                            onSubjectClick = { sub ->
                                if (uiState.isSelectionMode) viewModel.toggleSubjectSelection(sub.id) else viewModel.showEditTagDialog(sub)
                            },
                            onSubjectLongClick = { viewModel.toggleSubjectSelection(it) },
                            onMove = viewModel::moveSubject,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                        6 -> ContentList(
                            contentItems = contentTags,
                            subjects = subjects,
                            selectedIds = uiState.selectedContentIds,
                            isReorderMode = uiState.isReorderMode,
                            onContentClick = { tag ->
                                if (uiState.isSelectionMode) viewModel.toggleContentSelection(tag.id) else viewModel.showEditTagDialog(tag)
                            },
                            onContentLongClick = { viewModel.toggleContentSelection(it) },
                            onMove = viewModel::moveContent,
                            bottomPadding = paddingValues.calculateBottomPadding()
                        )
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { 
                                if (uiState.isSelectionMode) {
                                    val totalSelected = uiState.selectedCameraIds.size + 
                                                        uiState.selectedFilmIds.size + 
                                                        uiState.selectedLensIds.size +
                                                        uiState.selectedTeleconverterIds.size +
                                                        uiState.selectedFilterIds.size +
                                                        uiState.selectedFlashIds.size +
                                                        uiState.selectedKitIds.size +
                                                        uiState.selectedSubjectIds.size +
                                                        uiState.selectedContentIds.size
                                    Text(stringResource(R.string.selected_count, totalSelected))
                                } else if (uiState.isReorderMode) {
                                    Text("Reorder Items")
                                } else {
                                    val categoryName = categories[uiState.selectedCategoryIndex].name
                                    val accessoriesName = stringResource(R.string.category_accessories)
                                    val title = if (categoryName == accessoriesName) {
                                        when (selectedAccessoriesTabIndex) {
                                            0 -> stringResource(R.string.category_accessories_tcs)
                                            1 -> stringResource(R.string.category_accessories_filters)
                                            2 -> stringResource(R.string.category_accessories_flashes)
                                            else -> accessoriesName
                                        }
                                    } else {
                                        categoryName
                                    }
                                    Text(title)
                                }
                            },
                            navigationIcon = {
                                if (uiState.isSelectionMode || uiState.isReorderMode) {
                                    IconButton(
                                        onClick = { 
                                            if (uiState.isReorderMode) viewModel.toggleReorderMode() else viewModel.clearSelection() 
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.action_clear_selection))
                                    }
                                } else if (navigator.canNavigateBack()) {
                                    IconButton(
                                        onClick = { 
                                            scope.launch {
                                                navigator.navigateBack()
                                            }
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back_to_categories))
                                    }
                                }
                            },
                            actions = {
                                if (uiState.isSelectionMode) {
                                    IconButton(onClick = { viewModel.selectAll() }) {
                                        Icon(painterResource(R.drawable.ic_select_all), contentDescription = stringResource(R.string.action_select_all))
                                    }
                                    IconButton(onClick = { viewModel.deleteSelectedGear() }) {
                                        Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete))
                                    }
                                } else if (!uiState.isReorderMode) {
                                    IconButton(onClick = { viewModel.toggleReorderMode() }) {
                                        Icon(painterResource(R.drawable.select_check_box_24px), contentDescription = "Toggle Reorder")
                                    }
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        if (!uiState.isSelectionMode && !uiState.isReorderMode) {
                            FloatingActionButton(
                                onClick = {
                                    when (uiState.selectedCategoryIndex) {
                                        0 -> onAddKit()
                                        1 -> onAddCamera()
                                        2 -> onAddLens()
                                        3 -> onAddFilm()
                                        4 -> {
                                            when (selectedAccessoriesTabIndex) {
                                                0 -> onAddTeleconverter()
                                                1 -> onAddFilter()
                                                2 -> onAddFlash()
                                            }
                                        }
                                        5 -> viewModel.showAddTagDialog()
                                        6 -> viewModel.showAddContentDialog()
                                    }
                                },
                                modifier = Modifier.navigationBarsPadding()
                            ) {
                                Icon(painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.action_add))
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding())
                            .consumeWindowInsets(innerPadding)
                    ) {
                        content(innerPadding)
                    }
                }
            }
        }
    )

    val editingTag = uiState.editingTag
    if (uiState.showTagDialog) {
        SubjectDialog(
            tag = editingTag,
            onDismiss = viewModel::dismissTagDialog,
            onConfirm = { name ->
                if (editingTag == null) {
                    viewModel.addTag(name, Tag.CATEGORY_SUBJECT, null)
                } else {
                    viewModel.updateTag(editingTag.copy(name = name))
                }
            }
        )
    }

    if (uiState.showContentDialog) {
        ContentDialog(
            tag = editingTag,
            subjects = subjects,
            onDismiss = viewModel::dismissTagDialog,
            onConfirm = { name, parentId ->
                if (editingTag == null) {
                    viewModel.addTag(name, Tag.CATEGORY_CONTENT, parentId)
                } else {
                    viewModel.updateTag(editingTag.copy(name = name, parentId = parentId))
                }
            }
        )
    }
}

data class GearCategory(
    val name: String, 
    val iconRes: Int
)

@Composable
fun SubjectDialog(
    tag: Tag?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(tag?.name ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tag == null) stringResource(R.string.add_subject) else stringResource(R.string.edit_subject)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_subject_name)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDialog(
    tag: Tag?,
    subjects: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long?) -> Unit
) {
    var name by remember { mutableStateOf(tag?.name ?: "") }
    var parentId by remember { mutableStateOf(tag?.parentId) }
    var parentExpanded by remember { mutableStateOf(value = false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tag == null) stringResource(R.string.add_content) else stringResource(R.string.edit_content)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = parentExpanded,
                    onExpandedChange = { parentExpanded = !parentExpanded }
                ) {
                    val currentParent = subjects.find { it.id == parentId }
                    OutlinedTextField(
                        value = currentParent?.name ?: stringResource(R.string.select_subject),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.associated_subject)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = parentExpanded,
                        onDismissRequest = { parentExpanded = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub.name) },
                                onClick = {
                                    parentId = sub.id
                                    parentExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_content_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, parentId) },
                enabled = (name.isNotBlank() && parentId != null)
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun AccessoriesGearScreen(
    teleconverters: List<Teleconverter>,
    filters: List<Filter>,
    flashes: List<Flash>,
    selectedTeleconverterIds: Set<Long>,
    selectedFilterIds: Set<Long>,
    selectedFlashIds: Set<Long>,
    selectedTabIndex: Int,
    isReorderMode: Boolean,
    onTabSelected: (Int) -> Unit,
    onTeleconverterClick: (Long) -> Unit,
    onTeleconverterLongClick: (Long) -> Unit,
    onFilterClick: (Long) -> Unit,
    onFilterLongClick: (Long) -> Unit,
    onFlashClick: (Long) -> Unit,
    onFlashLongClick: (Long) -> Unit,
    onMoveTeleconverter: (Int, Int) -> Unit,
    onMoveFilter: (Int, Int) -> Unit,
    onMoveFlash: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text(stringResource(R.string.category_accessories_tcs)) },
                icon = { Icon(painterResource(R.drawable.ic_zoom_in), contentDescription = null) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text(stringResource(R.string.category_accessories_filters)) },
                icon = { Icon(painterResource(R.drawable.filter_24px), contentDescription = null) }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                text = { Text(stringResource(R.string.category_accessories_flashes)) },
                icon = { Icon(painterResource(R.drawable.ic_flash_on), contentDescription = null) }
            )
        }
        
        when (selectedTabIndex) {
            0 -> TeleconverterList(
                teleconverters = teleconverters,
                selectedIds = selectedTeleconverterIds,
                isReorderMode = isReorderMode,
                onTeleconverterClick = onTeleconverterClick,
                onTeleconverterLongClick = onTeleconverterLongClick,
                onMove = onMoveTeleconverter,
                bottomPadding = bottomPadding
            )
            1 -> FilterList(
                filters = filters,
                selectedIds = selectedFilterIds,
                isReorderMode = isReorderMode,
                onFilterClick = onFilterClick,
                onFilterLongClick = onFilterLongClick,
                onMove = onMoveFilter,
                bottomPadding = bottomPadding
            )
            2 -> FlashList(
                flashes = flashes,
                selectedIds = selectedFlashIds,
                isReorderMode = isReorderMode,
                onFlashClick = onFlashClick,
                onFlashLongClick = onFlashLongClick,
                onMove = onMoveFlash,
                bottomPadding = bottomPadding
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubjectList(
    subjects: List<Tag>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onSubjectClick: (Tag) -> Unit,
    onSubjectLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (subjects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_subjects), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(subjects) { index, sub ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = sub.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(sub.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(painterResource(R.drawable.ic_label), contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, subjects.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onSubjectClick(sub) },
                            onLongClick = { onSubjectLongClick(sub.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentList(
    contentItems: List<Tag>,
    subjects: List<Tag>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onContentClick: (Tag) -> Unit,
    onContentLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (contentItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_content), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        // Reordering grouped content is tricky, so we'll just show the full list in reorder mode
        if (isReorderMode) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
            ) {
                itemsIndexed(contentItems) { index, tag ->
                    val parent = subjects.find { it.id == tag.parentId }
                    ListItem(
                        headlineContent = { Text(tag.name) },
                        supportingContent = { Text("Parent: ${parent?.name ?: "None"}") },
                        trailingContent = { ReorderButtons(index, contentItems.size, onMove) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
            }
        } else {
            val groupedContent = remember(contentItems) {
                contentItems.groupBy { it.parentId }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
            ) {
                subjects.forEach { subject ->
                    val itemsForSubject = groupedContent[subject.id] ?: emptyList()
                    if (itemsForSubject.isNotEmpty()) {
                        item(key = "subject_${subject.id}") {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        items(itemsForSubject, key = { it.id }) { tag ->
                            val isSelected = selectedIds.contains(tag.id)
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.titleMedium
                                    ) 
                                },
                                leadingContent = {
                                    if (isSelected) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check_circle), 
                                            contentDescription = "Selected", 
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Icon(painterResource(R.drawable.ic_label), contentDescription = null, modifier = Modifier.size(24.dp))
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onContentClick(tag) },
                                        onLongClick = { onContentLongClick(tag.id) }
                                    )
                                    .padding(vertical = 4.dp)
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
                
                val orphans = contentItems.filter { it.parentId == null || subjects.none { s -> s.id == it.parentId } }
                if (orphans.isNotEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.label_uncategorised),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(orphans) { tag ->
                        ListItem(
                            headlineContent = { Text(tag.name) },
                            leadingContent = {
                                Icon(painterResource(R.drawable.ic_label), contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onContentClick(tag) },
                                    onLongClick = { onContentLongClick(tag.id) }
                                )
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KitList(
    kits: List<CameraKitWithGear>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onKitClick: (Long) -> Unit,
    onKitLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (kits.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_kits), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(kits) { index, kitWithGear ->
                val kit = kitWithGear.cameraKit
                ListItem(
                    headlineContent = { 
                        Text(
                            text = kit.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        val cameraName = kitWithGear.camera?.name ?: stringResource(R.string.unknown_camera)
                        val lensName = kitWithGear.lens?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.unknown_lens)
                        val filterName = kitWithGear.filter?.let { " + ${it.name}" } ?: ""
                        Text(
                            text = "$cameraName + $lensName$filterName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(kit.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_inventory_2),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, kits.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onKitClick(kit.id) },
                            onLongClick = { onKitLongClick(kit.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TeleconverterList(
    teleconverters: List<Teleconverter>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onTeleconverterClick: (Long) -> Unit,
    onTeleconverterLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (teleconverters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_teleconverters), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(teleconverters) { index, tc ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = tc.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        Text(
                            text = "${tc.magnification}x • ${tc.lightLossStops} stop(s) loss",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(tc.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_zoom_in),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, teleconverters.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onTeleconverterClick(tc.id) },
                            onLongClick = { onTeleconverterLongClick(tc.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilterList(
    filters: List<Filter>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onFilterClick: (Long) -> Unit,
    onFilterLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (filters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_filters), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(filters) { index, filter ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = filter.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        Text(
                            text = "${filter.type} • ${filter.compensationStops} stop(s) comp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(filter.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.filter_24px),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, filters.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onFilterClick(filter.id) },
                            onLongClick = { onFilterLongClick(filter.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FlashList(
    flashes: List<Flash>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onFlashClick: (Long) -> Unit,
    onFlashLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (flashes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_flashes), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(flashes) { index, flash ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = "${flash.make} ${flash.model}",
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        Text(
                            text = "GN: ${flash.guideNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(flash.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_flash_on),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, flashes.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onFlashClick(flash.id) },
                            onLongClick = { onFlashLongClick(flash.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LensList(
    lenses: List<LensWithCameras>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onLensClick: (Long) -> Unit,
    onLensLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (lenses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_lenses), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(lenses) { index, lensWithCameras ->
                val lens = lensWithCameras.lens
                ListItem(
                    headlineContent = { 
                        Text(
                            text = "${lens.make} ${lens.model}",
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        val focalLength = if (lens.minFocalLength == lens.maxFocalLength) {
                            "${lens.minFocalLength}mm"
                        } else {
                            "${lens.minFocalLength}-${lens.maxFocalLength}mm"
                        }
                        val aperture = if (lens.minAperture == lens.maxAperture) {
                            "f/${lens.minAperture}"
                        } else {
                            "f/${lens.minAperture}–f/${lens.maxAperture}"
                        }
                        Column {
                            Text(
                                text = "$focalLength $aperture • ${lens.mount}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            ) 
                            if (lensWithCameras.cameras.isNotEmpty()) {
                                Text(
                                    text = "Used with: ${lensWithCameras.cameras.joinToString { it.name }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    leadingContent = {
                        if (selectedIds.contains(lens.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.lensd),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, lenses.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onLensClick(lens.id) },
                            onLongClick = { onLensLongClick(lens.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CameraList(
    cameras: List<Camera>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onCameraClick: (Long) -> Unit,
    onCameraLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (cameras.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_cameras), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(cameras) { index, camera ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = camera.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        Column {
                            Text(
                                text = camera.type.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            ) 
                            if (camera.shutterSpeeds.isNotEmpty()) {
                                val shutterText = "${camera.shutterSpeeds.first()}..${camera.shutterSpeeds.last()}"
                                Text(
                                    text = shutterText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    leadingContent = {
                        if (selectedIds.contains(camera.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_photo_camera),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, cameras.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onCameraClick(camera.id) },
                            onLongClick = { onCameraLongClick(camera.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilmList(
    films: List<Film>,
    selectedIds: Set<Long>,
    isReorderMode: Boolean,
    onFilmClick: (Long) -> Unit,
    onFilmLongClick: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    if (films.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_films), style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding + 80.dp)
        ) {
            itemsIndexed(films) { index, film ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = film.name,
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    supportingContent = { 
                        Text(
                            text = "Abbr: ${film.abbreviation} • ISO ${film.iso} • ${film.expectedExposures} exp • ${film.type}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        ) 
                    },
                    leadingContent = {
                        if (selectedIds.contains(film.id)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle), 
                                contentDescription = "Selected", 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera_roll),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    trailingContent = if (isReorderMode) {
                        { ReorderButtons(index, films.size, onMove) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onFilmClick(film.id) },
                            onLongClick = { onFilmLongClick(film.id) }
                        )
                        .padding(vertical = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun ReorderButtons(
    index: Int,
    listSize: Int,
    onMove: (Int, Int) -> Unit,
) {
    Row {
        IconButton(
            onClick = { onMove(index, index - 1) },
            enabled = index > 0
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back), 
                contentDescription = "Move Up", 
                modifier = Modifier.size(20.dp).rotate(90f)
            )
        }
        IconButton(
            onClick = { onMove(index, index + 1) },
            enabled = index < listSize - 1
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back), 
                contentDescription = "Move Down", 
                modifier = Modifier.size(20.dp).rotate(-90f)
            )
        }
    }
}
