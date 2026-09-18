package com.fotologue.app.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.fotologue.core.data.ExposureSettings
import com.fotologue.core.designsystem.FotologueTheme
import com.fotologue.app.ui.viewmodel.LightMeterUiState
import org.junit.Rule
import org.junit.Test

class LightMeterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lightMeter_showsEvAndIso() {
        val uiState = LightMeterUiState(
            currentEv = 12.5,
            suggestions = emptyList()
        )

        composeTestRule.setContent {
            val previewView = PreviewView(InstrumentationRegistry.getInstrumentation().targetContext)
            FotologueTheme {
                LightMeterScreenContent(
                    uiState = uiState,
                    isLandscape = false,
                    isThumbnailEnabled = false,
                    isCameraPermissionGranted = true,
                    shouldShowCameraPermissionRationale = false,
                    onCameraPermissionRequest = {},
                    previewView = previewView,
                    onBack = {},
                    onToggleMeteringMode = {},
                    onFocalLengthChanged = {},
                    onSpotLocationUpdated = { _, _, _, _ -> },
                    onTakeThumbnail = {},
                    onUseSettings = {},
                    targetIso = 400
                )
            }
        }

        composeTestRule.onNodeWithText("EV: 12.5").assertExists()
        composeTestRule.onNodeWithText("ISO 400", substring = true).assertExists()
    }

    @Test
    fun lightMeter_showsSuggestions() {
        val suggestion = ExposureSettings("1/250", "f/5.6", 400)
        val uiState = LightMeterUiState(
            currentEv = 12.5,
            suggestions = listOf(suggestion)
        )

        composeTestRule.setContent {
            val previewView = PreviewView(InstrumentationRegistry.getInstrumentation().targetContext)
            FotologueTheme {
                LightMeterScreenContent(
                    uiState = uiState,
                    isLandscape = false,
                    isThumbnailEnabled = false,
                    isCameraPermissionGranted = true,
                    shouldShowCameraPermissionRationale = false,
                    onCameraPermissionRequest = {},
                    previewView = previewView,
                    onBack = {},
                    onToggleMeteringMode = {},
                    onFocalLengthChanged = {},
                    onSpotLocationUpdated = { _, _, _, _ -> },
                    onTakeThumbnail = {},
                    onUseSettings = {},
                    targetIso = 400
                )
            }
        }

        composeTestRule.onNodeWithText("f/5.6").assertExists()
        composeTestRule.onNodeWithText("1/250").assertExists()
        composeTestRule.onNodeWithText("Use").assertExists()
    }
}
