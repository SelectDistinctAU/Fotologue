package com.fotologue.core.data.lab

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LabEquipmentDao {
    @Query("SELECT * FROM lab_equipment ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<LabEquipment>>

    @Query("SELECT * FROM lab_equipment")
    suspend fun getAllEquipmentOnce(): List<LabEquipment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: LabEquipment): Long

    @Update
    suspend fun updateEquipment(equipment: LabEquipment)

    @Delete
    suspend fun deleteEquipment(equipment: LabEquipment)

    @Query("DELETE FROM lab_equipment")
    suspend fun deleteAllEquipment()
}

@Dao
interface LabEquipmentSubCategoryDao {
    @Query("SELECT * FROM lab_equipment_subcategories GROUP BY name ORDER BY name ASC")
    fun getAllSubCategories(): Flow<List<LabEquipmentSubCategory>>

    @Query("SELECT * FROM lab_equipment_subcategories WHERE category = :category GROUP BY name ORDER BY name ASC")
    fun getSubCategoriesForCategory(category: String): Flow<List<LabEquipmentSubCategory>>

    @Query("SELECT * FROM lab_equipment_subcategories GROUP BY name")
    suspend fun getAllSubCategoriesOnce(): List<LabEquipmentSubCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategory(subCategory: LabEquipmentSubCategory): Long

    @Update
    suspend fun updateSubCategory(subCategory: LabEquipmentSubCategory)

    @Delete
    suspend fun deleteSubCategory(subCategory: LabEquipmentSubCategory)

    @Query("DELETE FROM lab_equipment_subcategories")
    suspend fun deleteAllSubCategories()
}

@Dao
interface LabChemicalDao {
    @Query("SELECT * FROM lab_chemicals ORDER BY name ASC")
    fun getAllChemicals(): Flow<List<LabChemical>>

    @Query("SELECT * FROM lab_chemicals")
    suspend fun getAllChemicalsOnce(): List<LabChemical>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChemical(chemical: LabChemical): Long

    @Update
    suspend fun updateChemical(chemical: LabChemical)

    @Delete
    suspend fun deleteChemical(chemical: LabChemical)

    @Query("DELETE FROM lab_chemicals")
    suspend fun deleteAllChemicals()
}

@Dao
interface LabSupplierDao {
    @Query("SELECT * FROM lab_suppliers GROUP BY name ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<LabSupplier>>

    @Query("SELECT * FROM lab_suppliers GROUP BY name")
    suspend fun getAllSuppliersOnce(): List<LabSupplier>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: LabSupplier): Long

    @Update
    suspend fun updateSupplier(supplier: LabSupplier)

    @Delete
    suspend fun deleteSupplier(supplier: LabSupplier)

    @Query("DELETE FROM lab_suppliers")
    suspend fun deleteAllSuppliers()
}

@Dao
interface SupplierIssueLogDao {
    @Query("SELECT * FROM lab_supplier_issue_logs ORDER BY date DESC")
    fun getAllIssueLogs(): Flow<List<SupplierIssueLog>>

    @Query("SELECT * FROM lab_supplier_issue_logs WHERE supplierId = :supplierId ORDER BY date DESC")
    fun getIssueLogsForSupplier(supplierId: Long): Flow<List<SupplierIssueLog>>

    @Query("SELECT * FROM lab_supplier_issue_logs")
    suspend fun getAllIssueLogsOnce(): List<SupplierIssueLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssueLog(log: SupplierIssueLog): Long

    @Update
    suspend fun updateIssueLog(log: SupplierIssueLog)

    @Delete
    suspend fun deleteIssueLog(log: SupplierIssueLog)

    @Query("DELETE FROM lab_supplier_issue_logs")
    suspend fun deleteAllIssueLogs()
}

@Dao
interface LabCategoryDao {
    @Query("SELECT * FROM lab_categories GROUP BY name ORDER BY name ASC")
    fun getAllCategories(): Flow<List<LabCategory>>

    @Query("SELECT * FROM lab_categories GROUP BY name")
    suspend fun getAllCategoriesOnce(): List<LabCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: LabCategory): Long

    @Update
    suspend fun updateCategory(category: LabCategory)

    @Delete
    suspend fun deleteCategory(category: LabCategory)

    @Query("DELETE FROM lab_categories WHERE isSystemCore = 0")
    suspend fun deleteAllUserCategories()
}

@Dao
interface LabUnitDao {
    @Query("SELECT * FROM lab_units GROUP BY name ORDER BY name ASC")
    fun getAllUnits(): Flow<List<LabUnit>>

    @Query("SELECT * FROM lab_units GROUP BY name")
    suspend fun getAllUnitsOnce(): List<LabUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: LabUnit): Long

    @Update
    suspend fun updateUnit(unit: LabUnit)

    @Delete
    suspend fun deleteUnit(unit: LabUnit)

    @Query("DELETE FROM lab_units WHERE isSystemCore = 0")
    suspend fun deleteAllUserUnits()
}

@Dao
interface LabItemNameDao {
    @Query("SELECT * FROM lab_item_names GROUP BY name ORDER BY name ASC")
    fun getAllItemNames(): Flow<List<LabItemName>>

    @Query("SELECT * FROM lab_item_names GROUP BY name")
    suspend fun getAllItemNamesOnce(): List<LabItemName>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemName(itemName: LabItemName): Long

