package com.fotologue.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Route : NavKey {
    @Serializable
    data object Welcome : Route()

    @Serializable
    data object Hub : Route()

    @Serializable
    data object Timeline : Route()

    @Serializable
    data class Lab(val initialTab: Int = 0) : Route()

    @Serializable
    data object AssetsInventory : Route()

    @Serializable
    data object LabMaintenance : Route()

    @Serializable
    data class AddEditItemMaster(val masterId: Long? = null, val defaultCategory: String? = null) : Route()

    @Serializable
    data class AddStockLot(val masterId: Long? = null) : Route()

    @Serializable
    data class EditStockLot(val lotId: Long) : Route()

    @Serializable
    data object AddEquipment : Route()

    @Serializable
    data class EditEquipment(val equipmentId: Long) : Route()

    @Serializable
    data class AddEditSupplier(val supplierId: Long? = null) : Route()

    @Serializable
    data class SupplierIssueTracker(val supplierId: Long? = null) : Route()

    @Serializable
    data class FilmProcessingSession(val rollId: String? = null) : Route()

    @Serializable
    data class EnlargerPrintingSession(val logId: Long? = null) : Route()

    @Serializable
    data class LogDetail(val logId: Long) : Route()

    @Serializable
    data object AddLog : Route()

    @Serializable
    data class EditLog(val logId: Long) : Route()

    @Serializable
    data object GearLibrary : Route()

    @Serializable
    data object Taxonomy : Route()

    @Serializable
    data class AddCamera(val isPreArrival: Boolean = false) : Route()

    @Serializable
    data class EditCamera(val cameraId: Long) : Route()

    @Serializable
    data class AddFilm(val isPreArrival: Boolean = false) : Route()

    @Serializable
    data class EditFilm(val filmId: Long) : Route()

    @Serializable
    data class AddEditLens(val lensId: Long? = null, val cameraId: Long? = null, val isPreArrival: Boolean = false) : Route()

    @Serializable
    data class AddEditTeleconverter(val teleconverterId: Long? = null) : Route()

    @Serializable
    data class AddEditFilter(val filterId: Long? = null) : Route()

    @Serializable
    data class AddEditFlash(val flashId: Long? = null) : Route()

    @Serializable
    data class AddEditViewfinder(val viewfinderId: Long? = null) : Route()

    @Serializable
    data class AddEditCameraKit(val kitId: Long? = null) : Route()

    @Serializable
    data class LightMeter(
        val targetIso: Int,
        val cameraId: Long? = null,
        val lensId: Long? = null,
        val teleconverterId: Long? = null,
        val filterId: Long? = null,
        val filmId: Long? = null,
        val focalLength: Float? = null,
        val autoStartCalibration: Boolean = false
    ) : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Calibration : Route()

    @Serializable
    data object Help : Route()

    @Serializable
    data object RollCounter : Route()

    @Serializable
    data object ArchiveVault : Route()

    @Serializable
    data class ContactSheet(
        val cameraId: Long? = null,
        val filmId: Long? = null,
        val rollNumber: Int? = null
    ) : Route()
}
