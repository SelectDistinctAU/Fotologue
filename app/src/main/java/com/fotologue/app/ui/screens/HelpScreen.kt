package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fotologue.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HelpSection(
                title = "1. Setup Your Gear",
                iconRes = R.drawable.ic_inventory_2,
                content = "Before you can log exposures, you need to add your equipment. Go to the Gear Library to add your Cameras, Lenses, and Films. Fotologue includes an 'Online Lookup' to help you fill in specifications quickly."
            )

            HelpSection(
                title = "2. Adding a Log Entry",
                iconRes = R.drawable.ic_add_a_photo,
                content = "Tap the '+' button on the Timeline. Select your camera, lens, and film. You can also add accessories like filters, teleconverters, and flashes. If you haven't added a lens yet, use the 'Add New Lens...' shortcut right in the selection menu."
            )

            HelpSection(
                title = "3. Using the Light Meter",
                iconRes = R.drawable.ic_exposure,
                content = "Tap 'Measure Exposure' on the Add Log screen. Use the camera viewfinder to meter your scene. You can toggle between Spot and Center-weighted metering. Tap an exposure suggestion to apply it to your log and capture an optional thumbnail."
            )

            HelpSection(
                title = "4. Managing Logs",
                iconRes = R.drawable.ic_timeline,
                content = "Your logs are displayed in the Timeline. Long-press any entry to enter selection mode, where you can bulk-delete entries or export them to CSV, JSON, or a full ZIP package (including thumbnails)."
            )

            HelpSection(
                title = "5. Backup & Privacy",
                iconRes = R.drawable.ic_security,
                content = "Your data stays on your device. At no time are your images, locations, equipment, tags, or comments visible to the developers. Use the Settings screen to create a backup file if you want to move your data to a new device."
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Got it!")
            }
            
            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 16.dp))
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    iconRes: Int,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
