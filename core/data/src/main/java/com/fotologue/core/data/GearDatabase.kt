package com.fotologue.core.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GearSpecs(
    val cameras: List<CameraSpec>,
    val lenses: List<LensSpec>
)

object GearDatabase {
    private var specs: GearSpecs? = null
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    fun initialize(context: Context) {
        if (specs != null) return
        try {
            val jsonString = context.assets.open("gear_specs.json").bufferedReader().use { it.readText() }
            specs = json.decodeFromString<GearSpecs>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            specs = GearSpecs(emptyList(), emptyList())
        }
    }

    fun searchCameras(query: String): List<CameraSpec> {
        val currentSpecs = specs ?: return emptyList()
        if (query.isBlank()) return emptyList()
        return currentSpecs.cameras.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.modelNumber.contains(query, ignoreCase = true) ||
            it.mount.contains(query, ignoreCase = true)
        }
    }

    fun searchLenses(query: String): List<LensSpec> {
        val currentSpecs = specs ?: return emptyList()
        if (query.isBlank()) return emptyList()
        return currentSpecs.lenses.filter {
            it.make.contains(query, ignoreCase = true) ||
            it.model.contains(query, ignoreCase = true) ||
            it.modelNumber.contains(query, ignoreCase = true) ||
            it.mount.contains(query, ignoreCase = true)
        }
    }
}
