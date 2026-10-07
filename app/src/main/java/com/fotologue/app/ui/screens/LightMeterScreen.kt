package com.fotologue.app.ui.screens

import android.Manifest
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.core.data.ExposureSettings
import com.fotologue.app.ui.viewmodel.LightMeterViewModel
import com.fotologue.app.ui.viewmodel.LightMeterUiState
import com.fotologue.app.util.LuminanceAnalyzer
import com.fotologue.app.util.MeteringMode
import com.fotologue.app.R
import java.util.concurrent.Executors
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.graphics.RenderEffect
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.fotologue.core.data.Film
import com.fotologue.app.ui.viewmodel.CalibrationStage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@Composable
fun LightMeterScreen(
    viewModel: LightMeterViewModel,
    onBack: () -> Unit,
    onUseSettings: (ExposureSettings) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isThumbnailEnabled by viewModel.isThumbnailEnabled.collectAsStateWithLifecycle()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (!cameraPermissionState.status.isGranted) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Camera Permission Required") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.camera_24px),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Camera Access Needed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Fotologue requires camera access for live-view spot and centre-weighted exposure metering. Your video feed is processed 100% on-device and never stored or shared.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Camera Permission")
                }
            }
        }
        return
    }

    // NAVIGATION FIX: Return to Settings if we were in Wizard-only mode
    LaunchedEffect(uiState.calibrationFinished, uiState.calibrationCancelled) {
        if (uiState.calibrationFinished) {
            if (uiState.isWizardOnly) {
                onBack()
            }
            viewModel.onCalibrationFinishedHandled()
        }
        if (uiState.calibrationCancelled) {
            if (uiState.isWizardOnly) {
                onBack()
            }
            viewModel.onCalibrationCancelledHandled()
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    val imageCapture = remember { ImageCapture.Builder().build() }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(lifecycleOwner, cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.display.rotation
                    } else {
                        @Suppress("DEPRECATION")
                        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                    }

                    val preview = Preview.Builder()
                        .setTargetRotation(rotation)
                        .build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                    val imageAnalysisBuilder = ImageAnalysis.Builder()
                        .setTargetRotation(rotation)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    
                    Camera2Interop.Extender(imageAnalysisBuilder).setSessionCaptureCallback(object : CameraCaptureSession.CaptureCallback() {
                        override fun onCaptureCompleted(
                            session: CameraCaptureSession,
                            request: CaptureRequest,
                            result: TotalCaptureResult
                        ) {
                            viewModel.onCaptureResultUpdated(result)
                        }
                    })

                    val imageAnalysis = imageAnalysisBuilder.build()

                    imageAnalysis.setAnalyzer(cameraExecutor, LuminanceAnalyzer(
                        getMeteringMode = { viewModel.uiState.value.meteringMode },
                        getSpotRect = { viewModel.uiState.value.spotRect },
                        getLatestCaptureResult = { viewModel.latestCaptureResult },
                        getEvCalibration = { viewModel.getCalibrationValue() },
                        getBlackLevelOffset = { viewModel.blackLevelOffset.value },
                        getLuminanceGain = { viewModel.luminanceGain.value },
                        isFaceDetectionEnabled = { viewModel.isFaceDetectionEnabled.value },
                        onEvCalculated = { viewModel.onEvCalculated(it) },
                        onRawLuminanceCalculated = { viewModel.onRawLuminanceCalculated(it) },
                        onFaceDetected = viewModel::onFaceDetected
                    ))

                    imageCapture.targetRotation = rotation

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                        imageCapture
                    )

                    try {
                        val zoom = (uiState.currentFocalLength?.toFloat() ?: 50f) / 24.0f
                        camera.cameraControl.setZoomRatio(zoom.coerceIn(1.0f, 8.0f))
                    } catch (_: Exception) {
                        // Zoom ratio handled
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    fun takeThumbnail(onComplete: (String?) -> Unit) {
        val thumbnailsDir = File(context.filesDir, "thumbnails")
        if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs()
        }
        val file = File(thumbnailsDir, "thumb_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onComplete(Uri.fromFile(file).toString())
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onComplete(null)
                }
            }
        )
    }

    LightMeterScreenContent(
        uiState = uiState,
        isLandscape = isLandscape,
        isThumbnailEnabled = isThumbnailEnabled,
        isCameraPermissionGranted = cameraPermissionState.status.isGranted,
        shouldShowCameraPermissionRationale = cameraPermissionState.status.shouldShowRationale,
        onCameraPermissionRequest = { cameraPermissionState.launchPermissionRequest() },
        previewView = previewView,
        onBack = onBack,
        onToggleMeteringMode = viewModel::toggleMeteringMode,
        onFocalLengthChanged = viewModel::onFocalLengthChanged,
        onAdjustEvOffset = viewModel::adjustEvOffset,
        onSpotLocationUpdated = viewModel::updateSpotLocation,
        onTakeThumbnail = { takeThumbnail(it) },
        onToggleFilmSimulation = viewModel::toggleFilmSimulation,
        onStartCalibration = viewModel::startCalibration,
        onCaptureBlackPoint = viewModel::captureBlackPoint,
        onCaptureGrayPoint = { viewModel.captureGrayPoint(12.0) },
        onCancelCalibration = viewModel::cancelCalibration,
        onUseSettings = onUseSettings,
        targetIso = viewModel.targetIso
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightMeterScreenContent(
    uiState: LightMeterUiState,
    isLandscape: Boolean,
    isThumbnailEnabled: Boolean,
    isCameraPermissionGranted: Boolean,
    shouldShowCameraPermissionRationale: Boolean,
    onCameraPermissionRequest: () -> Unit,
    previewView: PreviewView,
    onBack: () -> Unit,
    onToggleMeteringMode: () -> Unit,
    onFocalLengthChanged: (Int) -> Unit,
    onAdjustEvOffset: (Float) -> Unit,
    onSpotLocationUpdated: (Int, Int, Int, Int) -> Unit,
    onTakeThumbnail: ((String?) -> Unit) -> Unit,
    onToggleFilmSimulation: () -> Unit,
    onStartCalibration: () -> Unit,
    onCaptureBlackPoint: () -> Unit,
    onCaptureGrayPoint: () -> Unit,
    onCancelCalibration: () -> Unit,
    onUseSettings: (ExposureSettings) -> Unit,
    targetIso: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.light_meter_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = onStartCalibration) {
                        Icon(
                            painter = painterResource(R.drawable.ic_handyman),
                            contentDescription = "Pro Calibration Wizard"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .consumeWindowInsets(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    CameraPreviewContent(
                        isCameraPermissionGranted = isCameraPermissionGranted,
                        shouldShowCameraPermissionRationale = shouldShowCameraPermissionRationale,
                        onCameraPermissionRequest = onCameraPermissionRequest,
                        previewView = previewView,
                        onSpotLocationUpdated = onSpotLocationUpdated,
                        uiState = uiState
                    )
                }

                InputSettingsColumn(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                        .padding(16.dp),
                    uiState = uiState,
                    isThumbnailEnabled = isThumbnailEnabled,
                    onToggleMeteringMode = onToggleMeteringMode,
                    onToggleFilmSimulation = onToggleFilmSimulation,
                    onFocalLengthChanged = onFocalLengthChanged,
                    onAdjustEvOffset = onAdjustEvOffset,
                    onTakeThumbnail = onTakeThumbnail,
                    onUseSettings = onUseSettings,
                    targetIso = targetIso,
                    bottomPadding = innerPadding.calculateBottomPadding()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .consumeWindowInsets(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CameraPreviewContent(
                        isCameraPermissionGranted = isCameraPermissionGranted,
                        shouldShowCameraPermissionRationale = shouldShowCameraPermissionRationale,
                        onCameraPermissionRequest = onCameraPermissionRequest,
                        previewView = previewView,
                        onSpotLocationUpdated = onSpotLocationUpdated,
                        uiState = uiState
                    )
                }

                InputSettingsColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                        .padding(16.dp),
                    uiState = uiState,
                    isThumbnailEnabled = isThumbnailEnabled,
                    onToggleMeteringMode = onToggleMeteringMode,
                    onToggleFilmSimulation = onToggleFilmSimulation,
                    onFocalLengthChanged = onFocalLengthChanged,
                    onAdjustEvOffset = onAdjustEvOffset,
                    onTakeThumbnail = onTakeThumbnail,
                    onUseSettings = onUseSettings,
                    targetIso = targetIso,
                    bottomPadding = innerPadding.calculateBottomPadding()
                )
            }
        }

        if (uiState.calibrationStage != CalibrationStage.NONE) {
            CalibrationWizardOverlay(
                stage = uiState.calibrationStage,
                rawLuminance = uiState.rawLuminance,
                onCaptureBlack = onCaptureBlackPoint,
                onCaptureGray = onCaptureGrayPoint,
                onCancel = onCancelCalibration
            )
        }
    }
}

