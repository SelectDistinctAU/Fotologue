package com.fotologue.core.data.usecase

import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.lab.LabProcessEvent
import com.fotologue.core.data.lab.PerformanceLog
import javax.inject.Inject

class ProcessDarkroomSessionUseCase @Inject constructor(
    private val repository: FotologueRepository
) {
    suspend fun saveProcessEvent(event: LabProcessEvent): Long {
        return repository.insertLabProcessEvent(event)
    }

    suspend fun savePerformanceLog(log: PerformanceLog): Long {
        return repository.insertPerformanceLog(log)
    }

    fun calculateRecommendedDevTime(filmName: String, developerName: String): Int {
        val fName = filmName.lowercase()
        val dName = developerName.lowercase()
        return when {
            fName.contains("hp5") || fName.contains("tri-x") -> if (dName.contains("d-76") || dName.contains("id-11")) 450 else 540
            fName.contains("portra") || fName.contains("gold") || fName.contains("ultramax") -> 195 // 3m 15s C-41
            fName.contains("velvia") || fName.contains("provia") || fName.contains("e100") -> 360 // 6m 00s E-6
            else -> 420 // 7m 00s default
        }
    }
}
