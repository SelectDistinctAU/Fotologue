package com.fotologue.feature.lab

import com.fotologue.core.data.FotologueRepository
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
import com.fotologue.core.data.lab.LabProcessEventDao
import com.fotologue.core.data.lab.LabStorageLocationDao
import com.fotologue.core.data.lab.LabSupplier
import com.fotologue.core.data.lab.LabSupplierDao
import com.fotologue.core.data.lab.LabUnit
import com.fotologue.core.data.lab.LabUnitDao
import com.fotologue.core.data.lab.OrderHistory
import com.fotologue.core.data.lab.OrderHistoryDao
import com.fotologue.core.data.lab.PerformanceLogDao
import com.fotologue.core.data.lab.StockLot
import com.fotologue.core.data.lab.StockLotDao
import com.fotologue.core.data.lab.SupplierIssueLogDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LabHomeViewModelTest {

    private val repository = mockk<FotologueRepository>(relaxed = true)
    private val equipmentDao = mockk<LabEquipmentDao>(relaxed = true)
    private val equipmentSubCategoryDao = mockk<LabEquipmentSubCategoryDao>(relaxed = true)
    private val chemicalDao = mockk<LabChemicalDao>(relaxed = true)
    private val supplierDao = mockk<LabSupplierDao>(relaxed = true)
    private val supplierIssueLogDao = mockk<SupplierIssueLogDao>(relaxed = true)
    private val categoryDao = mockk<LabCategoryDao>(relaxed = true)
    private val unitDao = mockk<LabUnitDao>(relaxed = true)
    private val itemNameDao = mockk<LabItemNameDao>(relaxed = true)
    private val masterDao = mockk<InventoryMasterDao>(relaxed = true)
    private val stockLotDao = mockk<StockLotDao>(relaxed = true)
    private val locationDao = mockk<LabStorageLocationDao>(relaxed = true)
    private val inventoryItemDao = mockk<InventoryItemDao>(relaxed = true)
    private val orderHistoryDao = mockk<OrderHistoryDao>(relaxed = true)
    private val performanceLogDao = mockk<PerformanceLogDao>(relaxed = true)
    private val labProcessEventDao = mockk<LabProcessEventDao>(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    private val equipmentFlow = MutableStateFlow<List<LabEquipment>>(emptyList())
    private val equipmentSubCategoryFlow = MutableStateFlow<List<LabEquipmentSubCategory>>(emptyList())
    private val chemicalFlow = MutableStateFlow<List<LabChemical>>(emptyList())
    private val supplierFlow = MutableStateFlow<List<LabSupplier>>(emptyList())
    private val categoryFlow = MutableStateFlow<List<LabCategory>>(emptyList())
    private val unitFlow = MutableStateFlow<List<LabUnit>>(emptyList())
    private val itemNameFlow = MutableStateFlow<List<LabItemName>>(emptyList())
    private val masterFlow = MutableStateFlow<List<InventoryMaster>>(emptyList())
    private val lotFlow = MutableStateFlow<List<StockLot>>(emptyList())
    private val inventoryFlow = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val orderFlow = MutableStateFlow<List<OrderHistory>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { equipmentDao.getAllEquipment() } returns equipmentFlow
        coEvery { equipmentSubCategoryDao.getAllSubCategories() } returns equipmentSubCategoryFlow
        coEvery { chemicalDao.getAllChemicals() } returns chemicalFlow
        coEvery { supplierDao.getAllSuppliers() } returns supplierFlow
        coEvery { categoryDao.getAllCategories() } returns categoryFlow
        coEvery { unitDao.getAllUnits() } returns unitFlow
        coEvery { itemNameDao.getAllItemNames() } returns itemNameFlow
        coEvery { masterDao.getAllMasters() } returns masterFlow
        coEvery { stockLotDao.getAllLots() } returns lotFlow
        coEvery { inventoryItemDao.getAllItems() } returns inventoryFlow
        coEvery { orderHistoryDao.getAllOrders() } returns orderFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `seedPresets inserts supplier contacts directory into supplierDao`() = runTest(testDispatcher) {
        val viewModel = LabHomeViewModel(
            repository, equipmentDao, equipmentSubCategoryDao, chemicalDao, supplierDao, supplierIssueLogDao, categoryDao, unitDao, itemNameDao, masterDao, stockLotDao, locationDao, inventoryItemDao, orderHistoryDao, performanceLogDao, labProcessEventDao
        )

        viewModel.seedPresets()
        testScheduler.advanceUntilIdle()

        coVerify { supplierDao.insertSupplier(any()) }
    }

    @Test
    fun `addCustomEquipment delegates to equipmentDao`() = runTest(testDispatcher) {
        val viewModel = LabHomeViewModel(
            repository, equipmentDao, equipmentSubCategoryDao, chemicalDao, supplierDao, supplierIssueLogDao, categoryDao, unitDao, itemNameDao, masterDao, stockLotDao, locationDao, inventoryItemDao, orderHistoryDao, performanceLogDao, labProcessEventDao
        )

        viewModel.addCustomEquipment("Intrepid Enlarger", "Enlarger", "LED head")
        testScheduler.advanceUntilIdle()

        coVerify { equipmentDao.insertEquipment(match { it.name == "Intrepid Enlarger" && it.category == "Enlarger" }) }
    }

    @Test
    fun `addCustomChemical delegates to chemicalDao`() = runTest(testDispatcher) {
        val viewModel = LabHomeViewModel(
            repository, equipmentDao, equipmentSubCategoryDao, chemicalDao, supplierDao, supplierIssueLogDao, categoryDao, unitDao, itemNameDao, masterDao, stockLotDao, locationDao, inventoryItemDao, orderHistoryDao, performanceLogDao, labProcessEventDao
        )

        viewModel.addCustomChemical("Ilford ID-11", "Film Developer", "1+1", 10)
        testScheduler.advanceUntilIdle()

        coVerify { chemicalDao.insertChemical(match { it.name == "Ilford ID-11" && it.dilution == "1+1" }) }
    }

    @Test
    fun `Presets pick lists contain non-empty curated defaults`() {
        assertEquals(true, LabPresets.equipmentPresets.isNotEmpty())
        assertEquals(true, LabPresets.chemicalPresets.isNotEmpty())
        assertEquals(true, LabPresets.paperPresets.isNotEmpty())
        assertEquals(true, LabPresets.supplierPresets.isNotEmpty())
    }
}
