package com.fotologue.app.data

import androidx.room.withTransaction
import com.fotologue.core.data.*
import com.fotologue.core.data.lab.LabProcessEventDao
import com.fotologue.core.data.lab.PerformanceLogDao
import com.fotologue.core.data.location.LocationProvider
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FotologueRepositoryTest {

    private lateinit var repository: FotologueRepository
    private val db: AppDatabase = mockk()
    private val cameraDao: CameraDao = mockk()
    private val filmDao: FilmDao = mockk()
    private val lensDao: LensDao = mockk()
    private val teleconverterDao: TeleconverterDao = mockk()
    private val filterDao: FilterDao = mockk()
    private val flashDao: FlashDao = mockk()
    private val logEntryDao: LogEntryDao = mockk()
    private val cameraKitDao: CameraKitDao = mockk()
    private val tagDao: TagDao = mockk()
    private val filmRollSessionDao: FilmRollSessionDao = mockk()
    private val viewfinderDao: ViewfinderDao = mockk<ViewfinderDao>().apply {
        every { getAllViewfinders() } returns flowOf(emptyList())
        every { getAllCameraViewfinderCrossRefs() } returns flowOf(emptyList())
    }
    private val locationProvider: LocationProvider = mockk()
    private val settingsRepository: SettingsRepository = mockk()

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")

        // Default behavior for withTransaction: just execute the block
        val slot = slot<suspend () -> Any?>()
        coEvery { db.withTransaction(capture(slot)) } coAnswers { slot.captured() }

        // Mock all flows that are initialized in the constructor
        every { cameraDao.getAllCameras() } returns flowOf(emptyList())
        every { filmDao.getAllFilms() } returns flowOf(emptyList())
        every { lensDao.getAllLenses() } returns flowOf(emptyList())
        every { teleconverterDao.getAllTeleconverters() } returns flowOf(emptyList())
        every { teleconverterDao.getAllCameraTeleconverterCrossRefs() } returns flowOf(emptyList())
        every { filterDao.getAllFilters() } returns flowOf(emptyList())
        every { filterDao.getAllCameraFilterCrossRefs() } returns flowOf(emptyList())
        every { flashDao.getAllFlashes() } returns flowOf(emptyList())
        every { flashDao.getAllCameraFlashCrossRefs() } returns flowOf(emptyList())
        every { logEntryDao.getAllLogEntries() } returns flowOf(emptyList())
        every { logEntryDao.getAllLogEntriesWithGear() } returns flowOf(emptyList())
        every { filmRollSessionDao.getAllSessions() } returns flowOf(emptyList())
        every { cameraKitDao.getAllCameraKits() } returns flowOf(emptyList())
        every { cameraKitDao.getAllCameraKitsWithGear() } returns flowOf(emptyList())
        every { tagDao.getAllTags() } returns flowOf(emptyList())
        every { lensDao.getAllCameraLensCrossRefs() } returns flowOf(emptyList())
        every { cameraDao.hasCameras() } returns flowOf(false)
        every { filmDao.hasFilms() } returns flowOf(false)
        every { lensDao.hasLenses() } returns flowOf(false)
        every { settingsRepository.isWelcomeDismissed } returns flowOf(false)
        every { settingsRepository.isSafelightActive } returns MutableStateFlow(false)

        val performanceLogDao = mockk<PerformanceLogDao>(relaxed = true)
        val labProcessEventDao = mockk<LabProcessEventDao>(relaxed = true)

        repository = FotologueRepository(
            db, cameraDao, filmDao, lensDao, teleconverterDao,
            filterDao, flashDao, logEntryDao, cameraKitDao,
            tagDao, filmRollSessionDao, viewfinderDao, performanceLogDao, labProcessEventDao, locationProvider, settingsRepository
        )
    }

    @Test
    fun `insertLens handles fixed lens camera correctly`() = runTest {
        val lens = Lens(
            id = 1, make = "Olympus", model = "35mm", type = "Compact", mount = "Fixed",
            minFocalLength = 35, maxFocalLength = 35, minAperture = 2.8, maxAperture = 22.0,
            filterSize = 0, isStabilized = false, minFocusDistance = 0.0, maxMagnification = 0.0
        )
        val camera = Camera(id = 10, name = "Olympus XA", type = CameraType.COMPACT)

        coEvery { lensDao.insertLens(any()) } returns 1L
        every { cameraDao.getCameraById(10) } returns flowOf(camera)
        coEvery { lensDao.deleteCameraLensCrossRefsForCamera(10) } just Runs
        coEvery { lensDao.insertCameraLensCrossRef(any()) } just Runs

        repository.insertLens(lens, listOf(10L))

        coVerify { lensDao.deleteCameraLensCrossRefsForCamera(10) }
        coVerify { lensDao.insertCameraLensCrossRef(match { it.cameraId == 10L && it.lensId == 1L }) }
    }
}
