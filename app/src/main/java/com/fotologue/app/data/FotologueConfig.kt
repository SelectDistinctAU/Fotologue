package com.fotologue.app.data

import kotlinx.serialization.Serializable

@Serializable
data class FotologueConfig(
    val cameras: List<Camera> = emptyList(),
    val lenses: List<Lens> = emptyList(),
    val cameraLensCrossRefs: List<CameraLensCrossRef> = emptyList(),
    val viewfinders: List<Viewfinder> = emptyList(),
    val cameraViewfinderCrossRefs: List<CameraViewfinderCrossRef> = emptyList(),
    val teleconverters: List<Teleconverter> = emptyList(),
    val cameraTeleconverterCrossRefs: List<CameraTeleconverterCrossRef> = emptyList(),
    val filters: List<Filter> = emptyList(),
    val cameraFilterCrossRefs: List<CameraFilterCrossRef> = emptyList(),
    val flashes: List<Flash> = emptyList(),
    val cameraFlashCrossRefs: List<CameraFlashCrossRef> = emptyList(),
    val films: List<Film> = emptyList(),
    val kits: List<CameraKit> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val logs: List<LogEntry> = emptyList(),
    val logEntryTags: List<LogEntryTagCrossRef> = emptyList(),
    val filmRollSessions: List<FilmRollSession> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)
