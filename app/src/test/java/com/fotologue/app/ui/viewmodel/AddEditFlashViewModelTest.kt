package com.fotologue.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.fotologue.app.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        viewModel = AddEditFlashViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveFlash calls repository with correct data`() = runTest {
        coEvery { repository.insertFlash(any()) } returns 1L
        
        viewModel.onMakeChange("Nikon")
        viewModel.onModelChange("SB-700")
        viewModel.onGuideNumberChange("28")
        
        var success = false
        viewModel.saveFlash { success = true }
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            repository.insertFlash(
                match { it.make == "Nikon" && it.model == "SB-700" && it.guideNumber == 28 }
            ) 
        }
        assertEquals(true, success)
    }
}
