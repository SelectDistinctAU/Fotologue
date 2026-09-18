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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCameraViewModelTest {

    private lateinit var viewModel: AddEditCameraViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.allCameras } returns flowOf(emptyList())
        viewModel = AddEditCameraViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNameChange updates state and savedStateHandle`() = runTest {
        viewModel.onNameChange("Nikon")
        assertEquals("Nikon", viewModel.uiState.value.name)
        assertEquals("Nikon", savedStateHandle.get<String>("name"))
    }

    @Test
    fun `initial state restores data from savedStateHandle`() = runTest {
        val restoredHandle = SavedStateHandle(mapOf("name" to "Canon", "modelNumber" to "AE-1"))
        val restoredViewModel = AddEditCameraViewModel(repository, restoredHandle)
        
        assertEquals("Canon", restoredViewModel.uiState.value.name)
        assertEquals("AE-1", restoredViewModel.uiState.value.modelNumber)
    }

    @Test
    fun `onTypeChange updates state and sets mount for fixed lens`() = runTest {
        viewModel.onTypeChange(CameraType.COMPACT)
        assertEquals(CameraType.COMPACT, viewModel.uiState.value.type)
        assertEquals("Fixed", viewModel.uiState.value.mount)
    }

    @Test
    fun `saveCamera with blank name fails validation`() = runTest {
        viewModel.onNameChange("")
        viewModel.saveCamera {}
        assertFalse(viewModel.uiState.value.isNameValid)
        coVerify(exactly = 0) { repository.insertCamera(any()) }
    }

    @Test
    fun `saveCamera calls repository insert for new camera`() = runTest {
        coEvery { repository.insertCamera(any()) } returns 1L
        
        viewModel.onNameChange("Leica M6")
        viewModel.onTypeChange(CameraType.FILM_SLR)
        viewModel.onMountChange("Leica M")
        
        var completed = false
        viewModel.saveCamera { completed = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.insertCamera(match { it.name == "Leica M6" && it.mount == "Leica M" }) }
        assertEquals(true, completed)
    }

    @Test
    fun `loadCamera updates state with camera data`() = runTest {
        val camera = Camera(id = 1L, name = "Canon AE-1", type = CameraType.FILM_SLR, mount = "Canon FD")
        every { repository.getCameraById(1L) } returns flowOf(camera)

        viewModel.setCameraId(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Canon AE-1", viewModel.uiState.value.name)
        assertEquals("Canon FD", viewModel.uiState.value.mount)
    }
}
