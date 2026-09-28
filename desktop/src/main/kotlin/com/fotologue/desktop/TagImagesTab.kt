package com.fotologue.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagImagesTab(
    masterVaultRolls: List<DesktopMasterRollItem>,
    exifToolManager: DesktopExifToolManager
) {
    var targetFolderPath by remember { mutableStateOf("") }
    var selectedRoll by remember { mutableStateOf<DesktopMasterRollItem?>(null) }
    var isRollDropdownExpanded by remember { mutableStateOf(false) }
    var logOutputText by remember { mutableStateOf("ExifTool Engine Ready. Detected Operating System: ${exifToolManager.currentOS.name}\n") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP CONTROL ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Roll Selector
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. SELECT ROLL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedRoll?.filmRollSerial ?: "Select an Active or Finished Roll...",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().clickable { isRollDropdownExpanded = true },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                        )
                        DropdownMenu(
                            expanded = isRollDropdownExpanded,
                            onDismissRequest = { isRollDropdownExpanded = false }
                        ) {
                            masterVaultRolls.filter { it.status == RollStatus.ACTIVE || it.status == RollStatus.FINISHED }.forEach { roll ->
                                DropdownMenuItem(
                                    text = { Text("${roll.filmRollSerial} (${roll.cameraName} • ${roll.filmName})") },
                                    onClick = {
                                        selectedRoll = roll
                                        isRollDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Folder Selector
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("2. SELECT IMAGE DIRECTORY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetFolderPath,
                            onValueChange = { targetFolderPath = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("e.g. D:\\Scans\\2026\\Roll1\\") }
                        )
                        Button(
                            onClick = {
                                val chosen = chooseDirectoryOrFile(targetFolderPath)
                                if (chosen != null) targetFolderPath = chosen
                            },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Browse")
                        }
                    }
                }
            }
        }

        // MAPPING AND EXECUTION AREA
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Frame / File Map (Left)
            Card(
                modifier = Modifier.weight(2f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("3. MAP FRAMES TO IMAGES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (selectedRoll == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select a roll to view frames.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else if (targetFolderPath.isBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select an image directory to map files.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        val scanDir = File(targetFolderPath)
                        val scanFiles = if (scanDir.exists() && scanDir.isDirectory) {
                            scanDir.listFiles { file -> file.extension.lowercase() in listOf("jpg", "jpeg", "tif", "tiff") }?.sortedBy { it.name } ?: emptyList()
                        } else emptyList()

                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedRoll!!.frames.size) { index ->
                                val frame = selectedRoll!!.frames[index]
                                val mappedFile = scanFiles.getOrNull(index)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small).padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Frame ${frame.frameNumber}", fontWeight = FontWeight.Bold)
                                        Text("${frame.shutterSpeed} • ${frame.aperture}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("➞", modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.primary)
                                    Column(modifier = Modifier.weight(1f)) {
                                        if (mappedFile != null) {
                                            Text(mappedFile.name, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                            Text("Found", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
                                        } else {
                                            Text("No File Match", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ExifTool Execution (Right)
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            logOutputText += "Execution started...\n"
                            // TODO: Add real batch iteration
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = selectedRoll != null && targetFolderPath.isNotBlank()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Write Metadata to Images", fontWeight = FontWeight.Bold)
                    }

                    Text("EXECUTION CONSOLE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                    Text("EXIFTOOL CONSOLE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = logOutputText,
                        onValueChange = {},
                        modifier = Modifier.fillMaxSize(),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        readOnly = true
                    )
                }
            }
        }
    }
}
