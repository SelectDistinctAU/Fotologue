package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LovCategory {
    ENLARGERS,
    LIGHT_HEADS,
    LENSES,
    PAPERS,
    FILTER_SYSTEMS,
    DEVELOPERS,
    TONERS
}

@Entity(tableName = "lab_lovs")
data class LovEntry(
    @PrimaryKey val id: String,
    val category: LovCategory,                                    // enum
    val name: String,                                             // display name
    val metadata: String = "{}",                                  // per-category JSON schema
    val createdAt: Long = System.currentTimeMillis()
)
