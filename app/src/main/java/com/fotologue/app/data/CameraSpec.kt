package com.fotologue.app.data

import kotlinx.serialization.Serializable

@Serializable
data class CameraSpec(
    val name: String,
    val modelNumber: String = "",
    val type: CameraType,
    val mount: String = "",
    val shutterSpeeds: List<String>,
    val fixedLens: LensSpec? = null
)
