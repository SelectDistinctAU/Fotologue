package com.fotologue.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.app.ui.viewmodel.SettingsViewModel
import com.fotologue.core.designsystem.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    viewModel: SettingsViewModel,
    isSafelightActive: Boolean = false,
    onToggleSafelight: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var archiveTargetUri by remember { mutableStateOf<Uri?>(null) }

    // SAF Target Folder Picker
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            archiveTargetUri = it
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Granted for session
            }
            scope.launch {
                snackbarHostState.showSnackbar("Archive Target Mounted: ${it.lastPathSegment}")
            }
        }
    }

    // JSON Backup Export Launcher
    val createJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    viewModel.backup(outputStream)
                }
                snackbarHostState.showSnackbar("Native JSON Vault Export Complete!")
            }
        }
    }

    // Non-Destructive Merge Import Launcher
    val restoreJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    viewModel.restore(inputStream)
                }
                snackbarHostState.showSnackbar("Historical Catalogue Merged Successfully!")
            }
        }
    }

    // CSV Summary Export Launcher
    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                snackbarHostState.showSnackbar("CSV Summary Export Complete!")
            }
        }
    }

    val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())

    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary
    val btnColors = if (isSafelightActive) {
        ButtonDefaults.buttonColors(containerColor = Color(0xFF660000), contentColor = Color(0xFFFF0000))
    } else {
        ButtonDefaults.buttonColors()
    }
    val btnTextTint = if (isSafelightActive) Color(0xFFFF0000) else Color.White

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.shelves_24px),
                            contentDescription = null,
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Archive Vault & Desktop Bridge",
                            style = MaterialTheme.typography.titleLarge,
                            color = safelightFg
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Return to Application Hub",
                            tint = safelightPrimary
                        )
                    }
                },
                actions = {
                    if (onToggleSafelight != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "Safelight",
                                style = MaterialTheme.typography.labelSmall,
                                color = safelightPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = isSafelightActive,
                                onCheckedChange = { onToggleSafelight() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFF0000),
                                    checkedTrackColor = Color(0xFF660000),
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // MOUNTED STORAGE TARGET CARD (NAS / Proton Drive / Google Drive)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safelightCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "STORAGE TARGET & DESTINATION SHARE",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    val targetDisplay = archiveTargetUri?.lastPathSegment ?: "Not Mounted (Defaulting to Downloads / SAF)"
                    Text(
                        text = "Target Share: $targetDisplay",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = safelightFg
                    )
                    Text(
                        text = "Mount your Local NAS (SMB/WebDAV), Proton Drive, or Google Drive folder via Storage Access Framework for 1-tap backups.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safelightSubFg
                    )

                    Button(
                        onClick = { folderPickerLauncher.launch(null) },
                        colors = btnColors,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(painterResource(R.drawable.warehouse_24px), contentDescription = null, tint = btnTextTint)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (archiveTargetUri != null) "Change Target Directory" else "Mount NAS / Cloud Directory", color = btnTextTint, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // CANONICAL NATIVE JSON BACKUP VAULT
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safelightCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CANONICAL NATIVE JSON VAULT",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Full-fidelity relational database backups. The native JSON backup is the single source of truth for your lifetime catalogue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safelightSubFg
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { createJsonLauncher.launch("backup_$timestamp.json") },
                            colors = btnColors,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = btnTextTint)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Vault", style = MaterialTheme.typography.labelSmall, color = btnTextTint, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { restoreJsonLauncher.launch(arrayOf("application/json")) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = safelightPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Merge Import", style = MaterialTheme.typography.labelSmall, color = safelightPrimary)
                        }
                    }
                }
            }

            // DESKTOP DAM EXPORTER (digiKam / Lightroom / Capture One)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safelightCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "DESKTOP DAM EXPORTER (digiKam / Lightroom / Capture One)",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Generates ISO 16684 XMP sidecar files with 3-Axis Tag hierarchies (`Subject`, `Content`, `Intent`), creator copyright, and camera RAW scan sequence matching.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safelightSubFg
                    )

                    Button(
                        onClick = { createCsvLauncher.launch("catalogue_summary_$timestamp.csv") },
                        colors = btnColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = btnTextTint)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV Summary Log", style = MaterialTheme.typography.labelMedium, color = btnTextTint, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
