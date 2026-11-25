package com.fotologue.core.data

import kotlinx.serialization.Serializable

@Serializable
data class LensSpec(
    val make: String,
    val model: String,
    val modelNumber: String,
    val type: String,
    val mount: String,
    val filterSize: Int,
    val isStabilized: Boolean,
    val minFocusDistance: Double,
    val maxMagnification: Double,
    val minAperture: Double,
    val maxAperture: Double,
    val minFocalLength: Int,
    val maxFocalLength: Int
) {
    companion object {
        fun parseFocalLengthRange(raw: String?): Pair<Int, Int> {
            if (raw.isNullOrBlank()) return Pair(50, 50)
            val clean = raw.lowercase().replace("mm", "").trim()
            if (clean.contains("-")) {
                val parts = clean.split("-")
                val min = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 50
                val max = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: min
                return Pair(min, max)
            } else if (clean.contains("..")) {
                val parts = clean.split("..")
                val min = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 50
                val max = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: min
                return Pair(min, max)
            }
            val single = clean.toIntOrNull() ?: 50
            return Pair(single, single)
        }
    }
}
