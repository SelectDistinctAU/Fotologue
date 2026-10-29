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
    onBack: () -> Unit,
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
                title = "1. Camera Gear Library",
                iconRes = R.drawable.settings_photo_camera_24px,
                content = "Manage your camera equipment with 100% relational integrity:\n\n• Master Preset Catalog: Search over 80+ classic film camera bodies, lenses, and film emulsions with brand-first and mount-first cascading filters.\n• Automated Web Spec Search Engine: Search for unlisted gear (e.g., HD PENTAX-FA 43mm Limited) to automatically fetch and pre-fill specs online in 1 tap.\n• Interactive Availability Status: Tap any status badge on camera/lens cards to instantly set Availability (In Stock, In Transit, On Loan, In Repair/CLA, Sold/Archived)."
            )

            HelpSection(
                title = "2. Assets & Inventory",
                iconRes = R.drawable.inventory_2_24px,
                content = "Track physical studio supplies and inbound shipments:\n\n• Consumables & Chemistry: Monitor chemical capacity, paper stocks, safety gear, and supplies.\n• Storage Locations: Assign dry cabinets, studio shelves, camera bags, and paper safes.\n• Pre-Arrival Inbound Orders: Track incoming camera and lens packages with live delivery status. Tapping 'Mark Delivered' converts the package into an active available asset without creating duplicates.\n• Custody & Loans Ledger: Track equipment lent to friends, hired gear, and active workshop repair status."
            )

            HelpSection(
                title = "3. Exposure Logging & Classification (FOTOLOGUE FIELD)",
                iconRes = R.drawable.hiking_24px,
                content = "Log frame-by-frame exposure metadata during street and studio shoots:\n\n• Precision Spot Light Meter: Real-time spot metering with target EV calculation, camera body/lens pairing, and automatic ND/contrast filter stop compensation.\n• Live GPS Geotagging: Captures exact latitude/longitude for every frame without uploading location data.\n• Camera Kit Quick-Fill: Select a pre-configured Camera Kit for 1-tap setup during shoots."
            )

            HelpSection(
                title = "4. Subject, Content and Intent Tags",
                iconRes = R.drawable.ic_label,
                content = "3-Axis Taxonomy Indexing for instant information retrieval:\n\n• Subjects: Domain classification (Architecture, Landscape, Portrait, Street, Wildlife).\n• Content: Specific visual details (Bridge, Building, Waterways, Fungi, Skyline).\n• Intent: Purpose of the series (Study, Series, Documentation, Personal, Commission).\n• Instant Search: Filter exposures instantly by gear or tag across your entire timeline."
            )

            HelpSection(
                title = "5. People & Vendor Management",
                iconRes = R.drawable.patient_list_24px,
                content = "Directory for film labs, equipment suppliers, repair technicians, and friends:\n\n• Native Phone Contact Picker: Pick phone contacts directly to log borrowers in the Custody Ledger.\n• Regional Lab & Vendor Directory: Store website links, phone numbers, and lab processing notes."
            )

            HelpSection(
                title = "6. Darkroom Processing & Printing (LAB)",
                iconRes = R.drawable.experiment_24px,
                content = "Execution tools for the wet darkroom:\n\n• Film Development Timer: Live step-by-step chemical development timer with an acoustic 60 BPM agitation metronome and 3-second pre-warning pips for working in total darkness without looking at a screen.\n• Enlarger Printing Session Ledger: Log enlarger machines, lens-to-paper distances, split-grade exposure step ledgers, and dry-down factor compensation."
            )

            HelpSection(
                title = "7. Studio Analysis & Reflection (STATION)",
                iconRes = R.drawable.hub_24px,
                content = "Reflect upon and analyze your photographic work:\n\n• Digital Contact Sheets: View roll grids with scene thumbnails to match digital RAW scan files against physical film rolls.\n• In-Grid Gap Adjuster: Tap skipped/blank frames to insert or remove frame gaps (+1 / -1 offset) to keep digital contact sheets 1-to-1 aligned with physical film rebate numbers."
            )

            HelpSection(
                title = "8. Archive Vault (ARCHIVE)",
                iconRes = R.drawable.backup_24px,
                content = "Your central data management and export hub:\n\n• Catalogue Backup & Restore: Export full-fidelity `.json` catalogue backups with non-destructive merge imports.\n• Desktop Photo Library Exporter: Export CSV summary logs and ISO 16684 XMP sidecars with 3-Axis Tag hierarchies for Adobe Lightroom, Capture One, and digiKam.\n• Desktop Sync Manifests: Bidirectional offline sync for Foundation Reference catalogs with Fotologue Desktop.\n• Cloud Master Preset Refresh: 1-tap master catalog refresh directly from GitHub."
            )

            HelpSection(
                title = "9. 100% Private & On-Device Security",
                iconRes = R.drawable.ic_security,
                content = "Your privacy is our top priority. All data—including thumbnails, GPS coordinates, gear lists, and notes—remains 100% local on your device.\n\n• On-Device ML: Smart Face-Aware tagging operates entirely on-device; no images or biometric data are ever transmitted or uploaded.\n• Zero Tracking: Fotologue contains no third-party analytics or cloud lock-in."
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
