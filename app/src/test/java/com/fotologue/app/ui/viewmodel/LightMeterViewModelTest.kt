package com.fotologue.app.ui.viewmodel

import android.graphics.Rect
import com.fotologue.app.data.*
import com.fotologue.app.util.MeteringMode
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
class LightMeterViewModelTest {

    private lateinit var viewModel: LightMeterViewModel
    private val repository: FotologueRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { settingsRepository.isThumbnailEnabled } returns flowOf(false)
        every { settingsRepository.isFaceDetectionEnabled } returns flowOf(false)
        every { settingsRepository.faceDetectionDisclosureShown } returns flowOf(false)
        every { settingsRepository.evCalibration } returns flowOf(0.0f)
        every { settingsRepository.blackLevelOffset } returns flowOf(0.0f)
        every { settingsRepository.luminanceGain } returns flowOf(1.0f)
        
        viewModel = LightMeterViewModel(repository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleMeteringMode updates state`() {
        assertEquals(MeteringMode.CENTER_WEIGHTED, viewModel.uiState.value.meteringMode)
        
        viewModel.toggleMeteringMode()
        assertEquals(MeteringMode.SPOT, viewModel.uiState.value.meteringMode)
        
        viewModel.toggleMeteringMode()
        assertEquals(MeteringMode.CENTER_WEIGHTED, viewModel.uiState.value.meteringMode)
    }

    @Test
    fun `updateSpotLocation updates state`() {
        val viewWidth = 1000
        val viewHeight = 1000
        val x = 500
        val y = 500
        
        viewModel.updateSpotLocation(x, y, viewWidth, viewHeight)
        
        assert(viewModel.uiState.value.spotRect != null)
    }

    @Test
    fun `onEvCalculated updates state and generates suggestions`() {
        val camera = Camera(id = 1L, name = "Camera", type = CameraType.FILM_SLR)
        every { repository.getCameraById(1L) } returns flowOf(camera)
        
        viewModel.setParams(400, cameraId = 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvCalculated(12.0)
        
        assertEquals(12.0, viewModel.uiState.value.currentEv, 0.1)
        // Suggestions should be generated (ExposureSuggester logic is tested elsewhere)
        assert(viewModel.uiState.value.suggestions.isNotEmpty())
    }

    @Test
    fun `setParams loads data and updates state`() = runTest {
        val camera = Camera(id = 1L, name = "Camera", type = CameraType.FILM_SLR)
        val lens = Lens(id = 2L, make = "Make", model = "Model", type = "Prime", mount = "Mount", minAperture = 1.8, maxAperture = 22.0, minFocalLength = 50, maxFocalLength = 50, filterSize = 0, isStabilized = false, minFocusDistance = 0.0, maxMagnification = 0.0)
        
        every { repository.getCameraById(1L) } returns flowOf(camera)
        every { repository.getLensById(2L) } returns flowOf(lens)

        viewModel.setParams(400, cameraId = 1L, lensId = 2L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(50, viewModel.uiState.value.currentFocalLength)
        assertEquals(50.0, viewModel.uiState.value.effectiveFocalLength ?: 0.0, 0.1)
    }
}
