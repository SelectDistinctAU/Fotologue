package com.fotologue.app.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fotologue.core.designsystem.FotologueTheme
import com.fotologue.app.ui.viewmodel.OnboardingUiState
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingSteps_showCorrectCompletionState() {
        // Given a state where camera is added but lens and film are not
        val uiState = OnboardingUiState(
            hasCamera = true,
            hasLens = false,
            hasFilm = false,
            isOnboardingComplete = false
        )

        composeTestRule.setContent {
            FotologueTheme {
                WelcomeScreenContent(
                    uiState = uiState,
                    onAddCamera = {},
                    onAddLens = {},
                    onAddFilm = {},
                    onShowHelp = {},
                    onRestoreBackup = {},
                    onToggleThumbnail = {},
                    onToggleFaceDetection = {},
                    onDismissDisclosure = {},
                    onSetExportSize = {}
                )
            }
        }

        // Check that "Add your first Camera" is marked as complete
        composeTestRule.onNodeWithText("Add your first Camera", substring = true).assertExists()
        composeTestRule.onNodeWithContentDescription("Complete").assertExists()
        
        // Check that others have "Add" icon
        composeTestRule.onAllNodesWithContentDescription("Add").assertCountEquals(2)
    }

    @Test
    fun clickingStep_triggersCallback() {
        val uiState = OnboardingUiState()
        var addCameraClicked = false
        
        composeTestRule.setContent {
            FotologueTheme {
                WelcomeScreenContent(
                    uiState = uiState,
                    onAddCamera = { addCameraClicked = true },
                    onAddLens = {},
                    onAddFilm = {},
                    onShowHelp = {},
                    onRestoreBackup = {},
                    onToggleThumbnail = {},
                    onToggleFaceDetection = {},
                    onDismissDisclosure = {},
                    onSetExportSize = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Add your first Camera", substring = true)
            .performScrollTo()
            .performClick()
        
        assert(addCameraClicked)
    }
}
