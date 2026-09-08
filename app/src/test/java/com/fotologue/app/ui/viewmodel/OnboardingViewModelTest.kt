package com.fotologue.app.ui.viewmodel

import android.content.ContentResolver
import android.net.Uri
import com.fotologue.app.data.ConfigurationManager
import com.fotologue.app.data.FotologueRepository
import com.fotologue.app.data.SettingsRepository
import com.fotologue.app.data.PictureExportSize
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.InputStream

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private lateinit var viewModel: OnboardingViewModel
    private val repository: FotologueRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val configurationManager: ConfigurationManager = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.hasCamera } returns flowOf(false)
        every { repository.hasLens } returns flowOf(false)
        every { repository.hasFilm } returns flowOf(false)
        every { repository.isOnboardingComplete } returns flowOf(false)
        every { settingsRepository.isThumbnailEnabled } returns flowOf(false)
        every { settingsRepository.isFaceDetectionEnabled } returns flowOf(false)
        every { settingsRepository.faceDetectionDisclosureShown } returns flowOf(false)
        every { settingsRepository.pictureExportSize } returns flowOf(PictureExportSize.NATURAL)
        coEvery { repository.insertDefaultTagsIfNeeded() } just Runs
        
        viewModel = OnboardingViewModel(repository, settingsRepository, configurationManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState updates when repository flows emit`() = runTest {
        every { repository.hasCamera } returns flowOf(true)
        every { repository.hasLens } returns flowOf(false)
        every { repository.hasFilm } returns flowOf(true)
        every { repository.isOnboardingComplete } returns flowOf(false)

        // Re-init to pick up the new flows
        viewModel = OnboardingViewModel(repository, settingsRepository, configurationManager)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(true, state.hasCamera)
        assertEquals(false, state.hasLens)
        assertEquals(true, state.hasFilm)
        assertEquals(false, state.isOnboardingComplete)
    }

    @Test
    fun `restoreBackup calls configurationManager`() = runTest {
        val uri = mockk<Uri>()
        val contentResolver = mockk<ContentResolver>()
        val inputStream = mockk<InputStream>()
        
        every { contentResolver.openInputStream(uri) } returns inputStream
        coEvery { configurationManager.importConfig(any()) } just Runs
        every { inputStream.close() } just Runs

        viewModel.restoreBackup(uri, contentResolver)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { configurationManager.importConfig(inputStream) }
    }
}
