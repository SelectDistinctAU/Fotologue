package com.fotologue.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DaoTests {
    private lateinit var cameraDao: CameraDao
    private lateinit var lensDao: LensDao
    private lateinit var filmDao: FilmDao
    private lateinit var logEntryDao: LogEntryDao
    private lateinit var tagDao: TagDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        cameraDao = db.cameraDao()
        lensDao = db.lensDao()
        filmDao = db.filmDao()
        logEntryDao = db.logEntryDao()
        tagDao = db.tagDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadCamera() = runTest {
        val camera = Camera(id = 1L, name = "Nikon F3", type = CameraType.FILM_SLR)
        cameraDao.insertCamera(camera)
        val allCameras = cameraDao.getAllCameras().first()
        assertEquals(allCameras[0].name, camera.name)
    }

    @Test
    @Throws(Exception::class)
    fun lensCameraRelationship() = runTest {
        val camera = Camera(id = 1L, name = "Nikon F3", type = CameraType.FILM_SLR)
        val lens = Lens(
            id = 1L, make = "Nikon", model = "50mm f/1.8", type = "Prime", mount = "Nikon F",
            minFocalLength = 50, maxFocalLength = 50, minAperture = 1.8, maxAperture = 22.0,
            filterSize = 52, isStabilized = false, minFocusDistance = 0.45, maxMagnification = 0.15
        )
        
        cameraDao.insertCamera(camera)
        lensDao.insertLens(lens)
        
        val crossRef = CameraLensCrossRef(cameraId = 1L, lensId = 1L)
        lensDao.insertCameraLensCrossRef(crossRef)
        
        val lensesForCamera = lensDao.getLensesForCamera(1L).first()
        assertEquals(1, lensesForCamera.size)
        assertEquals("50mm f/1.8", lensesForCamera[0].model)
        
        val camerasForLens = lensDao.getCamerasForLens(1L).first()
        assertEquals(1, camerasForLens.size)
        assertEquals("Nikon F3", camerasForLens[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun logEntryWithTagsRelationship() = runTest {
        val logEntry = LogEntry(
            id = 1L, timestamp = System.currentTimeMillis(), notes = "Test Log", 
            environmentalConditions = "Sunny", cameraId = null, filmId = null,
            exposureSettings = ExposureSettings("1/125", "f/8", 400)
        )
        val tag = Tag(id = 1L, name = "Landscape")
        
        logEntryDao.insertLogEntry(logEntry)
        tagDao.insertTag(tag)
        tagDao.insertLogEntryTagCrossRef(LogEntryTagCrossRef(1L, 1L))
        
        val logWithTags = tagDao.getTagsForLogEntry(1L).first()
        assertEquals("Test Log", logWithTags?.logEntry?.notes)
        assertEquals(1, logWithTags?.tags?.size)
        assertEquals("Landscape", logWithTags?.tags?.get(0)?.name)
    }

    @Test
    @Throws(Exception::class)
    fun logEntryWithGearRelationship() = runTest {
        val camera = Camera(id = 1L, name = "Leica M6", type = CameraType.FILM_SLR)
        val film = Film(id = 1L, name = "Kodak Portra 400", iso = 400, type = Film.TYPE_COLOR_NEGATIVE)
        val logEntry = LogEntry(
            id = 1L, timestamp = System.currentTimeMillis(), notes = "Street", 
            environmentalConditions = "", cameraId = 1L, filmId = 1L,
            exposureSettings = ExposureSettings("1/500", "f/2", 400)
        )
        
        cameraDao.insertCamera(camera)
        filmDao.insertFilm(film)
        logEntryDao.insertLogEntry(logEntry)
        
        val logWithGear = logEntryDao.getLogEntryWithGearById(1L).first()
        assertEquals("Leica M6", logWithGear?.camera?.name)
        assertEquals("Kodak Portra 400", logWithGear?.film?.name)
    }
}