    @Update
    suspend fun updateItemName(itemName: LabItemName)

    @Delete
    suspend fun deleteItemName(itemName: LabItemName)

    @Query("DELETE FROM lab_item_names")
    suspend fun deleteAllItemNames()
}

@Dao
interface InventoryMasterDao {
    @Query("SELECT * FROM lab_inventory_masters ORDER BY name ASC")
    fun getAllMasters(): Flow<List<InventoryMaster>>

    @Query("SELECT * FROM lab_inventory_masters")
    suspend fun getAllMastersOnce(): List<InventoryMaster>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaster(master: InventoryMaster): Long

    @Update
    suspend fun updateMaster(master: InventoryMaster)

    @Delete
    suspend fun deleteMaster(master: InventoryMaster)

    @Query("DELETE FROM lab_inventory_masters")
    suspend fun deleteAllMasters()
}

@Dao
interface StockLotDao {
    @Query("SELECT * FROM lab_stock_lots ORDER BY purchaseDate DESC")
    fun getAllLots(): Flow<List<StockLot>>

    @Query("SELECT * FROM lab_stock_lots WHERE masterId = :masterId")
    fun getLotsForMaster(masterId: Long): Flow<List<StockLot>>

    @Query("SELECT * FROM lab_stock_lots")
    suspend fun getAllLotsOnce(): List<StockLot>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: StockLot): Long

    @Update
    suspend fun updateLot(lot: StockLot)

    @Delete
    suspend fun deleteLot(lot: StockLot)

    @Query("DELETE FROM lab_stock_lots")
    suspend fun deleteAllLots()
}

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM lab_inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM lab_inventory_items WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: String): Flow<List<InventoryItem>>

    @Query("SELECT * FROM lab_inventory_items")
    suspend fun getAllItemsOnce(): List<InventoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem): Long

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("DELETE FROM lab_inventory_items")
    suspend fun deleteAllItems()
}

@Dao
interface OrderHistoryDao {
    @Query("SELECT * FROM lab_order_history ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderHistory>>

    @Query("SELECT * FROM lab_order_history")
    suspend fun getAllOrdersOnce(): List<OrderHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderHistory): Long

    @Update
    suspend fun updateOrder(order: OrderHistory)

    @Delete
    suspend fun deleteOrder(order: OrderHistory)

    @Query("DELETE FROM lab_order_history")
    suspend fun deleteAllOrders()
}

@Dao
interface PerformanceLogDao {
    @Query("SELECT * FROM lab_performance_logs WHERE logEntryId = :logEntryId")
    fun getLogsForEntry(logEntryId: Long): Flow<List<PerformanceLog>>

    @Query("SELECT * FROM lab_performance_logs")
    suspend fun getAllLogsOnce(): List<PerformanceLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PerformanceLog): Long

    @Query("DELETE FROM lab_performance_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface LabProcessEventDao {
    @Query("SELECT * FROM lab_process_events WHERE filmRollId = :filmRollId")
    fun getProcessEventsForRoll(filmRollId: String): Flow<List<LabProcessEvent>>

    @Query("SELECT * FROM lab_process_events")
    suspend fun getAllProcessEventsOnce(): List<LabProcessEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessEvent(event: LabProcessEvent): Long

    @Query("DELETE FROM lab_process_events")
    suspend fun deleteAllProcessEvents()
}

@Dao
interface PrintRecipeDao {
    @Query("SELECT * FROM print_recipes WHERE logEntryId = :logEntryId")
    fun getPrintRecipesForLogEntry(logEntryId: Long): Flow<List<PrintRecipe>>

    @Query("SELECT * FROM print_recipes")
    suspend fun getAllPrintRecipesOnce(): List<PrintRecipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintRecipe(recipe: PrintRecipe): Long

    @Update
    suspend fun updatePrintRecipe(recipe: PrintRecipe)

    @Delete
    suspend fun deletePrintRecipe(recipe: PrintRecipe)

    @Query("DELETE FROM print_recipes")
    suspend fun deleteAllPrintRecipes()
}

@Dao
interface LabStorageLocationDao {
    @Query("SELECT * FROM lab_storage_locations GROUP BY name ORDER BY name ASC")
    fun getAllStorageLocations(): Flow<List<LabStorageLocation>>

    @Query("SELECT * FROM lab_storage_locations GROUP BY name")
    suspend fun getAllStorageLocationsOnce(): List<LabStorageLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStorageLocation(location: LabStorageLocation): Long

    @Update
    suspend fun updateStorageLocation(location: LabStorageLocation)

    @Delete
    suspend fun deleteStorageLocation(location: LabStorageLocation)

    @Query("DELETE FROM lab_storage_locations")
    suspend fun deleteAllStorageLocations()
}

@Dao
interface PrintOutcomeDao {
    @Query("SELECT * FROM print_outcomes WHERE printRecipeId = :printRecipeId")
    fun getOutcomesForRecipe(printRecipeId: Long): Flow<List<PrintOutcome>>

    @Query("SELECT * FROM print_outcomes")
    suspend fun getAllOutcomesOnce(): List<PrintOutcome>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintOutcome(outcome: PrintOutcome): Long

    @Query("DELETE FROM print_outcomes")
    suspend fun deleteAllOutcomes()
}
