package com.fotologue.app.data

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
            return entries.find { 
                it.displayName.equals(value, ignoreCase = true) || 
                it.name.equals(value, ignoreCase = true) 
            } ?: COMPACT
        }
    }
}
