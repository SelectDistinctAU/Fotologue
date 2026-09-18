package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.fotologue.core.data.*
import com.fotologue.app.util.LogExporter
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactSheetViewModelTest {

    private lateinit var viewModel: ContactSheetViewModel
    private val repository: FotologueRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val logExporter: LogExporter = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { repository.allLogEntriesWithGear } returns flowOf(emptyList())
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allFilms } returns flowOf(emptyList())

        viewModel = ContactSheetViewModel(repository, settingsRepository, logExporter, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parseRollAndFrame parses dotted roll and frame strings correctly`() {
        val (roll, frame) = ContactSheetViewModel.parseRollAndFrame("3.12")
        assertEquals(3, roll)
        assertEquals(12, frame)
    }

    @Test
    fun `toggleFrameSelection adds and removes selected IDs`() = runTest {
        viewModel.toggleFrameSelection(10L)
        assertEquals(setOf(10L), viewModel.uiState.value.selectedFrameIds)
        assertEquals(true, viewModel.uiState.value.isSelectionMode)

        viewModel.toggleFrameSelection(10L)
        assertEquals(emptySet<Long>(), viewModel.uiState.value.selectedFrameIds)
        assertEquals(false, viewModel.uiState.value.isSelectionMode)
    }

    @Test
    fun `clearSelection resets selection mode`() = runTest {
        viewModel.toggleFrameSelection(10L)
        viewModel.clearSelection()

        assertEquals(emptySet<Long>(), viewModel.uiState.value.selectedFrameIds)
        assertEquals(false, viewModel.uiState.value.isSelectionMode)
    }

    @Test
    fun `deleteSelected calls repository for selected IDs`() = runTest {
        coEvery { repository.deleteLogEntries(any()) } just Runs

        viewModel.toggleFrameSelection(100L)
        viewModel.deleteSelected()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteLogEntries(listOf(100L)) }
        assertEquals(emptySet<Long>(), viewModel.uiState.value.selectedFrameIds)
    }

    @Test
    fun `shiftFrameNumbers calls repository shift method`() = runTest {
        val mockLogEntry = LogEntry(
            id = 1L,
            timestamp = System.currentTimeMillis(),
            notes = "",
            environmentalConditions = "",
            cameraId = 1L,
            filmId = 2L,
            exposureSettings = ExposureSettings("1/125", "f/8", 100),
            filmRoll = "3.01"
        )
        val mockLogWithGear = LogEntryWithGear(
            logEntry = mockLogEntry,
            camera = Camera(id = 1L, name = "Cam", type = CameraType.FILM_SLR, format = "35mm"),
            film = Film(id = 2L, name = "Film", iso = 100, type = Film.TYPE_COLOR_NEGATIVE, format = "35mm", expectedExposures = 36),
            lens = null,
            teleconverter = null,
            filter = null,
            kit = null,
            flash = null,
            viewfinder = null,
            tags = emptyList()
        )

        every { repository.allLogEntriesWithGear } returns flowOf(listOf(mockLogWithGear))
        every { repository.allCameras } returns flowOf(listOf(mockLogWithGear.camera!!))
        every { repository.allFilms } returns flowOf(listOf(mockLogWithGear.film!!))
        coEvery { repository.shiftRollFrameNumbers(any(), any(), any(), any(), any()) } just Runs

        val testViewModel = ContactSheetViewModel(repository, settingsRepository, logExporter, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        testViewModel.shiftFrameNumbers(4, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.shiftRollFrameNumbers(1L, 2L, 3, 4, 1) }
    }
}
