package com.fotologue.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.fotologue.core.designsystem.R
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubScreen(
    isSafelightActive: Boolean = false,
    onToggleSafelight: (() -> Unit)? = null,
    onNavigateToCameraGear: () -> Unit = {},
    onNavigateToAssetsInventory: () -> Unit = {},
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
                    Row(
                        modifier = Modifier.padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                if (isSafelightActive) com.fotologue.app.R.drawable.fotologueredshift
                                else com.fotologue.app.R.drawable.ic_fotologue_logo
                            ),
                            contentDescription = "Fotologue Logo",
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Fotologue",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = safelightFg,
                                maxLines = 1
                            )
                            Text(
                                text = "A Photographic Catalogue Suite",
                                style = MaterialTheme.typography.labelMedium,
                                color = safelightPrimary,
                                maxLines = 1
                            )
                        }
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
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.settings_24px),
                            contentDescription = "Settings",
                            tint = safelightPrimary,
                            modifier = Modifier.size(24.dp)
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
            item(key = "module_camera_gear") {
                HubModuleCard(
                    subtitle = "Cameras, Lenses, Films & Accessories",
                    iconRes = com.fotologue.app.R.drawable.settings_photo_camera_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToCameraGear
                )
            }

            item(key = "module_assets_inventory") {
                HubModuleCard(
                    subtitle = "Assets & Inventory",
                    iconRes = com.fotologue.app.R.drawable.inventory_2_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToAssetsInventory
                )
            }

            item(key = "module_field") {
                HubModuleCard(
                    subtitle = "Exposure Logging & Classification",
                    iconRes = R.drawable.hiking_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToField
                )
            }

            item(key = "module_taxonomy") {
                HubModuleCard(
                    subtitle = "Subject, Content & Intent Indexing",
                    iconRes = com.fotologue.app.R.drawable.ic_label,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToTaxonomy
                )
            }

            item(key = "module_contacts") {
                HubModuleCard(
                    subtitle = "People & Vendor Management",
                    iconRes = com.fotologue.app.R.drawable.patient_list_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToContacts
                )
            }

            item(key = "module_lab") {
                HubModuleCard(
                    subtitle = "Darkroom Processing & Printing",
                    iconRes = R.drawable.experiment_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToLab
                )
            }

            item(key = "module_station") {
                HubModuleCard(
                    subtitle = "Studio Analysis & Reflection",
                    iconRes = R.drawable.hub_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToStation
                )
            }

            item(key = "module_archive") {
                HubModuleCard(
                    subtitle = "Archive Vault",
                    iconRes = com.fotologue.app.R.drawable.backup_24px,
                    cardBg = safelightCardBg,
                    subColor = safelightPrimary,
                    onClick = onNavigateToArchive
                )
            }
        }
    }
}

@Composable
fun HubModuleCard(
    subtitle: String,
    iconRes: Int,
    cardBg: Color,
    subColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = subtitle
            },
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = subColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = subColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
