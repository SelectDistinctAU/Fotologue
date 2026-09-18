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
class AddEditFlashViewModelTest {

    private lateinit var viewModel: AddEditFlashViewModel
    private val repository: FotologueRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.allCameras } returns flowOf(emptyList())
        every { repository.allCameraFlashCrossRefs } returns flowOf(emptyList())
        viewModel = AddEditFlashViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveFlash calls repository with correct data`() = runTest {
        coEvery { repository.insertFlash(any(), any()) } just Runs
        
        viewModel.onMakeChange("Nikon")
        viewModel.onModelChange("SB-700")
        viewModel.onGuideNumberChange("28")
        
        var success = false
        viewModel.saveFlash { success = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertFlash(
                match { it.make == "Nikon" && it.model == "SB-700" && it.guideNumber == 28 },
                any()
            ) 
        }
        assertEquals(true, success)
    }
}
