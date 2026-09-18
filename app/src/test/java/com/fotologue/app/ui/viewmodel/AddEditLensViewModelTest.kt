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
class AddEditLensViewModelTest {

    private lateinit var viewModel: AddEditLensViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allCameraLensCrossRefs } returns flowOf(emptyList())
        
        viewModel = AddEditLensViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onCameraToggled updates selection and type`() = runTest {
        val camera = Camera(id = 1L, name = "Nikon F3", type = CameraType.FILM_SLR)
        
        every { repository.allCameras } returns flowOf(listOf(camera))
        viewModel = AddEditLensViewModel(repository, SavedStateHandle())
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCameraToggled(1L)
        assertEquals(setOf(1L), viewModel.uiState.value.selectedCameraIds)
        assertEquals(true, viewModel.uiState.value.isInterchangeable)
    }

    @Test
    fun `onCameraToggled with fixed lens camera clears other selections`() = runTest {
        val compact = Camera(id = 1L, name = "Olympus XA", type = CameraType.COMPACT)
        val slr = Camera(id = 2L, name = "Nikon F3", type = CameraType.FILM_SLR)
        
        every { repository.allCameras } returns flowOf(listOf(compact, slr))
        viewModel = AddEditLensViewModel(repository, SavedStateHandle())
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCameraToggled(2L) // Select SLR
        assertEquals(setOf(2L), viewModel.uiState.value.selectedCameraIds)

        viewModel.onCameraToggled(1L) // Select Compact
        assertEquals(setOf(1L), viewModel.uiState.value.selectedCameraIds)
        assertEquals("Fixed", viewModel.uiState.value.mount)
    }

    @Test
    fun `saveLens with no camera selected fails validation if cameras exist`() = runTest {
        val camera = Camera(id = 1L, name = "Nikon F3", type = CameraType.FILM_SLR)
        every { repository.allCameras } returns flowOf(listOf(camera))
        
        viewModel = AddEditLensViewModel(repository, SavedStateHandle())
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false
        viewModel.saveLens { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isCameraSelectionValid)
        assertEquals(false, successCalled)
        coVerify(exactly = 0) { repository.insertLens(any(), any()) }
    }

    @Test
    fun `saveLens calls repository with correct data`() = runTest {
        coEvery { repository.insertLens(any(), any()) } just Runs
        
        viewModel.onMakeChange("Nikon")
        viewModel.onModelChange("50mm f/1.8")
        viewModel.onMinFocalLengthChange("50")
        viewModel.onTypeChange("Prime")
        
        viewModel.saveLens {}
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertLens(
                match { it.make == "Nikon" && it.minFocalLength == 50 },
                any()
            ) 
        }
    }
}
