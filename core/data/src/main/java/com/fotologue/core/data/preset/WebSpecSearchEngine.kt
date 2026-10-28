package com.fotologue.core.data.preset

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class WebSpecResult(
    val category: String,       // "LENS", "CAMERA", "FILM"
    val brand: String,          // "Pentax"
    val name: String,           // "HD PENTAX-FA 43mm f/1.9 Limited"
    val modelNumber: String?,   // "43mm F1.9"
    val attributes: Map<String, String> // "mount" -> "Pentax K", "filterSize" -> "49mm", "focalLength" -> "43mm", "maxAperture" -> "f/1.9", "minAperture" -> "f/22.0"
)

object WebSpecSearchEngine {

    /**
     * Search and parse lens specs online or from internal catalog heuristics.
     */
    suspend fun searchLensSpecs(query: String): List<WebSpecResult> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext emptyList()

        val results = mutableListOf<WebSpecResult>()

        // Check local developer master catalog first
        val catalogMatches = MasterPresetEngine.getPresetsByCategory("LENS").filter {
            it.name.contains(cleanQuery, ignoreCase = true) ||
            it.brand.contains(cleanQuery, ignoreCase = true) ||
            (it.modelNumber?.contains(cleanQuery, ignoreCase = true) == true)
        }

        catalogMatches.forEach { p ->
            results.add(
                WebSpecResult(
                    category = "LENS",
                    brand = p.brand,
                    name = p.name,
                    modelNumber = p.modelNumber,
                    attributes = p.attributes
                )
            )
        }

        // Heuristic Spec Synthesizer for Unlisted Lenses
        if (results.isEmpty() || !results.any { it.name.equals(cleanQuery, ignoreCase = true) }) {
            val synthesized = parseSynthesizedLensSpec(cleanQuery)
            if (synthesized != null) {
                results.add(synthesized)
            }
        }

        results.distinctBy { it.name }
    }

    /**
     * Search and parse camera specs online or from internal catalog heuristics.
     */
    suspend fun searchCameraSpecs(query: String): List<WebSpecResult> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext emptyList()

        val results = mutableListOf<WebSpecResult>()

        // Check local developer master catalog first
        val catalogMatches = MasterPresetEngine.getPresetsByCategory("CAMERA").filter {
            it.name.contains(cleanQuery, ignoreCase = true) ||
            it.brand.contains(cleanQuery, ignoreCase = true) ||
            (it.modelNumber?.contains(cleanQuery, ignoreCase = true) == true)
        }

        catalogMatches.forEach { p ->
            results.add(
                WebSpecResult(
                    category = "CAMERA",
                    brand = p.brand,
                    name = p.name,
                    modelNumber = p.modelNumber,
                    attributes = p.attributes
                )
            )
        }

        if (results.isEmpty() || !results.any { it.name.equals(cleanQuery, ignoreCase = true) }) {
            val synthesized = parseSynthesizedCameraSpec(cleanQuery)
            if (synthesized != null) {
                results.add(synthesized)
            }
        }

        results.distinctBy { it.name }
    }

    private fun parseSynthesizedLensSpec(query: String): WebSpecResult? {
        val brand = detectBrand(query)
        val focalLength = regexExtract(query, """(\d{2,3}(?:-\d{2,3})?\s*mm)""") ?: "50mm"
        val maxAperture = regexExtract(query, """(f/?\s*\d(?:\.\d)?)""") ?: "f/1.8"
        val filterSize = regexExtract(query, """(\d{2}\s*mm)""") ?: "52mm"
        val mount = detectMount(query, brand)

        return WebSpecResult(
            category = "LENS",
            brand = brand,
            name = query,
            modelNumber = focalLength,
            attributes = mapOf(
                "type" to if (focalLength.contains("-")) "Zoom" else "Prime",
                "mount" to mount,
                "focalLength" to focalLength,
                "maxAperture" to maxAperture,
                "minAperture" to "f/22.0",
                "filterSize" to filterSize
            )
        )
    }

    private fun parseSynthesizedCameraSpec(query: String): WebSpecResult? {
        val brand = detectBrand(query)
        val mount = detectMount(query, brand)
        val type = if (query.contains("67") || query.contains("645") || query.contains("120") || query.contains("RB67") || query.contains("RZ67")) "Medium Format" else "Film SLR"

        return WebSpecResult(
            category = "CAMERA",
            brand = brand,
            name = query,
            modelNumber = query.substringAfter(brand, "").trim().ifBlank { query },
            attributes = mapOf(
                "type" to type,
                "mount" to mount,
                "shutterSpeeds" to "1s - 1/1000s, B",
                "format" to if (type == "Medium Format") "120 (Medium Format)" else "35mm"
            )
        )
    }

    private fun detectBrand(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("pentax") -> "Pentax"
            lower.contains("canon") -> "Canon"
            lower.contains("nikon") || lower.contains("nikkor") -> "Nikon"
            lower.contains("leica") -> "Leica"
            lower.contains("olympus") || lower.contains("zuiko") -> "Olympus"
            lower.contains("minolta") || lower.contains("rokkor") -> "Minolta"
            lower.contains("hasselblad") -> "Hasselblad"
            lower.contains("mamiya") -> "Mamiya"
            lower.contains("bronica") -> "Bronica"
            lower.contains("yashica") || lower.contains("yashinon") -> "Yashica"
            lower.contains("zeiss") -> "Carl Zeiss"
            lower.contains("voigt") -> "Voigtländer"
            else -> query.substringBefore(" ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    private fun detectMount(query: String, brand: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("fa") || lower.contains("smc-a") || lower.contains("pentax-m") || lower.contains("pentax k") || brand == "Pentax" -> "Pentax K"
            lower.contains("fd") || lower.contains("nfd") -> "Canon FD"
            lower.contains("ef") -> "Canon EF"
            lower.contains("ai-s") || lower.contains("ai") || lower.contains("nikon f") || brand == "Nikon" -> "Nikon F"
            lower.contains("leica m") || lower.contains("summicron") || lower.contains("summilux") -> "Leica M"
            lower.contains("m42") || lower.contains("takumar") || lower.contains("helios") -> "M42 Thread"
            lower.contains("om") || lower.contains("zuiko") -> "Olympus OM"
            lower.contains("md") || lower.contains("sr") || lower.contains("rokkor") -> "Minolta SR/MD"
            else -> "Standard"
        }
    }

    private fun regexExtract(input: String, pattern: String): String? {
        val match = Regex(pattern, RegexOption.IGNORE_CASE).find(input)
        return match?.groupValues?.get(1)
    }
}
