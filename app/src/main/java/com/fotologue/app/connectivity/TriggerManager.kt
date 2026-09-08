package com.fotologue.app.connectivity

import android.content.Context
import android.widget.Toast
import com.fotologue.app.data.*
import com.fotologue.app.location.LocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TriggerEvent(
    val event: String,
    val count: Int
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
                        handleShotTrigger()
                    }
                } catch (e: Exception) {
                    // Ignore invalid JSON
                }
            }
        }
    }

    private suspend fun handleShotTrigger() {
        val location = locationProvider.getCurrentLocation()
        val config = gearConfigurationManager.currentConfiguration.value
        
        // Fetch gear details to calculate effective focal length and aperture
        val tc = config.teleconverterId?.let { repository.getTeleconverterById(it).first() }
        
        val effectiveFocalLength = config.focalLength?.let { fl ->
            if (tc != null) (fl * tc.magnification).toFloat() else fl
        } ?: config.lensId?.let { lensId ->
            val lens = repository.getLensById(lensId).first()
            lens?.minFocalLength?.let { fl ->
                if (tc != null) (fl * tc.magnification).toFloat() else fl.toFloat()
            }
        }
        
        val effectiveAperture = if (tc != null) {
            val baseAperture = config.aperture?.removePrefix("f/")?.toDoubleOrNull() ?: 0.0
            val effectiveVal = baseAperture * tc.magnification
            "f/${if (effectiveVal % 1.0 == 0.0) effectiveVal.toInt() else String.format(java.util.Locale.US, "%.1f", effectiveVal)}"
        } else {
            config.aperture ?: "f/8"
        }

        repository.logExposure(
            notes = "Auto-logged via BLE",
            environmentalConditions = "",
            cameraId = config.cameraId,
            filmId = config.filmId,
            lensId = config.lensId,
            teleconverterId = config.teleconverterId,
            filterId = config.filterId,
            flashId = config.flashId,
            kitId = config.kitId,
            focalLength = config.focalLength ?: (repository.getLensById(config.lensId ?: -1).first()?.minFocalLength?.toFloat()),
            effectiveFocalLength = effectiveFocalLength,
            effectiveAperture = effectiveAperture,
            exposureSettings = ExposureSettings(
                shutterSpeed = config.shutterSpeed ?: "1/125",
                aperture = config.aperture ?: "f/8",
                iso = config.iso ?: 400,
                thumbnailUri = null
            ),
            tagIds = emptyList(),
            latitude = location?.latitude,
            longitude = location?.longitude
        )

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Shot auto-logged via BLE", Toast.LENGTH_SHORT).show()
        }
    }
}
