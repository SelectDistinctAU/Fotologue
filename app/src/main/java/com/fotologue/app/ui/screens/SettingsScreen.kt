package com.fotologue.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.data.PictureExportSize
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
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
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
            Surface(
                onClick = { createDocumentLauncher.launch("fotologue_backup.json") },
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
