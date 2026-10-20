package com.fotologue.core.data.usecase

import javax.inject.Inject
import kotlin.math.roundToInt

enum class AnselZone(
    val romanNumeral: String,
    val description: String,
    val evOffset: Double
) {
    ZONE_0("Zone 0", "Pure Black (Maximum Density)", -5.0),
    ZONE_I("Zone I", "Deep Shadow (First Exposure)", -4.0),
    ZONE_II("Zone II", "Shadow Detail (First Texture)", -3.0),
    ZONE_III("Zone III", "Textured Shadow (Fully Detailed)", -2.0),
    ZONE_IV("Zone IV", "Average Shadow / Dark Foliage", -1.0),
    ZONE_V("Zone V", "18% Neutral Mid-Gray Baseline", 0.0),
    ZONE_VI("Zone VI", "Average Skin Tone / Bright Foliage", +1.0),
    ZONE_VII("Zone VII", "Textured Highlight Detail", +2.0),
    ZONE_VIII("Zone VIII", "First Blank Highlight", +3.0),
    ZONE_IX("Zone IX", "Pure White (Specular Highlight)", +4.0),
    ZONE_X("Zone X", "Pure White Light Source", +5.0);

    companion object {
        fun fromEvDelta(deltaEv: Double): AnselZone {
            val offset = deltaEv.coerceIn(-5.0, 5.0)
            val index = (offset + 5.0).roundToInt().coerceIn(0, 10)
            return entries[index]
        }
    }
}

enum class DevelopmentRecommendation(
    val displayName: String,
    val description: String,
    val stopsAdjustment: String
) {
    PUSH_PLUS_ONE("Push Development (N+1)", "Flat low-contrast scene (<5 stops). Expand contrast to pop shadows.", "+1.0 Stop"),
    NORMAL("Normal Development (N)", "Standard scene contrast (5 - 10 stops). Balanced tone reproduction.", "0.0 Stops"),
    PULL_MINUS_ONE("Pull Development (N-1)", "High-contrast scene (>10 stops). Soften contrast to preserve highlights.", "-1.0 Stop");

    companion object {
        fun fromContrastRangeStops(contrastStops: Double): DevelopmentRecommendation {
            return when {
                contrastStops < 5.0 -> PUSH_PLUS_ONE
                contrastStops > 10.0 -> PULL_MINUS_ONE
                else -> NORMAL
            }
        }
    }
}

data class ZoneExposureResult(
    val currentZone: AnselZone,
    val meteredEv100: Double,
    val minSpotEv: Double,
    val maxSpotEv: Double,
    val contrastRangeStops: Double,
    val recommendation: DevelopmentRecommendation,
    val isLatitudeExceeded: Boolean
)

class CalculateZoneExposureUseCase @Inject constructor() {
    
    operator fun invoke(
        spotEv: Double,
        baselineEv: Double,
        allSpotEvs: List<Double>,
        maxLatitudeStops: Double = 10.0
    ): ZoneExposureResult {
        val delta = spotEv - baselineEv
        val zone = AnselZone.fromEvDelta(delta)

        val minEv = if (allSpotEvs.isNotEmpty()) allSpotEvs.minOrNull() ?: spotEv else spotEv
        val maxEv = if (allSpotEvs.isNotEmpty()) allSpotEvs.maxOrNull() ?: spotEv else spotEv
        val contrast = (maxEv - minEv).coerceAtLeast(0.0)

        val rec = DevelopmentRecommendation.fromContrastRangeStops(contrast)
        val exceeded = contrast > maxLatitudeStops

        return ZoneExposureResult(
            currentZone = zone,
            meteredEv100 = baselineEv,
            minSpotEv = minEv,
            maxSpotEv = maxEv,
            contrastRangeStops = contrast,
            recommendation = rec,
            isLatitudeExceeded = exceeded
        )
    }
}
