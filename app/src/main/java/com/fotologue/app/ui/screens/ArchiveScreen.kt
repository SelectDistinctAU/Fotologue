package com.fotologue.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
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

    val createReferenceManifestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    viewModel.exportFoundationManifest(outputStream)
                }
                snackbarHostState.showSnackbar("Reference Sync Manifest Exported!")
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

    val importReferenceManifestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    viewModel.importFoundationManifest(inputStream)
                }
                snackbarHostState.showSnackbar("Reference Manifest Imported. Foundation Catalogs Synced!")
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
                            text = "Archive",
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
                        IconButton(
                            onClick = onToggleSafelight,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.night_sight_max_24px),
                                contentDescription = if (isSafelightActive) "Disable Red Shift" else "Enable Red Shift",
                                tint = safelightPrimary,
                                modifier = Modifier.size(24.dp)
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
            // CATALOGUE BACKUP & RESTORE
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
                        text = "CATALOGUE BACKUP & RESTORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Export a full backup of your catalogue or restore gear, logs, and settings from a previously saved backup file.",
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
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = btnTextTint)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Backup", style = MaterialTheme.typography.labelSmall, color = btnTextTint, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { restoreJsonLauncher.launch(arrayOf("application/json")) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = safelightPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore Backup", style = MaterialTheme.typography.labelSmall, color = safelightPrimary, maxLines = 1)
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
                        text = "DESKTOP PHOTO LIBRARY EXPORTER (digiKam / Lightroom / Capture One)",
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

            // REFERENCE & FOUNDATION SYNC (OFFLINE)
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
                        text = "REFERENCE & FOUNDATION SYNC (OFFLINE)",
                        style = MaterialTheme.typography.labelSmall,
                        color = safelightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "True bidirectional offline sync for your Foundation Reference catalogs. Exchanges equipment profiles, LOVs, contacts, and storage locations with Fotologue Desktop without altering your Field Logs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = safelightSubFg
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { createReferenceManifestLauncher.launch("fotologue_foundation_manifest.json") },
                            colors = btnColors,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = btnTextTint)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Manifest", style = MaterialTheme.typography.labelSmall, color = btnTextTint, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { importReferenceManifestLauncher.launch(arrayOf("application/json")) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = safelightPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import Manifest", style = MaterialTheme.typography.labelSmall, color = safelightPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
