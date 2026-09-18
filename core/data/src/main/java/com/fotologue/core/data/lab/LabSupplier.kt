package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "lab_suppliers")
@Serializable
data class LabSupplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val region: String = "Australia",
    val website: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val reliabilityRating: Float? = null, // Defaults to null (unrated)
    val isUsed: Boolean = false, // Checkbox indicating active/used supplier contact
    val notes: String = ""
)
