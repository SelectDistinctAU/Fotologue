package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.fotologue.app.data.*
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
class AddEditTeleconverterViewModelTest {

    private lateinit var viewModel: AddEditTeleconverterViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allCameraTeleconverterCrossRefs } returns flowOf(emptyList())
        viewModel = AddEditTeleconverterViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveTeleconverter calls repository with correct data`() = runTest {
        coEvery { repository.insertTeleconverter(any(), any()) } just Runs
        
        viewModel.onNameChange("Nikon TC-14E")
        viewModel.onMagnificationChange("1.4")
        viewModel.onLightLossStopsChange("1.0")
        
        var success = false
        viewModel.saveTeleconverter { success = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertTeleconverter(
                match { it.name == "Nikon TC-14E" && it.magnification == 1.4 && it.lightLossStops == 1.0 },
                any()
            ) 
        }
        assertEquals(true, success)
    }

    @Test
    fun `loadTeleconverter updates state`() = runTest {
        val tc = Teleconverter(id = 1L, name = "Canon 2x", magnification = 2.0, lightLossStops = 2.0)
        every { repository.getTeleconverterById(1L) } returns flowOf(tc)

        viewModel.setTeleconverterId(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Canon 2x", viewModel.uiState.value.name)
        assertEquals("2.0", viewModel.uiState.value.magnification)
        assertEquals("2.0", viewModel.uiState.value.lightLossStops)
    }
}
