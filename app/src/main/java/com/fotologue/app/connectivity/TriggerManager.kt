package com.fotologue.app.connectivity

import android.content.Context
import android.widget.Toast
import com.fotologue.core.data.*
import com.fotologue.core.data.location.LocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Locale

@Serializable
data class TriggerEvent(
    val event: String,
    val id: String? = null, // Optional unique ID of the trigger device
    val count: Int = 0
)

class TriggerManager(
    private val context: Context,
    private val bleManager: BleManager,
    private val locationProvider: LocationProvider,
    private val gearConfigurationManager: GearConfigurationManager,
    private val repository: FotologueRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    fun startObserving() {
        scope.launch {
            bleManager.receivedData.collectLatest { data ->
                try {
                    val trigger = json.decodeFromString<TriggerEvent>(data)
                    if (trigger.event == "shot") {
                        handleShotTrigger(trigger.id)
                    }
                } catch (e: Exception) {
                    // Ignore invalid JSON
                }
            }
        }
    }

    private suspend fun handleShotTrigger(deviceId: String?) {
        val location = locationProvider.getCurrentLocation()
        val currentConfig = gearConfigurationManager.currentConfiguration.value
        
        // Phase 2 Routing: Try to find a camera linked to this specific Bluetooth device ID
        val targetCamera = if (deviceId != null) {
            repository.allCameras.first().find { it.triggerDeviceId == deviceId }
        } else null

        // If a specific camera is found, we use its ID. 
        // For now, we fall back to the primary UI configuration for Lens/Film/Settings.
        // In a future update, we can track "active sessions" per camera.
        val cameraId = targetCamera?.id ?: currentConfig.cameraId
        
        // Fetch gear details to calculate effective focal length and aperture
        val tc = currentConfig.teleconverterId?.let { repository.getTeleconverterById(it).first() }
        
        val effectiveFocalLength = currentConfig.focalLength?.let { fl ->
            if (tc != null) (fl * tc.magnification).toFloat() else fl
        } ?: currentConfig.lensId?.let { lensId ->
            val lens = repository.getLensById(lensId).first()
            lens?.minFocalLength?.let { fl ->
                if (tc != null) (fl * tc.magnification).toFloat() else fl.toFloat()
            }
        }
        
        val effectiveAperture = if (tc != null) {
            val baseAperture = currentConfig.aperture?.removePrefix("f/")?.toDoubleOrNull() ?: 0.0
            val effectiveVal = baseAperture * tc.magnification
            "f/${if (effectiveVal % 1.0 == 0.0) effectiveVal.toInt() else String.format(Locale.US, "%.1f", effectiveVal)}"
        } else {
            currentConfig.aperture ?: "f/8"
        }

        // Generate a descriptive note based on routing
        val logNote = if (targetCamera != null) {
            "Auto-logged via ${targetCamera.name} trigger"
        } else {
            "Auto-logged via primary BLE trigger"
        }

        // We use the Repository's suggested film roll flow logic to ensure frame counts are correct
        val suggestedRoll = repository.getSuggestedFilmRollFlow(cameraId, currentConfig.filmId).first()

        repository.logExposure(
            notes = logNote,
            environmentalConditions = "",
            cameraId = cameraId,
            filmId = currentConfig.filmId,
            lensId = currentConfig.lensId,
            teleconverterId = currentConfig.teleconverterId,
            filterId = currentConfig.filterId,
            flashId = currentConfig.flashId,
            kitId = currentConfig.kitId,
            focalLength = currentConfig.focalLength ?: (currentConfig.lensId?.let { repository.getLensById(it).first()?.minFocalLength?.toFloat() }),
            effectiveFocalLength = effectiveFocalLength,
            effectiveAperture = effectiveAperture,
            exposureSettings = ExposureSettings(
                shutterSpeed = currentConfig.shutterSpeed ?: "1/125",
                aperture = currentConfig.aperture ?: "f/8",
                iso = currentConfig.iso ?: 400,
                thumbnailUri = null
            ),
            tagIds = emptyList(),
            latitude = location?.latitude,
            longitude = location?.longitude,
            filmRoll = suggestedRoll
        )

        withContext(Dispatchers.Main) {
            val cameraLabel = targetCamera?.name ?: "Active Camera"
            Toast.makeText(context, "Shot logged for $cameraLabel", Toast.LENGTH_SHORT).show()
        }
    }
}
