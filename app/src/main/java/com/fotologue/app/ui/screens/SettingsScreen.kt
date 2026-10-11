package com.fotologue.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.core.data.MacroCopyNamingOptions
import com.fotologue.core.data.MacroCopyPreset
import com.fotologue.core.data.PictureExportSize
import com.fotologue.app.ui.viewmodel.SettingsEvent
import com.fotologue.app.ui.viewmodel.SettingsViewModel
import com.fotologue.app.R
import com.fotologue.app.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCalibration: () -> Unit,
    onNavigateToRollCounter: () -> Unit,
    onShowHelp: () -> Unit,
) {
    val isThumbnailEnabled by viewModel.isThumbnailEnabled.collectAsStateWithLifecycle()
    val isFaceDetectionEnabled by viewModel.isFaceDetectionEnabled.collectAsStateWithLifecycle()
    val faceDetectionDisclosureShown by viewModel.faceDetectionDisclosureShown.collectAsStateWithLifecycle()
    val pictureExportSize by viewModel.pictureExportSize.collectAsStateWithLifecycle()
    val contributorId by viewModel.contributorId.collectAsStateWithLifecycle()
    val contributorAlias by viewModel.contributorAlias.collectAsStateWithLifecycle()
    val contributorFullName by viewModel.contributorFullName.collectAsStateWithLifecycle()
    val creatorName by viewModel.creatorName.collectAsStateWithLifecycle()
    val isAutoCopyrightEnabled by viewModel.isAutoCopyrightEnabled.collectAsStateWithLifecycle()
    val copyrightNotice by viewModel.copyrightNotice.collectAsStateWithLifecycle()
    val contactInfo by viewModel.contactInfo.collectAsStateWithLifecycle()
    var showExportSizeMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showFaceDisclosure by remember { mutableStateOf(false) }

    if (showFaceDisclosure) {
        AlertDialog(
            onDismissRequest = { showFaceDisclosure = false },
            title = { Text(stringResource(R.string.face_detection_disclosure_title)) },
            text = { Text(stringResource(R.string.face_detection_disclosure_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showFaceDisclosure = false
                        viewModel.setFaceDetectionDisclosureShown()
                        viewModel.toggleFaceDetection(enabled = true)
                    }
                ) {
                    Text(stringResource(R.string.action_enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFaceDisclosure = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    val backupSuccessMsg = stringResource(R.string.backup_success)
    val restoreSuccessMsg = stringResource(R.string.restore_success)
    val errorMessageFormat = stringResource(R.string.error_message)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.BackupSuccess -> snackbarHostState.showSnackbar(backupSuccessMsg)
                is SettingsEvent.RestoreSuccess -> snackbarHostState.showSnackbar(restoreSuccessMsg)
                is SettingsEvent.Error -> snackbarHostState.showSnackbar(errorMessageFormat.format(event.message))
            }
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    viewModel.backup(outputStream)
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    viewModel.restore(inputStream)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.settings_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settings", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Return to Application Hub")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.header_capture),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.setting_auto_thumbnail),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.setting_auto_thumbnail_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isThumbnailEnabled,
                    onCheckedChange = { viewModel.toggleThumbnail(it) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.setting_face_detection),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.setting_face_detection_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isFaceDetectionEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && !faceDetectionDisclosureShown) {
                            showFaceDisclosure = true
                        } else {
                            viewModel.toggleFaceDetection(enabled)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                onClick = onNavigateToRollCounter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Film Roll Counter",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Manage current roll number and frame count",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.settings_panorama_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            val photoCanvasMode by viewModel.photoCanvasMode.collectAsStateWithLifecycle()

            // 1. APP THEME PALETTE SECTION
            Text(
                text = "App Theme Palette",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Select FlatLaf or Photographic color palettes for app controls:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "SYSTEM_DEFAULT" to "System Default",
                    "DAYLIGHT_LIGHT" to "Daylight Clean (Light)",
                    "SILVER_GELATIN" to "Silver Gelatin (B&W)",
                    "WARM_SEPIA" to "Warm Sepia (Toned)",
                    "SAFELIGHT_RED" to "Red Shift (Night Vision)"
                ).forEach { (themeKey, themeName) ->
                    FilterChip(
                        selected = appTheme == themeKey,
                        onClick = { viewModel.setAppTheme(themeKey) },
                        label = { Text(themeName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. ISO 3664 PHOTO REVIEW CANVAS SECTION
            Text(
                text = "Photo Review Canvas (ISO 3664)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Isolated neutral background color for zero-bias image evaluation:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ISO_MID_GRAY" to "ISO Gray (#505050)",
                    "PITCH_BLACK" to "Pitch Black (#00)",
                    "FLAT_WHITE" to "White (#FFF)"
                ).forEach { (canvasKey, canvasName) ->
                    FilterChip(
                        selected = photoCanvasMode == canvasKey,
                        onClick = { viewModel.setPhotoCanvasMode(canvasKey) },
                        label = { Text(canvasName, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. RED SHIFT & NIGHT VISION NOTICE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ℹ️ Red Shift & Night Vision Notice",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Red Shift eliminates blue light to preserve night-adapted vision during astrophotography and darkroom logbook duties. Digital screens cannot replace optical darkroom safelights. Always test with your specific paper stock, maintain distance from paper areas, and load panchromatic film in total darkness.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.header_photographer_info),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            OutlinedTextField(
                value = creatorName,
                onValueChange = { viewModel.setCreatorName(it) },
                label = { Text(stringResource(R.string.label_creator)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isAutoCopyrightEnabled,
                    onCheckedChange = { viewModel.setAutoCopyrightEnabled(it) }
                )
                Text(
                    text = stringResource(R.string.label_auto_copyright),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (isAutoCopyrightEnabled) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                Text(
                    text = stringResource(R.string.label_auto_copyright_preview, currentYear, creatorName.ifBlank { "[Name]" }),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = copyrightNotice,
                    onValueChange = { viewModel.setCopyrightNotice(it) },
                    label = { Text(stringResource(R.string.label_copyright)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = contactInfo,
                onValueChange = { viewModel.setContactInfo(it) },
                label = { Text(stringResource(R.string.label_contact)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.photographer_info_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            Text(
                text = stringResource(R.string.header_export),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Box {
                Surface(
                    onClick = { showExportSizeMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = stringResource(R.string.setting_export_size),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = pictureExportSize.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                DropdownMenu(
                    expanded = showExportSizeMenu,
                    onDismissRequest = { showExportSizeMenu = false }
                ) {
                    PictureExportSize.entries.forEach { size ->
                        DropdownMenuItem(
                            text = { Text(size.label) },
                            onClick = {
                                viewModel.setPictureExportSize(size)
                                showExportSizeMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            // Camera Macro Copying Presets Section
            val macroCopyPresets by viewModel.macroCopyPresets.collectAsStateWithLifecycle()
            var showAddPresetDialog by remember { mutableStateOf(false) }
            var presetNameInput by remember { mutableStateOf("") }
            var presetPrefixInput by remember { mutableStateOf("_DSF") }
            var presetStartNumInput by remember { mutableStateOf("1001") }
            var presetDigitCountInput by remember { mutableStateOf("4") }
            var presetIsReverseInput by remember { mutableStateOf(false) }

            if (showAddPresetDialog) {
                AlertDialog(
                    onDismissRequest = { showAddPresetDialog = false },
                    title = { Text("Add Camera Macro Copy Preset") },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = presetNameInput,
                                onValueChange = { presetNameInput = it },
                                label = { Text("Preset Name (e.g. Fujifilm X-T5)") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = presetPrefixInput,
                                onValueChange = { presetPrefixInput = it },
                                label = { Text("Prefix (e.g. _DSF, IMG_)") },
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = presetStartNumInput,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) presetStartNumInput = it },
                                    label = { Text("Start No.") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = presetDigitCountInput,
                                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 1) presetDigitCountInput = it },
                                    label = { Text("Digits") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = presetIsReverseInput,
                                    onCheckedChange = { presetIsReverseInput = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reverse Frame Sequence")
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (presetNameInput.isNotBlank()) {
                                    viewModel.saveMacroCopyPreset(
                                        MacroCopyPreset(
                                            name = presetNameInput.trim(),
                                            options = MacroCopyNamingOptions(
                                                enabled = true,
                                                prefix = presetPrefixInput.trim(),
                                                startNumber = presetStartNumInput.toIntOrNull() ?: 1001,
                                                digitCount = presetDigitCountInput.toIntOrNull() ?: 4,
                                                isReverse = presetIsReverseInput
                                            )
                                        )
                                    )
                                    showAddPresetDialog = false
                                }
                            }
                        ) {
                            Text(stringResource(R.string.action_save))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddPresetDialog = false }) {
                            Text(stringResource(R.string.action_cancel))
                        }
                    }
                )
            }

            Text(
                text = "Camera Macro Copying Presets",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pre-configured camera prefixes and file digit formatting for matching RAW scan files during XMP sidecar export.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            macroCopyPresets.forEach { preset ->
                ListItem(
                    headlineContent = { Text(preset.name, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Prefix: '${preset.options.prefix}' | Digits: ${preset.options.digitCount} | Start: ${preset.options.startNumber} ${if (preset.options.isReverse) "| Reverse" else ""}") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.deleteMacroCopyPreset(preset.id) }) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = "Delete preset")
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    presetNameInput = ""
                    presetPrefixInput = "_DSF"
                    presetStartNumInput = "1001"
                    presetDigitCountInput = "4"
                    presetIsReverseInput = false
                    showAddPresetDialog = true
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Macro Copy Preset")
            }

            val presetRefreshStatus by viewModel.presetRefreshStatus.collectAsStateWithLifecycle()

            OutlinedButton(
                onClick = { viewModel.refreshMasterPresetsFromCloud() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.sync_desktop_24px), contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Master Presets from Cloud")
            }

            presetRefreshStatus?.let { status ->
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            Text(
                text = stringResource(R.string.header_light_meter),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Surface(
                onClick = onNavigateToCalibration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.setting_ev_calibration),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.setting_ev_calibration_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.header_support),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Surface(
                onClick = onShowHelp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.setting_how_to_help),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.setting_how_to_help_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.header_backup_privacy),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = stringResource(R.string.privacy_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val currentConfiguration = LocalConfiguration.current
            Surface(
                onClick = {
                    val locale = currentConfiguration.locales[0]
                    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", locale)
                    val backupFileName = "backup_${dateFormat.format(Date())}.json"
                    createDocumentLauncher.launch(backupFileName)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.action_backup_library),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.action_backup_library_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                onClick = { openDocumentLauncher.launch(arrayOf("application/json")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.action_restore_backup),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.action_restore_backup_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CONTRIBUTOR PROFILE",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Used by Fotologue Desktop to organise and consolidate imported frames.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = contributorId.toString(),
                onValueChange = { 
                    val newId = it.toIntOrNull() ?: 1
                    viewModel.setContributorProfile(newId, contributorAlias, contributorFullName)
                },
                label = { Text("Contributor ID (Int)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = contributorAlias,
                onValueChange = { viewModel.setContributorProfile(contributorId, it, contributorFullName) },
                label = { Text("Alias / Shortcode (e.g. P1)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = contributorFullName,
                onValueChange = { viewModel.setContributorProfile(contributorId, contributorAlias, it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                onClick = { uriHandler.openUri("https://www.fotologue.au/privacy-policy") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.privacy_policy_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_build_info),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.build_version_format, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val configuration = LocalConfiguration.current
                val currentLocale = configuration.locales[0]
                val dateString = remember(currentLocale) { 
                    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", currentLocale)
                    dateFormat.format(Date(BuildConfig.BUILD_TIME))
                }
                Text(
                    text = stringResource(R.string.build_date_format, dateString),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 16.dp))
        }
    }
}
