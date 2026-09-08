package com.fotologue.app.ui.screens

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fotologue.app.data.LogEntryWithGear
import com.fotologue.app.ui.viewmodel.LogDetailViewModel
import com.fotologue.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailScreen(
    viewModel: LogDetailViewModel,
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    showBackButton: Boolean = true
) {
    val logWithGear by viewModel.logEntry.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.log_details)) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
                        }
                    }
                },
                actions = {
                    logWithGear?.let { entryWithGear ->
                        IconButton(onClick = { onEditClick(entryWithGear.logEntry.id) }) {
                            Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.action_edit))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .consumeWindowInsets(innerPadding)
        ) {
            logWithGear?.let { entryWithGear ->
                val entry = entryWithGear.logEntry
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (entry.exposureSettings.thumbnailUri != null) {
                        AsyncImage(
                            model = entry.exposureSettings.thumbnailUri,
                            contentDescription = "Thumbnail",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 16.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                    Text(
                        text = SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (entry.lastUpdated > entry.timestamp + 60000) { // More than 1 min difference
                        Text(
                            text = stringResource(R.string.last_updated_at, SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(entry.lastUpdated))),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem(label = stringResource(R.string.label_camera), value = entryWithGear.camera?.name ?: stringResource(R.string.unknown_deleted))
                    if (!entry.filmRoll.isNullOrBlank()) {
                        val expected = entryWithGear.film?.expectedExposures
                        val isHalf = entryWithGear.camera?.isHalfFrame == true
                        val total = if (isHalf && expected != null) expected * 2 else expected
                        val value = if (total != null) "${entry.filmRoll}/$total" else entry.filmRoll
                        DetailItem(label = stringResource(R.string.label_film_roll), value = value)
                    }
                    entryWithGear.lens?.let { lens ->
                        DetailItem(label = stringResource(R.string.label_lens), value = "${lens.make} ${lens.model}")
                    }
                    entry.focalLength?.let { fl ->
                        DetailItem(label = stringResource(R.string.label_focal_length), value = "${fl.toInt()}mm")
                    }
                    entryWithGear.teleconverter?.let { tc ->
                        DetailItem(label = stringResource(R.string.label_teleconverter), value = tc.name)
                    }
                    entryWithGear.filter?.let { filter ->
                        DetailItem(label = stringResource(R.string.label_filter), value = filter.name)
                    }
                    entryWithGear.flash?.let { flash ->
                        DetailItem(label = stringResource(R.string.label_flash), value = "${flash.make} ${flash.model}")
                    }
                    DetailItem(label = stringResource(R.string.label_film), value = entryWithGear.film?.let { "${it.name} (ISO ${it.iso}, ${it.expectedExposures} exp)" } ?: stringResource(R.string.unknown_deleted))
                    
                    if (entry.effectiveFocalLength != null || entry.effectiveAperture != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.label_effective_specs), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
                        entry.effectiveFocalLength?.let { efl ->
                            DetailItem(label = stringResource(R.string.label_effective_focal_length), value = "${efl.toInt()}mm")
                        }
                        entry.effectiveAperture?.let { ea ->
                            DetailItem(label = stringResource(R.string.label_effective_aperture), value = ea)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(text = stringResource(R.string.label_exposure), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    DetailItem(label = stringResource(R.string.label_shutter_speed), value = entry.exposureSettings.shutterSpeed)
                    DetailItem(label = stringResource(R.string.label_aperture), value = entry.exposureSettings.aperture)
                    DetailItem(label = stringResource(R.string.label_iso), value = entry.exposureSettings.iso.toString())

                    Spacer(modifier = Modifier.height(24.dp))

                    if (entryWithGear.tags.isNotEmpty()) {
                        Text(text = stringResource(R.string.label_tags), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                        ) {
                            entryWithGear.tags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tag.name) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (entry.environmentalConditions.isNotEmpty()) {
                        Text(text = stringResource(R.string.label_conditions), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                        Text(text = entry.environmentalConditions, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (entry.notes.isNotEmpty()) {
                        Text(text = stringResource(R.string.label_notes), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                        Text(text = entry.notes, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (entry.latitude != null && entry.longitude != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_location_on),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.label_location),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Card(
                            onClick = {
                                val gmmIntentUri = "geo:${entry.latitude},${entry.longitude}?q=${entry.latitude},${entry.longitude}".toUri()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.location_lat_long, entry.latitude, entry.longitude),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.ic_share), // Or a map icon if available
                                    contentDescription = "Open in Maps",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 16.dp))
                }
            } ?: run {
                Text(
                    stringResource(R.string.loading),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
