package com.fotologue.core.data.lab

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LabOrderDao {
    @Query("SELECT * FROM lab_orders ORDER BY submittedDate DESC")
    fun getAllLabOrders(): Flow<List<LabOrder>>

    @Query("SELECT * FROM lab_orders WHERE supplier_id = :supplierId ORDER BY submittedDate DESC")
    fun getLabOrdersBySupplier(supplierId: Long): Flow<List<LabOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabOrder(order: LabOrder): Long

    @Query("DELETE FROM lab_orders WHERE id = :id")
    suspend fun deleteLabOrder(id: Long)
}
