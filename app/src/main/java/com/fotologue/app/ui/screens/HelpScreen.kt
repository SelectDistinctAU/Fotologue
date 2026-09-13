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
                title = "1. Setup Your Gear & Taxonomy",
                iconRes = R.drawable.ic_inventory_2,
                content = "Add your equipment in the Gear Library (Cameras, Lenses, Films, Teleconverters, Filters, Flashes, Viewfinders, Subjects, Content, Intent). Live badges on category tabs show your item counts.\n\n• Camera Association: Link lenses and accessories to specific camera bodies. TLR and compact cameras automatically create and link fixed lenses.\n• 3-Axis Tag Taxonomy (Subject • Content • Intent): Categorize exposures by domain (*Subjects*), detail (*Content*), and purpose (*Intent*: *Study*, *Series*, *Documentation*, *Commission*).\n• Camera Kits: Combine camera, lens, film, and accessories for one-tap setup during shoots."
            )

            HelpSection(
                title = "2. Adding a Log Entry",
                iconRes = R.drawable.list_alt_add_24px,
                content = "Tap the '+' button on the Timeline. Pick a Camera Kit for instant quick fill or select gear manually. Fotologue dynamically filters lenses, film formats, and accessories based on your chosen camera—providing direct prompts to add matching film if none exists in your library."
            )

            HelpSection(
                title = "3. Using the Light Meter",
                iconRes = R.drawable.ic_exposure,
                content = "Tap 'Measure Exposure' on the Add Log screen. Use the live viewfinder to meter your scene with Spot or Center-weighted modes. Pre-visualize with B&W, C-41, or E-6 film look simulations. Tap an exposure suggestion to apply it to your log and capture an optional thumbnail."
            )

            HelpSection(
                title = "4. Meter Calibration",
                iconRes = R.drawable.ic_handyman,
                content = "To ensure professional accuracy, use the Pro Calibration Wizard (Settings > EV Calibration). Stage 1 'The Void' zeros sensor noise floor with a covered lens, while Stage 2 'The Balance' matches an 18% grey card or neutral surface."
            )

            HelpSection(
                title = "5. Digital Contact Sheet & Proof Sheets",
                iconRes = R.drawable.gallery_thumbnail_24px,
                content = "Tap the Contact Sheet icon on the Timeline to open the Digital Contact Sheet.\n\n• Visual Fingerprints: Compare digital scans against scene thumbnails to easily match scan files to field logs.\n• In-Grid Gap Adjuster: Tap any skipped/blank card to insert or remove frame gaps (+1 / -1 offset), keeping your digital contact sheet 1-to-1 aligned with physical film rebate numbers.\n• Printable PDF Proof Sheets: Export styled A4 PDF contact proof sheets complete with headers, thumbnails, rebate numbers, and exposure details for printing or digital archival binders."
            )

            HelpSection(
                title = "6. Managing Logs, Exports & Macro Presets",
                iconRes = R.drawable.ic_timeline,
                content = "Your logs appear in the Timeline with visual export badges ([Z], [C], [J], [X]).\n\n• Camera Macro Copy Presets: When exporting XMP sidecars, select or save camera presets (Fujifilm `_DSF`, Canon `IMG_`, Nikon `DSC_`, Sony `_DSC`) to automatically match RAW scan file sequences.\n• Full XMP Hierarchy: Exports `Subject`, `Content`, and `Intent` (`Intent|Study`, `Intent|Series`) hierarchies directly into Adobe Lightroom and Capture One catalogs."
            )

            HelpSection(
                title = "7. Backup & Privacy",
                iconRes = R.drawable.ic_security,
                content = "Your privacy is our highest priority. All data—including thumbnails, GPS coordinates, gear lists, and notes—remains 100% local on your device.\n\n• On-Device ML: Smart Face-Aware tagging operates entirely on-device; no biometric data or images are ever uploaded or transmitted.\n• SAF Backups: Export and restore complete backups using standard Android Storage Access Framework.\n• Zero Tracking: Fotologue contains no analytics or tracking."
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
