package com.fotologue.app.util

import android.graphics.Rect
import android.hardware.camera2.CaptureResult
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.log2
import kotlin.math.pow

class LuminanceAnalyzer(
    private val getMeteringMode: () -> MeteringMode,
    private val getSpotRect: () -> Rect?,
    private val getLatestCaptureResult: () -> CaptureResult?,
    private val getEvCalibration: () -> Float = { 0.0f },
    private val getBlackLevelOffset: () -> Float = { 0.0f },
    private val getLuminanceGain: () -> Float = { 1.0f },
    private val isFaceDetectionEnabled: () -> Boolean = { false },
    private val onEvCalculated: (Double) -> Unit,
    private val onRawLuminanceCalculated: (Double) -> Unit = {},
    private val onFaceDetected: (Boolean) -> Unit = {}
) : ImageAnalysis.Analyzer {

    private val faceDetector: FaceDetector by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )
    }

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        var isAsyncWorkStarted = false
        try {
            // 1. Luminance Analysis (Sync)
            if (image.planes.isNotEmpty()) {
                val plane = image.planes[0]
                val buffer = plane.buffer
                
                val width = image.width
                val height = image.height
                val rowStride = plane.rowStride
                val pixelStride = plane.pixelStride

                var sum = 0L
                var count = 0

                val meteringMode = getMeteringMode()
                val spotRect = getSpotRect()

                val analysisRect = when (meteringMode) {
                    MeteringMode.SPOT -> {
                        spotRect ?: Rect(width / 2 - 50, height / 2 - 50, width / 2 + 50, height / 2 + 50)
                    }
                    MeteringMode.CENTER_WEIGHTED -> {
                        Rect(width / 4, height / 4, 3 * width / 4, 3 * height / 4)
                    }
                }

                val left = analysisRect.left.coerceIn(0, width - 1)
                val top = analysisRect.top.coerceIn(0, height - 1)
                val right = analysisRect.right.coerceIn(0, width - 1)
                val bottom = analysisRect.bottom.coerceIn(0, height - 1)

                val step = if ((right - left) * (bottom - top) > 10000) 4 else 1

                val limit = buffer.limit()
                for (y in top..bottom step step) {
                    for (x in left..right step step) {
                        val index = y * rowStride + x * pixelStride
                        if (index in 0 until limit) {
                            sum += buffer.get(index).toInt() and 0xFF
                            count++
                        }
                    }
                }

                val avgLuminance = if (count > 0) sum.toDouble() / count else 0.0
                onRawLuminanceCalculated(avgLuminance)

                val metadata = getLatestCaptureResult()
                
                if (metadata != null) {
                    val exposureTime = metadata.get(CaptureResult.SENSOR_EXPOSURE_TIME)
                    val aperture = metadata.get(CaptureResult.LENS_APERTURE)
                    val iso = metadata.get(CaptureResult.SENSOR_SENSITIVITY)

                    if (exposureTime != null && exposureTime > 0 && aperture != null && aperture > 0 && iso != null && iso > 0) {
                        val t = exposureTime.toDouble() / 1_000_000_000.0
                        val n = aperture.toDouble()
                        val s = iso.toDouble()

                        val evSettings = log2(n.pow(2) / t)
                        val isoAdjustment = log2(s / 100.0)
                        
                        val k = 12.5
                        
                        // Apply Wizard Calibration
                        val adjustedLuminance = (avgLuminance - getBlackLevelOffset()).coerceAtLeast(0.0) * getLuminanceGain()
                        val luminanceAdjustment = if (adjustedLuminance > 0) log2(adjustedLuminance / k) else -10.0
                        
                        val rawEv100 = evSettings - isoAdjustment + luminanceAdjustment
                        
                        if (!rawEv100.isNaN() && !rawEv100.isInfinite()) {
                            val calibratedEv100 = rawEv100 - getEvCalibration()
                            onEvCalculated(calibratedEv100)
                        }
                    }
                }
            }

            // 2. Face Detection (Async)
            val mediaImage = image.image
            if (mediaImage != null && isFaceDetectionEnabled()) {
                try {
                    val visionImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                    isAsyncWorkStarted = true
                    faceDetector.process(visionImage)
                        .addOnSuccessListener { faces ->
                            onFaceDetected(faces.isNotEmpty())
                        }
                        .addOnCompleteListener {
                            try { image.close() } catch (_: Throwable) {}
                        }
                } catch (t: Throwable) {
                    isAsyncWorkStarted = false
                    t.printStackTrace()
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            if (!isAsyncWorkStarted) {
                try { image.close() } catch (_: Throwable) {}
            }
        }
    }
}
