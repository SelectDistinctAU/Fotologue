package com.fotologue.app.ui.screens

import android.Manifest
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.R
import com.fotologue.core.data.PictureExportSize
import com.fotologue.app.ui.viewmodel.OnboardingUiState
import com.fotologue.app.ui.viewmodel.OnboardingViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(
    viewModel: OnboardingViewModel,
    onAddCamera: () -> Unit,
    onAddLens: () -> Unit,
    onAddFilm: () -> Unit,
    onCalibrateMeter: () -> Unit,
    onShowHelp: () -> Unit,
    onSettingsClick: () -> Unit = {}
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

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val hasLocationPermission = androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    WelcomeScreenContent(
        uiState = uiState,
        isLocationPermissionGranted = hasLocationPermission,
        onRequestLocationPermission = {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        },
        onAddCamera = onAddCamera,
        onAddLens = onAddLens,
        onAddFilm = onAddFilm,
        onCalibrateMeter = onCalibrateMeter,
        onShowHelp = onShowHelp,
        onSettingsClick = onSettingsClick,
        onComplete = viewModel::completeOnboarding,
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
    isLocationPermissionGranted: Boolean = false,
    onRequestLocationPermission: () -> Unit = {},
    onAddCamera: () -> Unit,
    onAddLens: () -> Unit,
    onAddFilm: () -> Unit,
    onCalibrateMeter: () -> Unit,
    onShowHelp: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onComplete: () -> Unit,
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
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
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

            // 1. Equipment Setup (Required)
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

            // 2. Quick Settings (Optional)
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
                    // Location / GPS Tagging Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Automatic GPS Location Tagging",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Automatically attach GPS coordinates to exposure logs for geotagging.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isLocationPermissionGranted,
                            onCheckedChange = { onRequestLocationPermission() }
                        )
                    }

                    HorizontalDivider()

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

                    HorizontalDivider()

                    TextButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_settings), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View All Settings")
                    }
                }
            }

            // 3. Advanced Configuration (Optional)
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
                        isComplete = false,
                        iconRes = R.drawable.ic_handyman,
                        onClick = onCalibrateMeter
                    )
                }
            }

            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isReadyToGo
            ) {
                Text("Let's Go!")
            }

            Text(
                text = stringResource(R.string.welcome_equipment_more_info),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onShowHelp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painter = painterResource(R.drawable.ic_help), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.help))
                }

                OutlinedButton(
                    onClick = { restoreLauncher.launch(arrayOf("application/json")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painter = painterResource(R.drawable.ic_restore), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.restore_from_backup))
                }
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
