package com.fotologue.core.data.preset

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(InternalSerializationApi::class)
@Serializable
data class MasterPresetItem(
    val id: String,
    val category: String, // "CAMERA", "LENS", "FILM", "TELECONVERTER", "FILTER", "FLASH", "VIEWFINDER", "CONSUMABLE", "EQUIPMENT", "SUPPLIER"
    val subCategory: String? = null,
    val brand: String,
    val name: String,
    val modelNumber: String? = null,
    val attributes: Map<String, String> = emptyMap(),
    val isDeveloperPreset: Boolean = true
)

object MasterPresetEngine {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private var cachedPresets: List<MasterPresetItem> = emptyList()

    fun initialize(jsonContent: String) {
        try {
            cachedPresets = json.decodeFromString(jsonContent)
        } catch (_: Exception) {
            // Fallback if resource stream fails
        }
    }

    fun getPresetsByCategory(category: String): List<MasterPresetItem> {
        return cachedPresets.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun searchPresets(category: String, query: String, brandFilter: String = "All"): List<MasterPresetItem> {
        val categoryList = getPresetsByCategory(category)
        val brandList = if (brandFilter == "All") categoryList else categoryList.filter { it.brand.equals(brandFilter, ignoreCase = true) }
        if (query.isBlank()) return brandList
        return brandList.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.brand.contains(query, ignoreCase = true) || 
            (it.modelNumber?.contains(query, ignoreCase = true) == true)
        }
    }

    fun getBrandsForCategory(category: String): List<String> {
        return listOf("All") + getPresetsByCategory(category).map { it.brand }.distinct().sorted()
    }
}
