package com.fotologue.core.data.lab

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintSessionDao {
    @Query("SELECT * FROM print_sessions WHERE frame_id = :frameId ORDER BY createdAt DESC")
    fun getPrintSessionsForFrame(frameId: String): Flow<List<PrintSession>>

    @Query("SELECT * FROM print_sessions ORDER BY createdAt DESC")
    fun getAllPrintSessions(): Flow<List<PrintSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintSession(session: PrintSession)

    @Query("DELETE FROM print_sessions WHERE id = :id")
    suspend fun deletePrintSession(id: String)
}

@Dao
interface ExposureStepDao {
    @Query("SELECT * FROM exposure_steps WHERE session_id = :sessionId ORDER BY sortOrder ASC")
    fun getExposureStepsForSession(sessionId: String): Flow<List<ExposureStep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExposureStep(step: ExposureStep)

    @Query("DELETE FROM exposure_steps WHERE session_id = :sessionId")
    suspend fun deleteStepsForSession(sessionId: String)
}

@Dao
interface LovEntryDao {
    @Query("SELECT * FROM lab_lovs WHERE category = :category ORDER BY name ASC")
    fun getLovEntriesByCategory(category: LovCategory): Flow<List<LovEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLovEntry(entry: LovEntry)
}
