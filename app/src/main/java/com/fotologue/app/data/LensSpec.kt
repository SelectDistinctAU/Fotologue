package com.fotologue.app.data

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
)
