package com.fotologue.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object DesktopMasterPresetEngine {

    const val DEFAULT_REMOTE_CATALOG_URL = "https://raw.githubusercontent.com/geewy/Fotologue/main/core/data/src/main/resources/master_preset_catalog.json"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private fun getPresetsResourceDir(): File {
        var rootDir = File(System.getProperty("user.dir"))
        while (rootDir.exists() && !File(rootDir, "settings.gradle.kts").exists()) {
            val parent = rootDir.parentFile ?: break
            rootDir = parent
        }
        return File(rootDir, "core/data/src/main/resources/presets")
    }

    suspend fun refreshFromCloud(remoteUrl: String = DEFAULT_REMOTE_CATALOG_URL): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(remoteUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val jsonContent = connection.inputStream.bufferedReader().use { it.readText() }
                    val parsed = json.decodeFromString<List<MasterPresetItem>>(jsonContent)
                    if (parsed.isNotEmpty()) {
                        val categories = mapOf(
                            "CAMERA" to "cameras.json",
                            "LENS" to "lenses.json",
                            "FILM" to "films.json",
                            "TELECONVERTER" to "teleconverters.json",
                            "FILTER" to "filters.json",
                            "FLASH" to "flashes.json",
                            "VIEWFINDER" to "viewfinders.json",
                            "CONSUMABLE" to "consumables.json",
                            "EQUIPMENT" to "lab_equipment.json",
                            "SUPPLIER" to "suppliers.json"
                        )

                        val presetsDir = getPresetsResourceDir()
                        if (!presetsDir.exists()) presetsDir.mkdirs()

                        categories.forEach { (cat, fileName) ->
                            val itemsForCat = parsed.filter { it.category.equals(cat, ignoreCase = true) }
                            if (itemsForCat.isNotEmpty()) {
                                File(presetsDir, fileName).writeText(json.encodeToString(itemsForCat))
                            }
                        }
                        true
                    } else false
                } else false
            } catch (_: Exception) {
                false
            }
        }
    }
}
