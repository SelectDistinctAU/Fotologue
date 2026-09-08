package com.fotologue.app.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GearConfiguration(
    val cameraId: Long? = null,
    val filmId: Long? = null,
    val lensId: Long? = null,
    val teleconverterId: Long? = null,
    val filterId: Long? = null,
    val flashId: Long? = null,
    val kitId: Long? = null,
    val iso: Int? = null,
    val focalLength: Float? = null,
    val shutterSpeed: String? = null,
    val aperture: String? = null
)

class GearConfigurationManager(
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val currentConfiguration: StateFlow<GearConfiguration> = combine<Any?, GearConfiguration>(
        listOf(
            settingsRepository.lastCameraId,
            settingsRepository.lastFilmId,
            settingsRepository.lastLensId,
            settingsRepository.lastTcId,
            settingsRepository.lastFilterId,
            settingsRepository.lastFlashId,
            settingsRepository.lastKitId,
            settingsRepository.lastIso,
            settingsRepository.lastFocalLength,
            settingsRepository.lastShutterSpeed,
            settingsRepository.lastAperture
        )
    ) { args: Array<Any?> ->
        GearConfiguration(
            cameraId = args[0] as Long?,
            filmId = args[1] as Long?,
            lensId = args[2] as Long?,
            teleconverterId = args[3] as Long?,
            filterId = args[4] as Long?,
            flashId = args[5] as Long?,
            kitId = args[6] as Long?,
            iso = args[7] as Int?,
            focalLength = args[8] as Float?,
            shutterSpeed = args[9] as String?,
            aperture = args[10] as String?
        )
    }.stateIn(scope, SharingStarted.Eagerly, GearConfiguration())

    fun updateConfiguration(config: GearConfiguration) {
        scope.launch {
            settingsRepository.setLastGear(
                cameraId = config.cameraId,
                filmId = config.filmId,
                lensId = config.lensId,
                tcId = config.teleconverterId,
                filterId = config.filterId,
                flashId = config.flashId,
                kitId = config.kitId,
                iso = config.iso,
                focalLength = config.focalLength,
                shutterSpeed = config.shutterSpeed,
                aperture = config.aperture
            )
        }
    }
}
