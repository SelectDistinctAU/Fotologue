package com.fotologue.core.data

import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class OperatingSystem {
    WINDOWS, LINUX, MACOS, UNKNOWN
}

data class ExifToolExecutionResult(
    val success: Boolean,
    val stdout: String,
    val stderr: String,
    val exitCode: Int
)

@Singleton
class ExifToolManager @Inject constructor() {

    val currentOS: OperatingSystem = detectOperatingSystem()

    private fun detectOperatingSystem(): OperatingSystem {
        val osName = System.getProperty("os.name")?.lowercase(Locale.ROOT) ?: ""
        return when {
            osName.contains("win") -> OperatingSystem.WINDOWS
            osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OperatingSystem.LINUX
            osName.contains("mac") -> OperatingSystem.MACOS
            else -> OperatingSystem.UNKNOWN
        }
    }

    /**
     * Resolves the executable command for ExifTool depending on operating system.
     */
    fun resolveExifToolExecutablePath(): String {
        return when (currentOS) {
            OperatingSystem.WINDOWS -> "exiftool.exe"
            OperatingSystem.LINUX -> "exiftool"
            OperatingSystem.MACOS -> "exiftool"
            OperatingSystem.UNKNOWN -> "exiftool"
        }
    }

    /**
     * Injects field GPS coordinates, creator copyright, film roll serial, and 3-Axis tags
     * into an ISO 16684 XMP sidecar or RAW scan file using ExifTool.
     */
    fun injectGpsAndMetadata(
        targetFilePath: String,
        latitude: Double?,
        longitude: Double?,
        altitude: Double? = null,
        creatorName: String? = null,
        copyrightNotice: String? = null,
        filmRollSerial: String? = null,
        description: String? = null,
        hierarchicalTags: List<String> = emptyList()
    ): ExifToolExecutionResult {
        val executable = resolveExifToolExecutablePath()
        val commandList = mutableListOf<String>()
        commandList.add(executable)

        // GPS Coordinates
        if (latitude != null && longitude != null) {
            val latRef = if (latitude >= 0) "N" else "S"
            val lonRef = if (longitude >= 0) "E" else "W"
            val absLat = Math.abs(latitude)
            val absLon = Math.abs(longitude)

            commandList.add("-XMP-exif:GPSLatitude=$absLat")
            commandList.add("-XMP-exif:GPSLatitudeRef=$latRef")
            commandList.add("-XMP-exif:GPSLongitude=$absLon")
            commandList.add("-XMP-exif:GPSLongitudeRef=$lonRef")

            if (altitude != null) {
                commandList.add("-XMP-exif:GPSAltitude=$altitude")
            }
        }

        // Creator & Copyright
        if (!creatorName.isNullOrBlank()) {
            commandList.add("-XMP-dc:creator=$creatorName")
        }
        if (!copyrightNotice.isNullOrBlank()) {
            commandList.add("-XMP-dc:rights=$copyrightNotice")
        }

        // Film Roll Serial & Summary Description
        if (!filmRollSerial.isNullOrBlank()) {
            commandList.add("-XMP-fotologue:physicalSerial=$filmRollSerial")
        }
        if (!description.isNullOrBlank()) {
            commandList.add("-XMP-dc:description=$description")
        }

        // 3-Axis Tag Hierarchy
        hierarchicalTags.forEach { tag ->
            commandList.add("-XMP-lr:hierarchicalSubject+=$tag")
        }

        commandList.add("-overwrite_original")
        commandList.add(targetFilePath)

        return executeCommand(commandList)
    }

    /**
     * Executes the built command list via ProcessBuilder across Windows, macOS, or Linux.
     */
    fun executeCommand(commandList: List<String>): ExifToolExecutionResult {
        return try {
            val processBuilder = ProcessBuilder(commandList)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()

            val stdout = process.inputStream.bufferedReader().use { it.readText() }
            val exitCode = process.waitFor()

            ExifToolExecutionResult(
                success = exitCode == 0,
                stdout = stdout,
                stderr = "",
                exitCode = exitCode
            )
        } catch (e: Exception) {
            ExifToolExecutionResult(
                success = false,
                stdout = "",
                stderr = e.message ?: "Failed to execute ExifTool process",
                exitCode = -1
            )
        }
    }
}
