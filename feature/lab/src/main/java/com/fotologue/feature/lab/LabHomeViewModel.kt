package com.fotologue.feature.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.ExposureSettings
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.LogEntry
import com.fotologue.core.data.SettingsRepository
import com.fotologue.core.data.lab.InventoryItem
import com.fotologue.core.data.lab.InventoryItemDao
import com.fotologue.core.data.lab.InventoryMaster
import com.fotologue.core.data.lab.InventoryMasterDao
import com.fotologue.core.data.lab.LabCategory
import com.fotologue.core.data.lab.LabCategoryDao
import com.fotologue.core.data.lab.LabChemical
import com.fotologue.core.data.lab.LabChemicalDao
import com.fotologue.core.data.lab.LabEquipment
import com.fotologue.core.data.lab.LabEquipmentDao
import com.fotologue.core.data.lab.LabEquipmentSubCategory
import com.fotologue.core.data.lab.LabEquipmentSubCategoryDao
import com.fotologue.core.data.lab.LabItemName
import com.fotologue.core.data.lab.LabItemNameDao
import com.fotologue.core.data.lab.LabPresets
import com.fotologue.core.data.lab.LabOrder
import com.fotologue.core.data.lab.LabOrderDao
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.data.lab.LabProcessEventDao
import com.fotologue.core.data.lab.LabStorageLocation
import com.fotologue.core.data.lab.LabStorageLocationDao
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.LabSupplierDao
import com.fotologue.core.data.lab.LabUnit
import com.fotologue.core.data.lab.LabUnitDao
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.data.lab.OrderHistoryDao
import com.fotologue.core.data.lab.PerformanceLog
import com.fotologue.core.data.lab.PerformanceLogDao
import com.fotologue.core.data.lab.StockLot
import com.fotologue.core.data.lab.StockLotDao
import com.fotologue.core.data.lab.SupplierIssueLog
import com.fotologue.core.data.lab.SupplierIssueLogDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabHomeViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val equipmentDao: LabEquipmentDao,
    private val equipmentSubCategoryDao: LabEquipmentSubCategoryDao,
    private val chemicalDao: LabChemicalDao,
    private val supplierDao: LabSupplierDao,
    private val supplierIssueLogDao: SupplierIssueLogDao,
    private val categoryDao: LabCategoryDao,
    private val unitDao: LabUnitDao,
    private val itemNameDao: LabItemNameDao,
    private val inventoryMasterDao: InventoryMasterDao,
    private val stockLotDao: StockLotDao,
    private val storageLocationDao: LabStorageLocationDao,
    private val inventoryItemDao: InventoryItemDao,
    private val orderHistoryDao: OrderHistoryDao,
    private val performanceLogDao: PerformanceLogDao,
    private val labProcessEventDao: LabProcessEventDao,
    private val labOrderDao: LabOrderDao,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val labOrders: StateFlow<List<LabOrder>> = labOrderDao.getAllLabOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLabOrder(order: LabOrder) {
        viewModelScope.launch {
            labOrderDao.insertLabOrder(order)
        }
    }

    val performanceLogs: StateFlow<List<PerformanceLog>> = performanceLogDao.getAllPerformanceLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val processEvents: StateFlow<List<LabProcessEvent>> = labProcessEventDao.getAllProcessEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equipmentList: StateFlow<List<LabEquipment>> = equipmentDao.getAllEquipment()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogEntriesWithGear = repository.allLogEntriesWithGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilmRollSessions = repository.allFilmRollSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCameras = repository.allCameras
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFilms = repository.allFilms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equipmentSubCategoryList: StateFlow<List<LabEquipmentSubCategory>> = equipmentSubCategoryDao.getAllSubCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chemicalList: StateFlow<List<LabChemical>> = chemicalDao.getAllChemicals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierList: StateFlow<List<LabSupplier>> = supplierDao.getAllSuppliers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierIssueList: StateFlow<List<SupplierIssueLog>> = supplierIssueLogDao.getAllIssueLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryList: StateFlow<List<LabCategory>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unitList: StateFlow<List<LabUnit>> = unitDao.getAllUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val itemNameList: StateFlow<List<LabItemName>> = itemNameDao.getAllItemNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masterList: StateFlow<List<InventoryMaster>> = inventoryMasterDao.getAllMasters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lotList: StateFlow<List<StockLot>> = stockLotDao.getAllLots()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locationList: StateFlow<List<LabStorageLocation>> = storageLocationDao.getAllStorageLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryItemList: StateFlow<List<InventoryItem>> = inventoryItemDao.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderList: StateFlow<List<OrderHistory>> = orderHistoryDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSafelightActive: StateFlow<Boolean> = repository.isSafelightActive

    fun setSafelightActive(active: Boolean) {
        repository.setSafelightActive(active)
    }

    fun toggleSafelight() {
        repository.toggleSafelight()
    }

    /**
     * Seeds default Supplier Contacts, System Core Categories, Units, Item Names, and Equipment Sub-Categories.
     */
    fun seedPresets() {
        viewModelScope.launch {
            settingsRepository.setLocalFoundationChangesPending(true)
            if (supplierList.value.isEmpty()) {
                LabPresets.supplierPresets.forEach { 
                    supplierDao.insertSupplier(
                        LabSupplier(
                            name = it.name,
                            region = it.region,
                            website = it.website,
                            reliabilityRating = null
                        )
                    )
                }
            }
            if (locationList.value.isEmpty()) {
                storageLocationDao.insertStorageLocation(LabStorageLocation(name = "Chemical Shelf A", locationType = "Shelf"))
                storageLocationDao.insertStorageLocation(LabStorageLocation(name = "Darkroom Fridge", locationType = "Fridge"))
                storageLocationDao.insertStorageLocation(LabStorageLocation(name = "Storage Cabinet B", locationType = "Cabinet"))
                storageLocationDao.insertStorageLocation(LabStorageLocation(name = "Paper Safe", locationType = "Safe"))
            }
            if (categoryList.value.isEmpty()) {
                categoryDao.insertCategory(LabCategory(name = "Chemistry", defaultUnit = "L", isSystemCore = true))
                categoryDao.insertCategory(LabCategory(name = "Paper", defaultUnit = "sheets", isSystemCore = true))
                categoryDao.insertCategory(LabCategory(name = "Safety Gear", defaultUnit = "box", isSystemCore = true))
                categoryDao.insertCategory(LabCategory(name = "Supplies", defaultUnit = "pack", isSystemCore = true))
            }
            if (unitList.value.isEmpty()) {
                val coreUnits = listOf("L", "mL", "fl oz", "gal", "sheets", "box", "pair", "pack", "roll", "unit")
                coreUnits.forEach { unitDao.insertUnit(LabUnit(name = it, isSystemCore = true)) }
            }
            if (itemNameList.value.isEmpty()) {
                val defaultNames = listOf(
                    LabItemName(name = "Rapid Fixer", category = "Chemistry"),
                    LabItemName(name = "Multigrade Developer", category = "Chemistry"),
                    LabItemName(name = "ID-11 Developer", category = "Chemistry"),
                    LabItemName(name = "D-76 Developer", category = "Chemistry"),
                    LabItemName(name = "Rodinal / R09 One Shot", category = "Chemistry"),
                    LabItemName(name = "Indicator Stop Bath", category = "Chemistry"),
                    LabItemName(name = "Ilfotol Wetting Agent", category = "Chemistry"),
                    LabItemName(name = "Multigrade RC Deluxe Satin 8x10", category = "Paper"),
                    LabItemName(name = "Multigrade RC Deluxe Pearl 8x10", category = "Paper"),
                    LabItemName(name = "Multigrade FB Glossy 8x10", category = "Paper"),
                    LabItemName(name = "Nitrile Gloves XL", category = "Safety Gear"),
                    LabItemName(name = "Negative Storage Sleeves", category = "Supplies")
                )
                defaultNames.forEach { itemNameDao.insertItemName(it) }
            }
            if (equipmentSubCategoryList.value.isEmpty()) {
                val defaultSubCats = listOf(
                    LabEquipmentSubCategory(category = "Dev Tank", name = "Standard Plastic Tank for 2x35mm or 1x120"),
                    LabEquipmentSubCategory(category = "Dev Tank", name = "Multi-roll developing tank"),
                    LabEquipmentSubCategory(category = "Dev Tank", name = "Auto-load spiral system"),
                    LabEquipmentSubCategory(category = "Enlarger", name = "Compact 4x5 LED Enlarger"),
                    LabEquipmentSubCategory(category = "Enlarger Lens", name = "50mm f/2.8 Enlarging Lens"),
                    LabEquipmentSubCategory(category = "Enlarger Lens", name = "80mm f/4 Enlarging Lens"),
                    LabEquipmentSubCategory(category = "Tray", name = "8x10 Darkroom Processing Tray"),
                    LabEquipmentSubCategory(category = "Light Table", name = "Slim LED Light Pad")
                )
                defaultSubCats.forEach { equipmentSubCategoryDao.insertSubCategory(it) }
            }
        }
    }

    fun addSupplierIssueLog(supplierId: Long, summary: String, contactPerson: String, notes: String) {
        if (summary.isBlank()) return
        viewModelScope.launch {
            supplierIssueLogDao.insertIssueLog(
                SupplierIssueLog(
                    supplierId = supplierId,
                    issueSummary = summary.trim(),
                    contactPerson = contactPerson.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun updateSupplierIssueLog(issueLog: SupplierIssueLog) {
        viewModelScope.launch {
            supplierIssueLogDao.updateIssueLog(issueLog)
        }
    }

    fun deleteSupplierIssueLog(issueLog: SupplierIssueLog) {
        viewModelScope.launch {
            supplierIssueLogDao.deleteIssueLog(issueLog)
        }
    }

    fun addEquipmentSubCategory(category: String, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            equipmentSubCategoryDao.insertSubCategory(LabEquipmentSubCategory(category = category, name = name.trim()))
        }
    }

    fun updateEquipmentSubCategory(subCategory: LabEquipmentSubCategory) {
        viewModelScope.launch {
            equipmentSubCategoryDao.updateSubCategory(subCategory)
        }
    }

    fun deleteEquipmentSubCategory(subCategory: LabEquipmentSubCategory) {
        viewModelScope.launch {
            equipmentSubCategoryDao.deleteSubCategory(subCategory)
        }
    }

    fun addItemName(name: String, category: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            itemNameDao.insertItemName(LabItemName(name = name.trim(), category = category.ifBlank { "Chemistry" }))
        }
    }

    fun updateItemName(itemName: LabItemName) {
        viewModelScope.launch {
            itemNameDao.updateItemName(itemName)
        }
    }

    fun deleteItemName(itemName: LabItemName) {
        viewModelScope.launch {
            itemNameDao.deleteItemName(itemName)
        }
    }

    fun addCategory(name: String, defaultUnit: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryDao.insertCategory(LabCategory(name = name.trim(), defaultUnit = defaultUnit.trim()))
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateCategory(category: LabCategory) {
        viewModelScope.launch {
            categoryDao.updateCategory(category)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteCategory(category: LabCategory) {
        if (category.isSystemCore) return
        viewModelScope.launch {
            categoryDao.deleteCategory(category)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addUnit(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            unitDao.insertUnit(LabUnit(name = name.trim()))
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateUnit(unit: LabUnit) {
        viewModelScope.launch {
            unitDao.updateUnit(unit)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteUnit(unit: LabUnit) {
        if (unit.isSystemCore) return
        viewModelScope.launch {
            unitDao.deleteUnit(unit)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addStorageLocation(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            storageLocationDao.insertStorageLocation(LabStorageLocation(name = name.trim(), locationType = "Storage"))
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addStorageLocation(location: LabStorageLocation) {
        if (location.name.isBlank()) return
        viewModelScope.launch {
            storageLocationDao.insertStorageLocation(location)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateStorageLocation(location: LabStorageLocation) {
        viewModelScope.launch {
            storageLocationDao.updateStorageLocation(location)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteStorageLocation(location: LabStorageLocation) {
        viewModelScope.launch {
            storageLocationDao.deleteStorageLocation(location)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addMaster(master: InventoryMaster) {
        viewModelScope.launch {
            inventoryMasterDao.insertMaster(master)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    suspend fun addMasterAndGetId(master: InventoryMaster): Long {
        settingsRepository.setLocalFoundationChangesPending(true)
        return inventoryMasterDao.insertMaster(master)
    }

    fun updateMaster(master: InventoryMaster) {
        viewModelScope.launch {
            inventoryMasterDao.updateMaster(master)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteMaster(master: InventoryMaster) {
        viewModelScope.launch {
            inventoryMasterDao.deleteMaster(master)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addStockLot(lot: StockLot) {
        viewModelScope.launch {
            stockLotDao.insertLot(lot)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateStockLot(lot: StockLot) {
        viewModelScope.launch {
            stockLotDao.updateLot(lot)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteStockLot(lot: StockLot) {
        viewModelScope.launch {
            stockLotDao.deleteLot(lot)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    /**
     * Finds an existing chemical with matching Name, Type, Dilution, Volume Amount, and Unit.
     */
    fun findDuplicateChemical(
        name: String,
        type: String,
        dilution: String,
        volAmount: Float,
        volUnit: String
    ): LabChemical? {
        val trimmedName = name.trim()
        return chemicalList.value.find {
            it.name.equals(trimmedName, ignoreCase = true) &&
            it.type.equals(type.trim(), ignoreCase = true) &&
            it.dilution.equals(dilution.trim(), ignoreCase = true) &&
            it.volumeAmount == volAmount &&
            it.volumeUnit.equals(volUnit.trim(), ignoreCase = true)
        }
    }

    /**
     * Finds an existing equipment item with matching Name and Category.
     */
    fun findDuplicateEquipment(name: String, category: String): LabEquipment? {
        val trimmedName = name.trim()
        return equipmentList.value.find {
            it.name.equals(trimmedName, ignoreCase = true) &&
            it.category.equals(category.trim(), ignoreCase = true)
        }
    }

    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryItemDao.insertItem(item)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryItemDao.updateItem(item)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryItemDao.deleteItem(item)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addSupplier(supplier: LabSupplier) {
        viewModelScope.launch {
            supplierDao.insertSupplier(supplier)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addOrder(order: OrderHistory) {
        viewModelScope.launch {
            orderHistoryDao.insertOrder(order)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateOrder(order: OrderHistory) {
        viewModelScope.launch {
            orderHistoryDao.updateOrder(order)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteOrder(order: OrderHistory) {
        viewModelScope.launch {
            orderHistoryDao.deleteOrder(order)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun updateSupplier(supplier: LabSupplier) {
        viewModelScope.launch {
            supplierDao.updateSupplier(supplier)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun deleteSupplier(supplier: LabSupplier) {
        viewModelScope.launch {
            supplierDao.deleteSupplier(supplier)
            settingsRepository.setLocalFoundationChangesPending(true)
        }
    }

    fun addPerformanceLog(log: PerformanceLog) {
        viewModelScope.launch {
            try {
                val existingLogs = repository.allLogEntriesWithGear.first()
                val targetLogId = if (existingLogs.any { it.logEntry.id == log.logEntryId }) {
                    log.logEntryId
                } else if (existingLogs.isNotEmpty()) {
                    existingLogs.first().logEntry.id
                } else {
                    // Safe fallback log entry insertion if database has no exposure entries yet
                    val fallbackCamera = repository.allCameras.firstOrNull()?.firstOrNull()
                    val fallbackFilm = repository.allFilms.firstOrNull()?.firstOrNull()
                    val fallbackLens = repository.allLenses.firstOrNull()?.firstOrNull()
                    repository.insertLogEntry(
                        LogEntry(
                            timestamp = System.currentTimeMillis(),
                            notes = "Enlarger Printing Session Log",
                            environmentalConditions = "",
                            cameraId = fallbackCamera?.id,
                            filmId = fallbackFilm?.id,
                            lensId = fallbackLens?.id,
                            exposureSettings = ExposureSettings(iso = 400, shutterSpeed = "1/125", aperture = "f/8")
                        )
                    )
                }
                performanceLogDao.insertLog(log.copy(logEntryId = targetLogId, inventoryItemId = null))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addProcessEvent(event: LabProcessEvent) {
        viewModelScope.launch {
            labProcessEventDao.insertProcessEvent(event)
        }
    }

    /**
     * Adds a custom equipment item entered by the user.
     */
    fun addCustomEquipment(name: String, category: String, notes: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            equipmentDao.insertEquipment(
                LabEquipment(
                    name = name.trim(),
                    category = category.ifBlank { "General" },
                    notes = notes.trim()
                )
            )
        }
    }

    /**
     * Adds a chemical item entered by the user or selected from Pick List.
     */
    fun addCustomChemical(
        name: String,
        type: String,
        dilution: String,
        capacityRollsTotal: Int,
        quantityOnHand: Int = 1,
        volumeAmount: Float = 1.0f,
        volumeUnit: String = "L",
        expirationDate: Long? = null,
        supplier: String = "",
        cost: String = ""
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            chemicalDao.insertChemical(
                LabChemical(
                    name = name.trim(),
                    type = type.ifBlank { "General" },
                    dilution = dilution.trim().ifBlank { "1+1" },
                    capacityRollsTotal = if (capacityRollsTotal > 0) capacityRollsTotal else 12,
                    quantityOnHand = quantityOnHand,
                    volumeAmount = volumeAmount,
                    volumeUnit = volumeUnit,
                    expirationDate = expirationDate,
                    supplier = supplier.trim(),
                    cost = cost.trim()
                )
            )
        }
    }

    fun updateEquipment(equipment: LabEquipment) {
        viewModelScope.launch {
            equipmentDao.updateEquipment(equipment)
        }
    }

    fun updateChemical(chemical: LabChemical) {
        viewModelScope.launch {
            chemicalDao.updateChemical(chemical)
        }
    }

    fun incrementChemicalQuantity(chemical: LabChemical, amount: Int = 1) {
        viewModelScope.launch {
            chemicalDao.updateChemical(chemical.copy(quantityOnHand = chemical.quantityOnHand + amount))
        }
    }

    fun decrementChemicalQuantity(chemical: LabChemical) {
        if (chemical.quantityOnHand <= 1) return
        viewModelScope.launch {
            chemicalDao.updateChemical(chemical.copy(quantityOnHand = chemical.quantityOnHand - 1))
        }
    }

    fun deleteEquipment(equipment: LabEquipment) {
        viewModelScope.launch {
            equipmentDao.deleteEquipment(equipment)
        }
    }

    fun deleteChemical(chemical: LabChemical) {
        viewModelScope.launch {
            chemicalDao.deleteChemical(chemical)
        }
    }
}
