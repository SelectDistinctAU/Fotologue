package com.fotologue.app.util

import com.fotologue.core.data.Camera
import com.fotologue.core.data.ExposureSettings
import com.fotologue.core.data.Filter
import com.fotologue.core.data.Lens
import com.fotologue.core.data.Teleconverter
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
            val minAp = minOf(lens.minAperture, lens.maxAperture)
            val maxAp = maxOf(lens.minAperture, lens.maxAperture)
            val effectiveMaxAperture = minAp * 2.0.pow(lightLossStops / 2.0)
            
            val rangeStart = minOf(effectiveMaxAperture, maxAp)
            val rangeEnd = maxOf(effectiveMaxAperture, maxAp)
            
            apertures = apertures.filter { it in rangeStart..rangeEnd }
            
            if (!apertures.contains(effectiveMaxAperture)) apertures = apertures + effectiveMaxAperture
            if (!apertures.contains(maxAp)) apertures = apertures + maxAp
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
        if (seconds <= 0 || seconds.isNaN() || seconds.isInfinite()) return null
        
        val speedsToUse = expandShutterSpeeds(cameraSpeeds)

        return speedsToUse.minByOrNull { speed ->
            val speedSeconds = parseShutterSpeed(speed)
            abs(speedSeconds - seconds)
        }
    }

    fun expandShutterSpeeds(cameraSpeeds: List<String>): List<String> {
        if (cameraSpeeds.isEmpty()) return defaultShutterSpeeds

        val discreteSpeeds = mutableListOf<String>()
        for (raw in cameraSpeeds) {
            val item = raw.trim()
            if (item.contains(" - ") || (item.contains("-") && !item.startsWith("-"))) {
                val parts = item.split(" - ", "-")
                if (parts.size >= 2) {
                    val minSec = parseShutterSpeed(parts[0])
                    val maxSec = parseShutterSpeed(parts[1])
                    val startSec = minOf(minSec, maxSec)
                    val endSec = maxOf(minSec, maxSec)

                    val matchingDefaults = defaultShutterSpeeds.filter { speedStr ->
                        val sec = parseShutterSpeed(speedStr)
                        sec in (startSec - 0.0001)..(endSec + 0.0001)
                    }
                    if (matchingDefaults.isNotEmpty()) {
                        discreteSpeeds.addAll(matchingDefaults)
                    } else {
                        discreteSpeeds.addAll(defaultShutterSpeeds)
                    }
                } else {
                    discreteSpeeds.addAll(defaultShutterSpeeds)
                }
            } else if (!item.equals("B", ignoreCase = true) && item.isNotBlank()) {
                discreteSpeeds.add(item)
            }
        }
        return if (discreteSpeeds.isNotEmpty()) discreteSpeeds.distinct() else defaultShutterSpeeds
    }

    private fun parseShutterSpeed(speed: String): Double {
        return try {
            val cleanSpeed = speed.replace("s", "", ignoreCase = true)
                .replace("B", "", ignoreCase = true)
                .replace(",", "")
                .trim()

            if (cleanSpeed.isBlank()) return 1.0

            if (cleanSpeed.contains("/")) {
                val parts = cleanSpeed.split("/")
                if (parts.size == 2) {
                    val num = parts[0].trim().toDoubleOrNull() ?: 1.0
                    val den = parts[1].trim().toDoubleOrNull() ?: 1.0
                    return if (den != 0.0) num / den else 1.0
                }
            }
            cleanSpeed.toDoubleOrNull() ?: 1.0
        } catch (_: Throwable) {
            1.0
        }
    }
}