@Composable
private fun CameraPreviewContent(
    isCameraPermissionGranted: Boolean,
    shouldShowCameraPermissionRationale: Boolean,
    onCameraPermissionRequest: () -> Unit,
    previewView: PreviewView,
    onSpotLocationUpdated: (Int, Int, Int, Int) -> Unit,
    uiState: LightMeterUiState
) {
    if (isCameraPermissionGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            onSpotLocationUpdated(
                                offset.x.toInt(),
                                offset.y.toInt(),
                                size.width,
                                size.height
                            )
                        }
                    }
            )

            if (uiState.isSimulatingFilm) {
                uiState.film?.let { film ->
                    when (film.type) {
                        Film.TYPE_BLACK_AND_WHITE -> {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    color = Color.Gray,
                                    blendMode = BlendMode.Saturation
                                )
                            }
                        }
                        Film.TYPE_COLOR_NEGATIVE -> {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    color = Color(0xFFFFCC00).copy(alpha = 0.05f),
                                    blendMode = BlendMode.Color
                                )
                            }
                        }
                        Film.TYPE_SLIDE -> {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    color = Color(0xFF0033FF).copy(alpha = 0.03f),
                                    blendMode = BlendMode.Overlay
                                )
                            }
                        }
                    }
                }
            }

            MeteringOverlay(
                mode = uiState.meteringMode,
                spotRect = uiState.spotRect
            )
        }
    } else {
        PermissionRequestContent(
            shouldShowRationale = shouldShowCameraPermissionRationale,
            onRequestPermission = onCameraPermissionRequest,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun InputSettingsColumn(
    modifier: Modifier,
    uiState: LightMeterUiState,
    isThumbnailEnabled: Boolean,
    onToggleMeteringMode: () -> Unit,
    onToggleFilmSimulation: () -> Unit,
    onFocalLengthChanged: (Int) -> Unit,
    onAdjustEvOffset: (Float) -> Unit,
    onTakeThumbnail: ((String?) -> Unit) -> Unit,
    onUseSettings: (ExposureSettings) -> Unit,
    targetIso: Int,
    bottomPadding: Dp = 0.dp
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val evText = stringResource(R.string.ev_display, uiState.currentEv)
            Text(
                text = evText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (uiState.film != null) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onToggleFilmSimulation) {
                    Icon(
                        painter = painterResource(R.drawable.filter_b_and_w_24px),
                        contentDescription = "Toggle Film Simulation",
                        tint = if (uiState.isSimulatingFilm) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onToggleMeteringMode) {
                Text(
                    if (uiState.meteringMode == MeteringMode.SPOT) stringResource(R.string.metering_mode_spot)
                    else stringResource(R.string.metering_mode_center)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.label_iso) + " $targetIso",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            uiState.filter?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "| ${it.name} (-${it.compensationStops} stops)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        uiState.effectiveFocalLength?.let { ef ->
            uiState.effectiveMaxAperture?.let { em ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%.0fmm f/%.1f (Effective)".format(ef, em),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large [-] and [+] Focal Length Fine-Tuning Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentFL = uiState.currentFocalLength ?: 50
            Text(
                text = "Focal Length (${currentFL}mm):",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = { onFocalLengthChanged((currentFL - 5).coerceAtLeast(14)) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("-", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${currentFL}mm",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                FilledTonalIconButton(
                    onClick = { onFocalLengthChanged((currentFL + 5).coerceAtMost(800)) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        if (uiState.lens != null && (uiState.lens.minFocalLength != uiState.lens.maxFocalLength)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(
                    R.string.focal_length_display,
                    uiState.currentFocalLength ?: uiState.lens.minFocalLength
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Slider(
                value = (uiState.currentFocalLength ?: uiState.lens.minFocalLength).toFloat(),
                onValueChange = { onFocalLengthChanged(it.toInt()) },
                valueRange = uiState.lens.minFocalLength.toFloat()..uiState.lens.maxFocalLength.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = bottomPadding + 16.dp)
        ) {
            items(uiState.suggestions) { suggestion ->
                ExposureSuggestionItem(
                    suggestion = suggestion,
                    onClick = {
                        if (isThumbnailEnabled) {
                            onTakeThumbnail { uri ->
                                onUseSettings(suggestion.copy(thumbnailUri = uri))
                            }
                        } else {
                            onUseSettings(suggestion)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalibrationWizardOverlay(
    stage: CalibrationStage,
    rawLuminance: Double,
    onCaptureBlack: () -> Unit,
    onCaptureGray: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = true) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "The Wizard's Calibration",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            when (stage) {
                CalibrationStage.BLACK_POINT -> {
                    Text(
                        text = "STAGE 1: THE VOID",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cover the camera lens completely to measure sensor noise. Be careful it doesn't stare back.",
                        textAlign = TextAlign.Center,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Sensor Signal: %.2f".format(rawLuminance),
                        style = MaterialTheme.typography.displaySmall,
                        color = if (rawLuminance < 10) Color.Green else Color.Red
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onCaptureBlack, enabled = rawLuminance < 20) {
                        Text("Measure Black Point")
                    }
                }
                CalibrationStage.GRAY_POINT -> {
                    Text(
                        text = "STAGE 2: THE BALANCE",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Point at an 18% Gray Card or a neutral, evenly lit surface (like a gray wall) in daylight.",
                        textAlign = TextAlign.Center,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Luminance: %.2f".format(rawLuminance),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onCaptureGray) {
                        Text("Calibrate Sensitivity")
                    }
                }
                else -> {}
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun PermissionRequestContent(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val textToShow = if (shouldShowRationale) {
            stringResource(R.string.permission_camera_rationale)
        } else {
            stringResource(R.string.permission_camera_required)
        }
        Text(textToShow, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequestPermission) {
            Text(stringResource(R.string.action_request_permission))
        }
    }
}

@Composable
fun MeteringOverlay(mode: MeteringMode, spotRect: Rect?) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        when (mode) {
            MeteringMode.SPOT -> {
                if (spotRect != null) {
                    drawRect(
                        color = Color.Yellow,
                        topLeft = Offset(spotRect.left.toFloat(), spotRect.top.toFloat()),
                        size = Size(spotRect.width().toFloat(), spotRect.height().toFloat()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    drawRect(
                        color = Color.Yellow,
                        topLeft = Offset((size.width / 2) - 50f, (size.height / 2) - 50f),
                        size = Size(100f, 100f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            MeteringMode.CENTER_WEIGHTED -> {
                drawRect(
                    color = Color.White.copy(alpha = 0.5f),
                    topLeft = Offset(size.width / 4, size.height / 4),
                    size = Size(size.width / 2, size.height / 2),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ExposureSuggestionItem(
    suggestion: ExposureSettings,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = suggestion.aperture, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = suggestion.shutterSpeed, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.action_use), color = MaterialTheme.colorScheme.primary)
        }
    }
}
