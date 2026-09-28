package com.fotologue.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.awt.Desktop
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipInputStream
import javax.imageio.ImageIO
import javax.swing.JFileChooser

enum class DesktopOS {
    WINDOWS, LINUX, MACOS, UNKNOWN
}

data class ExifResult(
    val success: Boolean,
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

enum class RollStatus {
    UNASSIGNED, ACTIVE, FINISHED
}

data class DesktopFrameDetail(
    val frameNumber: Int = 1,
    val cameraName: String = "Canon AE-1 Program",
    val filmName: String = "Ilford HP5 Plus 400",
    val shutterSpeed: String = "1/250",
    val aperture: String = "f/5.6",
    val iso: Int = 400,
    val timestampMs: Long = 1788574279476,
    val dateDisplay: String = "19 Sep 2026 14:30",
    val notes: String = "",
    val tags: List<String> = emptyList(),
    var latitude: Double? = null,
    var longitude: Double? = null,
    val developerName: String? = null,
    val paperStock: String? = null,
    val enlargerLens: String? = null,
    val baseSec: String? = null,
    val gradeFilter: String? = null,
    val dodgeBurnNotes: String? = null,
    val outcomeStatus: String? = null,
    val learningTag: String? = null,
    val imageBytes: ByteArray? = null
)

data class DesktopMasterRollItem(
    val id: String = UUID.randomUUID().toString(),
    var status: RollStatus = RollStatus.UNASSIGNED,
    var contributorId: Int = 1,
    var cameraName: String,
    var filmName: String,
    var filmRollSerial: String,
    var frameCount: Int,
    var frames: List<DesktopFrameDetail> = emptyList(),
    var isSelectedForMerge: Boolean = false
)

data class ZipJobLotResult(
    val zipCount: Int,
    val jsonCount: Int,
    val extractedJsonNames: List<String>,
    val parsedRolls: List<DesktopMasterRollItem>,
    val logOutput: String
)

class DesktopExifToolManager {
    val currentOS: DesktopOS = detectOS()

    private fun detectOS(): DesktopOS {
        val osName = System.getProperty("os.name")?.lowercase(Locale.ROOT) ?: ""
        return when {
            osName.contains("win") -> DesktopOS.WINDOWS
            osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> DesktopOS.LINUX
            osName.contains("mac") -> DesktopOS.MACOS
            else -> DesktopOS.UNKNOWN
        }
    }

    fun resolveExecutable(): String {
        return when (currentOS) {
            DesktopOS.WINDOWS -> "exiftool.exe"
            else -> "exiftool"
        }
    }

    fun injectGpsAndMetadata(
        targetFilePath: String,
        latitude: Double?,
        longitude: Double?,
        creatorName: String? = "Gerard Bendotti",
        filmRollSerial: String? = "PNTX-TX400-R01",
        description: String? = "Fotologue Field Exposure Log + ExifTool Geotagged Scan"
    ): ExifResult {
        val executable = resolveExecutable()
        val commandList = mutableListOf<String>()
        commandList.add(executable)

        if (latitude != null && longitude != null) {
            val absLat = Math.abs(latitude)
            val absLon = Math.abs(longitude)
            val latRef = if (latitude >= 0) "N" else "S"
            val lonRef = if (longitude >= 0) "E" else "W"

            commandList.add("-XMP-exif:GPSLatitude=$absLat")
            commandList.add("-XMP-exif:GPSLatitudeRef=$latRef")
            commandList.add("-XMP-exif:GPSLongitude=$absLon")
            commandList.add("-XMP-exif:GPSLongitudeRef=$lonRef")
        }

        if (!creatorName.isNullOrBlank()) {
            commandList.add("-XMP-dc:creator=$creatorName")
        }
        if (!filmRollSerial.isNullOrBlank()) {
            commandList.add("-XMP-fotologue:physicalSerial=$filmRollSerial")
        }
        if (!description.isNullOrBlank()) {
            commandList.add("-XMP-dc:description=$description")
        }

        commandList.add("-overwrite_original")
        commandList.add(targetFilePath)

        return try {
            val pb = ProcessBuilder(commandList)
            pb.redirectErrorStream(true)
            val process = pb.start()
            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            val exitCode = process.waitFor()

            ExifResult(
                success = exitCode == 0,
                stdout = stdout,
                stderr = "",
                exitCode = exitCode
            )
        } catch (e: Exception) {
            ExifResult(
                success = false,
                stdout = "",
                stderr = e.message ?: "Failed to execute $executable process",
                exitCode = -1
            )
        }
    }
}

fun detectGoogleDrivePath(): String? {
    val userHome = System.getProperty("user.home") ?: ""
    val possiblePaths = listOf(
        "G:\\My Drive\\FotologueBackups",
        "G:\\My Drive",
        "$userHome\\Google Drive\\FotologueBackups",
        "$userHome\\Google Drive",
        "$userHome\\GoogleDrive"
    )
    return possiblePaths.find { File(it).exists() }
}

@Serializable
data class LocationExportDtoV1(
    val lat: Double?,
    val long: Double?
)

@Serializable
data class ExportDtoV1(
    val id: Long = 0,
    val contributorId: Int = 1,
    val contributorAlias: String = "P1",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val camera: String = "Unknown Camera",
    val film: String = "Unknown Film",
    val filmIso: Int = 0,
    val filmRoll: String? = null,
    val location: LocationExportDtoV1? = null,
    val tags: List<String> = emptyList() // Added tags support!
)

@Serializable
data class ExportEnvelopeV3(
    val schemaVersion: Int = 3,
    val appVersion: String = "3.4.0",
    val exportTimestamp: Long = System.currentTimeMillis(),
    val logs: List<ExportDtoV1> = emptyList()
)

object MasterVaultJsonParser {

    private val jsonFormatter = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun parseAndMigrate(jsonText: String): List<DesktopMasterRollItem> {
        return try {
            val rootElement = jsonFormatter.parseToJsonElement(jsonText)

            val schemaVersion = when {
                rootElement is JsonObject && rootElement.containsKey("schemaVersion") -> {
                    rootElement["schemaVersion"]?.jsonPrimitive?.intOrNull ?: 3
                }
                rootElement is JsonArray -> 1
                else -> 1
            }

            when (schemaVersion) {
                3 -> parseV3Envelope(jsonText)
                else -> parseV1OrV2Array(jsonText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseV3Envelope(jsonText: String): List<DesktopMasterRollItem> {
        return try {
            val envelope = jsonFormatter.decodeFromString<ExportEnvelopeV3>(jsonText)
            mapDtosToRolls(envelope.logs)
        } catch (_: Exception) {
            parseV1OrV2Array(jsonText)
        }
    }

    private fun parseV1OrV2Array(jsonText: String): List<DesktopMasterRollItem> {
        return try {
            val dtos = jsonFormatter.decodeFromString<List<ExportDtoV1>>(jsonText)
            mapDtosToRolls(dtos)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun mapDtosToRolls(dtos: List<ExportDtoV1>): List<DesktopMasterRollItem> {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
        return dtos.groupBy { 
            val cam = it.camera.ifBlank { "Canon AE-1 Program" }
            val film = it.film.ifBlank { "Ilford HP5 Plus 400" }
            val roll = it.filmRoll?.ifBlank { "Roll 1" } ?: "Roll 1"
            "${it.contributorId} • $cam • $film • $roll"
        }.map { (_, entries) ->
            val first = entries.first()
            val camera = first.camera.ifBlank { "Canon AE-1 Program" }
            var film = first.film.ifBlank { "Ilford HP5 Plus 400" }
            if (first.filmIso > 0 && !film.contains("${first.filmIso}")) {
                film = "$film (ISO ${first.filmIso})"
            }
            val rollSerial = first.filmRoll?.ifBlank { "Roll 1" } ?: "Roll 1"
            val frameCount = entries.size
            val dateStr = sdf.format(Date(first.timestamp))

            val sortedEntries = entries.sortedBy { it.timestamp }
            val parsedFrames = sortedEntries.mapIndexed { idx, dto ->
                val derivedFrameNum = dto.filmRoll?.substringAfter(".")?.toIntOrNull() ?: (idx + 1)
                DesktopFrameDetail(
                    frameNumber = derivedFrameNum,
                    cameraName = camera,
                    filmName = film,
                    shutterSpeed = "1/250", // Note: The raw v1 JSON didn't export shutter speed, so it defaults here
                    aperture = "f/5.6",     // Note: Default fallback
                    iso = if (dto.filmIso > 0) dto.filmIso else 400,
                    timestampMs = dto.timestamp,
                    dateDisplay = sdf.format(Date(dto.timestamp)),
                    notes = dto.notes,
                    tags = dto.tags,
                    latitude = dto.location?.lat,
                    longitude = dto.location?.long,
                    developerName = null,
                    paperStock = null,
                    enlargerLens = null,
                    baseSec = null,
                    gradeFilter = null,
                    dodgeBurnNotes = null,
                    outcomeStatus = null,
                    learningTag = null
                )
            }

            val resolvedStatus = if (rollSerial == "Roll 1" || rollSerial.startsWith("1.") || !rollSerial.contains("-")) RollStatus.UNASSIGNED else RollStatus.FINISHED

            DesktopMasterRollItem(
                status = resolvedStatus,
                contributorId = first.contributorId,
                cameraName = camera,
                filmName = film,
                filmRollSerial = "$rollSerial ($frameCount frames)",
                frameCount = frameCount,
                frames = parsedFrames
            )
        }
    }
}

suspend fun processZipJobLot(
    targetPath: String,
    onProgress: (index: Int, total: Int, currentFileName: String, logChunk: String) -> Unit
): ZipJobLotResult = withContext(Dispatchers.IO) {
    val targetFile = File(targetPath)
    val zipsToProcess = mutableListOf<File>()

    if (targetFile.isDirectory) {
        targetFile.walkTopDown().filter { it.extension.equals("zip", ignoreCase = true) }.forEach {
            zipsToProcess.add(it)
        }
    } else if (targetFile.extension.equals("zip", ignoreCase = true)) {
        zipsToProcess.add(targetFile)
    }

    val extractedJsons = mutableListOf<String>()
    val masterRolls = mutableListOf<DesktopMasterRollItem>()
    val logBuilder = StringBuilder()
    val totalCount = zipsToProcess.size
    logBuilder.append("Found $totalCount ZIP archives in batch job lot.\n\n")

    var jsonCount = 0
    zipsToProcess.forEachIndexed { index, zipFile ->
        val currentNum = index + 1
        val chunk = StringBuilder()
        try {
            // First pass: extract all thumbnails into a map so we can assign them to specific frames
            val thumbnailsMap = mutableMapOf<String, ByteArray>()
            var zipInputStream = ZipInputStream(zipFile.inputStream())
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                val entryNameLower = entry.name.lowercase(Locale.ROOT)
                if (!entry.isDirectory && (entryNameLower.endsWith(".jpg") || entryNameLower.endsWith(".png"))) {
                    val buffer = ByteArray(1024)
                    val baos = ByteArrayOutputStream()
                    var len: Int
                    while (zipInputStream.read(buffer).also { len = it } > 0) {
                        baos.write(buffer, 0, len)
                    }
                    val fileName = File(entry.name).nameWithoutExtension
                    thumbnailsMap[fileName] = baos.toByteArray()
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            zipInputStream.close()

            // Second pass: extract metadata, foundation archives, and pair with thumbnails
            zipInputStream = ZipInputStream(zipFile.inputStream())
            entry = zipInputStream.nextEntry

            while (entry != null) {
                val entryNameLower = entry.name.lowercase(Locale.ROOT)
                
                // --- INGEST FOUNDATION ARCHIVE (THE CROWDSOURCED KNOWLEDGE BASE) ---
                if (!entry.isDirectory && entryNameLower.endsWith("foundation_archive.json")) {
                    val buffer = ByteArray(1024)
                    val baos = ByteArrayOutputStream()
                    var len: Int
                    while (zipInputStream.read(buffer).also { len = it } > 0) {
                        baos.write(buffer, 0, len)
                    }
                    val jsonContent = baos.toString("UTF-8")
                    
                    try {
                        val element = Json { ignoreUnknownKeys = true }.parseToJsonElement(jsonContent)
                        val jsonObj = element.jsonObject
                        
                        // Fallback contributor ID extraction
                        var contributorId = 1
                        val logsArray = jsonObj["logs"]?.jsonArray
                        if (logsArray != null && logsArray.isNotEmpty()) {
                            val firstLog = logsArray[0].jsonObject
                            val camId = firstLog["cameraId"]?.jsonPrimitive?.intOrNull
                            if (camId != null) contributorId = camId
                        }

                        // Parse Arrays Manually since we don't have the Mobile Data Module imported in Desktop
                        val cameras = jsonObj["cameras"]?.jsonArray ?: JsonArray(emptyList())
                        cameras.forEach { camElement ->
                            val name = camElement.jsonObject["name"]?.jsonPrimitive?.content ?: ""
                            if (name.isNotBlank()) DatabaseRepository.checkOrQueueFoundationItem(contributorId, "CAMERA", name)
                        }

                        val films = jsonObj["films"]?.jsonArray ?: JsonArray(emptyList())
                        films.forEach { filmElement ->
                            val name = filmElement.jsonObject["name"]?.jsonPrimitive?.content ?: ""
                            val iso = filmElement.jsonObject["iso"]?.jsonPrimitive?.intOrNull
                            if (name.isNotBlank()) DatabaseRepository.checkOrQueueFoundationItem(contributorId, "FILM", name, iso)
                        }

                        val suppliers = jsonObj["labSuppliers"]?.jsonArray ?: JsonArray(emptyList())
                        suppliers.forEach { supElement ->
                            val name = supElement.jsonObject["name"]?.jsonPrimitive?.content ?: ""
                            if (name.isNotBlank()) DatabaseRepository.checkOrQueueFoundationItem(contributorId, "SUPPLIER", name)
                        }
                        
                        logBuilder.append("✓ Intercepted Foundation Archive from ${zipFile.name}. Pushed unrecognized gear to Review Queue.\n")
                    } catch (e: Exception) {
                        logBuilder.append("⚠️ Failed to parse Foundation Archive in ${zipFile.name}: ${e.message}\n")
                    }
                }

                if (!entry.isDirectory && (entryNameLower.endsWith("metadata.json") || entryNameLower.endsWith(".json") && !entryNameLower.contains("foundation"))) {
                    jsonCount++
                    extractedJsons.add("${zipFile.name} -> ${entry.name}")

                    val buffer = ByteArray(1024)
                    val baos = ByteArrayOutputStream()
                    var len: Int
                    while (zipInputStream.read(buffer).also { len = it } > 0) {
                        baos.write(buffer, 0, len)
                    }
                    val jsonContent = baos.toString("UTF-8")

                    val parsedRolls = MasterVaultJsonParser.parseAndMigrate(jsonContent)
                    val thumbnailsList = thumbnailsMap.values.toList()
                    val thumbnailKeys = thumbnailsMap.keys.toList()

                    parsedRolls.forEach { roll ->
                        // 3-Tier Robust Thumbnail Matcher (Timestamp -> Frame Serial _F01/_F02 -> Positional Index)
                        val updatedFrames = roll.frames.mapIndexed { fIdx, f -> 
                            var bytes = thumbnailsMap[f.timestampMs.toString()]

                            if (bytes == null) {
                                val fNumFormatted = String.format(Locale.US, "F%02d", f.frameNumber)
                                val fNumAlt = "_F${f.frameNumber}"
                                val matchingKey = thumbnailKeys.find { key ->
                                    key.contains(fNumFormatted, ignoreCase = true) || 
                                    key.endsWith(fNumAlt, ignoreCase = true) ||
                                    key.endsWith("_${f.frameNumber}", ignoreCase = true) ||
                                    key.contains("frame_${f.frameNumber}", ignoreCase = true)
                                }
                                if (matchingKey != null) {
                                    bytes = thumbnailsMap[matchingKey]
                                }
                            }

                            if (bytes == null && fIdx in thumbnailsList.indices) {
                                bytes = thumbnailsList[fIdx]
                            }

                            f.copy(imageBytes = bytes) 
                        }
                        
                        val resolvedStatus = if (roll.filmRollSerial.startsWith("Roll 1") || roll.filmRollSerial.startsWith("1.") || !roll.filmRollSerial.contains("-")) RollStatus.UNASSIGNED else RollStatus.FINISHED
                        
                        val rollItem = DesktopMasterRollItem(
                            status = resolvedStatus,
                            cameraName = roll.cameraName,
                            filmName = roll.filmName,
                            filmRollSerial = roll.filmRollSerial,
                            frameCount = roll.frameCount,
                            frames = updatedFrames
                        )
                        masterRolls.add(rollItem)
                        chunk.append("  [✓ Extracted & Merged metadata.json] ${zipFile.name} -> ${rollItem.cameraName} • ${rollItem.filmName} (${rollItem.filmRollSerial})\n")
                    }
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            zipInputStream.close()
        } catch (e: Exception) {
            chunk.append("  [! Error processing ${zipFile.name}]: ${e.message}\n")
        }

        logBuilder.append(chunk)
        withContext(Dispatchers.Main) {
            onProgress(currentNum, totalCount, zipFile.name, chunk.toString())
        }
    }

    logBuilder.append("\nBatch job lot processing complete. Successfully extracted and merged $jsonCount metadata.json graphs across $totalCount ZIP exports into master vault!\n")

    ZipJobLotResult(
        zipCount = totalCount,
        jsonCount = jsonCount,
        extractedJsonNames = extractedJsons,
        parsedRolls = masterRolls,
        logOutput = logBuilder.toString()
    )
}

fun chooseDirectoryOrFile(initialPath: String): String? {
    return try {
        val fileChooser = JFileChooser(initialPath.ifBlank { System.getProperty("user.home") }).apply {
            fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            dialogTitle = "Select Target Scan File or NAS Archive Directory"
        }
        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            fileChooser.selectedFile.absolutePath
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

fun byteArrayToImageBitmap(bytes: ByteArray?): ImageBitmap? {
    if (bytes == null || bytes.isEmpty()) return null
    return try {
        val bufferedImage = ImageIO.read(ByteArrayInputStream(bytes))
        bufferedImage?.toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
}

@Suppress("DEPRECATION")
fun main() = application {
    DatabaseManager.initDatabase()
    val logoBitmap = try {
        useResource("logo.png") { loadImageBitmap(it) }
    } catch (_: Exception) {
        null
    }
    val logoPainter = logoBitmap?.let { BitmapPainter(it) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Fotologue Desktop v3.0.0-DEV (Lexicon Edition)",
        icon = logoPainter,
        state = WindowState(size = DpSize(1380.dp, 900.dp))
    ) {
        DesktopApp(logoPainter = logoPainter)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DesktopApp(logoPainter: Painter? = null) {
    val scope = rememberCoroutineScope()
    val exifToolManager = remember { DesktopExifToolManager() }
    var selectedTab by remember { mutableIntStateOf(0) } // Default to Stage 1 Ingestion!

    var logOutputText by remember { mutableStateOf("ExifTool Engine Ready. Detected Operating System: ${exifToolManager.currentOS.name}") }
    var isProcessingJobLot by remember { mutableStateOf(false) }
    var jobLotCurrentIndex by remember { mutableIntStateOf(0) }
    var jobLotTotalCount by remember { mutableIntStateOf(0) }
    var jobLotCurrentFileName by remember { mutableStateOf("") }

    // Operator Status Banner Feedback
    var vaultFeedbackBanner by remember { mutableStateOf<String?>(null) }

    // Stage 2 Master List Filter & Sort State
    var currentStatusTab by remember { mutableStateOf<RollStatus?>(null) }
    var filterCameraQuery by remember { mutableStateOf("All Cameras") }
    var filterFilmQuery by remember { mutableStateOf("All Films") }
    var sortOption by remember { mutableStateOf("Date (Newest First)") }
    var isolateOrphansOnly by remember { mutableStateOf(false) }
    
    // Dynamic Master Vault State for Extracted Rolls
    val masterVaultRolls = remember { mutableStateListOf<DesktopMasterRollItem>() }
    
    LaunchedEffect(Unit) {
        val loadedRolls = DatabaseRepository.loadAllRolls()
        masterVaultRolls.clear()
        masterVaultRolls.addAll(loadedRolls)
    }

    var selectedRollIndex by remember { mutableIntStateOf(0) }
    var selectedFrameIndex by remember { mutableIntStateOf(0) }

    val filteredAndSortedRolls = remember(masterVaultRolls.toList(), currentStatusTab, sortOption, filterCameraQuery, filterFilmQuery, isolateOrphansOnly) {
        masterVaultRolls.filter { roll ->
            val statusMatch = (currentStatusTab == null || roll.status == currentStatusTab)
            val cameraMatch = (filterCameraQuery == "All Cameras" || roll.cameraName.equals(filterCameraQuery, ignoreCase = true))
            val filmMatch = (filterFilmQuery == "All Films" || roll.filmName.equals(filterFilmQuery, ignoreCase = true))
            val orphanMatch = if (isolateOrphansOnly) roll.frameCount <= 3 else true
            statusMatch && cameraMatch && filmMatch && orphanMatch
        }.sortedWith { a, b ->
            when (sortOption) {
                "Camera Name" -> a.cameraName.compareTo(b.cameraName)
                "Film Stock" -> a.filmName.compareTo(b.filmName)
                "Frame Count" -> b.frameCount.compareTo(a.frameCount)
                "Date (Oldest First)" -> (a.frames.firstOrNull()?.timestampMs ?: 0L).compareTo(b.frames.firstOrNull()?.timestampMs ?: 0L)
                else -> (b.frames.firstOrNull()?.timestampMs ?: 0L).compareTo(a.frames.firstOrNull()?.timestampMs ?: 0L)
            }
        }
    }

    var showBulkEditDialog by remember { mutableStateOf(false) }
    var bulkEditCamera by remember { mutableStateOf("") }
    var bulkEditFilm by remember { mutableStateOf("") }

    if (showBulkEditDialog) {
        AlertDialog(
            onDismissRequest = { showBulkEditDialog = false },
            title = { Text("Bulk Standardise Selected Rolls", fontWeight = FontWeight.Bold) },
            text = {
                val selectedCount = masterVaultRolls.count { it.isSelectedForMerge }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Apply the following Camera and Film Stock to all $selectedCount selected rolls:")
                    OutlinedTextField(
                        value = bulkEditCamera,
                        onValueChange = { bulkEditCamera = it },
                        label = { Text("Standardised Camera Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bulkEditFilm,
                        onValueChange = { bulkEditFilm = it },
                        label = { Text("Standardised Film Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val selectedCount = masterVaultRolls.count { it.isSelectedForMerge }
                    masterVaultRolls.forEachIndexed { idx, roll ->
                        if (roll.isSelectedForMerge) {
                            val updatedFrames = roll.frames.map { f ->
                                f.copy(cameraName = bulkEditCamera, filmName = bulkEditFilm)
                            }
                            masterVaultRolls[idx] = roll.copy(
                                cameraName = bulkEditCamera,
                                filmName = bulkEditFilm,
                                frames = updatedFrames
                            )
                        }
                    }
                    vaultFeedbackBanner = "✅ Bulk Standardised $selectedCount rolls to $bulkEditCamera • $bulkEditFilm."
                    showBulkEditDialog = false
                }) {
                    Text("Apply to Selected", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    var showHarmonizeDialog by remember { mutableStateOf(false) }
    var harmonizeTargetCamera by remember { mutableStateOf("") }
    var harmonizeTargetFilm by remember { mutableStateOf("") }

    if (showHarmonizeDialog) {
        val selectedRolls = masterVaultRolls.filter { it.isSelectedForMerge }
        AlertDialog(
            onDismissRequest = { showHarmonizeDialog = false },
            title = { Text("Standardise Camera & Film Stock", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("The selected rolls contain different camera bodies or film stocks. Select or enter the Standardised names to apply across all ${selectedRolls.size} selected rolls:")
                    
                    OutlinedTextField(
                        value = harmonizeTargetCamera,
                        onValueChange = { harmonizeTargetCamera = it },
                        label = { Text("Standardised Camera Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = harmonizeTargetFilm,
                        onValueChange = { harmonizeTargetFilm = it },
                        label = { Text("Standardised Film Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetCamera = harmonizeTargetCamera.ifBlank { "Canon AE-1 Program" }
                        val targetFilm = harmonizeTargetFilm.ifBlank { "Ilford HP5 Plus 400" }

                        // Harmonize and Merge in one seamless step!
                        val allRawFrames = selectedRolls.flatMap { it.frames }.sortedBy { it.timestampMs }
                        val harmonizedFrames = allRawFrames.mapIndexed { idx, frame ->
                            frame.copy(
                                frameNumber = idx + 1,
                                cameraName = targetCamera,
                                filmName = targetFilm
                            )
                        }

                        val maxCap = if (targetFilm.contains("120") || targetFilm.contains("800T") || targetCamera.contains("Yashica") || targetCamera.contains("Hasselblad")) 12 else 36
                        val chunked = harmonizedFrames.chunked(maxCap)

                        val createdRolls = chunked.mapIndexed { rIdx, chunk ->
                            val renumbered = chunk.mapIndexed { idx, f -> f.copy(frameNumber = idx + 1) }
                            val rollNumStr = if (rIdx + 1 < 10) "0${rIdx + 1}" else "${rIdx + 1}"
                            val camCode = targetCamera.filter { it.isLetterOrDigit() }.take(3).uppercase()
                            val filmCode = targetFilm.filter { it.isLetterOrDigit() }.take(3).uppercase()
                            val serial = "$camCode-$filmCode-R$rollNumStr (${renumbered.size} frames)"

                            DesktopMasterRollItem(
                                status = RollStatus.ACTIVE,
                                cameraName = targetCamera,
                                filmName = targetFilm,
                                filmRollSerial = serial,
                                frameCount = renumbered.size,
                                frames = renumbered
                            )
                        }

                        masterVaultRolls.removeAll(selectedRolls)
                        masterVaultRolls.addAll(0, createdRolls)
                        selectedRollIndex = 0
                        showHarmonizeDialog = false

                        DatabaseRepository.deleteRolls(selectedRolls)
                        createdRolls.forEach { DatabaseRepository.saveRoll(it) }

                        vaultFeedbackBanner = "✅ Standardised & Combined ${selectedRolls.size} rolls into ${createdRolls.size} unified active roll(s) ($targetCamera • $targetFilm)!"
                    }
                ) {
                    Text("Standardise & Merge Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHarmonizeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (logoPainter != null) {
                                Image(
                                    painter = logoPainter,
                                    contentDescription = "Fotologue Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                "Fotologue Desktop v3.0.0-DEV",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "• Analogue & Hybrid Photography",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // EXPLICIT EXCEL-STYLE NAVIGATION TABS
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "📥 Import",
                        "⚙️ Process (${masterVaultRolls.size})",
                        "🏷️ Tag Images",
                        "🏛️ Administration"
                    )
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                HorizontalDivider()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // STAGE 1: INGESTION & VAULT INFLOW (ZIP Job Lot, Google Drive, NAS Shares)
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // OPERATOR STATUS FEEDBACK BANNER
                                if (!vaultFeedbackBanner.isNullOrBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = vaultFeedbackBanner!!,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { vaultFeedbackBanner = null }) {
                                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                        }
                                    }
                                }

                                // 1. UNIFIED BATCH JOB LOT ZIP PROCESSOR CARD
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        val detectedDrive = detectGoogleDrivePath()
                                        val driveStatus = if (detectedDrive != null) "✅ Detected Google Drive at: $detectedDrive" else "Google Drive Desktop client not detected at default location."

                                        Text(
                                            "📦 IMPORT ARCHIVES ENGINE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            "Select a directory to scan for ZIP exports (e.g., Local Drive, Google Drive Desktop, or Mounted NAS Share). Fotologue Desktop will recursively scan the folder, safely extract all new embedded metadata graphs & thumbnails, and skip any previously imported frames (100% Zero-Duplicate Guarantee).",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(driveStatus, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)

                                        if (isProcessingJobLot) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    "📦 Processing ZIP $jobLotCurrentIndex / $jobLotTotalCount: $jobLotCurrentFileName",
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                val progress = if (jobLotTotalCount > 0) jobLotCurrentIndex.toFloat() / jobLotTotalCount.toFloat() else 0f
                                                LinearProgressIndicator(
                                                    progress = { progress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(8.dp)
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    val initial = detectedDrive ?: System.getProperty("user.home")
                                                    val chosen = chooseDirectoryOrFile(initial)
                                                    if (chosen != null) {
                                                        scope.launch {
                                                            isProcessingJobLot = true
                                                            logOutputText = "Starting Batch Job Lot Processing on: $chosen\n\n"
                                                            val jobRes = processZipJobLot(chosen) { index, total, currentFileName, chunk ->
                                                                jobLotCurrentIndex = index
                                                                jobLotTotalCount = total
                                                                jobLotCurrentFileName = currentFileName
                                                                logOutputText += chunk
                                                            }
                                                            // 1. Gather all existing frame signatures across the ENTIRE Master Vault
                                                            // We fingerprint frames by the immutable raw timestamp string.
                                                            val existingFrameSignatures = masterVaultRolls.flatMap { roll ->
                                                                roll.frames.map { f -> f.timestampMs.toString() }
                                                            }.toSet()

                                                            var newlyAddedCount = 0
                                                            var skippedCount = 0

                                                            jobRes.parsedRolls.forEach { parsedRoll ->
                                                                // Filter out individual frames that already exist ANYWHERE in the master vault based purely on their raw timestamp
                                                                val newFrames = parsedRoll.frames.filter { f ->
                                                                    !existingFrameSignatures.contains(f.timestampMs.toString())
                                                                }

                                                                if (newFrames.isEmpty()) {
                                                                    skippedCount++ // ALL frames from this ZIP roll are already safely in the vault!
                                                                } else {
                                                                    val updatedRoll = parsedRoll.copy(
                                                                        frameCount = newFrames.size,
                                                                        frames = newFrames,
                                                                        filmRollSerial = parsedRoll.filmRollSerial.replace(Regex("\\(\\d+ frames\\)"), "(${newFrames.size} frames)")
                                                                    )
                                                                    masterVaultRolls.add(updatedRoll)
                                                                    DatabaseRepository.saveRoll(updatedRoll)
                                                                    newlyAddedCount++
                                                                }
                                                            }
                                                            isProcessingJobLot = false
                                                            logOutputText = jobRes.logOutput
                                                            vaultFeedbackBanner = "✅ Batch Import Complete: Processed ${jobRes.zipCount} ZIP archives. Added $newlyAddedCount new roll(s) to database. Skipped $skippedCount rolls where ALL frames were already imported."
                                                            selectedTab = 1 // Switch to Process!
                                                        }
                                                    }
                                                },
                                                enabled = !isProcessingJobLot,
                                                modifier = Modifier.height(48.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(if (isProcessingJobLot) "Processing Target Directory..." else "Select Directory to Import Archives", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // STAGE 2: MASTER QA & DETAIL WORKBENCH (FULL MASTER-DETAIL PANEL + THUMBNAIL PREVIEW)
                            val activeRoll = masterVaultRolls.getOrNull(selectedRollIndex.coerceIn(0, (masterVaultRolls.size - 1).coerceAtLeast(0)))
                            val activeFrames = activeRoll?.frames ?: emptyList()
                            val activeFrame = activeFrames.getOrNull(selectedFrameIndex.coerceIn(0, (activeFrames.size - 1).coerceAtLeast(0)))
                            val selectedCount = masterVaultRolls.count { it.isSelectedForMerge }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // OPERATOR STATUS FEEDBACK BANNER
                                if (!vaultFeedbackBanner.isNullOrBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = vaultFeedbackBanner!!,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { vaultFeedbackBanner = null }) {
                                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // LEFT MASTER PANE: MASTER ROLLS & FRAMES TREE (1/3 WIDTH)
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                                .verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                "🎞️ MASTER ROLLS (${filteredAndSortedRolls.size})",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            // STATUS TABS ROW (Excel-style Segmented Toggles)
                                            Row(
                                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surface).padding(3.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                val orphanCount = masterVaultRolls.count { it.status == RollStatus.UNASSIGNED }
                                                val activeCount = masterVaultRolls.count { it.status == RollStatus.ACTIVE }
                                                val finishedCount = masterVaultRolls.count { it.status == RollStatus.FINISHED }
                                                val totalCount = masterVaultRolls.size

                                                val tabs = listOf(
                                                    Triple("All ($totalCount)", null as RollStatus?, MaterialTheme.colorScheme.primary),
                                                    Triple("Unassigned ($orphanCount)", RollStatus.UNASSIGNED, Color(0xFFE65100)),
                                                    Triple("Active ($activeCount)", RollStatus.ACTIVE, Color(0xFF2E7D32)),
                                                    Triple("Vault ($finishedCount)", RollStatus.FINISHED, MaterialTheme.colorScheme.secondary)
                                                )

                                                tabs.forEach { (label, status, color) ->
                                                    val isSelected = currentStatusTab == status
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(if (isSelected) color else Color.Transparent)
                                                            .clickable { currentStatusTab = status }
                                                            .padding(vertical = 6.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }
                                            }

                                            // MASTER LIST TOOLBAR - ROW 1: PERMANENT FILTERS & TRIAGE
                                            val visibleIds = remember(filteredAndSortedRolls) { filteredAndSortedRolls.map { it.id }.toSet() }
                                            val visibleSelectedCount = filteredAndSortedRolls.count { it.isSelectedForMerge }
                                            val allVisibleSelected = visibleSelectedCount > 0 && visibleSelectedCount == filteredAndSortedRolls.size

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Orphan Toggle Button
                                                OutlinedButton(
                                                    onClick = { isolateOrphansOnly = !isolateOrphansOnly },
                                                    colors = if (isolateOrphansOnly) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer) else ButtonDefaults.outlinedButtonColors(),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(if (isolateOrphansOnly) Icons.Default.Close else Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(if (isolateOrphansOnly) "Clear Filter" else "Find Unassigned", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                }
                                                
                                                // Select Visible Button (Exclusively checks visible items and unchecks hidden ones!)
                                                OutlinedButton(
                                                    onClick = { 
                                                        masterVaultRolls.forEachIndexed { idx, roll ->
                                                            masterVaultRolls[idx] = roll.copy(isSelectedForMerge = visibleIds.contains(roll.id))
                                                        }
                                                        vaultFeedbackBanner = "✅ Exclusively selected the ${filteredAndSortedRolls.size} visible items."
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Select Visible (${filteredAndSortedRolls.size})", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                }

                                                if (selectedCount > 0) {
                                                    OutlinedButton(
                                                        onClick = {
                                                            masterVaultRolls.forEachIndexed { idx, roll ->
                                                                if (roll.isSelectedForMerge) {
                                                                    masterVaultRolls[idx] = roll.copy(isSelectedForMerge = false)
                                                                }
                                                            }
                                                            vaultFeedbackBanner = "🧹 Cleared all selections."
                                                        },
                                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                                    ) {
                                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Clear ($selectedCount)", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                    }
                                                }
                                            }

                                            // MASTER LIST TOOLBAR - ROW 2: CONTEXTUAL MULTI-SELECT ACTIONS (Appears BELOW Row 1)
                                            if (selectedCount >= 1) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (selectedCount >= 2) {
                                                        Button(
                                                            onClick = {
                                                                val selectedRolls = masterVaultRolls.filter { it.isSelectedForMerge }
                                                                if (selectedRolls.size >= 2) {
                                                                    val distinctCameras = selectedRolls.map { it.cameraName }
                                                                        .filter { it != "Camera" && !it.contains("Unknown") && it.isNotBlank() }
                                                                        .distinct()
                                                                    val distinctFilms = selectedRolls.map { it.filmName }
                                                                        .filter { it != "Film Stock" && !it.contains("Unknown") && it.isNotBlank() }
                                                                        .distinct()

                                                                    if (distinctCameras.size > 1 || distinctFilms.size > 1) {
                                                                        harmonizeTargetCamera = distinctCameras.firstOrNull() ?: selectedRolls.first().cameraName
                                                                        harmonizeTargetFilm = distinctFilms.firstOrNull() ?: selectedRolls.first().filmName
                                                                        showHarmonizeDialog = true
                                                                        return@Button
                                                                    }

                                                                    val targetCamera = distinctCameras.firstOrNull() ?: selectedRolls.first().cameraName.ifBlank { "Canon AE-1 Program" }
                                                                    val targetFilm = distinctFilms.firstOrNull() ?: selectedRolls.first().filmName.ifBlank { "Ilford HP5 Plus 400" }

                                                                    val allRawFrames = selectedRolls.flatMap { it.frames }.sortedBy { it.timestampMs }
                                                                    val harmonizedFrames = allRawFrames.mapIndexed { idx, frame ->
                                                                        frame.copy(
                                                                            frameNumber = idx + 1,
                                                                            cameraName = targetCamera,
                                                                            filmName = targetFilm
                                                                        )
                                                                    }

                                                                    val maxCap = if (targetFilm.contains("120") || targetFilm.contains("800T") || targetCamera.contains("Yashica") || targetCamera.contains("Hasselblad")) 12 else 36
                                                                    val chunked = harmonizedFrames.chunked(maxCap)

                                                                    val createdRolls = chunked.mapIndexed { rIdx, chunk ->
                                                                        val renumbered = chunk.mapIndexed { idx, f -> f.copy(frameNumber = idx + 1) }
                                                                        val rollNumStr = if (rIdx + 1 < 10) "0${rIdx + 1}" else "${rIdx + 1}"
                                                                        val camCode = targetCamera.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                        val filmCode = targetFilm.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                        val serial = "$camCode-$filmCode-R$rollNumStr (${renumbered.size} frames)"

                                                                        DesktopMasterRollItem(
                                                                            status = RollStatus.ACTIVE,
                                                                            cameraName = targetCamera,
                                                                            filmName = targetFilm,
                                                                            filmRollSerial = serial,
                                                                            frameCount = renumbered.size,
                                                                            frames = renumbered
                                                                        )
                                                                    }

                                                                    masterVaultRolls.removeAll(selectedRolls)
                                                                    masterVaultRolls.addAll(0, createdRolls)
                                                                    selectedRollIndex = 0

                                                                    DatabaseRepository.deleteRolls(selectedRolls)
                                                                    createdRolls.forEach { DatabaseRepository.saveRoll(it) }

                                                                    vaultFeedbackBanner = "🔗 Combined ${selectedRolls.size} legacy imports into ${createdRolls.size} unified roll(s) preserving ALL ${harmonizedFrames.size} exposure frames!"
                                                                }
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Merge ($selectedCount)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                        }
                                                    } else {
                                                        val singleSelected = masterVaultRolls.find { it.isSelectedForMerge }
                                                        if (singleSelected != null && singleSelected.status == RollStatus.UNASSIGNED) {
                                                            val matchingActive = masterVaultRolls.find { 
                                                                it.status == RollStatus.ACTIVE &&
                                                                it.cameraName.equals(singleSelected.cameraName, ignoreCase = true) &&
                                                                it.filmName.equals(singleSelected.filmName, ignoreCase = true) 
                                                            }

                                                            if (matchingActive != null) {
                                                                Button(
                                                                    onClick = {
                                                                        val activeIdx = masterVaultRolls.indexOf(matchingActive)
                                                                        val orphanIdx = masterVaultRolls.indexOf(singleSelected)
                                                                        if (activeIdx >= 0 && orphanIdx >= 0) {
                                                                            val combinedFrames = (matchingActive.frames + singleSelected.frames)
                                                                                .sortedBy { it.timestampMs }
                                                                                .mapIndexed { idx, f -> f.copy(frameNumber = idx + 1) }

                                                                            val maxCap = if (matchingActive.filmName.contains("120") || matchingActive.filmName.contains("800T") || matchingActive.cameraName.contains("Yashica")) 12 else 36

                                                                            if (combinedFrames.size <= maxCap) {
                                                                                val updatedSerial = matchingActive.filmRollSerial.replace(Regex("\\(\\d+ frames\\)"), "(${combinedFrames.size} frames)")
                                                                                masterVaultRolls[activeIdx] = matchingActive.copy(
                                                                                    frameCount = combinedFrames.size,
                                                                                    filmRollSerial = updatedSerial,
                                                                                    frames = combinedFrames
                                                                                )
                                                                                masterVaultRolls.removeAt(orphanIdx)
                                                                                selectedRollIndex = activeIdx
                                                                                DatabaseRepository.saveRoll(masterVaultRolls[activeIdx])
                                                                                DatabaseRepository.deleteRolls(listOf(singleSelected))
                                                                                vaultFeedbackBanner = "🏠 Auto-Linked ${singleSelected.frameCount} unassigned frame(s) into Active Roll: $updatedSerial!"
                                                                            } else {
                                                                                vaultFeedbackBanner = "⚠️ Cannot Auto-Link: Exceeds max capacity ($maxCap frames)!"
                                                                            }
                                                                        }
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Auto-Link (1)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                                }
                                                            } else {
                                                                Button(
                                                                    onClick = {
                                                                        val orphanIdx = masterVaultRolls.indexOf(singleSelected)
                                                                        if (orphanIdx >= 0) {
                                                                            val camCode = singleSelected.cameraName.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                            val filmCode = singleSelected.filmName.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                            val highestRollNum = masterVaultRolls
                                                                                .filter { it.cameraName == singleSelected.cameraName && it.filmName == singleSelected.filmName }
                                                                                .mapNotNull { Regex("-R(\\d+)").find(it.filmRollSerial)?.groupValues?.get(1)?.toIntOrNull() }
                                                                                .maxOrNull() ?: 0
                                                                                
                                                                            val nextRollNum = if (highestRollNum + 1 < 10) "0${highestRollNum + 1}" else "${highestRollNum + 1}"
                                                                            val newSerial = "$camCode-$filmCode-R$nextRollNum (${singleSelected.frameCount} frames)"

                                                                            masterVaultRolls[orphanIdx] = singleSelected.copy(
                                                                                status = RollStatus.ACTIVE,
                                                                                filmRollSerial = newSerial,
                                                                                isSelectedForMerge = false
                                                                            )
                                                                            DatabaseRepository.saveRoll(masterVaultRolls[orphanIdx])
                                                                            vaultFeedbackBanner = "🆕 Started New Active Roll: $newSerial!"
                                                                        }
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Start New Roll", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                                }
                                                            }
                                                        }
                                                    }

                                                    Button(
                                                        onClick = {
                                                            val selectedRolls = masterVaultRolls.filter { it.isSelectedForMerge }
                                                            masterVaultRolls.removeAll(selectedRolls)
                                                            DatabaseRepository.deleteRolls(selectedRolls)
                                                            vaultFeedbackBanner = "🗑️ Deleted ${selectedRolls.size} selected rolls from Master Vault."
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Delete Selected ($selectedCount)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                                    }
                                                }
                                            }

                                            // EXCEL-STYLE DATA GRID HEADER WITH IN-LINE DROPDOWNS
                                            Row(
                                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Master Checkbox in Grid Header
                                                Checkbox(
                                                    checked = allVisibleSelected,
                                                    onCheckedChange = { checked ->
                                                        masterVaultRolls.forEachIndexed { idx, roll ->
                                                            if (visibleIds.contains(roll.id)) {
                                                                masterVaultRolls[idx] = roll.copy(isSelectedForMerge = checked)
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.width(36.dp).padding(end = 4.dp)
                                                )
                                                
                                                // Camera Header with Filter
                                                var cameraFilterExpanded by remember { mutableStateOf(false) }
                                                Box(modifier = Modifier.weight(1.5f)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().clickable { cameraFilterExpanded = true },
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(if (filterCameraQuery == "All Cameras") "Camera" else "Cam: $filterCameraQuery", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (filterCameraQuery != "All Cameras") MaterialTheme.colorScheme.primary else Color.Unspecified)
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        if (sortOption == "Camera Name") Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    }
                                                    DropdownMenu(
                                                        expanded = cameraFilterExpanded,
                                                        onDismissRequest = { cameraFilterExpanded = false }
                                                    ) {
                                                        DropdownMenuItem(
                                                            text = { Text("Sort Ascending (A-Z)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) },
                                                            onClick = { sortOption = "Camera Name"; cameraFilterExpanded = false }
                                                        )
                                                        HorizontalDivider()
                                                        val uniqueCameras = listOf("All Cameras") + masterVaultRolls.map { it.cameraName }.distinct().sorted()
                                                        uniqueCameras.forEach { opt ->
                                                            DropdownMenuItem(
                                                                text = { Text(opt, style = MaterialTheme.typography.bodySmall) },
                                                                onClick = { filterCameraQuery = opt; cameraFilterExpanded = false }
                                                            )
                                                        }
                                                    }
                                                }

                                                // Film Stock Header with Filter
                                                var filmFilterExpanded by remember { mutableStateOf(false) }
                                                Box(modifier = Modifier.weight(1.5f)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().clickable { filmFilterExpanded = true },
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(if (filterFilmQuery == "All Films") "Film Stock" else "Film: $filterFilmQuery", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (filterFilmQuery != "All Films") MaterialTheme.colorScheme.primary else Color.Unspecified)
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        if (sortOption == "Film Stock") Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    }
                                                    DropdownMenu(
                                                        expanded = filmFilterExpanded,
                                                        onDismissRequest = { filmFilterExpanded = false }
                                                    ) {
                                                        DropdownMenuItem(
                                                            text = { Text("Sort Ascending (A-Z)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold) },
                                                            onClick = { sortOption = "Film Stock"; filmFilterExpanded = false }
                                                        )
                                                        HorizontalDivider()
                                                        val uniqueFilms = listOf("All Films") + masterVaultRolls.map { it.filmName }.distinct().sorted()
                                                        uniqueFilms.forEach { opt ->
                                                            DropdownMenuItem(
                                                                text = { Text(opt, style = MaterialTheme.typography.bodySmall) },
                                                                onClick = { filterFilmQuery = opt; filmFilterExpanded = false }
                                                            )
                                                        }
                                                    }
                                                }

                                                // Serial Header (Clickable for sorting)
                                                Row(modifier = Modifier.weight(1f).clickable { sortOption = "Date (Newest First)" }, verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Serial", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                    if (sortOption.contains("Date")) Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                                
                                                // Frames Header (Clickable for sorting outliers)
                                                Row(modifier = Modifier.width(60.dp).clickable { sortOption = "Frame Count" }, verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Frames", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                    if (sortOption == "Frame Count") Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

                                            filteredAndSortedRolls.forEachIndexed { _, roll ->
                                                val originalVaultIndex = masterVaultRolls.indexOf(roll)
                                                val isSelectedRoll = originalVaultIndex == selectedRollIndex

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(if (isSelectedRoll) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                                        .clickable {
                                                            selectedRollIndex = originalVaultIndex
                                                            selectedFrameIndex = 0
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Checkbox(
                                                        checked = roll.isSelectedForMerge,
                                                        onCheckedChange = { checked ->
                                                            if (originalVaultIndex >= 0) {
                                                                masterVaultRolls[originalVaultIndex] = roll.copy(isSelectedForMerge = checked)
                                                            }
                                                        },
                                                        modifier = Modifier.width(36.dp).padding(end = 4.dp)
                                                    )
                                                    Text(roll.cameraName, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
                                                    Text(roll.filmName, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
                                                    Text(roll.filmRollSerial.substringBefore(" "), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                                    Text("${roll.frameCount}", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                }
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                            }
                                        }
                                    }

                                    // RIGHT DETAIL PANE: IMAGE PREVIEW + FULL LINEAGE INSPECTION & EDIT FORM (2/3 WIDTH)
                                    Card(
                                        modifier = Modifier
                                            .weight(2f)
                                            .fillMaxHeight(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        if (activeRoll != null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(20.dp)
                                                    .verticalScroll(rememberScrollState()),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                // 1. ROLL HEADER & STAGE DISPATCH CONTROLS
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = "${activeRoll.cameraName} • ${activeRoll.filmName}",
                                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                                        )
                                                        Text(
                                                            text = "Physical Serial: ${activeRoll.filmRollSerial} • Status: ${activeRoll.status.name}",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }

                                                    if (activeRoll.status != RollStatus.UNASSIGNED) {
                                                        Button(
                                                            onClick = {
                                                                selectedTab = 2 // Jump to Stage 3 Tag Images!
                                                            }
                                                        ) {
                                                            Icon(Icons.Default.LocationOn, contentDescription = null)
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Tag Images in Stage 3", fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }

                                                // FULL-WIDTH HERO TRIAGE BANNER (WHEN INSPECTING UNASSIGNED FRAMES)
                                                if (activeRoll.status == RollStatus.UNASSIGNED) {
                                                    val matchingActiveRoll = masterVaultRolls.find { 
                                                        it.status == RollStatus.ACTIVE &&
                                                        it.cameraName.equals(activeRoll.cameraName, ignoreCase = true) &&
                                                        it.filmName.equals(activeRoll.filmName, ignoreCase = true) 
                                                    }

                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = CardDefaults.cardColors(containerColor = if (matchingActiveRoll != null) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = if (matchingActiveRoll != null) "MATCHING ACTIVE ROLL FOUND: ${matchingActiveRoll.filmRollSerial}" else "UNASSIGNED FRAMES",
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (matchingActiveRoll != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                                                )
                                                                Text(
                                                                    text = if (matchingActiveRoll != null) "Link these ${activeRoll.frameCount} frame(s) directly to ${matchingActiveRoll.filmRollSerial}." else "Provision a canonical roll serial (e.g. ${activeRoll.cameraName.filter{ it.isLetterOrDigit() }.take(3).uppercase()}-${activeRoll.filmName.filter{ it.isLetterOrDigit() }.take(3).uppercase()}-R01) and mark active.",
                                                                    style = MaterialTheme.typography.bodySmall
                                                                )
                                                            }

                                                            Spacer(modifier = Modifier.width(12.dp))

                                                            if (matchingActiveRoll != null) {
                                                                Button(
                                                                    onClick = {
                                                                        val activeIdx = masterVaultRolls.indexOf(matchingActiveRoll)
                                                                        if (activeIdx >= 0) {
                                                                            val combinedFrames = (matchingActiveRoll.frames + activeRoll.frames)
                                                                                .sortedBy { it.timestampMs }
                                                                                .mapIndexed { idx, f -> f.copy(frameNumber = idx + 1) }

                                                                            val maxCap = if (matchingActiveRoll.filmName.contains("120") || matchingActiveRoll.filmName.contains("800T") || matchingActiveRoll.cameraName.contains("Yashica")) 12 else 36

                                                                            if (combinedFrames.size <= maxCap) {
                                                                                val updatedSerial = matchingActiveRoll.filmRollSerial.replace(Regex("\\(\\d+ frames\\)"), "(${combinedFrames.size} frames)")
                                                                                masterVaultRolls[activeIdx] = matchingActiveRoll.copy(
                                                                                    frameCount = combinedFrames.size,
                                                                                    filmRollSerial = updatedSerial,
                                                                                    frames = combinedFrames
                                                                                )
                                                                                masterVaultRolls.removeAt(selectedRollIndex)
                                                                                selectedRollIndex = activeIdx
                                                                                DatabaseRepository.saveRoll(masterVaultRolls[activeIdx])
                                                                                DatabaseRepository.deleteRolls(listOf(activeRoll))
                                                                                vaultFeedbackBanner = "🏠 Auto-Linked ${activeRoll.frameCount} unassigned frame(s) into Active Roll: $updatedSerial!"
                                                                            } else {
                                                                                vaultFeedbackBanner = "⚠️ Cannot Auto-Link: Exceeds maximum capacity ($maxCap frames)!"
                                                                            }
                                                                        }
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                                                ) {
                                                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("🏠 Auto-Link Frames", fontWeight = FontWeight.Bold)
                                                                }
                                                            } else {
                                                                Button(
                                                                    onClick = {
                                                                        val camCode = activeRoll.cameraName.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                        val filmCode = activeRoll.filmName.filter { it.isLetterOrDigit() }.take(3).uppercase()
                                                                        val highestRollNum = masterVaultRolls
                                                                            .filter { it.cameraName == activeRoll.cameraName && it.filmName == activeRoll.filmName }
                                                                            .mapNotNull { Regex("-R(\\d+)").find(it.filmRollSerial)?.groupValues?.get(1)?.toIntOrNull() }
                                                                            .maxOrNull() ?: 0
                                                                            
                                                                        val nextRollNum = if (highestRollNum + 1 < 10) "0${highestRollNum + 1}" else "${highestRollNum + 1}"
                                                                        val newSerial = "$camCode-$filmCode-R$nextRollNum (${activeRoll.frameCount} frames)"

                                                                        masterVaultRolls[selectedRollIndex] = activeRoll.copy(
                                                                            status = RollStatus.ACTIVE,
                                                                            filmRollSerial = newSerial
                                                                        )
                                                                        DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                                        vaultFeedbackBanner = "🆕 Started New Active Roll: $newSerial!"
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                                ) {
                                                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("🆕 Start New Active Roll", fontWeight = FontWeight.Bold)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                // EDITABLE ROLL METADATA
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = activeRoll.cameraName,
                                                        onValueChange = { newCam ->
                                                            masterVaultRolls[selectedRollIndex] = activeRoll.copy(cameraName = newCam)
                                                            DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                        },
                                                        label = { Text("Camera Body") },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    OutlinedTextField(
                                                        value = activeRoll.filmName,
                                                        onValueChange = { newFilm ->
                                                            masterVaultRolls[selectedRollIndex] = activeRoll.copy(filmName = newFilm)
                                                            DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                        },
                                                        label = { Text("Film Stock") },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    OutlinedTextField(
                                                        value = activeRoll.filmRollSerial,
                                                        onValueChange = { newSerial ->
                                                            val resolvedStatus = if (newSerial.contains("-R")) RollStatus.ACTIVE else activeRoll.status
                                                            masterVaultRolls[selectedRollIndex] = activeRoll.copy(
                                                                status = resolvedStatus,
                                                                filmRollSerial = newSerial
                                                            )
                                                            DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                        },
                                                        label = { Text("Physical Roll Serial") },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }

                                                HorizontalDivider()

                                                // 2. FRAME SELECTOR STRIP (Uses FlowRow to wrap exactly like physical 35mm cutoffs!)
                                                Text(
                                                    "🎞️ SELECT FRAME TO INSPECT & QA (${activeFrames.size} FRAMES LOGGED)",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )

                                                @OptIn(ExperimentalLayoutApi::class)
                                                FlowRow(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                                    maxItemsInEachRow = 6 // Standard 35mm film binder sleeve cutoff!
                                                ) {
                                                    activeFrames.forEachIndexed { fIdx, frame ->
                                                        OutlinedButton(
                                                            onClick = { selectedFrameIndex = fIdx },
                                                            colors = if (fIdx == selectedFrameIndex) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                                                        ) {
                                                            Text("Frame #${frame.frameNumber}")
                                                        }
                                                    }
                                                }

                                                // 3. FRAME DETAIL PANEL & DECODED THUMBNAIL PREVIEW
                                                if (activeFrame != null) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                        verticalAlignment = Alignment.Top
                                                    ) {
                                                        // IMAGE PREVIEW THUMBNAIL
                                                        val decodedBitmap = remember(activeFrame.imageBytes) {
                                                            byteArrayToImageBitmap(activeFrame.imageBytes)
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .size(240.dp, 240.dp)
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            if (decodedBitmap != null) {
                                                                Image(
                                                                    bitmap = decodedBitmap,
                                                                    contentDescription = "Scan Preview",
                                                                    contentScale = ContentScale.Crop,
                                                                    modifier = Modifier.fillMaxSize()
                                                                )
                                                            } else {
                                                                Column(
                                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                                                                    Text("Scan Thumbnail", style = MaterialTheme.typography.labelSmall)
                                                                    Text("Frame #${activeFrame.frameNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                                }
                                                            }
                                                        }

                                                        // FULL LINEAGE DETAIL SPECS
                                                        Column(
                                                            modifier = Modifier.weight(1f),
                                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Text(
                                                                "🥾 FIELD EXPOSURE SPECS",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                            Text("Camera: ${activeFrame.cameraName} • Film: ${activeFrame.filmName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                                            Text("Shutter: ${activeFrame.shutterSpeed} • Aperture: ${activeFrame.aperture} • ISO: ${activeFrame.iso} • Date: ${activeFrame.dateDisplay}", style = MaterialTheme.typography.bodyMedium)

                                                            if (activeFrame.tags.isNotEmpty()) {
                                                                Text("Tags: ${activeFrame.tags.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                                            }
                                                            if (activeFrame.notes.isNotBlank()) {
                                                                Text("Notes: ${activeFrame.notes}", style = MaterialTheme.typography.bodyMedium)
                                                            }

                                                            // COMPACT INLINE GPS COORDINATES FOR THIS FRAME
                                                            var editLatText by remember(activeFrame) { mutableStateOf(activeFrame.latitude?.toString() ?: "") }
                                                            var editLonText by remember(activeFrame) { mutableStateOf(activeFrame.longitude?.toString() ?: "") }

                                                            Row(
                                                                modifier = Modifier.padding(vertical = 4.dp),
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    "📍 GPS:",
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.primary
                                                                )

                                                                OutlinedTextField(
                                                                    value = editLatText,
                                                                    onValueChange = { newLat ->
                                                                        editLatText = newLat
                                                                        val parsedLat = newLat.toDoubleOrNull()
                                                                        val updatedFrames = activeFrames.toMutableList()
                                                                        val idx = selectedFrameIndex.coerceIn(0, (updatedFrames.size - 1).coerceAtLeast(0))
                                                                        if (idx in updatedFrames.indices) {
                                                                            updatedFrames[idx] = updatedFrames[idx].copy(latitude = parsedLat)
                                                                            masterVaultRolls[selectedRollIndex] = activeRoll.copy(frames = updatedFrames)
                                                                        }
                                                                    },
                                                                    label = { Text("Lat") },
                                                                    modifier = Modifier.width(140.dp),
                                                                    singleLine = true,
                                                                    textStyle = MaterialTheme.typography.bodySmall
                                                                )

                                                                OutlinedTextField(
                                                                    value = editLonText,
                                                                    onValueChange = { newLon ->
                                                                        editLonText = newLon
                                                                        val parsedLon = newLon.toDoubleOrNull()
                                                                        val updatedFrames = activeFrames.toMutableList()
                                                                        val idx = selectedFrameIndex.coerceIn(0, (updatedFrames.size - 1).coerceAtLeast(0))
                                                                        if (idx in updatedFrames.indices) {
                                                                            updatedFrames[idx] = updatedFrames[idx].copy(longitude = parsedLon)
                                                                            masterVaultRolls[selectedRollIndex] = activeRoll.copy(frames = updatedFrames)
                                                                        }
                                                                    },
                                                                    label = { Text("Lon") },
                                                                    modifier = Modifier.width(140.dp),
                                                                    singleLine = true,
                                                                    textStyle = MaterialTheme.typography.bodySmall
                                                                )
                                                                
                                                                if (editLatText.isNotBlank() && editLonText.isNotBlank()) {
                                                                    IconButton(
                                                                        onClick = {
                                                                            val lat = editLatText.toDoubleOrNull()
                                                                            val lon = editLonText.toDoubleOrNull()
                                                                            if (lat != null && lon != null) {
                                                                                try {
                                                                                    val uri = "https://www.google.com/maps/search/?api=1&query=$lat,$lon"
                                                                                    Desktop.getDesktop().browse(URI(uri))
                                                                                } catch (_: Exception) {}
                                                                            }
                                                                        }
                                                                    ) {
                                                                        Icon(Icons.Default.LocationOn, contentDescription = "View Map", tint = MaterialTheme.colorScheme.primary)
                                                                    }
                                                                }
                                                            }
                                                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                                            Text(
                                                                "🧪 LAB & DARKROOM LINEAGE",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                            if (activeFrame.developerName == null) {
                                                                Text("No Lab Development data logged for this roll yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            } else {
                                                                Text("Film Developer: ${activeFrame.developerName}", style = MaterialTheme.typography.bodySmall)
                                                            }
                                                            
                                                            if (activeFrame.paperStock == null) {
                                                                Text("No Enlarger Print Session data logged for this frame.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            } else {
                                                                Text("Enlarger Recipe: ${activeFrame.paperStock} • ${activeFrame.enlargerLens} • ${activeFrame.baseSec} @ ${activeFrame.gradeFilter}", style = MaterialTheme.typography.bodySmall)
                                                                Text("Dodge & Burn: ${activeFrame.dodgeBurnNotes ?: "None"}", style = MaterialTheme.typography.bodySmall)
                                                                Text("Print Outcome: ${if (activeFrame.outcomeStatus == "KEEPER") "🟢 KEEPER (Exhibition)" else "🔴 REJECT"}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = if (activeFrame.outcomeStatus == "KEEPER") Color(0xFF2E7D32) else MaterialTheme.colorScheme.error)
                                                                Text("Reflection: ${activeFrame.learningTag ?: "Fine-art candidate"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // STAGE 3: TAG IMAGES
                            TagImagesTab(
                                masterVaultRolls = masterVaultRolls,
                                exifToolManager = exifToolManager
                            )
                        }
                        3 -> {
                            // STAGE 4: FOUNDATION ADMIN (GATEKEEPER)
                            FoundationAdminTab(
                                onPublishManifest = {
                                    // TODO: Wire up foundation manifest JSON generation logic
                                    vaultFeedbackBanner = "📤 Published Foundation Manifest to selected directory."
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundationAdminTab(onPublishManifest: () -> Unit) {
    var pendingItems by remember { mutableStateOf(emptyList<FoundationReviewQueueEntity>()) }
    var selectedCategory by remember { mutableIntStateOf(0) } // 0 = Cameras, 1 = Films, 2 = Suppliers
    var cameras by remember { mutableStateOf(emptyList<ReferenceCameraItem>()) }
    var films by remember { mutableStateOf(emptyList<ReferenceFilmItem>()) }
    var suppliers by remember { mutableStateOf(emptyList<ReferenceSupplierItem>()) }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingCamera by remember { mutableStateOf<ReferenceCameraItem?>(null) }
    var editingFilm by remember { mutableStateOf<ReferenceFilmItem?>(null) }
    var editingSupplier by remember { mutableStateOf<ReferenceSupplierItem?>(null) }

    fun refreshAllData() {
        transaction {
            pendingItems = FoundationReviewQueueEntity.find { FoundationReviewQueueTable.status eq "PENDING" }.toList()
        }
        cameras = DatabaseRepository.loadFoundationCameras()
        films = DatabaseRepository.loadFoundationFilms()
        suppliers = DatabaseRepository.loadFoundationSuppliers()
    }

    LaunchedEffect(Unit) {
        refreshAllData()
    }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddEditDialog = false
                editingCamera = null
                editingFilm = null
                editingSupplier = null
            },
            title = { Text(if ((editingCamera?.id ?: 0) > 0 || (editingFilm?.id ?: 0) > 0 || (editingSupplier?.id ?: 0) > 0) "Edit Reference Item" else "Add Reference Item", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (selectedCategory) {
                        0 -> {
                            val item = editingCamera ?: ReferenceCameraItem(name = "")
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingCamera = item.copy(name = it) },
                                label = { Text("Camera Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.type,
                                onValueChange = { editingCamera = item.copy(type = it) },
                                label = { Text("Camera Type (SLR, Compact, TLR)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.mount ?: "",
                                onValueChange = { editingCamera = item.copy(mount = it) },
                                label = { Text("Lens Mount (e.g. K-Mount, EF)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        1 -> {
                            val item = editingFilm ?: ReferenceFilmItem(name = "")
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingFilm = item.copy(name = it) },
                                label = { Text("Film Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.iso.toString(),
                                onValueChange = { editingFilm = item.copy(iso = it.toIntOrNull() ?: 400) },
                                label = { Text("ISO Rating") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.abbreviation,
                                onValueChange = { editingFilm = item.copy(abbreviation = it) },
                                label = { Text("Abbreviation (e.g. HP5)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            val item = editingSupplier ?: ReferenceSupplierItem(name = "")
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingSupplier = item.copy(name = it) },
                                label = { Text("Supplier Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.website ?: "",
                                onValueChange = { editingSupplier = item.copy(website = it) },
                                label = { Text("Website") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    when (selectedCategory) {
                        0 -> editingCamera?.let { if (it.name.isNotBlank()) DatabaseRepository.saveFoundationCamera(it) }
                        1 -> editingFilm?.let { if (it.name.isNotBlank()) DatabaseRepository.saveFoundationFilm(it) }
                        2 -> editingSupplier?.let { if (it.name.isNotBlank()) DatabaseRepository.saveFoundationSupplier(it) }
                    }
                    refreshAllData()
                    showAddEditDialog = false
                    editingCamera = null
                    editingFilm = null
                    editingSupplier = null
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddEditDialog = false
                    editingCamera = null
                    editingFilm = null
                    editingSupplier = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Pane: Review Queue
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "📥 REVIEW QUEUE (${pendingItems.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Unrecognised items from Fotologue Mobile imports.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider()

                if (pendingItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Queue is empty. You're fully synced!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(pendingItems.size) { index ->
                            val item = pendingItems[index]
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "[${item.itemType}] ${item.submittedName}",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Submitted by Contributor ID: ${item.contributorId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                transaction {
                                                    when (item.itemType) {
                                                        "CAMERA" -> FoundationCameraEntity.new { name = item.submittedName; type = "Unknown" }
                                                        "FILM" -> FoundationFilmEntity.new { name = item.submittedName; iso = item.submittedIso ?: 400; type = "Unknown"; abbreviation = item.submittedName.take(3).uppercase(); format = "Unknown" }
                                                        "SUPPLIER" -> FoundationSupplierEntity.new { name = item.submittedName }
                                                    }
                                                    item.status = "ACCEPTED"
                                                }
                                                refreshAllData()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Accept")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                transaction {
                                                    item.status = "REJECTED"
                                                }
                                                refreshAllData()
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Reject")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Right Pane: Master Reference Data Manager (Full CRUD)
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "🏛️ REFERENCE DATA MASTER LIST",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Items accepted or created here will be included in the Reference Sync Package for Fotologue Mobile to pull.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sub-category selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val categories = listOf("📷 Cameras (${cameras.size})", "🎞️ Films (${films.size})", "🏬 Suppliers (${suppliers.size})")
                    categories.forEachIndexed { idx, catTitle ->
                        val isSel = selectedCategory == idx
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedCategory = idx },
                            label = { Text(catTitle, fontWeight = FontWeight.Bold) }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            when (selectedCategory) {
                                0 -> editingCamera = ReferenceCameraItem(name = "")
                                1 -> editingFilm = ReferenceFilmItem(name = "")
                                2 -> editingSupplier = ReferenceSupplierItem(name = "")
                            }
                            showAddEditDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                }

                HorizontalDivider()

                // Data Grid for Selected Category
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedCategory) {
                        0 -> { // Cameras
                            if (cameras.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No reference cameras logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(cameras.size) { idx ->
                                        val cam = cameras[idx]
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(cam.name, fontWeight = FontWeight.Bold)
                                                    Text("${cam.type} • Mount: ${cam.mount ?: "Standard"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    IconButton(onClick = {
                                                        editingCamera = cam
                                                        showAddEditDialog = true
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = {
                                                        DatabaseRepository.deleteFoundationCamera(cam.id)
                                                        refreshAllData()
                                                    }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> { // Films
                            if (films.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No reference films logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(films.size) { idx ->
                                        val film = films[idx]
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("${film.name} (ISO ${film.iso})", fontWeight = FontWeight.Bold)
                                                    Text("${film.type} • ${film.format} • Abbr: ${film.abbreviation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    IconButton(onClick = {
                                                        editingFilm = film
                                                        showAddEditDialog = true
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = {
                                                        DatabaseRepository.deleteFoundationFilm(film.id)
                                                        refreshAllData()
                                                    }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> { // Suppliers
                            if (suppliers.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No reference suppliers logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(suppliers.size) { idx ->
                                        val sup = suppliers[idx]
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(sup.name, fontWeight = FontWeight.Bold)
                                                    Text(sup.website ?: "No Website", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    IconButton(onClick = {
                                                        editingSupplier = sup
                                                        showAddEditDialog = true
                                                    }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(onClick = {
                                                        DatabaseRepository.deleteFoundationSupplier(sup.id)
                                                        refreshAllData()
                                                    }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onPublishManifest,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync to Mobile", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
