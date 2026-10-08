package com.fotologue.core.data

import kotlinx.serialization.Serializable

@Serializable
enum class CameraType(val displayName: String, val isInterchangeable: Boolean) {
    DSLR("DSLR", true),
    MIRRORLESS("Mirrorless", true),
    COMPACT("Compact", false),
    TLR("TLR (Twin Lens Reflex)", false),
    BRIDGE("Bridge", false),
    FILM_SLR("Film SLR", true),
    RANGEFINDER("Rangefinder", true),
    MEDIUM_FORMAT("Medium Format", false),
    SMARTPHONE("Smartphone", false),
    ACTION("Action", false),
    INSTANT("Instant", false),
    DIGITAL_CINE("Digital Cine", true),
    TRAIL_WILDLIFE("Trail/Wildlife", false),
    THREE_SIXTY_DEGREE("360-Degree", false),
    UNDERWATER("Underwater", false);

    companion object {
        fun fromString(value: String): CameraType {
            val v = value.trim()
            if (v.equals("TLR", ignoreCase = true) || v.contains("TLR", ignoreCase = true)) return TLR
            if (v.contains("Medium Format", ignoreCase = true)) return MEDIUM_FORMAT
            if (v.contains("Film SLR", ignoreCase = true) || v.contains("SLR", ignoreCase = true)) return FILM_SLR
            if (v.contains("Rangefinder", ignoreCase = true)) return RANGEFINDER
            if (v.contains("Compact", ignoreCase = true)) return COMPACT

            return entries.find { 
                it.displayName.equals(v, ignoreCase = true) || 
                it.name.equals(v, ignoreCase = true) 
            } ?: COMPACT
        }
    }
}
