package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.fotologue.app.ui.viewmodel.SettingsViewModel
import com.fotologue.app.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNavigateToWizard: () -> Unit
) {
    val calibration by viewModel.evCalibration.collectAsStateWithLifecycle()
    val blackOffset by viewModel.blackLevelOffset.collectAsStateWithLifecycle()
    val gain by viewModel.luminanceGain.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ev_calibration_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_back))
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Manual Offset",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Fine-tune the meter to match your external gear.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "${if (calibration > 0) "+" else ""}${String.format("%.1f", calibration)} EV",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Slider(
                value = calibration,
                onValueChange = { viewModel.updateCalibration(it) },
                valueRange = -3.0f..3.0f,
                steps = 19,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("-3.0", style = MaterialTheme.typography.labelSmall)
                Text("0.0", style = MaterialTheme.typography.labelSmall)
                Text("+3.0", style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { viewModel.updateCalibration(0.0f) },
                enabled = calibration != 0.0f
            ) {
                Text(stringResource(R.string.action_reset_zero))
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Hardware Baseline",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Measured sensor characteristics from the Pro Wizard.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Noise Floor (Black)", style = MaterialTheme.typography.bodyMedium)
                        Text("%.2f units".format(blackOffset), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Signal Gain (Sensitivity)", style = MaterialTheme.typography.bodyMedium)
                        Text("%.2fx".format(gain), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = onNavigateToWizard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_handyman), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Launch Pro Calibration Wizard")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.resetHardwareCalibration() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_replay), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Hardware Calibration")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
