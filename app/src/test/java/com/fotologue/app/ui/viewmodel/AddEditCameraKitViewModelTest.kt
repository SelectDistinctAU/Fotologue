package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.fotologue.core.data.*
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
class AddEditCameraKitViewModelTest {

    private lateinit var viewModel: AddEditCameraKitViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allLenses } returns flowOf(emptyList())
        every { repository.allCameraLensCrossRefs } returns flowOf(emptyList())
        every { repository.allFilms } returns flowOf(emptyList())
        every { repository.allTeleconverters } returns flowOf(emptyList())
        every { repository.allFilters } returns flowOf(emptyList())
        every { repository.allFlashes } returns flowOf(emptyList())
        every { repository.allViewfinders } returns flowOf(emptyList())
        every { repository.allCameraViewfinderCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraTeleconverterCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraFilterCrossRefs } returns flowOf(emptyList())
        every { repository.allCameraFlashCrossRefs } returns flowOf(emptyList())
        
        viewModel = AddEditCameraKitViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNameChange updates state`() = runTest {
        viewModel.onNameChange("Street Kit")
        assertEquals("Street Kit", viewModel.uiState.value.name)
    }

    @Test
    fun `saveKit calls repository with correct data`() = runTest {
        coEvery { repository.insertCameraKit(any()) } returns 1L
        
        viewModel.onNameChange("Street Kit")
        viewModel.onCameraSelected(1L)
        viewModel.onLensSelected(2L)
        viewModel.onFilmSelected(3L)
        
        var completed = false
        viewModel.saveKit { completed = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertCameraKit(
                match { it.name == "Street Kit" && it.cameraId == 1L && it.lensId == 2L && it.filmId == 3L }
            ) 
        }
        assertEquals(true, completed)
    }
}
