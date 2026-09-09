package com.fotologue.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fotologue.app.ui.viewmodel.SettingsViewModel
import com.fotologue.app.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onNavigateToWizard: () -> Unit,
) {
    val calibration by viewModel.evCalibration.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ev_calibration_title)) },
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
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_exposure),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.exposure_compensation),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ev_calibration_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "${if (calibration > 0) "+" else ""}${String.format(Locale.US, "%.1f", calibration)} EV",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Slider(
                value = calibration,
                onValueChange = { viewModel.updateCalibration(it) },
                valueRange = -5f..5f,
                steps = 19, // 0.5 steps
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("-5.0", style = MaterialTheme.typography.labelSmall)
                Text("0.0", style = MaterialTheme.typography.labelSmall)
                Text("+5.0", style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNavigateToWizard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_handyman), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Launch Pro Calibration Wizard")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { viewModel.updateCalibration(0f) },
                modifier = Modifier.fillMaxWidth(),
                enabled = calibration != 0f
            ) {
                Text(stringResource(R.string.action_reset_zero))
            }
        }
    }
}
