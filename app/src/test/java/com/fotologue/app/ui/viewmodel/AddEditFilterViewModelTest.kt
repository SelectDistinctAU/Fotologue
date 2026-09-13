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
class AddEditFilterViewModelTest {

    private lateinit var viewModel: AddEditFilterViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allCameraFilterCrossRefs } returns flowOf(emptyList())
        viewModel = AddEditFilterViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveFilter calls repository with correct data`() = runTest {
        coEvery { repository.insertFilter(any(), any()) } just Runs
        
        viewModel.onNameChange("Hoya ND8")
        viewModel.onTypeChange("ND")
        viewModel.onCompensationStopsChange("3.0")
        
        var success = false
        viewModel.saveFilter { success = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertFilter(
                match { it.name == "Hoya ND8" && it.type == "ND" && it.compensationStops == 3.0 },
                any()
            ) 
        }
        assertEquals(true, success)
    }
}
