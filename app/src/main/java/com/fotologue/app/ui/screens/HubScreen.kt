package com.fotologue.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubScreen(
    isSafelightActive: Boolean = false,
    onToggleSafelight: (() -> Unit)? = null,
    onNavigateToCameraGear: () -> Unit = {},
    onNavigateToTaxonomy: () -> Unit = {},
    onNavigateToField: () -> Unit,
    onNavigateToContacts: () -> Unit = {},
    onNavigateToLab: () -> Unit,
    onNavigateToStation: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safelightBg = if (isSafelightActive) Color.Black else MaterialTheme.colorScheme.surface
    val safelightCardBg = if (isSafelightActive) Color(0xFF1F0000) else MaterialTheme.colorScheme.surfaceVariant
    val safelightFg = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurface
    val safelightSubFg = if (isSafelightActive) Color(0xFFFF3333) else MaterialTheme.colorScheme.onSurfaceVariant
    val safelightPrimary = if (isSafelightActive) Color(0xFFFF0000) else MaterialTheme.colorScheme.primary

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = safelightBg,
        contentColor = safelightFg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = safelightBg, titleContentColor = safelightFg),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(com.fotologue.app.R.mipmap.ic_launcher_round),
                            contentDescription = "Fotologue Logo",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Fotologue",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = safelightFg
                            )
                            Text(
                                text = "A Photographic Catalogue Suite",
                                style = MaterialTheme.typography.labelMedium,
                                color = safelightPrimary
                            )
                        }
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
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(R.drawable.settings_24px),
                            contentDescription = "Settings",
                            tint = safelightPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "section_header_modules") {
                Text(
                    text = "APPLICATION STAGING PORTAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = safelightPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            item(key = "module_camera_gear") {
                HubModuleCard(
                    title = "CAMERA GEAR",
                    subtitle = "Cameras, Lenses, Films & Accessories",
                    description = "Cameras • Lenses • Films • Teleconverters • Filters • Flashes • Viewfinders • Kit Bags",
                    iconRes = com.fotologue.app.R.drawable.settings_photo_camera_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToCameraGear
                )
            }

            item(key = "module_taxonomy") {
                HubModuleCard(
                    title = "TAXONOMY & TAGS",
                    subtitle = "Subject, Content & Intent Indexing",
                    description = "Subjects • Content • Intent • Hierarchical Tag Index",
                    iconRes = com.fotologue.app.R.drawable.ic_label,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToTaxonomy
                )
            }

            item(key = "module_field") {
                HubModuleCard(
                    title = "FIELD",
                    subtitle = "In-situ Exposure & Observation",
                    description = "Exposure Logging • Light Meter",
                    iconRes = R.drawable.hiking_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToField
                )
            }

            item(key = "module_contacts") {
                HubModuleCard(
                    title = "CONTACTS DIRECTORY",
                    subtitle = "People & Vendor Management",
                    description = "Suppliers • Labs • Repair Techs • Friends",
                    iconRes = com.fotologue.app.R.drawable.patient_list_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToContacts
                )
            }

            item(key = "module_lab") {
                HubModuleCard(
                    title = "LAB",
                    subtitle = "Wet Darkroom Processing & Inventory",
                    description = "Item Specifications • Stock Lots & In-Flow • Suppliers • Maintenance",
                    iconRes = R.drawable.experiment_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToLab
                )
            }

            item(key = "module_station") {
                HubModuleCard(
                    title = "STATION",
                    subtitle = "Studio Analysis & Reflection",
                    description = "Digital Contact Sheets • Frame Gap Alignment • Darkroom Reflections",
                    iconRes = R.drawable.hub_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToStation
                )
            }

            item(key = "module_archive") {
                HubModuleCard(
                    title = "ARCHIVE",
                    subtitle = "Master Registry & Preservation Vault",
                    description = "Canonical JSON Backup Vault • Merge Import • XMP Desktop Sidecar Exports",
                    iconRes = R.drawable.shelves_24px,
                    cardBg = safelightCardBg,
                    titleColor = safelightFg,
                    subColor = safelightPrimary,
                    descColor = safelightSubFg,
                    onClick = onNavigateToArchive
                )
            }
        }
    }
}

@Composable
fun HubModuleCard(
    title: String,
    subtitle: String,
    description: String,
    iconRes: Int,
    cardBg: Color,
    titleColor: Color,
    subColor: Color,
    descColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = subColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = titleColor
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = subColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = descColor
                )
            }
        }
    }
}
