package com.fotologue.core.data.lab

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "asset_availability",
    primaryKeys = ["assetCategory", "assetRefId"]
)
data class AssetAvailability(
    val assetCategory: String, // 'Camera', 'Lens', 'Film Stock', 'Consumable', 'Lab Equipment'
    val assetRefId: Long,       // Foreign Key pointing to c.id, l.id, f.id, etc.
    val status: String = "AVAILABLE", // 'AVAILABLE', 'IN_TRANSIT', 'LOANED_OUT', 'IN_REPAIR', 'SOLD', 'LOST'
    val statusReason: String? = null,
    val activeRefId: Long? = null,
    val statusChangedAt: Long = System.currentTimeMillis(),
    val expectedResolutionAt: Long? = null
)

@Dao
interface AssetAvailabilityDao {
    @Query("SELECT * FROM asset_availability WHERE assetCategory = :category AND assetRefId = :refId LIMIT 1")
    suspend fun getAvailability(category: String, refId: Long): AssetAvailability?

    @Query("SELECT * FROM asset_availability WHERE assetCategory = :category AND assetRefId = :refId LIMIT 1")
    fun getAvailabilityFlow(category: String, refId: Long): Flow<AssetAvailability?>

    @Query("SELECT * FROM asset_availability WHERE assetCategory = :category")
    fun getCategoryAvailability(category: String): Flow<List<AssetAvailability>>

    @Query("SELECT * FROM asset_availability")
    fun getAllAvailability(): Flow<List<AssetAvailability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAvailability(availability: AssetAvailability)

    @Update
    suspend fun updateAvailability(availability: AssetAvailability)

    @Query("DELETE FROM asset_availability WHERE assetCategory = :category AND assetRefId = :refId")
    suspend fun deleteAvailability(category: String, refId: Long)
}
