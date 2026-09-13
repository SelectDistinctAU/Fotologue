package com.fotologue.app.ui.viewmodel

import com.fotologue.app.data.ConfigurationManager
import com.fotologue.app.data.MacroCopyNamingOptions
import com.fotologue.app.data.MacroCopyPreset
import com.fotologue.app.data.PictureExportSize
import com.fotologue.app.data.SettingsRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val repository: SettingsRepository = mockk()
    private val configurationManager: ConfigurationManager = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.isThumbnailEnabled } returns flowOf(false)
        every { repository.isFaceDetectionEnabled } returns flowOf(false)
        every { repository.faceDetectionDisclosureShown } returns flowOf(false)
        every { repository.pictureExportSize } returns flowOf(PictureExportSize.NATURAL)
        every { repository.evCalibration } returns flowOf(0.0f)
        every { repository.blackLevelOffset } returns flowOf(0.0f)
        every { repository.luminanceGain } returns flowOf(1.0f)
        every { repository.creatorName } returns flowOf("")
        every { repository.isAutoCopyrightEnabled } returns flowOf(false)
        every { repository.copyrightNotice } returns flowOf("")
        every { repository.contactInfo } returns flowOf("")
        every { repository.macroCopyPresets } returns flowOf(MacroCopyPreset.DEFAULT_PRESETS)
        
        viewModel = SettingsViewModel(repository, configurationManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleThumbnail calls repository`() = runTest {
        coEvery { repository.setThumbnailEnabled(any()) } just Runs
        
        viewModel.toggleThumbnail(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { repository.setThumbnailEnabled(true) }
    }

    @Test
    fun `setPictureExportSize calls repository`() = runTest {
        coEvery { repository.setPictureExportSize(any()) } just Runs
        
        viewModel.setPictureExportSize(PictureExportSize.SPACE_SAVER)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { repository.setPictureExportSize(PictureExportSize.SPACE_SAVER) }
    }

    @Test
    fun `setAutoCopyrightEnabled calls repository`() = runTest {
        coEvery { repository.setAutoCopyrightEnabled(any()) } just Runs
        
        viewModel.setAutoCopyrightEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { repository.setAutoCopyrightEnabled(true) }
    }

    @Test
    fun `updateCalibration calls repository`() = runTest {
        coEvery { repository.setEvCalibration(any()) } just Runs
        
        viewModel.updateCalibration(1.0f)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { repository.setEvCalibration(1.0f) }
    }

    @Test
    fun `saveMacroCopyPreset calls repository`() = runTest {
        coEvery { repository.saveMacroCopyPreset(any()) } just Runs

        val preset = MacroCopyPreset(name = "Test Setup", options = MacroCopyNamingOptions())
        viewModel.saveMacroCopyPreset(preset)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveMacroCopyPreset(preset) }
    }

    @Test
    fun `deleteMacroCopyPreset calls repository`() = runTest {
        coEvery { repository.deleteMacroCopyPreset(any()) } just Runs

        viewModel.deleteMacroCopyPreset("default_fujifilm")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteMacroCopyPreset("default_fujifilm") }
    }
}
