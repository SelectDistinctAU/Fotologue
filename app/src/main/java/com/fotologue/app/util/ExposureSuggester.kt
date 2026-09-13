package com.fotologue.app.util

import com.fotologue.app.data.Camera
import com.fotologue.app.data.ExposureSettings
import com.fotologue.app.data.Filter
import com.fotologue.app.data.Lens
import com.fotologue.app.data.Teleconverter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow

object ExposureSuggester {

    val commonApertures = listOf(1.4, 2.0, 2.8, 4.0, 5.6, 8.0, 11.0, 16.0, 22.0)
    val defaultShutterSpeeds = listOf(
        "1/1000", "1/500", "1/250", "1/125", "1/60", "1/30", "1/15", "1/8", "1/4", "1/2", "1"
    )
    val commonShutterSpeeds = defaultShutterSpeeds

    fun formatAperture(aperture: Double): String {
        val formatted = if (aperture % 1.0 == 0.0) {
            aperture.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", aperture)
        }
        return "f/$formatted"
    }

    fun generateExposureSuggestions(
        ev100: Double, 
        targetIso: Int, 
        camera: Camera? = null,
        lens: Lens? = null,
        teleconverter: Teleconverter? = null,
        filter: Filter? = null
    ): List<ExposureSettings> {
        val suggestions = mutableListOf<ExposureSettings>()
        
        // EV = log2(N^2 / t) + log2(ISO / 100)
        // t = N^2 / (2^(EV - log2(ISO / 100)))
        
        val isoAdjustment = log2(targetIso.toDouble() / 100.0)
        val filterCompensation = filter?.compensationStops ?: 0.0
        val targetEv = ev100 - isoAdjustment - filterCompensation

        var apertures = commonApertures

        if (lens != null) {
            val lightLossStops = teleconverter?.lightLossStops ?: 0.0
            val effectiveMaxAperture = lens.minAperture * 2.0.pow(lightLossStops / 2.0)
            
            // Filter out apertures that are wider (smaller f-number) than effectiveMaxAperture
            // or smaller (larger f-number) than lens.maxAperture.
            apertures = apertures.filter { it in effectiveMaxAperture..lens.maxAperture }
            
            // Ensure the lens's own physical limits (adjusted by TC) are included if they are valid for the gear.
            if (!apertures.contains(effectiveMaxAperture)) apertures = apertures + effectiveMaxAperture
            if (!apertures.contains(lens.maxAperture)) apertures = apertures + lens.maxAperture
            apertures = apertures.sorted()
        }

        for (aperture in apertures) {
            val tSeconds = aperture.pow(2) / 2.0.pow(targetEv)
            val shutterSpeedStr = formatShutterSpeed(tSeconds, camera?.shutterSpeeds ?: emptyList())
            if (shutterSpeedStr != null) {
                suggestions.add(
                    ExposureSettings(
                        shutterSpeed = shutterSpeedStr,
                        aperture = formatAperture(aperture),
                        iso = targetIso
                    )
                )
            }
        }
        
        return suggestions
    }

    private fun formatShutterSpeed(seconds: Double, cameraSpeeds: List<String>): String? {
        if (seconds <= 0) return null
        
        val speedsToUse = if (cameraSpeeds.isNotEmpty()) cameraSpeeds else defaultShutterSpeeds

        return speedsToUse.minByOrNull { speed ->
            val speedSeconds = parseShutterSpeed(speed)
            abs(speedSeconds - seconds)
        }
    }

    private fun parseShutterSpeed(speed: String): Double {
        return if (speed.contains("/")) {
            val parts = speed.split("/")
            if (parts.size == 2) {
                parts[0].toDouble() / parts[1].toDouble()
            } else 1.0
        } else {
            speed.toDoubleOrNull() ?: 1.0
        }
    }
}
