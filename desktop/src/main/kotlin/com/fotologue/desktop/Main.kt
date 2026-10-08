package com.fotologue.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollbarAdapter
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.compose.ui.text.style.TextOverflow
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

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

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

            val parsedFrames = entries.mapIndexed { idx, dto ->
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
            }.sortedBy { it.frameNumber }

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
                        val jsonParser = Json { ignoreUnknownKeys = true }
                        val element = jsonParser.parseToJsonElement(jsonContent)
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
                    val sortedThumbnailEntries = thumbnailsMap.entries.sortedBy { (key, _) ->
                        key.toLongOrNull() ?: 0L
                    }
                    val sortedThumbnailsList = sortedThumbnailEntries.map { it.value }
                    val sortedThumbnailKeys = sortedThumbnailEntries.map { it.key }

                    parsedRolls.forEach { roll ->
                        // 3-Tier Robust Thumbnail Matcher (Timestamp -> Frame Serial _F01/_F02 -> Chronological Index)
                        val updatedFrames = roll.frames.mapIndexed { fIdx, f -> 
                            var bytes = thumbnailsMap[f.timestampMs.toString()]

                            if (bytes == null) {
                                val fNumFormatted = String.format(Locale.US, "F%02d", f.frameNumber)
                                val fNumAlt = "_F${f.frameNumber}"
                                val matchingKey = sortedThumbnailKeys.find { key ->
                                    key.contains(fNumFormatted, ignoreCase = true) || 
                                    key.endsWith(fNumAlt, ignoreCase = true) ||
                                    key.endsWith("_${f.frameNumber}", ignoreCase = true) ||
                                    key.contains("frame_${f.frameNumber}", ignoreCase = true)
                                }
                                if (matchingKey != null) {
                                    bytes = thumbnailsMap[matchingKey]
                                }
                            }

                            if (bytes == null && fIdx in sortedThumbnailsList.indices) {
                                bytes = sortedThumbnailsList[fIdx]
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
                // STAGE NAVIGATION TABS (WITH MATERIAL SYMBOLS)
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        Triple("Import", Icons.Default.Share, 0),
                        Triple("Process (${masterVaultRolls.size})", Icons.Default.Refresh, 1),
                        Triple("Tag Images", Icons.Default.LocationOn, 2),
                        Triple("Administration", Icons.Default.Settings, 3)
                    )
                    tabs.forEach { (title, icon, index) ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
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
                                            "Select a directory to scan for ZIP exports (e.g., Local Drive, Google Drive Desktop, or Mounted NAS Share). Fotologue Desktop will recursively scan the folder, safely extract all new embedded metadata graphs & thumbnails, and skip any previously imported frames (100% Consolidation Guarantee).",
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
                                                    Triple("Completed ($finishedCount)", RollStatus.FINISHED, MaterialTheme.colorScheme.secondary)
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

                                                            // EDITABLE NOTES / COMMENTS
                                                            var editNotesText by remember(activeFrame) { mutableStateOf(activeFrame.notes) }
                                                            OutlinedTextField(
                                                                value = editNotesText,
                                                                onValueChange = { newNotes ->
                                                                    editNotesText = newNotes
                                                                    val updatedFrames = activeFrames.toMutableList()
                                                                    val idx = selectedFrameIndex.coerceIn(0, (updatedFrames.size - 1).coerceAtLeast(0))
                                                                    if (idx in updatedFrames.indices) {
                                                                        val updatedFrame = updatedFrames[idx].copy(notes = newNotes)
                                                                        updatedFrames[idx] = updatedFrame
                                                                        masterVaultRolls[selectedRollIndex] = activeRoll.copy(frames = updatedFrames)
                                                                        DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                                    }
                                                                },
                                                                label = { Text("Notes / Comments") },
                                                                placeholder = { Text("Add comments e.g. abandoned bike near fence") },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                maxLines = 3,
                                                                textStyle = MaterialTheme.typography.bodySmall
                                                            )

                                                            // INTERACTIVE TAXONOMY TAG PICKER (Matching Mobile)
                                                            Text("Tags & Taxonomy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                                            val taxonomyContentMap = remember {
                                                                mapOf(
                                                                    "Events" to listOf("Wedding", "Concert", "Sport", "Festival", "Protest", "Celebration"),
                                                                    "Nature" to listOf("Sunset/sunrise", "Landscape", "Animals", "Flora", "Seascape"),
                                                                    "Urban/built environment" to listOf("Architecture", "Street", "Cityscape", "Building", "Bridge", "Interior", "Skyline"),
                                                                    "People & Portraits" to listOf("Portrait", "Group", "Candid", "Self-Portrait"),
                                                                    "Objects & Still Life" to listOf("Vehicle", "Food/Drink", "Art", "Document"),
                                                                    "Abstract" to listOf("Pattern", "Texture", "Shadow", "Reflection")
                                                                )
                                                            }

                                                            val taxonomyIntentMap = remember {
                                                                mapOf(
                                                                    "Study" to listOf("Light & Shadow Study", "Material & Film Test", "Composition Study"),
                                                                    "Portfolio" to listOf("Exhibition", "Publication", "Client Work"),
                                                                    "Experimental" to listOf("Push/Pull Test", "Double Exposure", "Expired Film Test"),
                                                                    "Documentary" to listOf("Historical Record", "Journalism", "Family Archive")
                                                                )
                                                            }

                                                            var selectedSubject by remember(activeFrame) { mutableStateOf("Events") }
                                                            var selectedIntent by remember(activeFrame) { mutableStateOf("Study") }
                                                            var subjectExpanded by remember { mutableStateOf(false) }
                                                            var intentExpanded by remember { mutableStateOf(false) }

                                                            // 1. Subject Dropdown & Content Chips
                                                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                                Box(modifier = Modifier.weight(1f)) {
                                                                    OutlinedTextField(
                                                                        value = selectedSubject,
                                                                        onValueChange = {},
                                                                        readOnly = true,
                                                                        label = { Text("Subject Group") },
                                                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                                                        modifier = Modifier.clickable { subjectExpanded = true }.fillMaxWidth(),
                                                                        textStyle = MaterialTheme.typography.bodySmall
                                                                    )
                                                                    DropdownMenu(
                                                                        expanded = subjectExpanded,
                                                                        onDismissRequest = { subjectExpanded = false }
                                                                    ) {
                                                                        taxonomyContentMap.keys.forEach { subj ->
                                                                            DropdownMenuItem(
                                                                                text = { Text(subj) },
                                                                                onClick = {
                                                                                    selectedSubject = subj
                                                                                    subjectExpanded = false
                                                                                }
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            val availableContentChips = taxonomyContentMap[selectedSubject] ?: emptyList()
                                                            Text("Content Tags (${selectedSubject}):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                availableContentChips.forEach { chipTag ->
                                                                    val isSelected = activeFrame.tags.contains(chipTag)
                                                                    FilterChip(
                                                                        selected = isSelected,
                                                                        onClick = {
                                                                            val updatedTags = activeFrame.tags.toMutableList()
                                                                            if (isSelected) updatedTags.remove(chipTag) else updatedTags.add(chipTag)
                                                                            val updatedFrames = activeFrames.toMutableList()
                                                                            val idx = selectedFrameIndex.coerceIn(0, (updatedFrames.size - 1).coerceAtLeast(0))
                                                                            if (idx in updatedFrames.indices) {
                                                                                updatedFrames[idx] = updatedFrames[idx].copy(tags = updatedTags)
                                                                                masterVaultRolls[selectedRollIndex] = activeRoll.copy(frames = updatedFrames)
                                                                                DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                                            }
                                                                        },
                                                                        label = { Text(chipTag, style = MaterialTheme.typography.labelSmall) }
                                                                    )
                                                                }
                                                            }

                                                            // 2. Intent Dropdown & Intent Sub-Tag Chips
                                                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                                Box(modifier = Modifier.weight(1f)) {
                                                                    OutlinedTextField(
                                                                        value = selectedIntent,
                                                                        onValueChange = {},
                                                                        readOnly = true,
                                                                        label = { Text("Intent Category") },
                                                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                                                        modifier = Modifier.clickable { intentExpanded = true }.fillMaxWidth(),
                                                                        textStyle = MaterialTheme.typography.bodySmall
                                                                    )
                                                                    DropdownMenu(
                                                                        expanded = intentExpanded,
                                                                        onDismissRequest = { intentExpanded = false }
                                                                    ) {
                                                                        taxonomyIntentMap.keys.forEach { intent ->
                                                                            DropdownMenuItem(
                                                                                text = { Text(intent) },
                                                                                onClick = {
                                                                                    selectedIntent = intent
                                                                                    intentExpanded = false
                                                                                }
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            val availableIntentChips = taxonomyIntentMap[selectedIntent] ?: emptyList()
                                                            Text("Intent Sub-Tags (${selectedIntent}):", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                            ) {
                                                                availableIntentChips.forEach { chipTag ->
                                                                    val isSelected = activeFrame.tags.contains(chipTag)
                                                                    FilterChip(
                                                                        selected = isSelected,
                                                                        onClick = {
                                                                            val updatedTags = activeFrame.tags.toMutableList()
                                                                            if (isSelected) updatedTags.remove(chipTag) else updatedTags.add(chipTag)
                                                                            val updatedFrames = activeFrames.toMutableList()
                                                                            val idx = selectedFrameIndex.coerceIn(0, (updatedFrames.size - 1).coerceAtLeast(0))
                                                                            if (idx in updatedFrames.indices) {
                                                                                updatedFrames[idx] = updatedFrames[idx].copy(tags = updatedTags)
                                                                                masterVaultRolls[selectedRollIndex] = activeRoll.copy(frames = updatedFrames)
                                                                                DatabaseRepository.saveRoll(masterVaultRolls[selectedRollIndex])
                                                                            }
                                                                        },
                                                                        label = { Text(chipTag, style = MaterialTheme.typography.labelSmall) }
                                                                    )
                                                                }
                                                            }

                                                            // DISPLAY ACTIVE HASHTAG CHIPS
                                                            if (activeFrame.tags.isNotEmpty()) {
                                                                Text("Active Tags:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                                ) {
                                                                    activeFrame.tags.forEach { tag ->
                                                                        Card(
                                                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                                                        ) {
                                                                            Text(
                                                                                "#$tag",
                                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                                                style = MaterialTheme.typography.labelSmall,
                                                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                                                            )
                                                                        }
                                                                    }
                                                                }
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
                            // STAGE 4: ADMINISTRATION
                            FoundationAdminTab(
                                onPublishManifest = {
                                    val detectedDrive = detectGoogleDrivePath() ?: System.getProperty("user.home")
                                    val targetDir = chooseDirectoryOrFile(detectedDrive)
                                    if (targetDir != null) {
                                        val jsonPayload = DatabaseRepository.exportReferenceSyncPackageJson()
                                        val manifestFile = if (File(targetDir).isDirectory) File(targetDir, "fotologue_foundation_manifest.json") else File(File(targetDir).parentFile, "fotologue_foundation_manifest.json")
                                        manifestFile.writeText(jsonPayload)
                                        vaultFeedbackBanner = "📤 Published Reference Sync Package to: ${manifestFile.absolutePath}"
                                    }
                                },
                                onImportManifest = {
                                    val detectedDrive = detectGoogleDrivePath() ?: System.getProperty("user.home")
                                    val targetFile = chooseDirectoryOrFile(detectedDrive)
                                    if (targetFile != null && File(targetFile).exists()) {
                                        val jsonPayload = File(targetFile).readText()
                                        DatabaseRepository.importReferenceSyncPackageJson(jsonPayload)
                                        vaultFeedbackBanner = "📥 Imported Reference Sync Package from: ${File(targetFile).name}"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatOrderStatus(status: String): String {
    return when (status.uppercase()) {
        "WAITLISTED" -> "Waitlisted"
        "PACKAGING" -> "Packaging"
        "IN_TRANSIT" -> "In Transit"
        "DELIVERED", "RECEIVED" -> "Delivered"
        else -> status.replace('_', ' ').lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

private fun parseOrderStatusToEnum(display: String): String {
    return when (display) {
        "Waitlisted" -> "WAITLISTED"
        "Packaging" -> "PACKAGING"
        "In Transit" -> "IN_TRANSIT"
        "Delivered" -> "DELIVERED"
        else -> display.uppercase().replace(' ', '_')
    }
}

private fun formatInteractionType(type: String): String {
    return when (type.uppercase()) {
        "LOAN_OUT", "LOAN" -> "Loan Out"
        "RENTAL_IN", "RENTAL" -> "Rental In"
        "SERVICE_REPAIR", "REPAIR" -> "Service / Repair"
        "PURCHASE_ORDER" -> "Purchase Order"
        else -> type.replace('_', ' ').lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

private fun parseInteractionTypeToEnum(display: String): String {
    return when (display) {
        "Purchase Order" -> "PURCHASE_ORDER"
        "Loan Out" -> "LOAN_OUT"
        "Rental In" -> "RENTAL_IN"
        "Service / Repair" -> "SERVICE_REPAIR"
        else -> display.uppercase().replace(' ', '_')
    }
}

class FoundationAdminFormState {
    var showAddEditDialog by mutableStateOf(false)
    var showReviewQueueDialog by mutableStateOf(false)
    var selectedAssociatedCameraIds by mutableStateOf(setOf<Int>())
    var showCameraPresetsDialog by mutableStateOf(false)
    var selectedCameraTypeFilter by mutableStateOf("All")
    var cameraSearchQuery by mutableStateOf("")
    var showLensPresetsDialog by mutableStateOf(false)
    var selectedLensMountFilter by mutableStateOf("All")
    var lensSearchQueryText by mutableStateOf("")
    var showFilmPresetsDialog by mutableStateOf(false)
    var selectedFilmManufacturer by mutableStateOf("All")
    var showEquipmentPresetsDialog by mutableStateOf(false)
    var selectedEquipmentCategoryFilter by mutableStateOf("All")
    var equipmentSearchQuery by mutableStateOf("")
    var isPresetDropdownExpanded by mutableStateOf(false)

    var editingCamera by mutableStateOf<ReferenceCameraItem?>(null)
    var fixedLensName by mutableStateOf("")
    var fixedLensFocalLength by mutableStateOf("80mm")
    var fixedLensMaxAperture by mutableStateOf("f/3.5")
    var fixedLensFilterSize by mutableStateOf("Bay I")
    var editingLens by mutableStateOf<ReferenceLensItem?>(null)
    var editingFilm by mutableStateOf<ReferenceFilmItem?>(null)
    var editingKit by mutableStateOf<ReferenceKitItem?>(null)
    var editingTeleconverter by mutableStateOf<ReferenceTeleconverterItem?>(null)
    var editingFilter by mutableStateOf<ReferenceFilterItem?>(null)
    var editingFlash by mutableStateOf<ReferenceFlashItem?>(null)
    var editingViewfinder by mutableStateOf<ReferenceViewfinderItem?>(null)
    var editingSupplier by mutableStateOf<ReferenceSupplierItem?>(null)
    var editingConsumable by mutableStateOf<ReferenceConsumableItem?>(null)
    var editingLabEquipment by mutableStateOf<ReferenceLabEquipmentItem?>(null)
    var editingStorageLocation by mutableStateOf<ReferenceStorageLocationItem?>(null)
    var editingCustodyInteraction by mutableStateOf<ReferenceCustodyInteractionItem?>(null)

    var desktopDeliveryDialogOrder by mutableStateOf<ReferenceCustodyInteractionItem?>(null)
    var desktopSelectedDeliveryLocation by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FoundationAdminTab(onPublishManifest: () -> Unit, onImportManifest: () -> Unit) {
    val masterBrands = remember {
        listOf(
            "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
            "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
            "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
            "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "Other..."
        )
    }
    var pendingItems by remember { mutableStateOf(emptyList<FoundationReviewQueueEntity>()) }
    var selectedThemeGroup by remember { mutableIntStateOf(0) } // 0 = Photography Entities (Gear Collection), 1 = Lab & Inventory
    var selectedCategory by remember { mutableIntStateOf(0) } // 0=Cam, 1=Lens, 2=Film, 3=Kit, 4=Acc, 5=Sup, 6=Con, 7=Eq

    var contributors by remember { mutableStateOf(emptyList<ContributorItem>()) }
    var selectedContributor by remember { mutableStateOf<ContributorItem?>(null) }
    var isContributorDropdownExpanded by remember { mutableStateOf(false) }

    var cameras by remember { mutableStateOf(emptyList<ReferenceCameraItem>()) }
    var lenses by remember { mutableStateOf(emptyList<ReferenceLensItem>()) }
    var films by remember { mutableStateOf(emptyList<ReferenceFilmItem>()) }
    var kits by remember { mutableStateOf(emptyList<ReferenceKitItem>()) }
    var teleconverters by remember { mutableStateOf(emptyList<ReferenceTeleconverterItem>()) }
    var filters by remember { mutableStateOf(emptyList<ReferenceFilterItem>()) }
    var flashes by remember { mutableStateOf(emptyList<ReferenceFlashItem>()) }
    var viewfinders by remember { mutableStateOf(emptyList<ReferenceViewfinderItem>()) }
    var suppliers by remember { mutableStateOf(emptyList<ReferenceSupplierItem>()) }
    var consumables by remember { mutableStateOf(emptyList<ReferenceConsumableItem>()) }
    var labEquipment by remember { mutableStateOf(emptyList<ReferenceLabEquipmentItem>()) }
    var storageLocations by remember { mutableStateOf(emptyList<ReferenceStorageLocationItem>()) }
    var custodyInteractions by remember { mutableStateOf(emptyList<ReferenceCustodyInteractionItem>()) }

    val formState = remember { FoundationAdminFormState() }

    var desktopDeliveryDialogOrder by formState::desktopDeliveryDialogOrder
    var desktopSelectedDeliveryLocation by formState::desktopSelectedDeliveryLocation

    var showAddEditDialog by formState::showAddEditDialog
    var showReviewQueueDialog by formState::showReviewQueueDialog
    var selectedAssociatedCameraIds by formState::selectedAssociatedCameraIds
    var showCameraPresetsDialog by formState::showCameraPresetsDialog
    var selectedCameraTypeFilter by formState::selectedCameraTypeFilter
    var cameraSearchQuery by formState::cameraSearchQuery
    var showLensPresetsDialog by formState::showLensPresetsDialog
    var selectedLensMountFilter by formState::selectedLensMountFilter
    var lensSearchQueryText by formState::lensSearchQueryText
    var showFilmPresetsDialog by formState::showFilmPresetsDialog
    var selectedFilmManufacturer by formState::selectedFilmManufacturer
    var showEquipmentPresetsDialog by formState::showEquipmentPresetsDialog
    var selectedEquipmentCategoryFilter by formState::selectedEquipmentCategoryFilter
    var equipmentSearchQuery by formState::equipmentSearchQuery
    var isPresetDropdownExpanded by formState::isPresetDropdownExpanded
    var editingCamera by formState::editingCamera
    var fixedLensName by formState::fixedLensName
    var fixedLensFocalLength by formState::fixedLensFocalLength
    var fixedLensMaxAperture by formState::fixedLensMaxAperture
    var fixedLensFilterSize by formState::fixedLensFilterSize
    var editingLens by formState::editingLens
    var editingFilm by formState::editingFilm
    var editingKit by formState::editingKit
    var editingTeleconverter by formState::editingTeleconverter
    var editingFilter by formState::editingFilter
    var editingFlash by formState::editingFlash
    var editingViewfinder by formState::editingViewfinder
    var editingSupplier by formState::editingSupplier
    var editingConsumable by formState::editingConsumable
    var editingLabEquipment by formState::editingLabEquipment
    var editingStorageLocation by formState::editingStorageLocation
    var editingCustodyInteraction by formState::editingCustodyInteraction

    fun refreshAllData() {
        transaction {
            pendingItems = FoundationReviewQueueEntity.find { FoundationReviewQueueTable.status eq "PENDING" }.toList()
        }
        contributors = DatabaseRepository.loadContributors()
        if (selectedContributor == null) selectedContributor = contributors.firstOrNull()
        cameras = DatabaseRepository.loadFoundationCameras()
        lenses = DatabaseRepository.loadFoundationLenses()
        films = DatabaseRepository.loadFoundationFilms()
        kits = DatabaseRepository.loadFoundationKits()
        teleconverters = DatabaseRepository.loadFoundationTeleconverters()
        filters = DatabaseRepository.loadFoundationFilters()
        flashes = DatabaseRepository.loadFoundationFlashes()
        viewfinders = DatabaseRepository.loadFoundationViewfinders()
        suppliers = DatabaseRepository.loadFoundationSuppliers()
        consumables = DatabaseRepository.loadFoundationConsumables()
        labEquipment = DatabaseRepository.loadFoundationLabEquipment()
        storageLocations = DatabaseRepository.loadStorageLocations()
        custodyInteractions = DatabaseRepository.loadCustodyInteractions()
    }

    LaunchedEffect(Unit) {
        refreshAllData()
    }

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddEditDialog = false
                editingCamera = null
                editingLens = null
                editingFilm = null
                editingKit = null
                editingTeleconverter = null
                editingFilter = null
                editingFlash = null
                editingViewfinder = null
                editingSupplier = null
                editingConsumable = null
                editingLabEquipment = null
            },
            title = {
                val action = if ((editingCamera?.id ?: 0) > 0 || (editingLens?.id ?: 0) > 0 || (editingFilm?.id ?: 0) > 0 || (editingKit?.id ?: 0) > 0 || (editingTeleconverter?.id ?: 0) > 0 || (editingFilter?.id ?: 0) > 0 || (editingFlash?.id ?: 0) > 0 || (editingViewfinder?.id ?: 0) > 0 || (editingSupplier?.id ?: 0) > 0 || (editingConsumable?.id ?: 0) > 0 || (editingLabEquipment?.id ?: 0) > 0 || (editingStorageLocation?.id ?: 0) > 0 || (editingCustodyInteraction?.id ?: 0) > 0) "Edit" else "Add"
                val categoryName = when (selectedCategory) {
                    0 -> "Camera"
                    1 -> "Lens"
                    2 -> "Film"
                    3 -> "Camera Kit"
                    4 -> "Teleconverter / Extension Ring"
                    5 -> "Filter"
                    6 -> "Flash"
                    7 -> "Viewfinder"
                    8 -> "Contact"
                    9 -> "Consumable"
                    10 -> "Lab Equipment"
                    11 -> "Storage Location"
                    12 -> "Pre-Arrival Order"
                    13 -> "Custody Interaction"
                    else -> "Item"
                }
                Text("$action $categoryName", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (selectedCategory) {
                        0 -> { // Camera Form with Streamlined 1-to-1 Parity (Serial Number Moved to End)
                            val item = editingCamera ?: ReferenceCameraItem(name = "")
                            var cameraTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var filmFormatDropdownExpanded by remember { mutableStateOf(false) }

                            val cameraTypes = listOf("Film SLR", "Rangefinder", "TLR", "Point & Shoot", "Compact", "Medium Format SLR", "Large Format")
                            val filmFormats = listOf("35mm", "120", "Sheet", "Half-Frame")

                            Button(
                                onClick = { showCameraPresetsDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            }

                            var cameraBrandDropdownExpanded by remember { mutableStateOf(false) }

                            // Row 1: Brand & Model Name
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.name.substringBefore(" ").ifBlank { "Pentax" },
                                        onValueChange = { mkg -> editingCamera = item.copy(name = "$mkg ${if (item.name.contains(" ")) item.name.substringAfter(" ") else ""}".trim()) },
                                        label = { Text("Brand") },
                                        placeholder = { Text("e.g. Pentax, Nikon, Canon") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { cameraBrandDropdownExpanded = !cameraBrandDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = cameraBrandDropdownExpanded,
                                        onDismissRequest = { cameraBrandDropdownExpanded = false }
                                    ) {
                                        masterBrands.forEach { brandOpt ->
                                            DropdownMenuItem(
                                                text = { Text(brandOpt) },
                                                onClick = {
                                                    if (brandOpt != "Other...") {
                                                        val modelPart = if (item.name.contains(" ")) item.name.substringAfter(" ") else item.name
                                                        editingCamera = item.copy(name = "$brandOpt $modelPart".trim())
                                                    }
                                                    cameraBrandDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.name,
                                    onValueChange = { editingCamera = item.copy(name = it) },
                                    label = { Text("Model Name") },
                                    placeholder = { Text("e.g. LX, ME Super, K1000") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 2: Camera Type & Film Format
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.type,
                                        onValueChange = { selectedType ->
                                            val isTlrOrFixed = selectedType == "TLR" || selectedType == "Point & Shoot" || selectedType == "Compact"
                                            val newFormat = if (selectedType == "TLR" || selectedType == "Medium Format SLR") "120" else item.format
                                            val newMount = if (isTlrOrFixed) "Fixed" else item.mount
                                            editingCamera = item.copy(type = selectedType, format = newFormat, mount = newMount)
                                            if (isTlrOrFixed && fixedLensName.isBlank()) {
                                                fixedLensName = "${item.name} Taking Lens".trim()
                                            }
                                        },
                                        label = { Text("Camera Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { cameraTypeDropdownExpanded = !cameraTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = cameraTypeDropdownExpanded,
                                        onDismissRequest = { cameraTypeDropdownExpanded = false }
                                    ) {
                                        cameraTypes.forEach { typeOption ->
                                            DropdownMenuItem(
                                                text = { Text(typeOption) },
                                                onClick = {
                                                    val isTlrOrFixed = typeOption == "TLR" || typeOption == "Point & Shoot" || typeOption == "Compact"
                                                    val newFormat = if (typeOption == "TLR" || typeOption == "Medium Format SLR") "120" else item.format
                                                    val newMount = if (isTlrOrFixed) "Fixed" else item.mount
                                                    editingCamera = item.copy(type = typeOption, format = newFormat, mount = newMount)
                                                    if (isTlrOrFixed && fixedLensName.isBlank()) {
                                                        fixedLensName = "${item.name} Taking Lens".trim()
                                                    }
                                                    cameraTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.format,
                                        onValueChange = { editingCamera = item.copy(format = it) },
                                        label = { Text("Film Format") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { filmFormatDropdownExpanded = !filmFormatDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = filmFormatDropdownExpanded,
                                        onDismissRequest = { filmFormatDropdownExpanded = false }
                                    ) {
                                        filmFormats.forEach { formatOption ->
                                            DropdownMenuItem(
                                                text = { Text(formatOption) },
                                                onClick = {
                                                    editingCamera = item.copy(format = formatOption)
                                                    filmFormatDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                "Hint: If your camera uses multiple film formats (e.g. interchangeable backs), add it once for each format.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Row 3: Lens Mount & Shutter Speeds
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    var mountDropdownExpanded by remember { mutableStateOf(false) }
                                    val availableMounts = remember { DatabaseRepository.loadLensMountLovs() }
                                    OutlinedTextField(
                                        value = item.mount ?: "",
                                        onValueChange = { editingCamera = item.copy(mount = it) },
                                        label = { Text("Lens Mount") },
                                        placeholder = { Text("e.g. Pentax K, Canon FD, Fixed") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { mountDropdownExpanded = !mountDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = mountDropdownExpanded,
                                        onDismissRequest = { mountDropdownExpanded = false }
                                    ) {
                                        availableMounts.forEach { mntOpt ->
                                            DropdownMenuItem(
                                                text = { Text(mntOpt) },
                                                onClick = {
                                                    editingCamera = item.copy(mount = mntOpt)
                                                    mountDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.shutterSpeeds ?: "",
                                    onValueChange = { editingCamera = item.copy(shutterSpeeds = it) },
                                    label = { Text("Shutter Speeds") },
                                    placeholder = { Text("1s - 1/1000s, B") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                "Hint: Enter speeds as a range (e.g. 1s - 1/1000s, B) or comma separated (e.g. 1/1000, 1/500, 1/250, B).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Inline Taking Lens Section for TLR / Fixed Lens Cameras
                            val isFixedCamera = item.type == "TLR" || item.type == "Point & Shoot" || item.type == "Compact" || item.mount == "Fixed"
                            if (isFixedCamera) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("📷 Integrated Taking Lens Specifications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                        OutlinedTextField(
                                            value = fixedLensName,
                                            onValueChange = { fixedLensName = it },
                                            label = { Text("Taking Lens Name (e.g. Yashinon 80mm f/3.5)") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = fixedLensFocalLength,
                                                onValueChange = { fixedLensFocalLength = it },
                                                label = { Text("Focal Length") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = fixedLensMaxAperture,
                                                onValueChange = { fixedLensMaxAperture = it },
                                                label = { Text("Max Aperture") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = fixedLensFilterSize,
                                                onValueChange = { fixedLensFilterSize = it },
                                                label = { Text("Filter Size") },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Row 4 (LAST): Serial Number (Optional)
                            OutlinedTextField(
                                value = item.serialNumber ?: "",
                                onValueChange = { editingCamera = item.copy(serialNumber = it) },
                                label = { Text("Serial Number (Optional)") },
                                placeholder = { Text("Optional") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        1 -> { // Lens Form with Associated Cameras and Preset Catalogue Search
                            val item = editingLens ?: ReferenceLensItem(name = "")
                            var selectedAssociatedCameraIds by remember { mutableStateOf(setOf<Int>()) }
                            var lensSearchQuery by remember { mutableStateOf("") }
                            var isLensSearchDropdownExpanded by remember { mutableStateOf(false) }

                            var makeDropdownExpanded by remember { mutableStateOf(false) }
                            var lensTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var mountDropdownExpanded by remember { mutableStateOf(false) }

                            val commonMakes = listOf(
                                "Alpa", "Arca-Swiss", "Arax", "Bronica", "Canon", "Chamonix", "Chroma", "Contax",
                                "Fujifilm", "Hasselblad", "Intrepid", "Kodak", "Konica", "Leica", "Linhof",
                                "Lomography", "Mamiya", "Minolta", "Nikon", "Olympus", "Pentax", "Phase One",
                                "Plaubel", "Ricoh", "Rollei", "Shen Hao", "Sinar", "Toyo", "Voigtländer", "Yashica", "Other..."
                            )
                            val lensTypes = listOf("Prime", "Zoom", "Macro", "Anamorphic", "Tilt-Shift")

                            // Section 1: Associated Cameras (Interchangeable Lens Bodies Only, Unless Fixed Camera is Missing Taking Lens)
                            Text("Associated Cameras", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            val compatibleLensCameras = cameras.filter { cam ->
                                val isInterchangeable = cam.mount != "Fixed" && cam.type != "TLR" && cam.type != "Point & Shoot" && cam.type != "Compact"
                                if (isInterchangeable) true
                                else {
                                    lenses.none { l -> l.name.contains(cam.name, ignoreCase = true) || (l.make?.contains(cam.name, ignoreCase = true) == true) }
                                }
                            }
                            if (compatibleLensCameras.isEmpty()) {
                                Text("No compatible cameras found. Please add an SLR, Rangefinder, or Medium Format camera first.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    compatibleLensCameras.forEach { camera ->
                                        val isSelected = selectedAssociatedCameraIds.contains(camera.id)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAssociatedCameraIds = if (isSelected) selectedAssociatedCameraIds - camera.id else selectedAssociatedCameraIds + camera.id
                                            },
                                            label = { Text(camera.name, style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) } } else null
                                        )
                                    }
                                }
                            }
                            Text(
                                "Note: Only cameras with interchangeable lens mounts (or fixed-lens bodies missing their taking lens record) are listed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Button(
                                onClick = { showLensPresetsDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Section 3: Manual Entry / Detailed Specs
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.make ?: "",
                                        onValueChange = { editingLens = item.copy(make = it, name = "${it} ${item.model ?: ""}".trim()) },
                                        label = { Text("Brand") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { makeDropdownExpanded = !makeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = makeDropdownExpanded,
                                        onDismissRequest = { makeDropdownExpanded = false }
                                    ) {
                                        commonMakes.forEach { mOpt ->
                                            DropdownMenuItem(
                                                text = { Text(mOpt) },
                                                onClick = {
                                                    editingLens = item.copy(make = mOpt, name = "$mOpt ${item.model ?: ""}".trim())
                                                    makeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.model ?: "",
                                    onValueChange = { editingLens = item.copy(model = it, name = "${item.make ?: ""} $it".trim()) },
                                    label = { Text("Model Name") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.type,
                                        onValueChange = { editingLens = item.copy(type = it) },
                                        label = { Text("Lens Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { lensTypeDropdownExpanded = !lensTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = lensTypeDropdownExpanded,
                                        onDismissRequest = { lensTypeDropdownExpanded = false }
                                    ) {
                                        lensTypes.forEach { tOpt ->
                                            DropdownMenuItem(
                                                text = { Text(tOpt) },
                                                onClick = {
                                                    editingLens = item.copy(type = tOpt)
                                                    lensTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    val availableLensMounts = remember { DatabaseRepository.loadLensMountLovs() }
                                    OutlinedTextField(
                                        value = item.mount ?: "",
                                        onValueChange = { editingLens = item.copy(mount = it) },
                                        label = { Text("Lens Mount") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { mountDropdownExpanded = !mountDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = mountDropdownExpanded,
                                        onDismissRequest = { mountDropdownExpanded = false }
                                    ) {
                                        availableLensMounts.forEach { mountOpt ->
                                            DropdownMenuItem(
                                                text = { Text(mountOpt) },
                                                onClick = {
                                                    editingLens = item.copy(mount = mountOpt)
                                                    mountDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.adapterUsed ?: "",
                                onValueChange = { editingLens = item.copy(adapterUsed = it) },
                                label = { Text("Lens Mount Adapter Used (Optional)") },
                                placeholder = { Text("e.g. M42 to Canon FD Adapter, Leica L39 to M") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.focalLength ?: "50mm",
                                    onValueChange = { editingLens = item.copy(focalLength = it) },
                                    label = { Text("Focal Length (mm)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.filterSize ?: "",
                                    onValueChange = { editingLens = item.copy(filterSize = it) },
                                    label = { Text("Filter Size (mm)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.maxAperture ?: "f/1.4",
                                    onValueChange = { editingLens = item.copy(maxAperture = it) },
                                    label = { Text("Max Ap (e.g. 1.8)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.minAperture ?: "f/22",
                                    onValueChange = { editingLens = item.copy(minAperture = it) },
                                    label = { Text("Min Ap (e.g. 22)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.minFocusMeters ?: "",
                                    onValueChange = { editingLens = item.copy(minFocusMeters = it) },
                                    label = { Text("Min Focus (m)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.maxMagnification ?: "",
                                    onValueChange = { editingLens = item.copy(maxMagnification = it) },
                                    label = { Text("Max Mag (x)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = item.serialNumber ?: "",
                                    onValueChange = { editingLens = item.copy(serialNumber = it) },
                                    label = { Text("Serial Number (Optional)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = item.isImageStabilised,
                                        onCheckedChange = { editingLens = item.copy(isImageStabilised = it) }
                                    )
                                    Text("Image Stabilised", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        2 -> { // Film Form with 1-to-1 Mobile Parity and Australian English Spelling
                            val item = editingFilm ?: ReferenceFilmItem(name = "")
                            var filmTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var filmFormatDropdownExpanded by remember { mutableStateOf(false) }
                            var exposuresDropdownExpanded by remember { mutableStateOf(false) }

                            val filmTypes = listOf("Colour Negative (C-41)", "Black & White Negative (B&W)", "Colour Slide / Reversal (E-6)", "B&W Slide", "Infrared")
                            val filmFormats = listOf("35mm", "120", "Sheet", "110", "APS")
                            val exposureOptions = listOf("36", "24", "12", "16", "10")

                            Button(
                                onClick = { showFilmPresetsDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = item.name,
                                    onValueChange = { 
                                        editingFilm = item.copy(name = it)
                                        isPresetDropdownExpanded = true
                                    },
                                    label = { Text("Film Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { isPresetDropdownExpanded = !isPresetDropdownExpanded }) {
                                            Icon(Icons.Default.Search, contentDescription = "Preset Catalogue Search")
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = isPresetDropdownExpanded && DatabaseRepository.searchPresetFilms(item.name).isNotEmpty(),
                                    onDismissRequest = { isPresetDropdownExpanded = false }
                                ) {
                                    DatabaseRepository.searchPresetFilms(item.name).take(5).forEach { preset ->
                                        DropdownMenuItem(
                                            text = { Text("${preset.name} (ISO ${preset.iso} • ${preset.type})") },
                                            onClick = {
                                                editingFilm = preset
                                                isPresetDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.abbreviation,
                                    onValueChange = { editingFilm = item.copy(abbreviation = it) },
                                    label = { Text("Abbreviation (e.g. TX400, G200)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.format,
                                        onValueChange = { editingFilm = item.copy(format = it) },
                                        label = { Text("Film Format") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { filmFormatDropdownExpanded = !filmFormatDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = filmFormatDropdownExpanded,
                                        onDismissRequest = { filmFormatDropdownExpanded = false }
                                    ) {
                                        filmFormats.forEach { formatOpt ->
                                            DropdownMenuItem(
                                                text = { Text(formatOpt) },
                                                onClick = {
                                                    editingFilm = item.copy(format = formatOpt)
                                                    filmFormatDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.iso.toString(),
                                    onValueChange = { editingFilm = item.copy(iso = it.toIntOrNull() ?: 400) },
                                    label = { Text("ISO") },
                                    modifier = Modifier.weight(1f)
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.expectedExposures.toString(),
                                        onValueChange = { editingFilm = item.copy(expectedExposures = it.toIntOrNull() ?: 36) },
                                        label = { Text("Expected Exposures (e.g. 12, 24, 36)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { exposuresDropdownExpanded = !exposuresDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = exposuresDropdownExpanded,
                                        onDismissRequest = { exposuresDropdownExpanded = false }
                                    ) {
                                        exposureOptions.forEach { expOpt ->
                                            DropdownMenuItem(
                                                text = { Text("$expOpt Exposures") },
                                                onClick = {
                                                    editingFilm = item.copy(expectedExposures = expOpt.toIntOrNull() ?: 36)
                                                    exposuresDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = item.type,
                                    onValueChange = { editingFilm = item.copy(type = it) },
                                    label = { Text("Film Type") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { filmTypeDropdownExpanded = !filmTypeDropdownExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = filmTypeDropdownExpanded,
                                    onDismissRequest = { filmTypeDropdownExpanded = false }
                                ) {
                                    filmTypes.forEach { typeOpt ->
                                        DropdownMenuItem(
                                            text = { Text(typeOpt) },
                                            onClick = {
                                                editingFilm = item.copy(type = typeOpt)
                                                filmTypeDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        3 -> { // Desktop Kit Builder with Single-Choice Controlled Dropdowns (1-to-1 Mobile Parity)
                            val item = editingKit ?: ReferenceKitItem(name = "", cameraName = cameras.firstOrNull()?.name ?: "")
                            var cameraDropdownExpanded by remember { mutableStateOf(false) }
                            var lensDropdownExpanded by remember { mutableStateOf(false) }
                            var filmDropdownExpanded by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingKit = item.copy(name = it) },
                                label = { Text("Kit Name (e.g. Street Kit, Landscape Setup)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.cameraName.ifBlank { "Select Camera" },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Camera") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { cameraDropdownExpanded = !cameraDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = cameraDropdownExpanded && cameras.isNotEmpty(),
                                        onDismissRequest = { cameraDropdownExpanded = false }
                                    ) {
                                        cameras.forEach { cam ->
                                            DropdownMenuItem(
                                                text = { Text("📷 ${cam.name} (${cam.type})") },
                                                onClick = {
                                                    editingKit = item.copy(cameraName = cam.name)
                                                    cameraDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.lensNames?.ifBlank { "Select Lens" } ?: "Select Lens",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Lens") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { lensDropdownExpanded = !lensDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = lensDropdownExpanded && lenses.isNotEmpty(),
                                        onDismissRequest = { lensDropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("None (Fixed-Lens or Body Only)") },
                                            onClick = {
                                                editingKit = item.copy(lensNames = null)
                                                lensDropdownExpanded = false
                                            }
                                        )
                                        lenses.forEach { l ->
                                            DropdownMenuItem(
                                                text = { Text("📷 ${l.name} (${l.mount ?: "Lens"})") },
                                                onClick = {
                                                    editingKit = item.copy(lensNames = l.name)
                                                    lensDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.defaultFilmName?.ifBlank { "Select Film" } ?: "Select Film",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Film") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { filmDropdownExpanded = !filmDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = filmDropdownExpanded && films.isNotEmpty(),
                                        onDismissRequest = { filmDropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("None") },
                                            onClick = {
                                                editingKit = item.copy(defaultFilmName = null)
                                                filmDropdownExpanded = false
                                            }
                                        )
                                        films.forEach { fl ->
                                            DropdownMenuItem(
                                                text = { Text("🎞️ ${fl.name} (ISO ${fl.iso})") },
                                                onClick = {
                                                    editingKit = item.copy(defaultFilmName = fl.name)
                                                    filmDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.notes ?: "",
                                    onValueChange = { editingKit = item.copy(notes = it) },
                                    label = { Text("Kit Notes") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        4 -> { // Teleconverter & Extension Ring Form with Domain Restrictions
                            val item = editingTeleconverter ?: ReferenceTeleconverterItem(name = "")
                            var converterTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var magDropdownExpanded by remember { mutableStateOf(false) }
                            var lightLossDropdownExpanded by remember { mutableStateOf(false) }
                            var mountDropdownExpanded by remember { mutableStateOf(false) }
                            var selectedAssociatedCameraIds by remember { mutableStateOf(setOf<Int>()) }

                            val converterTypes = listOf("Teleconverter (e.g. 1.4x, 2x)", "Extension Ring / Tube (e.g. 12mm, 25mm)", "Speed Booster / Focal Reducer")
                            val magOptions = listOf("1.4x", "2x", "3x", "12mm", "20mm", "25mm", "36mm")
                            val lightLossOptions = listOf("0 Stops", "0.5 Stops", "1 Stop", "1.5 Stops", "2 Stops")

                            // Section 1: Associated Cameras (Filtered to SLR/Interchangeable Bodies Only!)
                            Text("Associated Cameras (Interchangeable Lenses Only)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            val interchangeableCameras = cameras.filter { it.mount != "Fixed" && it.type != "TLR" && it.type != "Point & Shoot" && it.type != "Compact" }
                            if (interchangeableCameras.isEmpty()) {
                                Text("No interchangeable lens cameras found. Please add an SLR or Rangefinder camera first.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    interchangeableCameras.forEach { camera ->
                                        val isSelected = selectedAssociatedCameraIds.contains(camera.id)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAssociatedCameraIds = if (isSelected) selectedAssociatedCameraIds - camera.id else selectedAssociatedCameraIds + camera.id
                                            },
                                            label = { Text(camera.name, style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) } } else null
                                        )
                                    }
                                }
                            }
                            Text(
                                "Note: Teleconverters and Extension Rings attach behind interchangeable lenses (SLRs / Rangefinders). For TLRs and fixed-lens cameras, front aux lenses should be logged under Filters / Front Attachments.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Section 2: Form Fields Matching Mobile 1-to-1
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = item.converterType,
                                    onValueChange = { editingTeleconverter = item.copy(converterType = it) },
                                    label = { Text("Converter Type") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { converterTypeDropdownExpanded = !converterTypeDropdownExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = converterTypeDropdownExpanded,
                                    onDismissRequest = { converterTypeDropdownExpanded = false }
                                ) {
                                    converterTypes.forEach { cOpt ->
                                        DropdownMenuItem(
                                            text = { Text(cOpt) },
                                            onClick = {
                                                editingTeleconverter = item.copy(converterType = cOpt)
                                                converterTypeDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingTeleconverter = item.copy(name = it) },
                                label = { Text("Converter / Extension Ring Name (e.g. Canon FD 2x-A)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.magnification,
                                        onValueChange = { editingTeleconverter = item.copy(magnification = it) },
                                        label = { Text("Magnification / Extension") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { magDropdownExpanded = !magDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = magDropdownExpanded,
                                        onDismissRequest = { magDropdownExpanded = false }
                                    ) {
                                        magOptions.forEach { mOpt ->
                                            DropdownMenuItem(
                                                text = { Text(mOpt) },
                                                onClick = {
                                                    editingTeleconverter = item.copy(magnification = mOpt)
                                                    magDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.lightLossStops,
                                        onValueChange = { editingTeleconverter = item.copy(lightLossStops = it) },
                                        label = { Text("Light Loss (Stops)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { lightLossDropdownExpanded = !lightLossDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = lightLossDropdownExpanded,
                                        onDismissRequest = { lightLossDropdownExpanded = false }
                                    ) {
                                        lightLossOptions.forEach { lOpt ->
                                            DropdownMenuItem(
                                                text = { Text(lOpt) },
                                                onClick = {
                                                    editingTeleconverter = item.copy(lightLossStops = lOpt)
                                                    lightLossDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        5 -> { // Filter Form with 1-to-1 Mobile Parity
                            val item = editingFilter ?: ReferenceFilterItem(name = "")
                            var selectedAssociatedCameraIds by remember { mutableStateOf(setOf<Int>()) }
                            var threadDropdownExpanded by remember { mutableStateOf(false) }
                            var filterTypeDropdownExpanded by remember { mutableStateOf(false) }

                            val threadSizes = listOf("49mm", "52mm", "55mm", "58mm", "62mm", "67mm", "72mm", "77mm", "82mm", "Bayonet I", "Bayonet II", "Bayonet III")
                            val filterTypes = listOf("ND (Neutral Density)", "Yellow (Y2/8)", "Red (R2/25)", "Green", "Orange", "CPL (Circular Polariser)", "UV / Skylight", "Front Aux Lens (Mutar / Diopter)", "Specialty")

                            // Section 1: Associated Lenses
                            Text("Associated Lenses", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            if (lenses.isEmpty()) {
                                Text("No lenses found in library. Please add a lens first.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    lenses.forEach { lens ->
                                        val isSelected = selectedAssociatedCameraIds.contains(lens.id)
                                        val cleanModel = if (lens.name.startsWith(lens.make ?: "", ignoreCase = true)) {
                                            lens.name.substring(lens.make?.length ?: 0).trim()
                                        } else {
                                            lens.name
                                        }
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAssociatedCameraIds = if (isSelected) selectedAssociatedCameraIds - lens.id else selectedAssociatedCameraIds + lens.id
                                            },
                                            label = { Text("$cleanModel (${lens.filterSize ?: "52mm"})", style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) } } else null
                                        )
                                    }
                                }
                            }
                            Text(
                                "Note: Front auxiliary lenses (e.g. Rolleiflex Mutar, Bayonet close-up diopters, anamorphic adapters) and filters are logged here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Section 2: Form Controls
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingFilter = item.copy(name = it) },
                                label = { Text("Filter Name (e.g. Hoya 52mm ND8)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.threadSize,
                                        onValueChange = { editingFilter = item.copy(threadSize = it) },
                                        label = { Text("Thread Size") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { threadDropdownExpanded = !threadDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = threadDropdownExpanded,
                                        onDismissRequest = { threadDropdownExpanded = false }
                                    ) {
                                        threadSizes.forEach { tOpt ->
                                            DropdownMenuItem(
                                                text = { Text(tOpt) },
                                                onClick = {
                                                    editingFilter = item.copy(threadSize = tOpt)
                                                    threadDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.filterType,
                                        onValueChange = { editingFilter = item.copy(filterType = it) },
                                        label = { Text("Filter Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { filterTypeDropdownExpanded = !filterTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = filterTypeDropdownExpanded,
                                        onDismissRequest = { filterTypeDropdownExpanded = false }
                                    ) {
                                        filterTypes.forEach { ftOpt ->
                                            DropdownMenuItem(
                                                text = { Text(ftOpt) },
                                                onClick = {
                                                    editingFilter = item.copy(filterType = ftOpt)
                                                    filterTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.serialNumber ?: "0.0",
                                    onValueChange = { input ->
                                        if (input.isBlank() || input.all { c -> c.isDigit() || c == '.' }) {
                                            editingFilter = item.copy(serialNumber = input)
                                        }
                                    },
                                    label = { Text("Compensation (Stops, e.g. 3.0)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        6 -> { // Flash Form with 1-to-1 Mobile Parity & Ring Flash Support
                            val item = editingFlash ?: ReferenceFlashItem(name = "")
                            var selectedAssociatedCameraIds by remember { mutableStateOf(setOf<Int>()) }
                            var syncTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var flashTypeDropdownExpanded by remember { mutableStateOf(false) }

                            val flashTypes = listOf("Speedlight / Shoe Mount", "Macro Ring Flash / Ring Lite", "Macro Twin Flash / Commander", "Bracket / Handle Mount")
                            val syncTypes = listOf("Hotshoe", "Coldshoe", "PC Sync Cord", "Bulb Sync", "Wireless Optical / TTL")

                            // Section 1: Associated Cameras (All Cameras including TLRs)
                            Text("Associated Cameras", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            if (cameras.isEmpty()) {
                                Text("No cameras found. Please add a camera first.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    cameras.forEach { camera ->
                                        val isSelected = selectedAssociatedCameraIds.contains(camera.id)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAssociatedCameraIds = if (isSelected) selectedAssociatedCameraIds - camera.id else selectedAssociatedCameraIds + camera.id
                                            },
                                            label = { Text(camera.name, style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) } } else null
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Section 2: Form Controls
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.name.substringBefore(" ").ifBlank { "Nikon" },
                                    onValueChange = { mkg -> editingFlash = item.copy(name = "$mkg ${item.guideNumber}".trim()) },
                                    label = { Text("Brand") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.name,
                                    onValueChange = { editingFlash = item.copy(name = it) },
                                    label = { Text("Model Name (e.g. SB-700, MR-14EX)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.syncType,
                                        onValueChange = { editingFlash = item.copy(syncType = it) },
                                        label = { Text("Sync Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { syncTypeDropdownExpanded = !syncTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = syncTypeDropdownExpanded,
                                        onDismissRequest = { syncTypeDropdownExpanded = false }
                                    ) {
                                        syncTypes.forEach { sOpt ->
                                            DropdownMenuItem(
                                                text = { Text(sOpt) },
                                                onClick = {
                                                    editingFlash = item.copy(syncType = sOpt)
                                                    syncTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.serialNumber ?: "Speedlight / Shoe Mount",
                                        onValueChange = { editingFlash = item.copy(serialNumber = it) },
                                        label = { Text("Flash Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { flashTypeDropdownExpanded = !flashTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = flashTypeDropdownExpanded,
                                        onDismissRequest = { flashTypeDropdownExpanded = false }
                                    ) {
                                        flashTypes.forEach { fOpt ->
                                            DropdownMenuItem(
                                                text = { Text(fOpt) },
                                                onClick = {
                                                    editingFlash = item.copy(serialNumber = fOpt)
                                                    flashTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.serialNumber ?: "",
                                onValueChange = { editingFlash = item.copy(serialNumber = it) },
                                label = { Text("Serial Number (Optional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        7 -> { // Viewfinder Form with 1-to-1 Mobile Parity & Camera Filtering
                            val item = editingViewfinder ?: ReferenceViewfinderItem(name = "")
                            var selectedAssociatedCameraIds by remember { mutableStateOf(setOf<Int>()) }
                            var vfTypeDropdownExpanded by remember { mutableStateOf(false) }

                            val vfTypes = listOf("Eye-Level Prism", "Waist-Level Finder", "Action Finder", "Right-Angle Finder", "Magnifier / Diopter", "Coldshoe Auxiliary Finder")

                            // Section 1: Associated Cameras (Filtered to bodies supporting removable/external finders)
                            Text("Associated Cameras (Interchangeable Viewfinders Only)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            val interchangeableFinderCameras = cameras.filter { camera ->
                                val nameLower = camera.name.lowercase()
                                val isFixedHoodTlr = nameLower.contains("yashica") || nameLower.contains("mat") || nameLower.contains("rolleicord") || nameLower.contains("flexaret")
                                val isRemovableFinderSLR = nameLower.contains("lx") || nameLower.contains("f3") || nameLower.contains("f2") || nameLower.contains("f4") || nameLower.contains("f5") || nameLower.contains("f-1") || nameLower.contains("500c") || nameLower.contains("500cm") || nameLower.contains("exakta") || nameLower.contains("mamiya c")
                                val isColdshoeFinderBody = camera.type == "Rangefinder" || camera.type == "Medium Format SLR" || camera.type == "Large Format" || nameLower.contains("leica")
                                !isFixedHoodTlr && (isRemovableFinderSLR || isColdshoeFinderBody)
                            }
                            if (interchangeableFinderCameras.isEmpty()) {
                                Text("No camera bodies supporting interchangeable viewfinders found.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    interchangeableFinderCameras.forEach { camera ->
                                        val isSelected = selectedAssociatedCameraIds.contains(camera.id)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAssociatedCameraIds = if (isSelected) selectedAssociatedCameraIds - camera.id else selectedAssociatedCameraIds + camera.id
                                            },
                                            label = { Text(camera.name, style = MaterialTheme.typography.labelSmall) },
                                            leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) } } else null
                                        )
                                    }
                                }
                            }
                            Text(
                                "Note: Only camera bodies with removable viewfinders (e.g. Pentax LX, Nikon F3, Hasselblad 500C/M) or coldshoe finders (e.g. Leica M3) are listed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // Section 2: Form Controls
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingViewfinder = item.copy(name = it) },
                                label = { Text("Viewfinder Name (e.g. Pentax FA-1 Finder)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.type,
                                        onValueChange = { editingViewfinder = item.copy(type = it) },
                                        label = { Text("Viewfinder Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { vfTypeDropdownExpanded = !vfTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = vfTypeDropdownExpanded,
                                        onDismissRequest = { vfTypeDropdownExpanded = false }
                                    ) {
                                        vfTypes.forEach { vOpt ->
                                            DropdownMenuItem(
                                                text = { Text(vOpt) },
                                                onClick = {
                                                    editingViewfinder = item.copy(type = vOpt)
                                                    vfTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.compatibleCamera ?: "",
                                    onValueChange = { editingViewfinder = item.copy(compatibleCamera = it) },
                                    label = { Text("Model Number (Optional)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = item.serialNumber ?: "",
                                onValueChange = { editingViewfinder = item.copy(serialNumber = it) },
                                label = { Text("Serial Number (Optional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        8 -> { // Contact Directory Form with Role Tags
                            val item = editingSupplier ?: ReferenceSupplierItem(name = "")
                            var isSupplierActive by remember { mutableStateOf(true) }
                            var selectedRoles by remember { mutableStateOf(setOf("#Supplier")) }
                            val roleTags = listOf("#Supplier", "#Lab", "#RepairTech", "#Borrower", "#Friend")
                            var isSupplierPresetDropdownExpanded by remember { mutableStateOf(false) }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { isSupplierPresetDropdownExpanded = true },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Select Supplier / Vendor from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                                DropdownMenu(
                                    expanded = isSupplierPresetDropdownExpanded && DatabaseRepository.searchPresetSuppliers(item.name).isNotEmpty(),
                                    onDismissRequest = { isSupplierPresetDropdownExpanded = false }
                                ) {
                                    DatabaseRepository.searchPresetSuppliers(item.name).forEach { preset ->
                                        DropdownMenuItem(
                                            text = { Text("${preset.name} (${preset.region ?: "Global"})") },
                                            onClick = {
                                                editingSupplier = item.copy(
                                                    name = preset.name,
                                                    region = preset.region,
                                                    website = preset.website,
                                                    notes = preset.notes
                                                )
                                                selectedRoles = setOf("#Supplier")
                                                isSupplierPresetDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingSupplier = item.copy(name = it) },
                                label = { Text("Contact Name (e.g. Rewind Photo Lab, Dave Smith)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Contact Role Tags
                            Text("Contact Roles", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                roleTags.forEach { role ->
                                    val isSel = selectedRoles.contains(role)
                                    FilterChip(
                                        selected = isSel,
                                        onClick = {
                                            selectedRoles = if (isSel) selectedRoles - role else selectedRoles + role
                                        },
                                        label = { Text(role, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isSupplierActive,
                                    onCheckedChange = { isSupplierActive = it }
                                )
                                Text("In Use (Active Contact - Appears in purchase, loan & repair dropdowns)", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.website ?: "",
                                    onValueChange = { editingSupplier = item.copy(website = it) },
                                    label = { Text("Website") },
                                    modifier = Modifier.weight(2f)
                                )
                                OutlinedTextField(
                                    value = item.region ?: "",
                                    onValueChange = { editingSupplier = item.copy(region = it) },
                                    label = { Text("Region") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.reliabilityRating.toString(),
                                    onValueChange = { editingSupplier = item.copy(reliabilityRating = (it.toIntOrNull() ?: 5).coerceIn(1, 5)) },
                                    label = { Text("Rating (1-5★)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.email ?: "",
                                    onValueChange = { editingSupplier = item.copy(email = it) },
                                    label = { Text("Email Address") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.phone ?: "",
                                    onValueChange = { editingSupplier = item.copy(phone = it) },
                                    label = { Text("Phone Number") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = item.address ?: "",
                                onValueChange = { editingSupplier = item.copy(address = it) },
                                label = { Text("Physical Address / Store Location") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = item.notes ?: "",
                                onValueChange = { editingSupplier = item.copy(notes = it) },
                                label = { Text("Contact Notes") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        9 -> { // Consumable Form with 1-to-1 Mobile Parity
                            val item = editingConsumable ?: ReferenceConsumableItem(name = "")
                            var categoryDropdownExpanded by remember { mutableStateOf(false) }
                            var mfgDropdownExpanded by remember { mutableStateOf(false) }
                            var containerUnitExpanded by remember { mutableStateOf(false) }
                            var innerUnitExpanded by remember { mutableStateOf(false) }
                            var supplierDropdownExpanded by remember { mutableStateOf(false) }
                            var locationDropdownExpanded by remember { mutableStateOf(false) }

                            val categories = listOf("Chemistry", "Paper", "Film Stock", "Safety Gear", "Supplies")
                            val manufacturers = listOf("Ilford", "Kodak", "Adox", "Foma", "CineStill", "Paterson", "Ansell", "Agfa", "Bergger", "Arista")
                            val containerUnits = listOf("L", "mL", "bottle", "box", "pack", "gal")
                            val innerUnits = listOf("mL", "sheets", "rolls", "items")

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { showEquipmentPresetsDialog = true },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                                DropdownMenu(
                                    expanded = isPresetDropdownExpanded && DatabaseRepository.searchPresetConsumables(item.name).isNotEmpty(),
                                    onDismissRequest = { isPresetDropdownExpanded = false }
                                ) {
                                    DatabaseRepository.searchPresetConsumables(item.name).take(5).forEach { preset ->
                                        DropdownMenuItem(
                                            text = { Text("${preset.name} (${preset.manufacturer ?: "Generic"})") },
                                            onClick = {
                                                editingConsumable = preset
                                                isPresetDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.category,
                                        onValueChange = { editingConsumable = item.copy(category = it) },
                                        label = { Text("Category") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { categoryDropdownExpanded = !categoryDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownExpanded,
                                        onDismissRequest = { categoryDropdownExpanded = false }
                                    ) {
                                        categories.forEach { cOpt ->
                                            DropdownMenuItem(
                                                text = { Text(cOpt) },
                                                onClick = {
                                                    editingConsumable = item.copy(category = cOpt)
                                                    categoryDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.manufacturer ?: "",
                                        onValueChange = { editingConsumable = item.copy(manufacturer = it) },
                                        label = { Text("Brand") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { mfgDropdownExpanded = !mfgDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = mfgDropdownExpanded,
                                        onDismissRequest = { mfgDropdownExpanded = false }
                                    ) {
                                        manufacturers.forEach { mOpt ->
                                            DropdownMenuItem(
                                                text = { Text(mOpt) },
                                                onClick = {
                                                    editingConsumable = item.copy(manufacturer = mOpt)
                                                    mfgDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingConsumable = item.copy(name = it) },
                                label = { Text("Consumable Product Name (e.g. Kodak D-76)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Major Container Size
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.containerAmount.toString(),
                                    onValueChange = { editingConsumable = item.copy(containerAmount = it.toDoubleOrNull() ?: 1.0) },
                                    label = { Text("Container Amount (Major)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.containerUnit,
                                        onValueChange = { editingConsumable = item.copy(containerUnit = it) },
                                        label = { Text("Container Unit") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { containerUnitExpanded = !containerUnitExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = containerUnitExpanded,
                                        onDismissRequest = { containerUnitExpanded = false }
                                    ) {
                                        containerUnits.forEach { uOpt ->
                                            DropdownMenuItem(
                                                text = { Text(uOpt) },
                                                onClick = {
                                                    editingConsumable = item.copy(containerUnit = uOpt)
                                                    containerUnitExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Minor Inner Yield
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.innerYieldTotal.toString(),
                                    onValueChange = { editingConsumable = item.copy(innerYieldTotal = it.toIntOrNull() ?: 1000) },
                                    label = { Text("Yield Total (Minor)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.innerUnit,
                                        onValueChange = { editingConsumable = item.copy(innerUnit = it) },
                                        label = { Text("Yield Unit") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { innerUnitExpanded = !innerUnitExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = innerUnitExpanded,
                                        onDismissRequest = { innerUnitExpanded = false }
                                    ) {
                                        innerUnits.forEach { iOpt ->
                                            DropdownMenuItem(
                                                text = { Text(iOpt) },
                                                onClick = {
                                                    editingConsumable = item.copy(innerUnit = iOpt)
                                                    innerUnitExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.supplierName ?: "",
                                        onValueChange = { editingConsumable = item.copy(supplierName = it) },
                                        label = { Text("Supplier / Vendor") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { supplierDropdownExpanded = !supplierDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = supplierDropdownExpanded && suppliers.isNotEmpty(),
                                        onDismissRequest = { supplierDropdownExpanded = false }
                                    ) {
                                        suppliers.forEach { sup ->
                                            DropdownMenuItem(
                                                text = { Text(sup.name) },
                                                onClick = {
                                                    editingConsumable = item.copy(supplierName = sup.name)
                                                    supplierDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.storageLocationName ?: "",
                                        onValueChange = { editingConsumable = item.copy(storageLocationName = it) },
                                        label = { Text("Storage Location") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { locationDropdownExpanded = !locationDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = locationDropdownExpanded && storageLocations.isNotEmpty(),
                                        onDismissRequest = { locationDropdownExpanded = false }
                                    ) {
                                        storageLocations.forEach { loc ->
                                            DropdownMenuItem(
                                                text = { Text("📦 ${loc.name}") },
                                                onClick = {
                                                    editingConsumable = item.copy(storageLocationName = loc.name)
                                                    locationDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.isHazardous,
                                    onCheckedChange = { editingConsumable = item.copy(isHazardous = it) }
                                )
                                Text("Hazardous Material / Safety Handling Required", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        10 -> { // Lab Equipment Form with Clean Layout & Intuitive Parent Linkage
                            val item = editingLabEquipment ?: ReferenceLabEquipmentItem(name = "")
                            var categoryDropdownExpanded by remember { mutableStateOf(false) }
                            var conditionDropdownExpanded by remember { mutableStateOf(false) }
                            var parentEquipDropdownExpanded by remember { mutableStateOf(false) }

                            val categories = listOf("Enlarger", "Enlarger Lens", "Dev Tank", "Tray", "Light Table", "Squeegee & Drying", "Scanner / Digitiser", "General")
                            val conditions = listOf("Excellent", "Good", "Fair", "Maintenance")

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { showEquipmentPresetsDialog = true },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Select from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                                DropdownMenu(
                                    expanded = isPresetDropdownExpanded && DatabaseRepository.searchPresetEquipment(item.name).isNotEmpty(),
                                    onDismissRequest = { isPresetDropdownExpanded = false }
                                ) {
                                    DatabaseRepository.searchPresetEquipment(item.name).take(5).forEach { preset ->
                                        DropdownMenuItem(
                                            text = { Text("${preset.name} (${preset.manufacturer ?: "Generic"})") },
                                            onClick = {
                                                editingLabEquipment = preset
                                                isPresetDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Row 1: Identification
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.name,
                                    onValueChange = { editingLabEquipment = item.copy(name = it) },
                                    label = { Text("Equipment Name") },
                                    placeholder = { Text("e.g. Beseler 23C II") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.manufacturer ?: "",
                                    onValueChange = { editingLabEquipment = item.copy(manufacturer = it) },
                                    label = { Text("Brand") },
                                    placeholder = { Text("e.g. Beseler, Paterson") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 2: Category & Parent Equipment Linkage
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.category,
                                        onValueChange = { editingLabEquipment = item.copy(category = it) },
                                        label = { Text("Category") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { categoryDropdownExpanded = !categoryDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownExpanded,
                                        onDismissRequest = { categoryDropdownExpanded = false }
                                    ) {
                                        categories.forEach { cOpt ->
                                            DropdownMenuItem(
                                                text = { Text(cOpt) },
                                                onClick = {
                                                    editingLabEquipment = item.copy(category = cOpt)
                                                    categoryDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    val isEnlargerLens = item.category == "Enlarger Lens"
                                    val isTopLevelParent = item.category == "Enlarger"
                                    val validParents = labEquipment.filter { parentEq ->
                                        parentEq.id != item.id && (if (isEnlargerLens) parentEq.category == "Enlarger" else parentEq.category == "Enlarger" || parentEq.category == "Scanner / Digitiser")
                                    }

                                    OutlinedTextField(
                                        value = if (isTopLevelParent) "None (Top-Level Parent Unit)" else (item.parentEquipmentName ?: "None (Standalone)"),
                                        onValueChange = { if (!isTopLevelParent) editingLabEquipment = item.copy(parentEquipmentName = it) },
                                        readOnly = isTopLevelParent,
                                        label = { Text("Parent Equipment") },
                                        placeholder = { Text("None (Standalone)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            if (!isTopLevelParent) {
                                                IconButton(onClick = { parentEquipDropdownExpanded = !parentEquipDropdownExpanded }) {
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                                }
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = parentEquipDropdownExpanded && validParents.isNotEmpty() && !isTopLevelParent,
                                        onDismissRequest = { parentEquipDropdownExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("None (Standalone Equipment)") },
                                            onClick = {
                                                editingLabEquipment = item.copy(parentEquipmentName = null)
                                                parentEquipDropdownExpanded = false
                                            }
                                        )
                                        validParents.forEach { parentEq ->
                                            DropdownMenuItem(
                                                text = { Text("⚡ ${parentEq.name} (${parentEq.category})") },
                                                onClick = {
                                                    editingLabEquipment = item.copy(parentEquipmentName = parentEq.name)
                                                    parentEquipDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Row 3: Condition & Model
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.condition,
                                        onValueChange = { editingLabEquipment = item.copy(condition = it) },
                                        label = { Text("Condition") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { conditionDropdownExpanded = !conditionDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = conditionDropdownExpanded,
                                        onDismissRequest = { conditionDropdownExpanded = false }
                                    ) {
                                        conditions.forEach { condOpt ->
                                            DropdownMenuItem(
                                                text = { Text(condOpt) },
                                                onClick = {
                                                    editingLabEquipment = item.copy(condition = condOpt)
                                                    conditionDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.modelNumber ?: "",
                                    onValueChange = { editingLabEquipment = item.copy(modelNumber = it) },
                                    label = { Text("Model Number") },
                                    placeholder = { Text("Optional") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.serialNumber ?: "",
                                    onValueChange = { editingLabEquipment = item.copy(serialNumber = it) },
                                    label = { Text("Serial Number") },
                                    placeholder = { Text("Optional") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        11 -> { // Storage Location Controlled Dropdown LOV
                            val item = editingStorageLocation ?: ReferenceStorageLocationItem(name = "", type = "Cabinet")
                            var storageTypeDropdownExpanded by remember { mutableStateOf(false) }

                            val storageTypes = listOf("Cabinet", "Shelf", "Bag", "Studio", "Paper Safe", "Drawer", "Vault")

                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { editingStorageLocation = item.copy(name = it) },
                                label = { Text("Storage Location Name (e.g. Dry Cabinet A, Camera Bag #1)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.type.ifBlank { "Cabinet" },
                                        onValueChange = { editingStorageLocation = item.copy(type = it) },
                                        label = { Text("Storage Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { storageTypeDropdownExpanded = !storageTypeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = storageTypeDropdownExpanded,
                                        onDismissRequest = { storageTypeDropdownExpanded = false }
                                    ) {
                                        storageTypes.forEach { sType ->
                                            DropdownMenuItem(
                                                text = { Text(sType) },
                                                onClick = {
                                                    editingStorageLocation = item.copy(type = sType)
                                                    storageTypeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = item.notes ?: "",
                                    onValueChange = { editingStorageLocation = item.copy(notes = it) },
                                    label = { Text("Notes / Location Info") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        12, 13 -> { // Custody / Pre-Arrival Order / Loan Ledger with Asset Classification & Catalog Picker
                            val item = editingCustodyInteraction ?: ReferenceCustodyInteractionItem(assetName = "", interactionType = if (selectedCategory == 12) "PURCHASE_ORDER" else "LOAN_OUT")
                            var assetTypeDropdownExpanded by remember { mutableStateOf(false) }
                            var assetPickerDropdownExpanded by remember { mutableStateOf(false) }
                            var typeDropdownExpanded by remember { mutableStateOf(false) }
                            var statusDropdownExpanded by remember { mutableStateOf(false) }
                            var contactDropdownExpanded by remember { mutableStateOf(false) }
                            var locationDropdownExpanded by remember { mutableStateOf(false) }

                            val categoryOptions = listOf(
                                "Camera", "Lens", "Film Stock", "Camera Kit",
                                "Teleconverter", "Filter", "Flash", "Viewfinder",
                                "Consumable", "Lab Equipment", "Other / General"
                            )
                            val interactionTypes = listOf("Purchase Order", "Loan Out", "Rental In", "Service / Repair")
                            val statusTypes = listOf("Waitlisted", "Packaging", "In Transit", "Delivered")

                            if (selectedCategory == 12) {
                                // Pre-Arrival Order Asset Classification
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedTextField(
                                            value = item.assetType.ifBlank { "Camera" },
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Asset Classification Category") },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = {
                                                IconButton(onClick = { assetTypeDropdownExpanded = !assetTypeDropdownExpanded }) {
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                                }
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = assetTypeDropdownExpanded,
                                            onDismissRequest = { assetTypeDropdownExpanded = false }
                                        ) {
                                            categoryOptions.forEach { cat ->
                                                DropdownMenuItem(
                                                    text = { Text(cat) },
                                                    onClick = {
                                                        editingCustodyInteraction = item.copy(assetType = cat)
                                                        assetTypeDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    OutlinedTextField(
                                        value = item.assetName,
                                        onValueChange = { editingCustodyInteraction = item.copy(assetName = it) },
                                        label = { Text("Incoming Asset Name / Model") },
                                        placeholder = { Text("e.g. Voigtländer 35mm f/1.4") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                // Custody Interaction Registered Catalog Asset Picker
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = item.assetName.ifBlank { "Select Registered Asset / Gear" },
                                        onValueChange = { editingCustodyInteraction = item.copy(assetName = it) },
                                        label = { Text("Registered Catalog Asset / Gear Item") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { assetPickerDropdownExpanded = !assetPickerDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = assetPickerDropdownExpanded,
                                        onDismissRequest = { assetPickerDropdownExpanded = false }
                                    ) {
                                        cameras.forEach { cam -> DropdownMenuItem(text = { Text("📷 ${cam.name} (${cam.type})") }, onClick = { editingCustodyInteraction = item.copy(assetName = cam.name, assetType = "Camera"); assetPickerDropdownExpanded = false }) }
                                        lenses.forEach { l -> DropdownMenuItem(text = { Text("📷 ${l.name} (${l.mount ?: "Lens"})") }, onClick = { editingCustodyInteraction = item.copy(assetName = l.name, assetType = "Lens"); assetPickerDropdownExpanded = false }) }
                                        kits.forEach { k -> DropdownMenuItem(text = { Text("🧰 ${k.name}") }, onClick = { editingCustodyInteraction = item.copy(assetName = k.name, assetType = "Camera Kit"); assetPickerDropdownExpanded = false }) }
                                        labEquipment.forEach { eq -> DropdownMenuItem(text = { Text("⚙️ ${eq.name} (${eq.category})") }, onClick = { editingCustodyInteraction = item.copy(assetName = eq.name, assetType = "Lab Equipment"); assetPickerDropdownExpanded = false }) }
                                        consumables.forEach { con -> DropdownMenuItem(text = { Text("🧪 ${con.name} (${con.type})") }, onClick = { editingCustodyInteraction = item.copy(assetName = con.name, assetType = "Consumable"); assetPickerDropdownExpanded = false }) }
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = formatInteractionType(item.interactionType),
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Interaction Type") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { typeDropdownExpanded = !typeDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = typeDropdownExpanded,
                                        onDismissRequest = { typeDropdownExpanded = false }
                                    ) {
                                        interactionTypes.forEach { iType ->
                                            DropdownMenuItem(
                                                text = { Text(iType) },
                                                onClick = {
                                                    editingCustodyInteraction = item.copy(interactionType = parseInteractionTypeToEnum(iType))
                                                    typeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.contactName ?: "",
                                        onValueChange = { editingCustodyInteraction = item.copy(contactName = it) },
                                        label = { Text("Contact / Vendor / Borrower Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { contactDropdownExpanded = !contactDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = contactDropdownExpanded && suppliers.isNotEmpty(),
                                        onDismissRequest = { contactDropdownExpanded = false }
                                    ) {
                                        suppliers.forEach { sup ->
                                            DropdownMenuItem(
                                                text = { Text("${sup.name} (${sup.region ?: "Global"})") },
                                                onClick = {
                                                    editingCustodyInteraction = item.copy(contactName = sup.name)
                                                    contactDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = formatOrderStatus(item.orderStatus),
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Order Lifecycle Status") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { statusDropdownExpanded = !statusDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = statusDropdownExpanded,
                                        onDismissRequest = { statusDropdownExpanded = false }
                                    ) {
                                        statusTypes.forEach { sType ->
                                            DropdownMenuItem(
                                                text = { Text(sType) },
                                                onClick = {
                                                    editingCustodyInteraction = item.copy(orderStatus = parseOrderStatusToEnum(sType))
                                                    statusDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = item.storageLocationName ?: "",
                                        onValueChange = { editingCustodyInteraction = item.copy(storageLocationName = it) },
                                        label = { Text("Storage Location (e.g. Dry Cabinet A)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            IconButton(onClick = { locationDropdownExpanded = !locationDropdownExpanded }) {
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = locationDropdownExpanded && storageLocations.isNotEmpty(),
                                        onDismissRequest = { locationDropdownExpanded = false }
                                    ) {
                                        storageLocations.forEach { loc ->
                                            DropdownMenuItem(
                                                text = { Text("📦 ${loc.name} (${loc.type})") },
                                                onClick = {
                                                    editingCustodyInteraction = item.copy(storageLocationName = loc.name)
                                                    locationDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.trackingNumber ?: "",
                                    onValueChange = { editingCustodyInteraction = item.copy(trackingNumber = it) },
                                    label = { Text("Tracking Number (Optional)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = item.carrier ?: "",
                                    onValueChange = { editingCustodyInteraction = item.copy(carrier = it) },
                                    label = { Text("Carrier (AusPost, DHL, FedEx)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    try {
                        when (selectedCategory) {
                            0 -> editingCamera?.let { cam ->
                                if (cam.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationCamera(cam)
                                    DatabaseRepository.saveLovValue("CAMERA_TYPE", cam.type)
                                    cam.mount?.let { mnt -> DatabaseRepository.saveLovValue("LENS_MOUNT", mnt) }
                                    val isFixedCamera = cam.type == "TLR" || cam.type == "Point & Shoot" || cam.type == "Compact" || cam.mount == "Fixed"
                                    if (isFixedCamera && fixedLensName.isNotBlank()) {
                                        DatabaseRepository.saveFoundationLens(
                                            ReferenceLensItem(
                                                name = fixedLensName,
                                                make = cam.name,
                                                model = fixedLensName,
                                                type = "Prime",
                                                mount = "Fixed",
                                                focalLength = fixedLensFocalLength,
                                                maxAperture = fixedLensMaxAperture,
                                                filterSize = fixedLensFilterSize
                                            )
                                        )
                                    }
                                }
                            }
                            1 -> editingLens?.let { l -> 
                                if (l.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationLens(l)
                                    val savedId = if (l.id > 0) l.id else (DatabaseRepository.loadFoundationLenses().find { it.name == l.name }?.id ?: 0)
                                    if (savedId > 0) {
                                        DatabaseRepository.saveCameraLensAssociations(savedId, selectedAssociatedCameraIds)
                                    }
                                    DatabaseRepository.saveLovValue("LENS_TYPE", l.type)
                                    l.mount?.let { mnt -> DatabaseRepository.saveLovValue("LENS_MOUNT", mnt) }
                                    l.make?.let { mk -> DatabaseRepository.saveLovValue("MANUFACTURER", mk) }
                                }
                            }
                            2 -> editingFilm?.let { fl -> 
                                if (fl.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationFilm(fl)
                                    DatabaseRepository.saveLovValue("FILM_TYPE", fl.type)
                                    DatabaseRepository.saveLovValue("FILM_FORMAT", fl.format)
                                }
                            }
                            3 -> editingKit?.let { if (it.name.isNotBlank()) DatabaseRepository.saveFoundationKit(it) }
                            4 -> editingTeleconverter?.let { tc -> 
                                if (tc.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationTeleconverter(tc)
                                    DatabaseRepository.saveLovValue("CONVERTER_TYPE", tc.converterType)
                                }
                            }
                            5 -> editingFilter?.let { flt -> 
                                if (flt.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationFilter(flt)
                                    DatabaseRepository.saveLovValue("FILTER_TYPE", flt.filterType)
                                    DatabaseRepository.saveLovValue("THREAD_SIZE", flt.threadSize)
                                }
                            }
                            6 -> editingFlash?.let { fls -> 
                                if (fls.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationFlash(fls)
                                    DatabaseRepository.saveLovValue("SYNC_TYPE", fls.syncType)
                                }
                            }
                            7 -> editingViewfinder?.let { vf -> 
                                if (vf.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationViewfinder(vf)
                                    DatabaseRepository.saveLovValue("VIEWFINDER_TYPE", vf.type)
                                }
                            }
                            8 -> editingSupplier?.let { sup -> 
                                if (sup.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationSupplier(sup)
                                    sup.region?.let { rgn -> DatabaseRepository.saveLovValue("REGION", rgn) }
                                }
                            }
                            9 -> editingConsumable?.let { con -> 
                                if (con.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationConsumable(con)
                                    DatabaseRepository.saveLovValue("CONSUMABLE_CATEGORY", con.category)
                                    con.manufacturer?.let { mfg -> DatabaseRepository.saveLovValue("MANUFACTURER", mfg) }
                                }
                            }
                            10 -> editingLabEquipment?.let { eq -> 
                                if (eq.name.isNotBlank()) {
                                    DatabaseRepository.saveFoundationLabEquipment(eq)
                                    DatabaseRepository.saveLovValue("EQUIPMENT_CATEGORY", eq.category)
                                    DatabaseRepository.saveLovValue("CONDITION", eq.condition)
                                    eq.manufacturer?.let { mfg -> DatabaseRepository.saveLovValue("MANUFACTURER", mfg) }
                                }
                            }
                            11 -> editingStorageLocation?.let { loc -> 
                                if (loc.name.isNotBlank()) {
                                    DatabaseRepository.saveStorageLocation(loc)
                                    DatabaseRepository.saveLovValue("STORAGE_TYPE", loc.type)
                                }
                            }
                            12, 13 -> editingCustodyInteraction?.let { cust -> 
                                if (cust.assetName.isNotBlank()) {
                                    DatabaseRepository.saveCustodyInteraction(cust)
                                    DatabaseRepository.saveLovValue("INTERACTION_TYPE", cust.interactionType)
                                    DatabaseRepository.saveLovValue("ORDER_STATUS", cust.orderStatus)
                                }
                            }
                        }
                        refreshAllData()
                        showAddEditDialog = false
                        editingCamera = null
                        editingLens = null
                        editingFilm = null
                        editingKit = null
                        editingTeleconverter = null
                        editingFilter = null
                        editingFlash = null
                        editingViewfinder = null
                        editingSupplier = null
                        editingConsumable = null
                        editingLabEquipment = null
                        editingStorageLocation = null
                        editingCustodyInteraction = null
                    } catch (e: Exception) {
                        println("Save Error: ${e.message}")
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddEditDialog = false
                    editingCamera = null
                    editingLens = null
                    editingFilm = null
                    editingKit = null
                    editingTeleconverter = null
                    editingFilter = null
                    editingFlash = null
                    editingViewfinder = null
                    editingSupplier = null
                    editingConsumable = null
                    editingLabEquipment = null
                    editingStorageLocation = null
                    editingCustodyInteraction = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReviewQueueDialog) {
        AlertDialog(
            onDismissRequest = { showReviewQueueDialog = false },
            title = { Text("📥 Review Queue (${pendingItems.size} Submissions)", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Unrecognised reference items submitted from Fotologue Mobile imports.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider()
                    if (pendingItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Queue is empty. All reference items are fully reconciled!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(pendingItems.size) { idx ->
                                val item = pendingItems[idx]
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("[${item.itemType}] ${item.submittedName}", fontWeight = FontWeight.Bold)
                                            Text("Submitted by Contributor ID: ${item.contributorId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                                Text("Accept", style = MaterialTheme.typography.labelSmall)
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
                                                Text("Reject", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showReviewQueueDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showCameraPresetsDialog) {
        val allBrands = remember { masterBrands }
        
        var selectedBrandFilter by remember { mutableStateOf("All") }
        var brandDropdownExpanded by remember { mutableStateOf(false) }
        var categoryDropdownExpanded by remember { mutableStateOf(false) }

        val availableTypesForBrand = remember(selectedBrandFilter, cameras) {
            val localPresets = cameras.map { c ->
                ReferenceCameraItem(
                    id = c.id,
                    name = c.name,
                    modelNumber = c.modelNumber,
                    type = c.type,
                    mount = c.mount,
                    format = c.format,
                    shutterSpeeds = c.shutterSpeeds
                )
            }
            val unifiedCatalog = (DatabaseRepository.searchPresetCameras("") + localPresets).distinctBy { it.name }
            val filtered = if (selectedBrandFilter == "All") unifiedCatalog else unifiedCatalog.filter { it.name.startsWith(selectedBrandFilter, ignoreCase = true) }
            val types = filtered.map { it.type }.distinct().sorted()
            
            if (types.size == 1) {
                selectedCameraTypeFilter = types.first()
            } else if (!types.contains(selectedCameraTypeFilter)) {
                selectedCameraTypeFilter = "All"
            }
            listOf("All") + types
        }

        AlertDialog(
            modifier = Modifier.width(620.dp),
            onDismissRequest = { showCameraPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Camera Body from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select brand and type filters to search camera body presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // 1. FILTER BY CAMERA BRAND (Top Dropdown)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedBrandFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Camera Brand") },
                            trailingIcon = { IconButton(onClick = { brandDropdownExpanded = !brandDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = brandDropdownExpanded,
                            onDismissRequest = { brandDropdownExpanded = false }
                        ) {
                            allBrands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, fontWeight = if (b == selectedBrandFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedBrandFilter = b
                                        brandDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. FILTER BY CAMERA TYPE (Second Dropdown, Restricted by Brand)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCameraTypeFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Camera Type") },
                            trailingIcon = { IconButton(onClick = { categoryDropdownExpanded = !categoryDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            availableTypesForBrand.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = if (cat == selectedCameraTypeFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedCameraTypeFilter = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cameraSearchQuery,
                        onValueChange = { cameraSearchQuery = it },
                        label = { Text("Search Cameras (Name, Brand, Mount)") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    // Fallback Action Button: Pre-fill selections into Desktop camera form
                    val fallbackButtonLabel = if (selectedBrandFilter != "All") "➕ Add New $selectedBrandFilter Camera" else "➕ Add New Custom Camera"
                    OutlinedButton(
                        onClick = {
                            val currentItem = editingCamera ?: ReferenceCameraItem(name = "")
                            val newBrand = if (selectedBrandFilter != "All") selectedBrandFilter else currentItem.name.substringBefore(" ")
                            val newType = if (selectedCameraTypeFilter != "All") selectedCameraTypeFilter else currentItem.type
                            val isTlrOrFixed = newType == "TLR" || newType == "Point & Shoot" || newType == "Compact"
                            val formatAuto = if (newType == "TLR" || newType == "Medium Format SLR") "120" else currentItem.format
                            val mountAuto = if (isTlrOrFixed) "Fixed" else currentItem.mount

                            editingCamera = currentItem.copy(
                                name = "$newBrand ".trimStart(),
                                type = newType,
                                format = formatAuto,
                                mount = mountAuto
                            )
                            showCameraPresetsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(fallbackButtonLabel, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }

                    HorizontalDivider()

                    val filteredPresets = remember(selectedBrandFilter, selectedCameraTypeFilter, cameraSearchQuery, cameras) {
                        val localPresets = cameras.map { c ->
                            ReferenceCameraItem(
                                id = c.id,
                                name = c.name,
                                modelNumber = c.modelNumber,
                                type = c.type,
                                mount = c.mount,
                                format = c.format,
                                shutterSpeeds = c.shutterSpeeds
                            )
                        }
                        
                        // Merge Developer list with user's local list
                        val unifiedCatalog = (DatabaseRepository.searchPresetCameras("") + localPresets).distinctBy { it.name }
                        
                        unifiedCatalog.filter { preset ->
                            val matchesBrand = selectedBrandFilter == "All" || preset.name.startsWith(selectedBrandFilter, ignoreCase = true)
                            val matchesType = selectedCameraTypeFilter == "All" || preset.type.equals(selectedCameraTypeFilter, ignoreCase = true)
                            val matchesQuery = cameraSearchQuery.isBlank() || preset.name.contains(cameraSearchQuery, ignoreCase = true)
                            matchesBrand && matchesType && matchesQuery
                        }
                    }

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No camera presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val preset = filteredPresets[idx]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val isTlrOrFixed = preset.type == "TLR" || preset.type == "Point & Shoot" || preset.type == "Compact"
                                            val formatAuto = if (preset.type == "TLR" || preset.type == "Medium Format SLR") "120" else preset.format
                                            val mountAuto = if (isTlrOrFixed) "Fixed" else preset.mount
                                            editingCamera = preset.copy(format = formatAuto, mount = mountAuto)
                                            if (isTlrOrFixed || preset.name.contains("Yashica", ignoreCase = true)) {
                                                val masterCam = DatabaseRepository.loadRawMasterPresetItems("CAMERA").firstOrNull { it.name.equals(preset.name, ignoreCase = true) }
                                                val refLensName = masterCam?.attributes?.get("fixedLens")
                                                val masterLens = if (refLensName != null) {
                                                    DatabaseRepository.loadRawMasterPresetItems("LENS").firstOrNull { it.name.equals(refLensName, ignoreCase = true) }
                                                } else null

                                                fixedLensName = masterLens?.name ?: refLensName ?: "${preset.name} Taking Lens"
                                                fixedLensFocalLength = masterLens?.attributes?.get("focalLength") ?: "80mm"
                                                fixedLensMaxAperture = masterLens?.attributes?.get("maxAperture") ?: "f/3.5"
                                                fixedLensFilterSize = masterLens?.attributes?.get("filterSize") ?: "Bay I"
                                            }
                                            showCameraPresetsDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(preset.name, fontWeight = FontWeight.Bold)
                                            Text("${preset.type} • Mount: ${preset.mount ?: "Standard"} • Format: ${preset.format} ${if (!preset.shutterSpeeds.isNullOrBlank()) "• ${preset.shutterSpeeds}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = {
                                                val isTlrOrFixed = preset.type == "TLR" || preset.type == "Point & Shoot" || preset.type == "Compact"
                                                val formatAuto = if (preset.type == "TLR" || preset.type == "Medium Format SLR") "120" else preset.format
                                                val mountAuto = if (isTlrOrFixed) "Fixed" else preset.mount
                                                editingCamera = preset.copy(format = formatAuto, mount = mountAuto)
                                                if (isTlrOrFixed || preset.name.contains("Yashica", ignoreCase = true)) {
                                                    val masterCam = DatabaseRepository.loadRawMasterPresetItems("CAMERA").firstOrNull { it.name.equals(preset.name, ignoreCase = true) }
                                                    val refLensName = masterCam?.attributes?.get("fixedLens")
                                                    val masterLens = if (refLensName != null) {
                                                        DatabaseRepository.loadRawMasterPresetItems("LENS").firstOrNull { it.name.equals(refLensName, ignoreCase = true) }
                                                    } else null

                                                    fixedLensName = masterLens?.name ?: refLensName ?: "${preset.name} Taking Lens"
                                                    fixedLensFocalLength = masterLens?.attributes?.get("focalLength") ?: "80mm"
                                                    fixedLensMaxAperture = masterLens?.attributes?.get("maxAperture") ?: "f/3.5"
                                                    fixedLensFilterSize = masterLens?.attributes?.get("filterSize") ?: "Bay I"
                                                }
                                                showCameraPresetsDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Select", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showCameraPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showLensPresetsDialog) {
        val allBrands = remember { masterBrands }
        
        var selectedLensBrandFilter by remember { mutableStateOf("All") }
        var brandDropdownExpanded by remember { mutableStateOf(false) }
        var mountDropdownExpanded by remember { mutableStateOf(false) }

        val availableMountsForBrand = remember(selectedLensBrandFilter, lenses) {
            val localPresets = lenses.map { l ->
                ReferenceLensItem(
                    id = l.id,
                    name = l.name,
                    make = l.make,
                    model = l.model,
                    type = l.type,
                    mount = l.mount,
                    focalLength = l.focalLength,
                    maxAperture = l.maxAperture,
                    minAperture = l.minAperture,
                    filterSize = l.filterSize
                )
            }
            val unifiedCatalog = (DatabaseRepository.searchPresetLenses("") + localPresets).distinctBy { it.name }
            val filtered = if (selectedLensBrandFilter == "All") unifiedCatalog else unifiedCatalog.filter { (it.make?.contains(selectedLensBrandFilter, ignoreCase = true) == true) || it.name.contains(selectedLensBrandFilter, ignoreCase = true) }
            val mounts = filtered.mapNotNull { it.mount }.distinct().sorted()
            
            if (mounts.size == 1) {
                selectedLensMountFilter = mounts.first()
            } else if (!mounts.contains(selectedLensMountFilter)) {
                selectedLensMountFilter = "All"
            }
            listOf("All") + mounts
        }

        AlertDialog(
            modifier = Modifier.width(620.dp),
            onDismissRequest = { showLensPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Lens from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select brand and mount filters to search lens presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // 1. FILTER BY LENS BRAND
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedLensBrandFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Brand") },
                            trailingIcon = { IconButton(onClick = { brandDropdownExpanded = !brandDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = brandDropdownExpanded,
                            onDismissRequest = { brandDropdownExpanded = false }
                        ) {
                            allBrands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, fontWeight = if (b == selectedLensBrandFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedLensBrandFilter = b
                                        brandDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. FILTER BY LENS MOUNT
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedLensMountFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Lens Mount") },
                            trailingIcon = { IconButton(onClick = { mountDropdownExpanded = !mountDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = mountDropdownExpanded,
                            onDismissRequest = { mountDropdownExpanded = false }
                        ) {
                            availableMountsForBrand.forEach { mnt ->
                                DropdownMenuItem(
                                    text = { Text(mnt, fontWeight = if (mnt == selectedLensMountFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedLensMountFilter = mnt
                                        mountDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = lensSearchQueryText,
                        onValueChange = { lensSearchQueryText = it },
                        label = { Text("Search Lenses (Name, Make, Mount)") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    // Fallback Action Button
                    val fallbackButtonLabel = if (selectedLensBrandFilter != "All") "➕ Add New $selectedLensBrandFilter Lens" else "➕ Add New Custom Lens"
                    OutlinedButton(
                        onClick = {
                            val currentItem = editingLens ?: ReferenceLensItem(name = "")
                            val newBrand = if (selectedLensBrandFilter != "All") selectedLensBrandFilter else currentItem.make
                            val newMount = if (selectedLensMountFilter != "All") selectedLensMountFilter else currentItem.mount

                            editingLens = currentItem.copy(
                                make = newBrand,
                                mount = newMount,
                                name = "$newBrand ".trimStart()
                            )
                            showLensPresetsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(fallbackButtonLabel, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }

                    HorizontalDivider()

                    val filteredPresets = remember(selectedLensBrandFilter, selectedLensMountFilter, lensSearchQueryText, lenses) {
                        val localPresets = lenses.map { l ->
                            ReferenceLensItem(
                                id = l.id,
                                name = l.name,
                                make = l.make,
                                model = l.model,
                                type = l.type,
                                mount = l.mount,
                                focalLength = l.focalLength,
                                maxAperture = l.maxAperture,
                                minAperture = l.minAperture,
                                filterSize = l.filterSize
                            )
                        }
                        
                        val unifiedCatalog = (DatabaseRepository.searchPresetLenses("") + localPresets).distinctBy { it.name }
                        
                        unifiedCatalog.filter { preset ->
                            val matchesBrand = selectedLensBrandFilter == "All" || (preset.make?.contains(selectedLensBrandFilter, ignoreCase = true) == true) || preset.name.contains(selectedLensBrandFilter, ignoreCase = true)
                            val matchesMount = selectedLensMountFilter == "All" || preset.mount.equals(selectedLensMountFilter, ignoreCase = true)
                            val matchesQuery = lensSearchQueryText.isBlank() || preset.name.contains(lensSearchQueryText, ignoreCase = true)
                            matchesBrand && matchesMount && matchesQuery
                        }
                    }

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No lens presets found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val preset = filteredPresets[idx]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val adapterAuto = if (selectedLensMountFilter != "All" && preset.mount != selectedLensMountFilter && !preset.mount.isNullOrBlank()) {
                                                "${preset.mount} to $selectedLensMountFilter Adapter"
                                            } else preset.adapterUsed
                                            editingLens = preset.copy(adapterUsed = adapterAuto)
                                            showLensPresetsDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(preset.name, fontWeight = FontWeight.Bold)
                                            Text("${preset.type} • Mount: ${preset.mount ?: "Standard"} • FL: ${preset.focalLength ?: "50mm"} ${preset.maxAperture ?: "f/1.4"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = {
                                                val adapterAuto = if (selectedLensMountFilter != "All" && preset.mount != selectedLensMountFilter && !preset.mount.isNullOrBlank()) {
                                                    "${preset.mount} to $selectedLensMountFilter Adapter"
                                                } else preset.adapterUsed
                                                editingLens = preset.copy(adapterUsed = adapterAuto)
                                                showLensPresetsDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Select", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showLensPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showFilmPresetsDialog) {
        val masterFilmBrands = listOf("Adox", "AgfaPhoto", "Bergger", "Cinestill", "Ferrania", "Foma", "Fujifilm", "Harman", "Ilford", "Kodak", "Lomography", "Orwo", "Polaroid", "Rollei", "Shanghai", "Ultrafine", "Yashica")
        var mfgDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            modifier = Modifier.width(620.dp),
            onDismissRequest = { showFilmPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Film Stock from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select a brand to view curated film presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // Film Brand Category Filter Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedFilmManufacturer,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Film Brand") },
                            trailingIcon = { IconButton(onClick = { mfgDropdownExpanded = !mfgDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = mfgDropdownExpanded,
                            onDismissRequest = { mfgDropdownExpanded = false }
                        ) {
                            masterFilmBrands.forEach { mfg ->
                                DropdownMenuItem(
                                    text = { Text(mfg, fontWeight = if (mfg == selectedFilmManufacturer) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedFilmManufacturer = mfg
                                        mfgDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Presets List for Selected Manufacturer
                    val filteredPresets = DatabaseRepository.searchPresetFilms(selectedFilmManufacturer)
                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No presets found for $selectedFilmManufacturer.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val preset = filteredPresets[idx]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            editingFilm = preset
                                            showFilmPresetsDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(preset.name, fontWeight = FontWeight.Bold)
                                            Text("ISO ${preset.iso} • ${preset.type} • Abbr: ${preset.abbreviation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = {
                                                editingFilm = preset
                                                showFilmPresetsDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Select", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFilmPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showEquipmentPresetsDialog) {
        val categories = listOf("All", "Enlarger", "Enlarger Lens", "Dev Tank", "Tray", "Light Table", "Squeegee & Drying", "Scanner / Digitiser")
        var equipCategoryDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            modifier = Modifier.width(620.dp),
            onDismissRequest = { showEquipmentPresetsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Lab Equipment from Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().height(440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select a category filter or type to search lab equipment presets:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // Equipment Category Filter Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedEquipmentCategoryFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Equipment Category") },
                            trailingIcon = { IconButton(onClick = { equipCategoryDropdownExpanded = !equipCategoryDropdownExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = equipCategoryDropdownExpanded,
                            onDismissRequest = { equipCategoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = if (cat == selectedEquipmentCategoryFilter) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedEquipmentCategoryFilter = cat
                                        equipCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = equipmentSearchQuery,
                        onValueChange = { equipmentSearchQuery = it },
                        label = { Text("Search Equipment (Name, Brand)") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    HorizontalDivider()

                    val allPresets = DatabaseRepository.searchPresetEquipment(equipmentSearchQuery)
                    val filteredPresets = if (selectedEquipmentCategoryFilter == "All") {
                        allPresets
                    } else {
                        allPresets.filter { it.category.equals(selectedEquipmentCategoryFilter, ignoreCase = true) }
                    }

                    if (filteredPresets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No equipment presets found for $selectedEquipmentCategoryFilter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(filteredPresets.size) { idx ->
                                val preset = filteredPresets[idx]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            editingLabEquipment = preset
                                            showEquipmentPresetsDialog = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(preset.name, fontWeight = FontWeight.Bold)
                                            Text("Brand: ${preset.manufacturer ?: "Generic"} • Category: ${preset.category} ${if (!preset.notes.isNullOrBlank()) "• ${preset.notes}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Button(
                                            onClick = {
                                                editingLabEquipment = preset
                                                showEquipmentPresetsDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Select", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEquipmentPresetsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Full-Width Stage 4 Reference Data Administration Manager
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var desktopPresetRefreshStatus by remember { mutableStateOf<String?>(null) }
            val adminScope = rememberCoroutineScope()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = {
                                desktopPresetRefreshStatus = "Connecting..."
                                adminScope.launch {
                                    val ok = DesktopMasterPresetEngine.refreshFromCloud()
                                    desktopPresetRefreshStatus = if (ok) "✅ Updated!" else "⚠️ URL Unreachable (Using Local)"
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh Cloud Presets", style = MaterialTheme.typography.bodySmall)
                        }
                        desktopPresetRefreshStatus?.let { status ->
                            Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                    // Actionable Review Queue Header Alert Button
                    Button(
                        onClick = { showReviewQueueDialog = true },
                        colors = if (pendingItems.isNotEmpty()) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary) else ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (pendingItems.isNotEmpty()) "Review Queue (${pendingItems.size} Pending)" else "Review Queue (0)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            when (selectedCategory) {
                                0 -> editingCamera = ReferenceCameraItem(name = "")
                                1 -> editingLens = ReferenceLensItem(name = "")
                                2 -> editingFilm = ReferenceFilmItem(name = "")
                                3 -> editingKit = ReferenceKitItem(name = "", cameraName = cameras.firstOrNull()?.name ?: "Canon AE-1 Program")
                                4 -> editingTeleconverter = ReferenceTeleconverterItem(name = "")
                                5 -> editingFilter = ReferenceFilterItem(name = "")
                                6 -> editingFlash = ReferenceFlashItem(name = "")
                                7 -> editingViewfinder = ReferenceViewfinderItem(name = "")
                                8 -> editingSupplier = ReferenceSupplierItem(name = "")
                                9 -> editingConsumable = ReferenceConsumableItem(name = "")
                                10 -> editingLabEquipment = ReferenceLabEquipmentItem(name = "")
                                11 -> editingStorageLocation = ReferenceStorageLocationItem(name = "")
                                12, 13 -> editingCustodyInteraction = ReferenceCustodyInteractionItem(assetName = "", interactionType = if (selectedCategory == 12) "PURCHASE_ORDER" else "LOAN_OUT")
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
            }

                // Contributor Profile Bar (User Configuration Selection)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active Contributor Profile:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Box {
                            FilterChip(
                                selected = true,
                                onClick = { isContributorDropdownExpanded = true },
                                label = { Text("${selectedContributor?.fullName ?: "Gerard Bendotti"} (ID #${selectedContributor?.id ?: 1})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                            )
                            DropdownMenu(
                                expanded = isContributorDropdownExpanded,
                                onDismissRequest = { isContributorDropdownExpanded = false }
                            ) {
                                contributors.forEach { contrib ->
                                    DropdownMenuItem(
                                        text = { Text("${contrib.fullName} (ID #${contrib.id} • ${contrib.alias})") },
                                        onClick = {
                                            selectedContributor = contrib
                                            isContributorDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Vertical Category Split Pane
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // LEFT VERTICAL CATEGORY RAIL (210.dp Width to prevent text overflow)
                    FoundationAdminLeftRail(
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        kitsCount = kits.size,
                        camerasCount = cameras.size,
                        lensesCount = lenses.size,
                        filmsCount = films.size,
                        teleconvertersCount = teleconverters.size,
                        filtersCount = filters.size,
                        flashesCount = flashes.size,
                        viewfindersCount = viewfinders.size,
                        suppliersCount = suppliers.size,
                        consumablesCount = consumables.size,
                        labEquipmentCount = labEquipment.size,
                        storageLocationsCount = storageLocations.size,
                        pendingPreArrivalOrdersCount = custodyInteractions.count { it.interactionType == "PURCHASE_ORDER" && !it.isReturned },
                        custodyInteractionsCount = custodyInteractions.count { it.interactionType != "PURCHASE_ORDER" }
                    )

                    // RIGHT MAIN CONTENT: DATA GRID FOR SELECTED CATEGORY
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        AdminDataGridContent(
                            selectedCategory = selectedCategory,
                            cameras = cameras, lenses = lenses, films = films, kits = kits,
                            teleconverters = teleconverters, filters = filters, flashes = flashes, viewfinders = viewfinders,
                            suppliers = suppliers, consumables = consumables, labEquipment = labEquipment,
                            storageLocations = storageLocations, custodyInteractions = custodyInteractions,
                            onEditCamera = { editingCamera = it; showAddEditDialog = true },
                            onDeleteCamera = { DatabaseRepository.deleteFoundationCamera(it); refreshAllData() },
                            onEditLens = { editingLens = it; selectedAssociatedCameraIds = DatabaseRepository.loadCameraIdsForLens(it.id); showAddEditDialog = true },
                            onDeleteLens = { DatabaseRepository.deleteFoundationLens(it); refreshAllData() },
                            onEditFilm = { editingFilm = it; showAddEditDialog = true },
                            onDeleteFilm = { DatabaseRepository.deleteFoundationFilm(it); refreshAllData() },
                            onEditKit = { editingKit = it; showAddEditDialog = true },
                            onDeleteKit = { DatabaseRepository.deleteFoundationKit(it); refreshAllData() },
                            onEditTeleconverter = { editingTeleconverter = it; showAddEditDialog = true },
                            onDeleteTeleconverter = { DatabaseRepository.deleteFoundationTeleconverter(it); refreshAllData() },
                            onEditFilter = { editingFilter = it; showAddEditDialog = true },
                            onDeleteFilter = { DatabaseRepository.deleteFoundationFilter(it); refreshAllData() },
                            onEditFlash = { editingFlash = it; showAddEditDialog = true },
                            onDeleteFlash = { DatabaseRepository.deleteFoundationFlash(it); refreshAllData() },
                            onEditViewfinder = { editingViewfinder = it; showAddEditDialog = true },
                            onDeleteViewfinder = { DatabaseRepository.deleteFoundationViewfinder(it); refreshAllData() },
                            onEditSupplier = { editingSupplier = it; showAddEditDialog = true },
                            onDeleteSupplier = { DatabaseRepository.deleteFoundationSupplier(it); refreshAllData() },
                            onEditConsumable = { editingConsumable = it; showAddEditDialog = true },
                            onDeleteConsumable = { DatabaseRepository.deleteFoundationConsumable(it); refreshAllData() },
                            onEditLabEquipment = { editingLabEquipment = it; showAddEditDialog = true },
                            onDeleteLabEquipment = { DatabaseRepository.deleteFoundationLabEquipment(it); refreshAllData() },
                            onEditStorageLocation = { editingStorageLocation = it; showAddEditDialog = true },
                            onDeleteStorageLocation = { DatabaseRepository.deleteStorageLocation(it); refreshAllData() },
                            onEditCustodyInteraction = { editingCustodyInteraction = it; showAddEditDialog = true },
                            onDeleteCustodyInteraction = { DatabaseRepository.deleteCustodyInteraction(it); refreshAllData() },
                            onMarkDelivered = { ord ->
                                desktopSelectedDeliveryLocation = storageLocations.firstOrNull()?.name ?: ""
                                desktopDeliveryDialogOrder = ord
                            }
                        )
                    }
                }

                HorizontalDivider()

                // Action Buttons Bar (Sync to Mobile & Import from Mobile)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPublishManifest,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync to Mobile", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onImportManifest,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import from Mobile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
}

@Composable
private fun FoundationAdminLeftRail(
    selectedCategory: Int,
    onSelectCategory: (Int) -> Unit,
    kitsCount: Int,
    camerasCount: Int,
    lensesCount: Int,
    filmsCount: Int,
    teleconvertersCount: Int,
    filtersCount: Int,
    flashesCount: Int,
    viewfindersCount: Int,
    suppliersCount: Int,
    consumablesCount: Int,
    labEquipmentCount: Int,
    storageLocationsCount: Int,
    pendingPreArrivalOrdersCount: Int,
    custodyInteractionsCount: Int
) {
    var isGearExpanded by remember { mutableStateOf(true) }
    var isContactsExpanded by remember { mutableStateOf(true) }
    var isLabExpanded by remember { mutableStateOf(true) }
    var isTransactionsExpanded by remember { mutableStateOf(true) }

    val railScrollState = rememberScrollState()

    Box(modifier = Modifier.width(210.dp).fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(6.dp)
                .verticalScroll(railScrollState),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header 1: CAMERA GEAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isGearExpanded = !isGearExpanded }
                    .padding(start = 6.dp, top = 6.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CAMERA GEAR",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    if (isGearExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (isGearExpanded) {
                val gearCategories = listOf(
                    Triple("Cameras ($camerasCount)", 0, Icons.Default.PhotoCamera),
                    Triple("Lenses ($lensesCount)", 1, Icons.Default.Camera),
                    Triple("Films ($filmsCount)", 2, Icons.Default.CameraRoll),
                    Triple("Teleconverters ($teleconvertersCount)", 4, Icons.Default.CenterFocusStrong),
                    Triple("Filters ($filtersCount)", 5, Icons.Default.Filter),
                    Triple("Flashes ($flashesCount)", 6, Icons.Default.FlashOn),
                    Triple("Viewfinders ($viewfindersCount)", 7, Icons.Default.Visibility),
                    Triple("Kit Bags ($kitsCount)", 3, Icons.Default.Inventory2)
                )
                gearCategories.forEach { (catTitle, catIdx, catIcon) ->
                    val isSel = selectedCategory == catIdx
                    NavigationDrawerItem(
                        label = { Text(catTitle, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        selected = isSel,
                        onClick = { onSelectCategory(catIdx) },
                        icon = { Icon(catIcon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.height(36.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Header 2: CONTACTS DIRECTORY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isContactsExpanded = !isContactsExpanded }
                    .padding(start = 6.dp, top = 6.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CONTACTS DIRECTORY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    if (isContactsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isContactsExpanded) {
                val contactsCategories = listOf(
                    Triple("Contacts ($suppliersCount)", 8, Icons.Default.AccountBox)
                )
                contactsCategories.forEach { (catTitle, catIdx, catIcon) ->
                    val isSel = selectedCategory == catIdx
                    NavigationDrawerItem(
                        label = { Text(catTitle, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        selected = isSel,
                        onClick = { onSelectCategory(catIdx) },
                        icon = { Icon(catIcon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.height(36.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Header 3: LAB & INVENTORY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isLabExpanded = !isLabExpanded }
                    .padding(start = 6.dp, top = 6.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LAB & INVENTORY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    if (isLabExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isLabExpanded) {
                val labCategories = listOf(
                    Triple("Consumables ($consumablesCount)", 9, Icons.Default.Science),
                    Triple("Equipment ($labEquipmentCount)", 10, Icons.Default.Build),
                    Triple("Storage Locations ($storageLocationsCount)", 11, Icons.Default.Home)
                )
                labCategories.forEach { (catTitle, catIdx, catIcon) ->
                    val isSel = selectedCategory == catIdx
                    NavigationDrawerItem(
                        label = { Text(catTitle, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        selected = isSel,
                        onClick = { onSelectCategory(catIdx) },
                        icon = { Icon(catIcon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.height(36.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Header 4: CUSTODY & TRANSACTIONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTransactionsExpanded = !isTransactionsExpanded }
                    .padding(start = 6.dp, top = 6.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CUSTODY & TRANSACTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    if (isTransactionsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isTransactionsExpanded) {
                val transactionCategories = listOf(
                    Triple("Pre-Arrival Orders ($pendingPreArrivalOrdersCount)", 12, Icons.Default.Info),
                    Triple("Custody Ledger ($custodyInteractionsCount)", 13, Icons.AutoMirrored.Filled.List)
                )
                transactionCategories.forEach { (catTitle, catIdx, catIcon) ->
                    val isSel = selectedCategory == catIdx
                    NavigationDrawerItem(
                        label = { Text(catTitle, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        selected = isSel,
                        onClick = { onSelectCategory(catIdx) },
                        icon = { Icon(catIcon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.height(36.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Header 5: TAXONOMY & TAGS
            var isTaxonomyExpanded by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTaxonomyExpanded = !isTaxonomyExpanded }
                    .padding(start = 6.dp, top = 6.dp, bottom = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TAXONOMY & TAGS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    if (isTaxonomyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isTaxonomyExpanded) {
                val isSel = selectedCategory == 14
                NavigationDrawerItem(
                    label = { Text("Taxonomy & Tags", style = MaterialTheme.typography.labelMedium, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    selected = isSel,
                    onClick = { onSelectCategory(14) },
                    icon = { Icon(Icons.Default.Filter, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.height(36.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(railScrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDataGridContent(
    selectedCategory: Int,
    cameras: List<ReferenceCameraItem>,
    lenses: List<ReferenceLensItem>,
    films: List<ReferenceFilmItem>,
    kits: List<ReferenceKitItem>,
    teleconverters: List<ReferenceTeleconverterItem>,
    filters: List<ReferenceFilterItem>,
    flashes: List<ReferenceFlashItem>,
    viewfinders: List<ReferenceViewfinderItem>,
    suppliers: List<ReferenceSupplierItem>,
    consumables: List<ReferenceConsumableItem>,
    labEquipment: List<ReferenceLabEquipmentItem>,
    storageLocations: List<ReferenceStorageLocationItem>,
    custodyInteractions: List<ReferenceCustodyInteractionItem>,
    onEditCamera: (ReferenceCameraItem) -> Unit,
    onDeleteCamera: (Int) -> Unit,
    onEditLens: (ReferenceLensItem) -> Unit,
    onDeleteLens: (Int) -> Unit,
    onEditFilm: (ReferenceFilmItem) -> Unit,
    onDeleteFilm: (Int) -> Unit,
    onEditKit: (ReferenceKitItem) -> Unit,
    onDeleteKit: (Int) -> Unit,
    onEditTeleconverter: (ReferenceTeleconverterItem) -> Unit,
    onDeleteTeleconverter: (Int) -> Unit,
    onEditFilter: (ReferenceFilterItem) -> Unit,
    onDeleteFilter: (Int) -> Unit,
    onEditFlash: (ReferenceFlashItem) -> Unit,
    onDeleteFlash: (Int) -> Unit,
    onEditViewfinder: (ReferenceViewfinderItem) -> Unit,
    onDeleteViewfinder: (Int) -> Unit,
    onEditSupplier: (ReferenceSupplierItem) -> Unit,
    onDeleteSupplier: (Int) -> Unit,
    onEditConsumable: (ReferenceConsumableItem) -> Unit,
    onDeleteConsumable: (Int) -> Unit,
    onEditLabEquipment: (ReferenceLabEquipmentItem) -> Unit,
    onDeleteLabEquipment: (Int) -> Unit,
    onEditStorageLocation: (ReferenceStorageLocationItem) -> Unit,
    onDeleteStorageLocation: (Int) -> Unit,
    onEditCustodyInteraction: (ReferenceCustodyInteractionItem) -> Unit,
    onDeleteCustodyInteraction: (Int) -> Unit,
    onMarkDelivered: (ReferenceCustodyInteractionItem) -> Unit
) {
    val gridListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedCategory) {
        0 -> { // Cameras
            if (cameras.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reference cameras logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(state = gridListState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(items = cameras, key = { it.id }) { cam ->
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
                                    Text("${cam.type} • Format: ${cam.format} • Mount: ${cam.mount ?: "Standard"}${if (!cam.modelNumber.isNullOrBlank()) " • Model: ${cam.modelNumber}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditCamera(cam) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteCamera(cam.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        1 -> { // Lenses
            if (lenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reference lenses logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(state = gridListState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(items = lenses, key = { it.id }) { l ->
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
                                    val isBadge = if (l.isImageStabilised) " • 🟢 IS" else ""
                                    val filterBadge = if (!l.filterSize.isNullOrBlank()) " • Filter: ${l.filterSize}" else ""
                                    Text(if (l.name.isNotBlank()) l.name else "${l.make ?: ""} ${l.model ?: "Lens"}".trim(), fontWeight = FontWeight.Bold)
                                    Text("${l.type} • ${l.focalLength ?: "50mm"} ${l.maxAperture ?: "f/1.4"}/${l.minAperture ?: "f/22"} • Mount: ${l.mount ?: "Standard"}$filterBadge$isBadge", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditLens(l) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteLens(l.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        2 -> { // Films
            if (films.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No reference film stocks logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(films.size) { idx ->
                        val fl = films[idx]
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
                                    Text(fl.name, fontWeight = FontWeight.Bold)
                                    Text("${fl.type} • Format: ${fl.format} • ISO: ${fl.iso} • Exp: ${fl.expectedExposures} [${fl.abbreviation}]", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditFilm(fl) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteFilm(fl.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        3 -> { // Kits
            if (kits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No camera kits logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(kits.size) { idx ->
                        val kt = kits[idx]
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
                                    Text(kt.name, fontWeight = FontWeight.Bold)
                                    Text("Camera: ${kt.cameraName} • Lenses: ${kt.lensNames ?: "None"} • Default Film: ${kt.defaultFilmName ?: "None"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditKit(kt) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteKit(kt.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        4 -> { // Teleconverters
            if (teleconverters.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No teleconverters or extension rings logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(teleconverters.size) { idx ->
                        val tc = teleconverters[idx]
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
                                    Text(tc.name, fontWeight = FontWeight.Bold)
                                    Text("Type: ${tc.converterType} • Mag: ${tc.magnification} • Light Loss: ${tc.lightLossStops} • Mount: ${tc.mount ?: "Standard"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditTeleconverter(tc) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteTeleconverter(tc.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        5 -> { // Filters
            if (filters.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No filters or front auxiliary attachments logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filters.size) { idx ->
                        val flt = filters[idx]
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
                                    Text(flt.name, fontWeight = FontWeight.Bold)
                                    Text("Thread: ${flt.threadSize} • Type: ${flt.filterType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditFilter(flt) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteFilter(flt.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        6 -> { // Flashes
            if (flashes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No flashes or ring lites logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(flashes.size) { idx ->
                        val fls = flashes[idx]
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
                                    Text(fls.name, fontWeight = FontWeight.Bold)
                                    Text("Guide No: ${fls.guideNumber} • Sync: ${fls.syncType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditFlash(fls) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteFlash(fls.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        7 -> { // Viewfinders
            if (viewfinders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No viewfinders or waist-level finders logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(viewfinders.size) { idx ->
                        val vf = viewfinders[idx]
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
                                    Text(vf.name, fontWeight = FontWeight.Bold)
                                    Text("Type: ${vf.type} ${if (!vf.compatibleCamera.isNullOrBlank()) "• Fits: ${vf.compatibleCamera}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditViewfinder(vf) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteViewfinder(vf.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        8 -> { // Suppliers
            if (suppliers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No suppliers logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    val stars = "★".repeat(sup.reliabilityRating.coerceIn(1, 5))
                                    Text(sup.name, fontWeight = FontWeight.Bold)
                                    Text("${sup.region ?: "Global"} • Rating: $stars • ${sup.website ?: "No Website"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditSupplier(sup) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteSupplier(sup.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        9 -> { // Consumables
            if (consumables.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No consumables logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(consumables.size) { idx ->
                        val con = consumables[idx]
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
                                    val hazBadge = if (con.isHazardous) " • ⚠️ Hazardous" else ""
                                    Text(con.name, fontWeight = FontWeight.Bold)
                                    Text("${con.manufacturer ?: "Generic"} • ${con.category} (${con.type ?: "General"}) • Dilution: ${con.dilution ?: "Stock"} • Unit: ${con.defaultUnit}$hazBadge", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditConsumable(con) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteConsumable(con.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        10 -> { // Lab Equipment
            if (labEquipment.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No lab equipment logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(labEquipment.size) { idx ->
                        val eq = labEquipment[idx]
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
                                    Text(eq.name, fontWeight = FontWeight.Bold)
                                    Text("${eq.manufacturer ?: "Generic"} ${eq.modelNumber ?: ""} • Category: ${eq.category} • Condition: ${eq.condition}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditLabEquipment(eq) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteLabEquipment(eq.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        11 -> { // Storage Locations
            if (storageLocations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No storage locations created yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(storageLocations.size) { idx ->
                        val loc = storageLocations[idx]
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
                                    Text("📦 ${loc.name}", fontWeight = FontWeight.Bold)
                                    Text("Type: ${loc.type} ${if (!loc.notes.isNullOrBlank()) "• Notes: ${loc.notes}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditStorageLocation(loc) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteStorageLocation(loc.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        12 -> { // Pre-Arrival Orders
            val orders = custodyInteractions.filter { it.interactionType == "PURCHASE_ORDER" }
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pre-arrival asset orders or waitlisted items currently pending.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(orders.size) { idx ->
                        val ord = orders[idx]
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
                                    Text("🚚 ${ord.assetName}", fontWeight = FontWeight.Bold)
                                    Text("Status: ${formatOrderStatus(ord.orderStatus)} • Vendor: ${ord.contactName ?: "Store"} ${if (!ord.trackingNumber.isNullOrBlank()) "• Tracking: ${ord.trackingNumber}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (ord.orderStatus != "DELIVERED" && ord.orderStatus != "RECEIVED") {
                                        Button(
                                            onClick = { onMarkDelivered(ord) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Mark Delivered", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                    IconButton(onClick = { onEditCustodyInteraction(ord) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        13 -> { // Custody Ledger
            val loansAndRentals = custodyInteractions.filter { it.interactionType != "PURCHASE_ORDER" }
            if (loansAndRentals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active gear loans, rentals, or repair interactions logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(loansAndRentals.size) { idx ->
                        val cust = loansAndRentals[idx]
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
                                    val statusText = if (cust.isReturned) "Returned" else "Active (${formatInteractionType(cust.interactionType)})"
                                    Text("${cust.assetName} ➞ ${cust.contactName ?: "Party"}", fontWeight = FontWeight.Bold)
                                    Text("Status: $statusText • Location: ${cust.storageLocationName ?: "In Storage"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onEditCustodyInteraction(cust) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteCustodyInteraction(cust.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        14 -> { // Taxonomy & Tags
            val rolls = remember { DatabaseRepository.loadAllRolls() }
            TaxonomyAdminPanel(masterVaultRolls = rolls)
        }
    }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(gridListState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaxonomyAdminPanel(
    masterVaultRolls: List<DesktopMasterRollItem>
) {
    // 1. Gather all tags and calculate live usage frequency across all master vault rolls & frames
    val tagUsageMap = remember(masterVaultRolls.toList()) {
        val map = mutableMapOf<String, Int>()
        masterVaultRolls.forEach { roll ->
            roll.frames.forEach { frame ->
                frame.tags.forEach { tag ->
                    val clean = tag.trim()
                    if (clean.isNotBlank()) {
                        map[clean] = (map[clean] ?: 0) + 1
                    }
                }
            }
        }
        map
    }

    // Sub-group / Parent Subject Mapping (1-to-1 Parity with Mobile)
    val subGroupMapping = remember {
        mapOf(
            "Architecture" to "Urban/built environment",
            "Street" to "Urban/built environment",
            "Cityscape" to "Urban/built environment",
            "Building" to "Urban/built environment",
            "Bridge" to "Urban/built environment",
            "Interior" to "Urban/built environment",
            "Skyline" to "Urban/built environment",
            "Wedding" to "Events",
            "Concert" to "Events",
            "Sport" to "Events",
            "Festival" to "Events",
            "Protest" to "Events",
            "Celebration" to "Events",
            "Sunset/sunrise" to "Nature",
            "Landscape" to "Nature",
            "Animals" to "Nature",
            "Flora" to "Nature",
            "Seascape" to "Nature",
            "Portrait" to "People & Portraits",
            "Group" to "People & Portraits",
            "Candid" to "People & Portraits",
            "Light & Shadow Study" to "Study",
            "Material & Film Test" to "Study",
            "Composition Study" to "Study"
        )
    }

    val availableSubGroups = remember {
        listOf("Urban/built environment", "Events", "Nature", "People & Portraits", "Objects & Still Life", "Abstract & Fine Art", "Study", "General")
    }

    // Default Seed Tags grouped by taxonomy category
    val defaultSubjects = remember { mutableStateListOf("Portrait", "Landscape", "Architecture", "Macro") }
    val defaultContent = remember { mutableStateListOf("Bike", "Street", "Outdoors", "Family", "Travel", "Night", "Abandoned", "Sunset/sunrise", "Building", "Bridge", "Interior", "Skyline", "Wedding", "Concert", "Sport", "Festival", "Celebration", "Animals", "Flora") }
    val defaultIntent = remember { mutableStateListOf("Portfolio", "Test Shot", "Documentary", "Experimental", "Light & Shadow Study", "Material & Film Test", "Composition Study") }

    var selectedTaxonomyFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var sortColumn by remember { mutableStateOf("NAME") }
    var sortAscending by remember { mutableStateOf(true) }

    var showAddTagDialog by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(oldTagName, category)
    var deletingTagName by remember { mutableStateOf<String?>(null) }

    // Build flat tag list with taxonomy metadata: Quadruple(tagName, category, subGroup, usageCount)
    val allTaxonomyTags = remember(defaultSubjects.toList(), defaultContent.toList(), defaultIntent.toList(), tagUsageMap) {
        val list = mutableListOf<Quadruple<String, String, String, Int>>()
        defaultSubjects.forEach { list.add(Quadruple(it, "Subjects", subGroupMapping[it] ?: "Primary Subject", tagUsageMap[it] ?: 0)) }
        defaultContent.forEach { list.add(Quadruple(it, "Content", subGroupMapping[it] ?: "Urban/built environment", tagUsageMap[it] ?: 0)) }
        defaultIntent.forEach { list.add(Quadruple(it, "Intent", subGroupMapping[it] ?: "Study", tagUsageMap[it] ?: 0)) }
        
        // Include any custom tags found in masterVaultRolls
        tagUsageMap.keys.forEach { tag ->
            if (list.none { it.first.equals(tag, ignoreCase = true) }) {
                list.add(Quadruple(tag, "Content", subGroupMapping[tag] ?: "General", tagUsageMap[tag] ?: 0))
            }
        }
        list
    }

    val filteredTags = remember(allTaxonomyTags, selectedTaxonomyFilter, searchQuery, sortColumn, sortAscending) {
        var result = allTaxonomyTags.filter { item ->
            val matchesCategory = selectedTaxonomyFilter == "All" || item.second.equals(selectedTaxonomyFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() || item.first.contains(searchQuery, ignoreCase = true) || item.second.contains(searchQuery, ignoreCase = true) || item.third.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        result = when (sortColumn) {
            "NAME" -> if (sortAscending) result.sortedBy { it.first.lowercase() } else result.sortedByDescending { it.first.lowercase() }
            "CATEGORY" -> if (sortAscending) result.sortedBy { it.second.lowercase() } else result.sortedByDescending { it.second.lowercase() }
            "SUBGROUP" -> if (sortAscending) result.sortedBy { it.third.lowercase() } else result.sortedByDescending { it.third.lowercase() }
            "USAGE" -> if (sortAscending) result.sortedBy { it.fourth } else result.sortedByDescending { it.fourth }
            else -> result
        }
        result
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // TOP CONTROL HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("🏷️ TAXONOMY & TAGS MANAGEMENT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(
                    "Subjects (${defaultSubjects.size}) • Content (${defaultContent.size}) • Intent (${defaultIntent.size}) • Total (${allTaxonomyTags.size})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(onClick = { showAddTagDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New Tag", fontWeight = FontWeight.Bold)
            }
        }

        // CATEGORY FILTER CHIPS & SEARCH
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val categories = listOf("All", "Subjects", "Content", "Intent")
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedTaxonomyFilter == cat,
                        onClick = { selectedTaxonomyFilter = cat },
                        label = { Text(cat) }
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Tags...") },
                modifier = Modifier.width(220.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        // HIGH DENSITY SORTABLE DATA GRID
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // COLUMN HEADERS
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAG NAME ${if (sortColumn == "NAME") (if (sortAscending) "▲" else "▼") else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f).clickable {
                            if (sortColumn == "NAME") sortAscending = !sortAscending else { sortColumn = "NAME"; sortAscending = true }
                        }
                    )
                    Text(
                        text = "CATEGORY ${if (sortColumn == "CATEGORY") (if (sortAscending) "▲" else "▼") else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f).clickable {
                            if (sortColumn == "CATEGORY") sortAscending = !sortAscending else { sortColumn = "CATEGORY"; sortAscending = true }
                        }
                    )
                    Text(
                        text = "SUB-GROUP / PARENT SUBJECT ${if (sortColumn == "SUBGROUP") (if (sortAscending) "▲" else "▼") else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.8f).clickable {
                            if (sortColumn == "SUBGROUP") sortAscending = !sortAscending else { sortColumn = "SUBGROUP"; sortAscending = true }
                        }
                    )
                    Text(
                        text = "USAGE FREQUENCY ${if (sortColumn == "USAGE") (if (sortAscending) "▲" else "▼") else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f).clickable {
                            if (sortColumn == "USAGE") sortAscending = !sortAscending else { sortColumn = "USAGE"; sortAscending = true }
                        }
                    )
                    Text("SOURCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("ACTIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                }

                HorizontalDivider()

                // DATA GRID ROWS
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(filteredTags.size, key = { "tax_tag_${filteredTags[it].first}_${filteredTags[it].second}" }) { idx ->
                        val (tagName, cat, subGroup, usageCount) = filteredTags[idx]
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#$tagName", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.weight(1.2f)) {
                                Text(cat, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Text(subGroup, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.8f))
                            Text("$usageCount frames", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.2f))
                            Text(if (cat in listOf("Subjects", "Content", "Intent")) "System Core" else "Custom Tag", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Row(modifier = Modifier.width(90.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { editingTag = Pair(tagName, cat) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { deletingTagName = tagName },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    }
                }
            }
        }
    }

    // ADD TAG DIALOG
    if (showAddTagDialog) {
        var newTagName by remember { mutableStateOf("") }
        var newTagCat by remember { mutableStateOf("Subjects") }

        AlertDialog(
            onDismissRequest = { showAddTagDialog = false },
            title = { Text("Add New Taxonomy Tag", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("Tag Name") },
                        placeholder = { Text("e.g. Street, Portrait") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Category:", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Subjects", "Content", "Intent").forEach { cat ->
                            FilterChip(
                                selected = newTagCat == cat,
                                onClick = { newTagCat = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newTagName.trim().removePrefix("#")
                        if (clean.isNotBlank()) {
                            when (newTagCat) {
                                "Subjects" -> if (!defaultSubjects.contains(clean)) defaultSubjects.add(clean)
                                "Content" -> if (!defaultContent.contains(clean)) defaultContent.add(clean)
                                "Intent" -> if (!defaultIntent.contains(clean)) defaultIntent.add(clean)
                            }
                        }
                        showAddTagDialog = false
                    }
                ) {
                    Text("Save Tag")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTagDialog = false }) { Text("Cancel") }
            }
        )
    }

    // EDIT TAG DIALOG (1-to-1 Parity with Mobile Edit Content Dialog)
    val currentEditingTag = editingTag
    if (currentEditingTag != null) {
        val oldName = currentEditingTag.first
        val oldCat = currentEditingTag.second
        var editName by remember { mutableStateOf(oldName) }
        var editSubGroup by remember { mutableStateOf(subGroupMapping[oldName] ?: "Nature") }
        var subGroupDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingTag = null },
            title = { Text("Edit $oldCat", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editSubGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Associated Subject") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.clickable { subGroupDropdownExpanded = true }.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = subGroupDropdownExpanded,
                            onDismissRequest = { subGroupDropdownExpanded = false }
                        ) {
                            availableSubGroups.forEach { sg ->
                                DropdownMenuItem(
                                    text = { Text(sg) },
                                    onClick = {
                                        editSubGroup = sg
                                        subGroupDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Content Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanNew = editName.trim().removePrefix("#")
                        if (cleanNew.isNotBlank()) {
                            // Global Renaming across Master Vault Rolls
                            if (!cleanNew.equals(oldName, ignoreCase = true)) {
                                masterVaultRolls.forEach { roll ->
                                    roll.frames.forEach { frame ->
                                        if (frame.tags.contains(oldName)) {
                                            val updatedTags = frame.tags.map { if (it == oldName) cleanNew else it }
                                            frame.tags.toMutableList().apply {
                                                clear()
                                                addAll(updatedTags)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        editingTag = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTag = null }) { Text("Cancel") }
            }
        )
    }

    // DELETE TAG DIALOG
    deletingTagName?.let { tagToDelete ->
        AlertDialog(
            onDismissRequest = { deletingTagName = null },
            title = { Text("Delete Tag: #$tagToDelete", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete #$tagToDelete? This tag will be removed from taxonomy indices.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        defaultSubjects.remove(tagToDelete)
                        defaultContent.remove(tagToDelete)
                        defaultIntent.remove(tagToDelete)
                        deletingTagName = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTagName = null }) { Text("Cancel") }
            }
        )
    }
}
