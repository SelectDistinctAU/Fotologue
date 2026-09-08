package com.fotologue.app.data

import kotlinx.serialization.Serializable

@Serializable
data class FotologueConfig(
    val cameras: List<Camera> = emptyList(),
    val lenses: List<Lens> = emptyList(),
    val cameraLensCrossRefs: List<CameraLensCrossRef> = emptyList(),
    val films: List<Film> = emptyList(),
    val kits: List<CameraKit> = emptyList(),
    val teleconverters: List<Teleconverter> = emptyList(),
    val filters: List<Filter> = emptyList(),
    val flashes: List<Flash> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val logs: List<LogEntry> = emptyList(),
    val logEntryTags: List<LogEntryTagCrossRef> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)
