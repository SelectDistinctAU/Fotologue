package com.fotologue.core.data.preset

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

data class WebSpecResult(
    val category: String,       // "LENS", "CAMERA", "FILM"
    val brand: String,          // "Praktica"
    val name: String,           // "Praktica LTL 3"
    val modelNumber: String?,   // "LTL 3"
    val attributes: Map<String, String>
)

object WebSpecSearchEngine {

    /**
     * Search and parse lens specs via Master Preset Catalog, AnalogDB API, Wikidata API & Wikipedia API.
     */
    suspend fun searchLensSpecs(query: String): List<WebSpecResult> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext emptyList()

        val results = mutableListOf<WebSpecResult>()

        // 1. Local master catalog query
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

        // 2. Live Online Query (Wikidata + Wikipedia)
        try {
            val wikiDataResults = fetchWikidataLensSpecs(cleanQuery)
            results.addAll(wikiDataResults)

            if (results.isEmpty()) {
                val wikiResults = fetchWikipediaLensSpecs(cleanQuery)
                results.addAll(wikiResults)
            }
        } catch (_: Exception) {
            // Live query offline
        }

        results.distinctBy { it.name.lowercase(Locale.getDefault()) }
    }

    /**
     * Search and parse camera specs via Master Preset Catalog, AnalogDB API, Wikidata API & Wikipedia API.
     */
    suspend fun searchCameraSpecs(query: String): List<WebSpecResult> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext emptyList()

        val results = mutableListOf<WebSpecResult>()

        // 1. Local master catalog query
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

        // 2. Live Online Query (AnalogDB REST API + Wikidata Entity API + Wikipedia)
        try {
            val analogDbResults = fetchAnalogDbCameraSpecs(cleanQuery)
            results.addAll(analogDbResults)

            if (results.isEmpty()) {
                val wikiDataResults = fetchWikidataCameraSpecs(cleanQuery)
                results.addAll(wikiDataResults)
            }

            // Try Query Variants if exact query returned no hits (e.g., "Praktica LTL3" -> "Praktica LTL 3")
            if (results.isEmpty()) {
                val normalizedQuery = cleanQuery.replace(Regex("""([a-zA-Z]+)(\d+)"""), "$1 $2")
                if (normalizedQuery != cleanQuery) {
                    val normalizedResults = fetchWikidataCameraSpecs(normalizedQuery)
                    results.addAll(normalizedResults)
                }
            }

            if (results.isEmpty()) {
                val wikiResults = fetchWikipediaCameraSpecs(cleanQuery)
                results.addAll(wikiResults)
            }
        } catch (_: Exception) {
            // Live query offline
        }

        results.distinctBy { it.name.lowercase(Locale.getDefault()) }
    }

    private fun fetchAnalogDbCameraSpecs(query: String): List<WebSpecResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val apiUrl = "https://api.analogdb.com/v1/cameras?search=$encoded"

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Fotologue/1.0 (Contact: selectdistinct.au)")
        }

        if (connection.responseCode != 200) return emptyList()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        val results = mutableListOf<WebSpecResult>()

        val nameMatches = Regex(""""name":"([^"]+)"""").findAll(responseText)

        nameMatches.forEach { match ->
            val name = match.groupValues[1].replace("""\"""", "")
            if (isValidCameraEntity(name.lowercase(Locale.getDefault()), name)) {
                val brand = detectBrand(name)
                val type = detectCameraType(name)
                val format = detectFilmFormat(name)
                val shutter = detectShutterSpeed(name, type)
                val mount = detectMount(name, brand)

                results.add(
                    WebSpecResult(
                        category = "CAMERA",
                        brand = brand,
                        name = name,
                        modelNumber = name.substringAfter(brand, "").trim().ifBlank { name },
                        attributes = mapOf(
                            "type" to type,
                            "mount" to mount,
                            "format" to format,
                            "shutterSpeeds" to shutter
                        )
                    )
                )
            }
        }
        return results
    }

    private fun fetchWikidataCameraSpecs(query: String): List<WebSpecResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val apiUrl = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=$encoded&language=en&type=item&format=json&limit=8"

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Fotologue/1.0 (Contact: selectdistinct.au)")
        }

        if (connection.responseCode != 200) return emptyList()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        val results = mutableListOf<WebSpecResult>()

        val labelMatches = Regex(""""label":"([^"]+)"""").findAll(responseText)
        val descMatches = Regex(""""description":"([^"]+)"""").findAll(responseText).toList()

        labelMatches.forEachIndexed { idx, match ->
            val label = match.groupValues[1].replace("""\"""", "")
            val desc = if (idx < descMatches.size) descMatches[idx].groupValues[1].replace("""\"""", "") else ""
            val fullText = "$label $desc"

            val lowerLabel = label.lowercase(Locale.getDefault())
            if (isValidCameraEntity(lowerLabel, fullText)) {
                val brand = detectBrand(fullText.ifBlank { query })
                val type = detectCameraType(fullText)
                val format = detectFilmFormat(fullText)
                val shutter = detectShutterSpeed(fullText, type)
                val mount = detectMount(fullText, brand)

                results.add(
                    WebSpecResult(
                        category = "CAMERA",
                        brand = brand,
                        name = label,
                        modelNumber = label.substringAfter(brand, "").trim().ifBlank { label },
                        attributes = mapOf(
                            "type" to type,
                            "mount" to mount,
                            "format" to format,
                            "shutterSpeeds" to shutter
                        )
                    )
                )
            }
        }
        return results
    }

    private fun fetchWikidataLensSpecs(query: String): List<WebSpecResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val apiUrl = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=$encoded&language=en&type=item&format=json&limit=8"

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Fotologue/1.0 (Contact: selectdistinct.au)")
        }

        if (connection.responseCode != 200) return emptyList()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        val results = mutableListOf<WebSpecResult>()

        val labelMatches = Regex(""""label":"([^"]+)"""").findAll(responseText)
        val descMatches = Regex(""""description":"([^"]+)"""").findAll(responseText).toList()

        labelMatches.forEachIndexed { idx, match ->
            val label = match.groupValues[1].replace("""\"""", "")
            val desc = if (idx < descMatches.size) descMatches[idx].groupValues[1].replace("""\"""", "") else ""
            val fullText = "$label $desc"

            val lowerLabel = label.lowercase(Locale.getDefault())
            if (isValidLensEntity(lowerLabel, fullText)) {
                val brand = detectBrand(fullText.ifBlank { query })
                val focal = regexExtract(fullText, """(\d{2,3}(?:-\d{2,3})?\s*mm)""") ?: "50mm"
                val aperture = regexExtract(fullText, """(f/?\s*\d(?:\.\d)?)""") ?: "f/1.8"
                val filter = regexExtract(fullText, """(\d{2}\s*mm)""") ?: "52mm"
                val mount = detectMount(fullText, brand)

                results.add(
                    WebSpecResult(
                        category = "LENS",
                        brand = brand,
                        name = label,
                        modelNumber = focal,
                        attributes = mapOf(
                            "type" to if (focal.contains("-")) "Zoom" else "Prime",
                            "mount" to mount,
                            "focalLength" to focal,
                            "maxAperture" to aperture,
                            "minAperture" to "f/22.0",
                            "filterSize" to filter
                        )
                    )
                )
            }
        }
        return results
    }

    private fun fetchWikipediaCameraSpecs(query: String): List<WebSpecResult> {
        val encoded = URLEncoder.encode("$query camera", "UTF-8")
        val apiUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&format=json"

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Fotologue/1.0 (Contact: selectdistinct.au)")
        }

        if (connection.responseCode != 200) return emptyList()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        return parseWikipediaCameraJsonResponse(responseText, query)
    }

    private fun fetchWikipediaLensSpecs(query: String): List<WebSpecResult> {
        val encoded = URLEncoder.encode("$query lens", "UTF-8")
        val apiUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&format=json"

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Fotologue/1.0 (Contact: selectdistinct.au)")
        }

        if (connection.responseCode != 200) return emptyList()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        return parseWikipediaLensJsonResponse(responseText, query)
    }

    private fun parseWikipediaCameraJsonResponse(json: String, query: String): List<WebSpecResult> {
        val list = mutableListOf<WebSpecResult>()
        val titleMatches = Regex(""""title":"([^"]+)"""").findAll(json)
        val snippetMatches = Regex(""""snippet":"([^"]+)"""").findAll(json).toList()

        titleMatches.forEachIndexed { idx, match ->
            val title = match.groupValues[1].replace("""\"""", "")
            val snippet = if (idx < snippetMatches.size) snippetMatches[idx].groupValues[1].replace("""\"""", "").replace("<span class=\"searchmatch\">", "").replace("</span>", "") else ""
            val fullText = "$title $snippet"

            val lowerTitle = title.lowercase(Locale.getDefault())
            if (isValidCameraEntity(lowerTitle, fullText)) {
                val brand = detectBrand(fullText.ifBlank { query })
                val type = detectCameraType(fullText)
                val format = detectFilmFormat(fullText)
                val shutter = detectShutterSpeed(fullText, type)
                val mount = detectMount(fullText, brand)

                list.add(
                    WebSpecResult(
                        category = "CAMERA",
                        brand = brand,
                        name = title,
                        modelNumber = title.substringAfter(brand, "").trim().ifBlank { title },
                        attributes = mapOf(
                            "type" to type,
                            "mount" to mount,
                            "format" to format,
                            "shutterSpeeds" to shutter
                        )
                    )
                )
            }
        }
        return list
    }

    private fun parseWikipediaLensJsonResponse(json: String, query: String): List<WebSpecResult> {
        val list = mutableListOf<WebSpecResult>()
        val titleMatches = Regex(""""title":"([^"]+)"""").findAll(json)
        val snippetMatches = Regex(""""snippet":"([^"]+)"""").findAll(json).toList()

        titleMatches.forEachIndexed { idx, match ->
            val title = match.groupValues[1].replace("""\"""", "")
            val snippet = if (idx < snippetMatches.size) snippetMatches[idx].groupValues[1].replace("""\"""", "").replace("<span class=\"searchmatch\">", "").replace("</span>", "") else ""
            val fullText = "$title $snippet"

            val lowerTitle = title.lowercase(Locale.getDefault())
            if (isValidLensEntity(lowerTitle, fullText)) {
                val brand = detectBrand(fullText.ifBlank { query })
                val focal = regexExtract(fullText, """(\d{2,3}(?:-\d{2,3})?\s*mm)""") ?: "50mm"
                val aperture = regexExtract(fullText, """(f/?\s*\d(?:\.\d)?)""") ?: "f/1.8"
                val filter = regexExtract(fullText, """(\d{2}\s*mm)""") ?: "52mm"
                val mount = detectMount(fullText, brand)

                list.add(
                    WebSpecResult(
                        category = "LENS",
                        brand = brand,
                        name = title,
                        modelNumber = focal,
                        attributes = mapOf(
                            "type" to if (focal.contains("-")) "Zoom" else "Prime",
                            "mount" to mount,
                            "focalLength" to focal,
                            "maxAperture" to aperture,
                            "minAperture" to "f/22.0",
                            "filterSize" to filter
                        )
                    )
                )
            }
        }
        return list
    }

    private fun isValidCameraEntity(lowerLabel: String, fullText: String): Boolean {
        if (lowerLabel.startsWith("list of") || lowerLabel.startsWith("category:") || lowerLabel.contains("digital camera brands")) {
            return false
        }
        val lowerText = fullText.lowercase(Locale.getDefault())
        if (lowerText.contains("single-lens reflex camera") && lowerLabel.contains("single-lens reflex")) return false
        if (lowerText.contains("stereo camera") && lowerLabel == "stereo camera") return false

        return lowerText.contains("camera") || lowerText.contains("slr") || lowerText.contains("rangefinder") ||
               lowerText.contains("tlr") || lowerText.contains("kodak") || lowerText.contains("nikon") ||
               lowerText.contains("canon") || lowerText.contains("pentax") || lowerText.contains("praktica") ||
               lowerText.contains("leica") || lowerText.contains("zorki") || lowerText.contains("fed")
    }

    private fun isValidLensEntity(lowerLabel: String, fullText: String): Boolean {
        if (lowerLabel.startsWith("list of") || lowerLabel.startsWith("category:")) {
            return false
        }
        val lowerText = fullText.lowercase(Locale.getDefault())
        return lowerText.contains("lens") || lowerText.contains("mm") || lowerText.contains("nikkor") ||
               lowerText.contains("zuiko") || lowerText.contains("rokkor") || lowerText.contains("takumar") ||
               lowerText.contains("summicron") || lowerText.contains("planar")
    }

    private fun detectCameraType(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower.contains("box camera") || lower.contains("brownie") || lower.contains("box") -> "Box Camera"
            lower.contains("rangefinder") || lower.contains("zorki") || lower.contains("fed") -> "Rangefinder"
            lower.contains("tlr") || lower.contains("twin-lens") || lower.contains("twin lens") -> "TLR"
            lower.contains("medium format") || lower.contains("6x7") || lower.contains("6x6") || lower.contains("6x4.5") || lower.contains("120") -> "Medium Format"
            lower.contains("point and shoot") || lower.contains("point-and-shoot") || lower.contains("compact") -> "Point & Shoot"
            lower.contains("large format") || lower.contains("4x5") || lower.contains("view camera") -> "Large Format"
            else -> "Film SLR"
        }
    }

    private fun detectFilmFormat(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower.contains("620") -> "620 (Medium Format)"
            lower.contains("120") || lower.contains("medium format") || lower.contains("6x6") || lower.contains("6x7") || lower.contains("box camera") || lower.contains("brownie") -> "120 (Medium Format)"
            lower.contains("127") -> "127 Format"
            lower.contains("110") -> "110 Pocket"
            lower.contains("4x5") -> "4x5 Large Format"
            else -> "35mm"
        }
    }

    private fun detectShutterSpeed(text: String, type: String): String {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            type == "Box Camera" || lower.contains("brownie") || lower.contains("simple shutter") || lower.contains("rotary shutter") -> "1/50s, B"
            lower.contains("1/2000") -> "1s - 1/2000s, B"
            lower.contains("1/4000") -> "1s - 1/4000s, B"
            lower.contains("1/500") -> "1s - 1/500s, B"
            else -> "1s - 1/1000s, B"
        }
    }

    private fun detectBrand(query: String): String {
        val lower = query.lowercase(Locale.getDefault())
        return when {
            lower.contains("praktica") || lower.contains("pentacon") -> "Praktica"
            lower.contains("zorki") || lower.contains("kmz") || lower.contains("zenit") -> "KMZ / Zorki"
            lower.contains("fed") -> "FED"
            lower.contains("kodak") || lower.contains("brownie") -> "Kodak"
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
        val lower = query.lowercase(Locale.getDefault())
        return when {
            lower.contains("zorki") || lower.contains("fed") || lower.contains("m39") || lower.contains("ltm") || lower.contains("leica thread") -> "M39 Thread"
            lower.contains("praktica") || lower.contains("pentacon") || lower.contains("m42") || lower.contains("takumar") || lower.contains("helios") -> "M42 Thread"
            lower.contains("brownie") || lower.contains("box camera") -> "Fixed"
            lower.contains("fa") || lower.contains("smc-a") || lower.contains("pentax-m") || lower.contains("pentax k") || brand == "Pentax" -> "Pentax K"
            lower.contains("fd") || lower.contains("nfd") -> "Canon FD"
            lower.contains("ef") -> "Canon EF"
            lower.contains("ai-s") || lower.contains("ai") || lower.contains("nikon f") || brand == "Nikon" -> "Nikon F"
            lower.contains("leica m") || lower.contains("summicron") || lower.contains("summilux") -> "Leica M"
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
