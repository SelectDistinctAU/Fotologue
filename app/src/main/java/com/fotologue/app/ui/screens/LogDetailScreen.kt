package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fotologue.app.R
import com.fotologue.core.data.Film
import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.app.ui.viewmodel.LogDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailScreen(
    viewModel: LogDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    showBackButton: Boolean = true
) {
    val logWithGear by viewModel.entryWithGear.collectAsStateWithLifecycle()

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
                    logWithGear?.let { entry ->
                        IconButton(onClick = { onEdit(entry.logEntry.id) }) {
                            Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.action_edit))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        logWithGear?.let { entry ->
            LogDetailContent(
                entryWithGearData = entry,
                modifier = Modifier.padding(innerPadding)
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun LogDetailContent(
    entryWithGearData: LogEntryWithGear,
    modifier: Modifier = Modifier
) {
    val log = entryWithGearData.logEntry
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        log.exposureSettings.thumbnailUri?.let { uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        val locale = LocalConfiguration.current.locales[0]
        val dateFormat = remember(locale) { SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' HH:mm", locale) }
        Text(
            text = dateFormat.format(Date(log.timestamp)),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        DetailSection(title = stringResource(R.string.label_exposure)) {
            ExposureRow(
                label = stringResource(R.string.label_shutter_speed),
                value = log.exposureSettings.shutterSpeed
            )
            ExposureRow(
                label = stringResource(R.string.label_aperture),
                value = log.exposureSettings.aperture
            )
            ExposureRow(
                label = stringResource(R.string.label_iso),
                value = log.exposureSettings.iso.toString()
            )
            if (!log.filmRoll.isNullOrBlank()) {
                val expected = entryWithGearData.film?.expectedExposures
                val isHalf = entryWithGearData.camera?.format == Film.FORMAT_35MM_HALF || entryWithGearData.camera?.isHalfFrame == true
                val total = if (isHalf && (expected != null)) expected * 2 else expected
                
                val displayText = if (total != null) {
                    "${log.filmRoll ?: ""}/$total"
                } else {
                    log.filmRoll ?: ""
                }
                ExposureRow(
                    label = stringResource(R.string.label_film_roll),
                    value = displayText
                )
            }
        }

        if (log.effectiveFocalLength != null || log.effectiveAperture != null) {
            DetailSection(title = stringResource(R.string.label_effective_specs)) {
                log.effectiveFocalLength?.let {
                    ExposureRow(
                        label = stringResource(R.string.label_effective_focal_length),
                        value = "${it.toInt()}mm"
                    )
                }
                log.effectiveAperture?.let {
                    ExposureRow(
                        label = stringResource(R.string.label_effective_aperture),
                        value = it
                    )
                }
            }
        }

        DetailSection(title = "Equipment") {
            GearRow(
                label = stringResource(R.string.label_camera),
                value = entryWithGearData.camera?.name ?: stringResource(R.string.unknown_camera),
                iconRes = R.drawable.ic_photo_camera
            )
            GearRow(
                label = stringResource(R.string.label_lens),
                value = entryWithGearData.lens?.let { "${it.make} ${it.model}" } ?: stringResource(R.string.none),
                iconRes = R.drawable.lensd
            )
            GearRow(
                label = stringResource(R.string.label_film),
                value = entryWithGearData.film?.let { "${it.name} (ISO ${it.iso})" } ?: stringResource(R.string.unknown_film),
                iconRes = R.drawable.ic_camera_roll
            )
            entryWithGearData.teleconverter?.let {
                GearRow(
                    label = stringResource(R.string.label_teleconverter),
                    value = it.name,
                    iconRes = R.drawable.frame_inspect_24px
                )
            }
            entryWithGearData.filter?.let {
                GearRow(
                    label = stringResource(R.string.label_filter),
                    value = it.name,
                    iconRes = R.drawable.filter_24px
                )
            }
            entryWithGearData.flash?.let {
                GearRow(
                    label = stringResource(R.string.label_flash),
                    value = "${it.make} ${it.model}",
                    iconRes = R.drawable.ic_flash_on
                )
            }
            entryWithGearData.viewfinder?.let {
                GearRow(
                    label = "Viewfinder",
                    value = it.name,
                    iconRes = R.drawable.viewfinders_24px
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

        if (entryWithGearData.tags.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.label_tags)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entryWithGearData.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }
        }

        if (log.notes.isNotBlank()) {
            DetailSection(title = stringResource(R.string.label_notes)) {
                Text(text = log.notes, style = MaterialTheme.typography.bodyLarge)
            }
        }

        val latitude = log.latitude
        val longitude = log.longitude
        if (latitude != null && longitude != null) {
            DetailSection(title = stringResource(R.string.label_location)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_on),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.location_lat_long, latitude, longitude),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        val lastUpdatedFormat = remember(locale) { SimpleDateFormat("MMM dd, yyyy HH:mm", locale) }
        Text(
            text = stringResource(R.string.last_updated_at, lastUpdatedFormat.format(Date(log.lastUpdated))),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun ExposureRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun GearRow(label: String, value: String, iconRes: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
