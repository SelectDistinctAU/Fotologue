package com.fotologue.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.net.Uri
import com.fotologue.app.ui.theme.FotologueTheme
import com.fotologue.app.ui.viewmodel.OnboardingViewModel
import com.fotologue.app.ui.viewmodel.OnboardingUiState
import com.fotologue.app.data.PictureExportSize
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(
    viewModel: OnboardingViewModel,
    onAddCamera: () -> Unit,
    onAddLens: () -> Unit,
    onAddFilm: () -> Unit,
    onCalibrateMeter: () -> Unit,
    onShowHelp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        // Core permissions required for the app's primary function
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    WelcomeScreenContent(
        uiState = uiState,
        onAddCamera = onAddCamera,
        onAddLens = onAddLens,
        onAddFilm = onAddFilm,
        onCalibrateMeter = onCalibrateMeter,
        onShowHelp = onShowHelp,
        onRestoreBackup = { uri -> viewModel.restoreBackup(uri, contentResolver) },
        onToggleThumbnail = { enabled ->
            if (enabled && !cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            }
            viewModel.toggleThumbnail(enabled)
        },
        onToggleFaceDetection = { enabled ->
            if (enabled) {
                if (!uiState.faceDetectionDisclosureShown) {
                    viewModel.toggleFaceDetection(enabled = true)
                    viewModel.setFaceDetectionDisclosureShown()
                } else {
                    if (!cameraPermissionState.status.isGranted) {
                        cameraPermissionState.launchPermissionRequest()
                    }
                    viewModel.toggleFaceDetection(enabled = true)
                }
            } else {
                viewModel.toggleFaceDetection(enabled = false)
            }
        },
        onDismissDisclosure = viewModel::setFaceDetectionDisclosureShown,
        onSetExportSize = viewModel::setPictureExportSize
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreenContent(
    uiState: OnboardingUiState,
    onAddCamera: () -> Unit,
    onAddLens: () -> Unit,
    onAddFilm: () -> Unit,
    onCalibrateMeter: () -> Unit,
    onShowHelp: () -> Unit,
    onRestoreBackup: (Uri) -> Unit,
    onToggleThumbnail: (Boolean) -> Unit,
    onToggleFaceDetection: (Boolean) -> Unit,
    onDismissDisclosure: () -> Unit,
    onSetExportSize: (PictureExportSize) -> Unit
) {
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onRestoreBackup(it) }
    }

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
                        onDismissDisclosure()
                        onToggleFaceDetection(true)
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = innerPadding.calculateBottomPadding() + 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                painter = painterResource(R.drawable.ic_photo_camera),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.welcome_headline),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.welcome_body),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Quick Settings (Optional) - Now on Top
            Text(
                text = stringResource(R.string.welcome_quick_settings_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Capture Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_auto_thumbnail),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.setting_auto_thumbnail_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isThumbnailEnabled,
                            onCheckedChange = onToggleThumbnail
                        )
                    }

                    HorizontalDivider()

                    // Face Detection Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_face_detection),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.setting_face_detection_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isFaceDetectionEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !uiState.faceDetectionDisclosureShown) {
                                    showFaceDisclosure = true
                                } else {
                                    onToggleFaceDetection(enabled)
                                }
                            }
                        )
                    }

                    HorizontalDivider()

                    // Export Setting
                    var showExportSizeMenu by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showExportSizeMenu = true }
                        ) {
                            Text(
                                text = stringResource(R.string.setting_export_size),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = uiState.pictureExportSize.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showExportSizeMenu,
                            onDismissRequest = { showExportSizeMenu = false }
                        ) {
                            PictureExportSize.entries.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text(size.label) },
                                    onClick = {
                                        onSetExportSize(size)
                                        showExportSizeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Equipment Setup (Required) - Now below Quick Settings
            Text(
                text = stringResource(R.string.welcome_equipment_setup_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OnboardingStep(
                        label = stringResource(R.string.onboarding_add_camera),
                        isComplete = uiState.hasCamera,
                        iconRes = R.drawable.ic_photo_camera,
                        onClick = onAddCamera
                    )
                    OnboardingStep(
                        label = stringResource(R.string.onboarding_add_lens),
                        description = stringResource(R.string.onboarding_add_lens_description),
                        isComplete = uiState.hasLens,
                        iconRes = R.drawable.lensd,
                        onClick = onAddLens
                    )
                    OnboardingStep(
                        label = stringResource(R.string.onboarding_add_film),
                        isComplete = uiState.hasFilm,
                        iconRes = R.drawable.ic_camera_roll,
                        onClick = onAddFilm
                    )
                }
            }

            // Advanced Configuration (Optional)
            Text(
                text = stringResource(R.string.welcome_advanced_config_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OnboardingStep(
                        label = stringResource(R.string.onboarding_calibrate_meter),
                        description = stringResource(R.string.onboarding_calibrate_meter_description),
                        isComplete = false, // Never "complete" as it's a tool
                        iconRes = R.drawable.ic_handyman,
                        onClick = onCalibrateMeter
                    )
                }
            }

            Text(
                text = stringResource(R.string.welcome_equipment_more_info),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            OutlinedButton(
                onClick = onShowHelp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painter = painterResource(R.drawable.ic_help), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.how_it_works_help))
            }

            OutlinedButton(
                onClick = { restoreLauncher.launch(arrayOf("application/json")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painter = painterResource(R.drawable.ic_restore), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.restore_from_backup))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingStep(
    label: String,
    description: String? = null,
    isComplete: Boolean,
    iconRes: Int,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isComplete,
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isComplete) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isComplete) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = "Complete",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    FotologueTheme {
        // WelcomeScreen(viewModel = ..., ...)
    }
}
