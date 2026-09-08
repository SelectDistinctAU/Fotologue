package com.fotologue.app.data

import kotlinx.serialization.Serializable

@Serializable
data class ExposureSettings(
    val shutterSpeed: String,
    val aperture: String,
    val iso: Int,
    val thumbnailUri: String? = null,
    val faceDetected: Boolean = false
)
