package com.fotologue.app.ui.viewmodel

import app.cash.turbine.test
import com.fotologue.app.data.*
import com.fotologue.app.util.LogExporter
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModelTest {

    private lateinit var viewModel: TimelineViewModel
    private val repository: FotologueRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val logExporter: LogExporter = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allLogEntriesWithGear } returns flowOf(emptyList())
        every { repository.isOnboardingComplete } returns flowOf(true)
        coEvery { repository.insertDefaultTagsIfNeeded() } just Runs
        
        viewModel = TimelineViewModel(repository, settingsRepository, logExporter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals(null, viewModel.selectedCameraId.value)
        assertEquals(emptyList<Camera>(), viewModel.allCameras.value)
        assertEquals(emptyList<LogEntryWithGear>(), viewModel.logEntries.value)
        assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
    }

    @Test
    fun `setCameraFilter updates selectedCameraId`() = runTest {
        viewModel.setCameraFilter(1L)
        assertEquals(1L, viewModel.selectedCameraId.value)
        
        viewModel.setCameraFilter(null)
        assertEquals(null, viewModel.selectedCameraId.value)
    }

    @Test
    fun `toggleSelection adds and removes IDs`() = runTest {
        viewModel.toggleSelection(1L)
        assertEquals(setOf(1L), viewModel.selectedIds.value)
        
        viewModel.toggleSelection(2L)
        assertEquals(setOf(1L, 2L), viewModel.selectedIds.value)
        
        viewModel.toggleSelection(1L)
        assertEquals(setOf(2L), viewModel.selectedIds.value)
    }

    @Test
    fun `clearSelection empties selectedIds`() = runTest {
        viewModel.toggleSelection(1L)
        viewModel.toggleSelection(2L)
        
        viewModel.clearSelection()
        assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
    }

    @Test
    fun `selectAll selects all visible log IDs`() = runTest {
        val logs = listOf(
            createLogEntryWithGear(1L),
            createLogEntryWithGear(2L)
        )
        val logsFlow = MutableStateFlow(logs)
        every { repository.allLogEntriesWithGear } returns logsFlow
        
        viewModel = TimelineViewModel(repository, settingsRepository, logExporter)
        
        viewModel.logEntries.test {
            // Depending on timing, we might get emptyList first or the logs immediately
            var currentLogs = awaitItem()
            if (currentLogs.isEmpty()) {
                currentLogs = awaitItem()
            }
            assertEquals(logs, currentLogs)
            
            viewModel.selectAll()
            assertEquals(setOf(1L, 2L), viewModel.selectedIds.value)
        }
    }

    @Test
    fun `deleteSelected calls repository and clears selection`() = runTest {
        coEvery { repository.deleteLogEntries(any()) } just Runs
        
        viewModel.toggleSelection(1L)
        viewModel.toggleSelection(2L)
        
        viewModel.deleteSelected()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { repository.deleteLogEntries(listOf(1L, 2L)) }
        assertEquals(emptySet<Long>(), viewModel.selectedIds.value)
    }

    private fun createLogEntryWithGear(id: Long): LogEntryWithGear {
        return LogEntryWithGear(
            logEntry = LogEntry(id = id, timestamp = 0, notes = "", environmentalConditions = "", cameraId = null, filmId = null, exposureSettings = ExposureSettings("", "", 0)),
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
