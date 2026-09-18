package com.fotologue.core.data

import com.fotologue.core.data.lab.InventoryItem
import com.fotologue.core.data.lab.LabChemical
import com.fotologue.core.data.lab.LabEquipment
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.data.lab.LabStorageLocation
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.data.lab.PerformanceLog
import com.fotologue.core.data.lab.PrintOutcome
import com.fotologue.core.data.lab.PrintRecipe
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
    val labEquipment: List<LabEquipment> = emptyList(),
    val labChemicals: List<LabChemical> = emptyList(),
    val labSuppliers: List<LabSupplier> = emptyList(),
    val inventoryItems: List<InventoryItem> = emptyList(),
    val orderHistory: List<OrderHistory> = emptyList(),
    val performanceLogs: List<PerformanceLog> = emptyList(),
    val labProcessEvents: List<LabProcessEvent> = emptyList(),
    val printRecipes: List<PrintRecipe> = emptyList(),
    val labStorageLocations: List<LabStorageLocation> = emptyList(),
    val printOutcomes: List<PrintOutcome> = emptyList(),
    val settings: Map<String, String> = emptyMap()
)
