package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.fotologue.core.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddLogViewModelTest {

    private lateinit var viewModel: AddLogViewModel
    private val repository: FotologueRepository = mockk()
    private val gearConfigurationManager: GearConfigurationManager = mockk()
    private var savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    private val camerasFlow = MutableStateFlow<List<Camera>>(emptyList())
    private val filmsFlow = MutableStateFlow<List<Film>>(emptyList())
    private val lensesFlow = MutableStateFlow<List<Lens>>(emptyList())
    private val crossRefsFlow = MutableStateFlow<List<CameraLensCrossRef>>(emptyList())
    private val nextFilmRollFlow = flowOf(1)
    private val meteringResultFlow = MutableStateFlow<ExposureSettings?>(null)
    private val newlyAddedLensIdFlow = MutableSharedFlow<Long>()
    private val cameraKitsWithGearFlow = MutableStateFlow<List<CameraKitWithGear>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { repository.allCameras } returns camerasFlow
        every { repository.allFilms } returns filmsFlow
        every { repository.allLenses } returns lensesFlow
        every { repository.allCameraLensCrossRefs } returns crossRefsFlow
        every { repository.allTags } returns flowOf(emptyList())
        every { repository.allTeleconverters } returns flowOf(emptyList())
        every { repository.allFilters } returns flowOf(emptyList())
        every { repository.allFlashes } returns flowOf(emptyList())
        every { repository.allCameraKits } returns flowOf(emptyList())
        every { repository.allCameraKitsWithGear } returns cameraKitsWithGearFlow
        every { repository.latestMeteringResult } returns meteringResultFlow
        every { repository.clearMeteringResult() } just Runs
        every { repository.allViewfinders } returns flowOf(emptyList())
        every { repository.allCameraViewfinderCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraTeleconverterCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraFilterCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraFlashCrossRefs } returns flowOf(emptyList())
        every { repository.getCameraById(any()) } returns flowOf(null)
        every { repository.getLensById(any()) } returns flowOf(null)
        every { repository.getSuggestedFilmRollFlow(any(), any()) } returns flowOf("1.01")
        every { repository.newlyAddedLensId } returns newlyAddedLensIdFlow
        coEvery { repository.insertDefaultTagsIfNeeded() } just Runs
        
        every { gearConfigurationManager.currentConfiguration } returns MutableStateFlow(GearConfiguration())
        every { gearConfigurationManager.updateConfiguration(any()) } just Runs
        
        every { repository.getFilmRollSession(any(), any()) } returns flowOf(null)
        coEvery { repository.updateFilmRollSession(any()) } just Runs

        viewModel = AddLogViewModel(repository, gearConfigurationManager, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default film roll`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("1.01", viewModel.uiState.value.filmRoll)
    }

    @Test
    fun `onCameraSelected updates shutter speeds`() = runTest {
        val camera = Camera(id = 1, name = "Nikon F3", type = CameraType.FILM_SLR, shutterSpeeds = listOf("1/2000", "1/1000"))
        every { repository.getCameraById(1L) } returns flowOf(camera)

        viewModel.onCameraSelected(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1L, viewModel.uiState.value.selectedCameraId)
        assertEquals(listOf("1/2000", "1/1000"), viewModel.uiState.value.availableShutterSpeeds)
    }

    @Test
    fun `onCameraSelected filters films based on format`() = runTest {
        val camera35 = Camera(id = 1, name = "Leica M3", type = CameraType.FILM_SLR, format = Film.FORMAT_35MM)
        val film35 = Film(id = 10, name = "HP5", iso = 400, type = "B&W", format = Film.FORMAT_35MM)
        val film120 = Film(id = 11, name = "HP5 120", iso = 400, type = "B&W", format = Film.FORMAT_120)
        
        camerasFlow.value = listOf(camera35)
        filmsFlow.value = listOf(film35, film120)
        
        every { repository.getCameraById(1L) } returns flowOf(camera35)
        
        viewModel.onCameraSelected(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val filteredFilms = viewModel.allFilms.value
        assertEquals(1, filteredFilms.size)
        assertEquals(Film.FORMAT_35MM, filteredFilms.first().format)
    }

    @Test
    fun `half frame camera format doubles expected exposures`() = runTest {
        val cameraHalf = Camera(id = 1, name = "Olympus Pen", type = CameraType.FILM_SLR, format = Film.FORMAT_35MM_HALF)
        val film35 = Film(id = 10, name = "HP5", iso = 400, type = "B&W", format = Film.FORMAT_35MM, expectedExposures = 36)
        
        camerasFlow.value = listOf(cameraHalf)
        filmsFlow.value = listOf(film35)
        
        every { repository.getCameraById(1L) } returns flowOf(cameraHalf)
        
        viewModel.onCameraSelected(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onFilmSelected(10L, 400)
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(72, viewModel.uiState.value.expectedExposures)
    }

    @Test
    fun `onNotesChange updates updates uiState`() = runTest {
        viewModel.onNotesChange("Test note")
        assertEquals("Test note", viewModel.uiState.value.notes)
    }

    @Test
    fun `saveLog calls repository with correct data`() = runTest {
        coEvery { repository.logExposure(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } just Runs
        val camera = Camera(id = 1, name = "Nikon F3", type = CameraType.FILM_SLR)
        every { repository.getCameraById(1L) } returns flowOf(camera)
        
        viewModel.onNotesChange("Sunset shot")
        viewModel.onCameraSelected(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onFilmSelected(2L, 400)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.saveLog()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.logExposure(
                notes = "Sunset shot",
                environmentalConditions = any(),
                cameraId = 1L,
                filmId = 2L,
                exposureSettings = any(),
                filmRoll = "1.01",
                tagIds = any(),
                effectiveAperture = "f/8"
            ) 
        }
    }

    @Test
    fun `onNotesChange updates savedStateHandle`() = runTest {
        viewModel.onNotesChange("Restorable note")
        assertEquals("Restorable note", savedStateHandle.get<String>("notes"))
    }

    @Test
    fun `initial state restores data from savedStateHandle`() = runTest {
        val restoredHandle = SavedStateHandle(mapOf("notes" to "Saved Note"))
        val restoredViewModel = AddLogViewModel(repository, gearConfigurationManager, restoredHandle)
        
        assertEquals("Saved Note", restoredViewModel.uiState.value.notes)
    }

    @Test
    fun `onCameraKitSelected updates state and persists gear`() = runTest {
        val kit = CameraKit(id = 1L, name = "My Kit", cameraId = 10L, lensId = 20L, filmId = 30L)
        val camera = Camera(id = 10L, name = "Camera", type = CameraType.FILM_SLR, shutterSpeeds = listOf("1/100"), mount = "Mount")
        val lens = Lens(id = 20L, make = "Make", model = "Model", type = "Prime", mount = "Mount", minAperture = 1.8, maxAperture = 22.0, minFocalLength = 50, maxFocalLength = 50, filterSize = 0, isStabilized = false, minFocusDistance = 0.0, maxMagnification = 0.0)
        val film = Film(id = 30L, name = "Film", iso = 400, type = Film.TYPE_COLOR_NEGATIVE)
        
        val kitWithGear = CameraKitWithGear(
            cameraKit = kit,
            camera = camera,
            lens = lens,
            film = film
        )
        
        cameraKitsWithGearFlow.value = listOf(kitWithGear)
        
        // Use a real state flow for the kits in the viewmodel by making it a property
        // But since it's already a property, we just need to ensure the collection happens.
        
        viewModel.onCameraKitSelected(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(10L, state.selectedCameraId)
        assertEquals(20L, state.selectedLensId)
        assertEquals(30L, state.selectedFilmId)
        assertEquals(400, state.iso)
        
        verify { gearConfigurationManager.updateConfiguration(match { 
            it.cameraId == 10L && it.lensId == 20L && it.filmId == 30L && it.kitId == 1L
        }) }
    }
}
