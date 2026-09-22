package com.fotologue.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.util.Locale
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

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fotologue Desktop Command Center (Windows 11 Pro / Linux)",
        state = WindowState(size = DpSize(1280.dp, 850.dp))
    ) {
        DesktopApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DesktopApp() {
    val exifToolManager = remember { DesktopExifToolManager() }
    var selectedTab by remember { mutableIntStateOf(0) }

    var targetFolderPath by remember { mutableStateOf("D:\\Scans\\2026\\PNTX-TX400-R01\\_DSF1024.xmp") }
    var selectedFilmRollSerial by remember { mutableStateOf("PNTX-TX400-R01") }
    var latText by remember { mutableStateOf("35.6762") }
    var lonText by remember { mutableStateOf("139.6503") }
    var logOutputText by remember { mutableStateOf("ExifTool Engine Ready. Detected Operating System: ${exifToolManager.currentOS.name}") }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Fotologue Desktop",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "• Analogue & Hybrid Photography Command Center",
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
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("🛠️ ExifTool Geotagging & XMP Dispatcher", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("📂 NAS & Storage Target Share", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("📜 Master Roll & Reflection Vault", fontWeight = FontWeight.Bold) }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // EXIFTOOL GEOTAGGING & XMP DISPATCHER WORKBENCH
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            "CROSS-PLATFORM EXIFTOOL DISPATCHER",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            "Detected System OS: ${exifToolManager.currentOS.name} (${if (exifToolManager.currentOS == DesktopOS.WINDOWS) "Windows 11 Pro - exiftool.exe" else "Linux/macOS - exiftool"})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = targetFolderPath,
                                                onValueChange = { targetFolderPath = it },
                                                label = { Text("Target Scan File / Folder Path (NAS Share / Local Drive)") },
                                                placeholder = { Text("e.g. D:\\Scans\\2026\\PNTX-TX400-R01\\_DSF1024.xmp or //NAS/FilmArchive/") },
                                                modifier = Modifier.weight(1f)
                                            )

                                            Button(
                                                onClick = {
                                                    val chosen = chooseDirectoryOrFile(targetFolderPath)
                                                    if (chosen != null) {
                                                        targetFolderPath = chosen
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(top = 8.dp)
                                                    .height(54.dp)
                                            ) {
                                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Browse...", fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = selectedFilmRollSerial,
                                                onValueChange = { selectedFilmRollSerial = it },
                                                label = { Text("Film Roll Serial") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = latText,
                                                onValueChange = { latText = it },
                                                label = { Text("Field Latitude") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = lonText,
                                                onValueChange = { lonText = it },
                                                label = { Text("Field Longitude") },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                val lat = latText.toDoubleOrNull() ?: 35.6762
                                                val lon = lonText.toDoubleOrNull() ?: 139.6503
                                                logOutputText = "Executing ExifTool on $targetFolderPath...\n"
                                                val res = exifToolManager.injectGpsAndMetadata(
                                                    targetFilePath = targetFolderPath,
                                                    latitude = lat,
                                                    longitude = lon,
                                                    creatorName = "Gerard Bendotti",
                                                    filmRollSerial = selectedFilmRollSerial,
                                                    description = "Fotologue Field Exposure Log + ExifTool Geotagged Scan"
                                                )
                                                logOutputText += "Exit Code: ${res.exitCode}\nOutput: ${res.stdout.ifBlank { res.stderr.ifBlank { "Success! Metadata & GPS Coordinates Written." } }}"
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                        ) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Geotag Scan & Emit ISO XMP Sidecar Now",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "EXIFTOOL EXECUTION CONSOLE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = logOutputText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "NAS & LOCAL STORAGE SHARE TARGETS",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    "Fotologue Desktop reads and writes directly to mounted NAS volumes (Synology, TrueNAS, Unraid over SMB/NFS) and local hard drives without Storage Access Framework restrictions.",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Button(
                                    onClick = {
                                        val chosen = chooseDirectoryOrFile(targetFolderPath)
                                        if (chosen != null) {
                                            targetFolderPath = chosen
                                        }
                                    },
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mount NAS / Select Hard Drive Folder...", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "MASTER ROLL & DARKROOM REFLECTION VAULT",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    "Full-fidelity desktop view of all analogue film rolls, development timers, enlarger print recipes, keeper ratings, and photographer reflections.",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
