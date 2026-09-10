package com.fotologue.app.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.fotologue.app.data.*
import com.fotologue.core.designsystem.FotologueTheme
import org.junit.Rule
import org.junit.Test

class TimelineScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun timelineEmpty_showsEmptyMessage() {
        composeTestRule.setContent {
            FotologueTheme {
                TimelineScreenContent(
                    logs = emptyList(),
                    cameras = emptyList(),
                    selectedCameraId = null,
                    isOnboardingComplete = true,
                    selectedIds = emptySet(),
                    selectedLogId = null,
                    onAddLogClick = {},
                    onLogClick = {},
                    onGearLibraryClick = {},
                    onSettingsClick = {},
                    onCameraFilterClick = {},
                    onToggleSelection = {},
                    onClearSelection = {},
                    onSelectAll = {},
                    onDeleteSelected = {},
                    onExportLogs = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("No logs yet", substring = true).assertExists()
    }

    @Test
    fun selectionMode_showsSelectionCountAndActions() {
        val logs = listOf(createLogEntryWithGear(1L))
        
        composeTestRule.setContent {
            FotologueTheme {
                TimelineScreenContent(
                    logs = logs,
                    cameras = emptyList(),
                    selectedCameraId = null,
                    isOnboardingComplete = true,
                    selectedIds = setOf(1L),
                    selectedLogId = null,
                    onAddLogClick = {},
                    onLogClick = {},
                    onGearLibraryClick = {},
                    onSettingsClick = {},
                    onCameraFilterClick = {},
                    onToggleSelection = {},
                    onClearSelection = {},
                    onSelectAll = {},
                    onDeleteSelected = {},
                    onExportLogs = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("1 selected").assertExists()
        composeTestRule.onNodeWithContentDescription("Clear selection").assertExists()
        composeTestRule.onNodeWithContentDescription("Delete").assertExists()
    }

    private fun createLogEntryWithGear(id: Long): LogEntryWithGear {
        return LogEntryWithGear(
            logEntry = LogEntry(
                id = id, 
                timestamp = 0, 
                notes = "Test Note", 
                environmentalConditions = "", 
                cameraId = null, 
                filmId = null, 
                exposureSettings = ExposureSettings("1/125", "f/8", 400)
            ),
            camera = null,
            film = null,
            lens = null,
            teleconverter = null,
            filter = null,
            flash = null,
            kit = null
        )
    }
}
