package com.fotologue.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao {
    @Query("SELECT * FROM cameras ORDER BY position ASC, id ASC")
    fun getAllCameras(): Flow<List<Camera>>

    @Query("SELECT * FROM cameras ORDER BY position ASC, id ASC")
    suspend fun getAllCamerasOnce(): List<Camera>

    @Insert
    suspend fun insertCamera(camera: Camera): Long

    @Update
    suspend fun updateCamera(camera: Camera)

    @Update
    suspend fun updateCameras(cameras: List<Camera>)

    @Delete
    suspend fun deleteCamera(camera: Camera)

    @Query("SELECT * FROM cameras WHERE id = :id")
    fun getCameraById(id: Long): Flow<Camera?>

    @Query("SELECT COUNT(*) > 0 FROM cameras")
    fun hasCameras(): Flow<Boolean>

    @Query("DELETE FROM cameras WHERE id IN (:ids)")
    suspend fun deleteCameras(ids: List<Long>)

    @Query("DELETE FROM cameras")
    suspend fun deleteAllCameras()
}

@Dao
interface FilmDao {
    @Query("SELECT * FROM films ORDER BY position ASC, id ASC")
    fun getAllFilms(): Flow<List<Film>>

    @Query("SELECT * FROM films ORDER BY position ASC, id ASC")
    suspend fun getAllFilmsOnce(): List<Film>

    @Insert
    suspend fun insertFilm(film: Film): Long

    @Update
    suspend fun updateFilm(film: Film)

    @Update
    suspend fun updateFilms(films: List<Film>)

    @Delete
    suspend fun deleteFilm(film: Film)

    @Query("SELECT * FROM films WHERE id = :id")
    fun getFilmById(id: Long): Flow<Film?>

    @Query("SELECT COUNT(*) > 0 FROM films")
    fun hasFilms(): Flow<Boolean>

    @Query("DELETE FROM films WHERE id IN (:ids)")
    suspend fun deleteFilms(ids: List<Long>)

    @Query("DELETE FROM films")
    suspend fun deleteAllFilms()
}

@Dao
interface ViewfinderDao {
    @Query("SELECT * FROM viewfinders ORDER BY position ASC, id ASC")
    fun getAllViewfinders(): Flow<List<Viewfinder>>

    @Query("SELECT * FROM viewfinders ORDER BY position ASC, id ASC")
    suspend fun getAllViewfindersOnce(): List<Viewfinder>

    @Insert
    suspend fun insertViewfinder(viewfinder: Viewfinder): Long

    @Update
    suspend fun updateViewfinder(viewfinder: Viewfinder)

    @Update
    suspend fun updateViewfinders(viewfinders: List<Viewfinder>)

    @Delete
    suspend fun deleteViewfinder(viewfinder: Viewfinder)

    @Query("SELECT * FROM viewfinders WHERE id = :id")
    fun getViewfinderById(id: Long): Flow<Viewfinder?>

    @Query("SELECT viewfinders.* FROM viewfinders INNER JOIN camera_viewfinder_cross_ref ON viewfinders.id = camera_viewfinder_cross_ref.viewfinderId WHERE camera_viewfinder_cross_ref.cameraId = :cameraId ORDER BY position ASC, id ASC")
    fun getViewfindersForCamera(cameraId: Long): Flow<List<Viewfinder>>

    @Query("SELECT viewfinders.* FROM viewfinders INNER JOIN camera_viewfinder_cross_ref ON viewfinders.id = camera_viewfinder_cross_ref.viewfinderId WHERE camera_viewfinder_cross_ref.cameraId = :cameraId ORDER BY position ASC, id ASC")
    suspend fun getViewfindersForCameraOnce(cameraId: Long): List<Viewfinder>

    @Query("SELECT cameras.* FROM cameras INNER JOIN camera_viewfinder_cross_ref ON cameras.id = camera_viewfinder_cross_ref.cameraId WHERE camera_viewfinder_cross_ref.viewfinderId = :viewfinderId ORDER BY position ASC, id ASC")
    fun getCamerasForViewfinder(viewfinderId: Long): Flow<List<Camera>>

    @Query("SELECT cameras.* FROM cameras INNER JOIN camera_viewfinder_cross_ref ON cameras.id = camera_viewfinder_cross_ref.cameraId WHERE camera_viewfinder_cross_ref.viewfinderId = :viewfinderId ORDER BY position ASC, id ASC")
    suspend fun getCamerasForViewfinderOnce(viewfinderId: Long): List<Camera>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraViewfinderCrossRef(crossRef: CameraViewfinderCrossRef)

    @Query("SELECT * FROM camera_viewfinder_cross_ref")
    fun getAllCameraViewfinderCrossRefs(): Flow<List<CameraViewfinderCrossRef>>

    @Query("SELECT * FROM camera_viewfinder_cross_ref")
    suspend fun getAllCameraViewfinderCrossRefsOnce(): List<CameraViewfinderCrossRef>

    @Query("DELETE FROM camera_viewfinder_cross_ref WHERE viewfinderId = :viewfinderId")
    suspend fun deleteCameraViewfinderCrossRefsForViewfinder(viewfinderId: Long)

    @Query("DELETE FROM camera_viewfinder_cross_ref WHERE cameraId = :cameraId")
    suspend fun deleteCameraViewfinderCrossRefsForCamera(cameraId: Long)

    @Query("DELETE FROM camera_viewfinder_cross_ref")
    suspend fun deleteAllCameraViewfinderCrossRefs()

    @Query("DELETE FROM viewfinders WHERE id IN (:ids)")
    suspend fun deleteViewfinders(ids: List<Long>)

    @Query("DELETE FROM viewfinders")
    suspend fun deleteAllViewfinders()
}

@Dao
interface LogEntryDao {
    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    fun getAllLogEntries(): Flow<List<LogEntry>>

    @Transaction
    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    fun getAllLogEntriesWithGear(): Flow<List<LogEntryWithGear>>

    @Query("SELECT * FROM log_entries")
    suspend fun getAllLogEntriesOnce(): List<LogEntry>

    @Query("SELECT * FROM log_entries WHERE id = :id")
    fun getLogEntryById(id: Long): Flow<LogEntry?>

    @Transaction
    @Query("SELECT * FROM log_entries WHERE id = :id")
    fun getLogEntryWithGearById(id: Long): Flow<LogEntryWithGear?>

    @Insert
    suspend fun insertLogEntry(logEntry: LogEntry): Long

    @Update
    suspend fun updateLogEntry(logEntry: LogEntry)

    @Delete
    suspend fun deleteLogEntry(logEntry: LogEntry)

    @Query("DELETE FROM log_entries WHERE id IN (:ids)")
    suspend fun deleteLogEntries(ids: List<Long>)

    @Query("DELETE FROM log_entries")
    suspend fun deleteAllLogEntries()

    @Query("SELECT filmRoll FROM log_entries WHERE cameraId = :cameraId AND filmId = :filmId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestFilmRoll(cameraId: Long, filmId: Long): String?

    @Query("UPDATE log_entries SET isExportedZip = 1 WHERE id IN (:ids)")
    suspend fun markAsExportedZip(ids: List<Long>)

    @Query("UPDATE log_entries SET isExportedCsv = 1 WHERE id IN (:ids)")
    suspend fun markAsExportedCsv(ids: List<Long>)

    @Query("UPDATE log_entries SET isExportedJson = 1 WHERE id IN (:ids)")
    suspend fun markAsExportedJson(ids: List<Long>)

    @Query("UPDATE log_entries SET isExportedXmp = 1 WHERE id IN (:ids)")
    suspend fun markAsExportedXmp(ids: List<Long>)
}

@Dao
interface LensDao {
    @Query("SELECT * FROM lenses ORDER BY position ASC, id ASC")
    fun getAllLenses(): Flow<List<Lens>>

    @Query("SELECT * FROM lenses ORDER BY position ASC, id ASC")
    suspend fun getAllLensesOnce(): List<Lens>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLens(lens: Lens): Long

    @Update
    suspend fun updateLens(lens: Lens)

    @Update
    suspend fun updateLenses(lenses: List<Lens>)

    @Delete
    suspend fun deleteLens(lens: Lens)

    @Query("SELECT * FROM lenses WHERE id = :id")
    fun getLensById(id: Long): Flow<Lens?>

    @Query("SELECT lenses.* FROM lenses INNER JOIN camera_lens_cross_ref ON lenses.id = camera_lens_cross_ref.lensId WHERE camera_lens_cross_ref.cameraId = :cameraId ORDER BY position ASC, id ASC")
    fun getLensesForCamera(cameraId: Long): Flow<List<Lens>>

    @Query("SELECT lenses.* FROM lenses INNER JOIN camera_lens_cross_ref ON lenses.id = camera_lens_cross_ref.lensId WHERE camera_lens_cross_ref.cameraId = :cameraId ORDER BY position ASC, id ASC")
    suspend fun getLensesForCameraOnce(cameraId: Long): List<Lens>

    @Query("SELECT cameras.* FROM cameras INNER JOIN camera_lens_cross_ref ON cameras.id = camera_lens_cross_ref.cameraId WHERE camera_lens_cross_ref.lensId = :lensId ORDER BY position ASC, id ASC")
    fun getCamerasForLens(lensId: Long): Flow<List<Camera>>

    @Query("SELECT cameras.* FROM cameras INNER JOIN camera_lens_cross_ref ON cameras.id = camera_lens_cross_ref.cameraId WHERE camera_lens_cross_ref.lensId = :lensId ORDER BY position ASC, id ASC")
    suspend fun getCamerasForLensOnce(lensId: Long): List<Camera>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraLensCrossRef(crossRef: CameraLensCrossRef)

    @Query("SELECT * FROM camera_lens_cross_ref")
    fun getAllCameraLensCrossRefs(): Flow<List<CameraLensCrossRef>>

    @Query("SELECT * FROM camera_lens_cross_ref")
    suspend fun getAllCameraLensCrossRefsOnce(): List<CameraLensCrossRef>

    @Query("DELETE FROM camera_lens_cross_ref WHERE lensId = :lensId")
    suspend fun deleteCameraLensCrossRefsForLens(lensId: Long)

    @Query("DELETE FROM camera_lens_cross_ref WHERE cameraId = :cameraId")
    suspend fun deleteCameraLensCrossRefsForCamera(cameraId: Long)

    @Query("SELECT COUNT(*) > 0 FROM lenses")
    fun hasLenses(): Flow<Boolean>

    @Query("DELETE FROM lenses WHERE id IN (:ids)")
    suspend fun deleteLenses(ids: List<Long>)

    @Query("DELETE FROM camera_lens_cross_ref")
    suspend fun deleteAllCameraLensCrossRefs()

    @Query("DELETE FROM lenses")
    suspend fun deleteAllLenses()
}

@Dao
interface TeleconverterDao {
    @Query("SELECT * FROM teleconverters ORDER BY position ASC, id ASC")
    fun getAllTeleconverters(): Flow<List<Teleconverter>>

    @Query("SELECT * FROM teleconverters ORDER BY position ASC, id ASC")
    suspend fun getAllTeleconvertersOnce(): List<Teleconverter>

    @Insert
    suspend fun insertTeleconverter(teleconverter: Teleconverter): Long

    @Update
    suspend fun updateTeleconverter(teleconverter: Teleconverter)

    @Update
    suspend fun updateTeleconverters(tcs: List<Teleconverter>)

    @Delete
    suspend fun deleteTeleconverter(teleconverter: Teleconverter)

    @Query("SELECT * FROM teleconverters WHERE id = :id")
    fun getTeleconverterById(id: Long): Flow<Teleconverter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraTeleconverterCrossRef(crossRef: CameraTeleconverterCrossRef)

    @Query("SELECT * FROM camera_teleconverter_cross_ref")
    fun getAllCameraTeleconverterCrossRefs(): Flow<List<CameraTeleconverterCrossRef>>

    @Query("SELECT * FROM camera_teleconverter_cross_ref")
    suspend fun getAllCameraTeleconverterCrossRefsOnce(): List<CameraTeleconverterCrossRef>

    @Query("DELETE FROM camera_teleconverter_cross_ref WHERE teleconverterId = :teleconverterId")
    suspend fun deleteCameraTeleconverterCrossRefsForTeleconverter(teleconverterId: Long)

    @Query("DELETE FROM camera_teleconverter_cross_ref WHERE cameraId = :cameraId")
    suspend fun deleteCameraTeleconverterCrossRefsForCamera(cameraId: Long)

    @Query("DELETE FROM camera_teleconverter_cross_ref")
    suspend fun deleteAllCameraTeleconverterCrossRefs()

    @Query("DELETE FROM teleconverters WHERE id IN (:ids)")
    suspend fun deleteTeleconverters(ids: List<Long>)

    @Query("DELETE FROM teleconverters")
    suspend fun deleteAllTeleconverters()
}

@Dao
interface FilterDao {
    @Query("SELECT * FROM filters ORDER BY position ASC, id ASC")
    fun getAllFilters(): Flow<List<Filter>>

    @Query("SELECT * FROM filters ORDER BY position ASC, id ASC")
    suspend fun getAllFiltersOnce(): List<Filter>

    @Insert
    suspend fun insertFilter(filter: Filter): Long

    @Update
    suspend fun updateFilter(filter: Filter)

    @Update
    suspend fun updateFilters(filters: List<Filter>)

    @Delete
    suspend fun deleteFilter(filter: Filter)

    @Query("SELECT * FROM filters WHERE id = :id")
    fun getFilterById(id: Long): Flow<Filter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraFilterCrossRef(crossRef: CameraFilterCrossRef)

    @Query("SELECT * FROM camera_filter_cross_ref")
    fun getAllCameraFilterCrossRefs(): Flow<List<CameraFilterCrossRef>>

    @Query("SELECT * FROM camera_filter_cross_ref")
    suspend fun getAllCameraFilterCrossRefsOnce(): List<CameraFilterCrossRef>

    @Query("DELETE FROM camera_filter_cross_ref WHERE filterId = :filterId")
    suspend fun deleteCameraFilterCrossRefsForFilter(filterId: Long)

    @Query("DELETE FROM camera_filter_cross_ref WHERE cameraId = :cameraId")
    suspend fun deleteCameraFilterCrossRefsForCamera(cameraId: Long)

    @Query("DELETE FROM camera_filter_cross_ref")
    suspend fun deleteAllCameraFilterCrossRefs()

    @Query("DELETE FROM filters WHERE id IN (:ids)")
    suspend fun deleteFilters(ids: List<Long>)

    @Query("DELETE FROM filters")
    suspend fun deleteAllFilters()
}

@Dao
interface FlashDao {
    @Query("SELECT * FROM flashes ORDER BY position ASC, id ASC")
    fun getAllFlashes(): Flow<List<Flash>>

    @Query("SELECT * FROM flashes ORDER BY position ASC, id ASC")
    suspend fun getAllFlashesOnce(): List<Flash>

    @Insert
    suspend fun insertFlash(flash: Flash): Long

    @Update
    suspend fun updateFlash(flash: Flash)

    @Update
    suspend fun updateFlashes(flashes: List<Flash>)

    @Delete
    suspend fun deleteFlash(flash: Flash)

    @Query("SELECT * FROM flashes WHERE id = :id")
    fun getFlashById(id: Long): Flow<Flash?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraFlashCrossRef(crossRef: CameraFlashCrossRef)

    @Query("SELECT * FROM camera_flash_cross_ref")
    fun getAllCameraFlashCrossRefs(): Flow<List<CameraFlashCrossRef>>

    @Query("SELECT * FROM camera_flash_cross_ref")
    suspend fun getAllCameraFlashCrossRefsOnce(): List<CameraFlashCrossRef>

    @Query("DELETE FROM camera_flash_cross_ref WHERE flashId = :flashId")
    suspend fun deleteCameraFlashCrossRefsForFlash(flashId: Long)

    @Query("DELETE FROM camera_flash_cross_ref WHERE cameraId = :cameraId")
    suspend fun deleteCameraFlashCrossRefsForCamera(cameraId: Long)

    @Query("DELETE FROM camera_flash_cross_ref")
    suspend fun deleteAllCameraFlashCrossRefs()

    @Query("DELETE FROM flashes WHERE id IN (:ids)")
    suspend fun deleteFlashes(ids: List<Long>)

    @Query("DELETE FROM flashes")
    suspend fun deleteAllFlashes()
}

@Dao
interface CameraKitDao {
    @Query("SELECT * FROM camera_kits ORDER BY position ASC, id ASC")
    fun getAllCameraKits(): Flow<List<CameraKit>>

    @Transaction
    @Query("SELECT * FROM camera_kits ORDER BY position ASC, id ASC")
    fun getAllCameraKitsWithGear(): Flow<List<CameraKitWithGear>>

    @Query("SELECT * FROM camera_kits ORDER BY position ASC, id ASC")
    suspend fun getAllCameraKitsOnce(): List<CameraKit>

    @Insert
    suspend fun insertCameraKit(cameraKit: CameraKit): Long

    @Update
    suspend fun updateCameraKit(cameraKit: CameraKit)

    @Update
    suspend fun updateCameraKits(kits: List<CameraKit>)

    @Delete
    suspend fun deleteCameraKit(cameraKit: CameraKit)

    @Query("SELECT * FROM camera_kits WHERE id = :id")
    fun getCameraKitById(id: Long): Flow<CameraKit?>

    @Query("DELETE FROM camera_kits WHERE id IN (:ids)")
    suspend fun deleteCameraKits(ids: List<Long>)

    @Query("DELETE FROM camera_kits")
    suspend fun deleteAllCameraKits()
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY position ASC, id ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tags ORDER BY position ASC, id ASC")
    suspend fun getAllTagsOnce(): List<Tag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Update
    suspend fun updateTags(tags: List<Tag>)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("DELETE FROM tags WHERE id IN (:ids)")
    suspend fun deleteTags(ids: List<Long>)

    @Query("DELETE FROM tags")
    suspend fun deleteAllTags()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntryTagCrossRef(crossRef: LogEntryTagCrossRef)

    @Query("SELECT * FROM log_entry_tag_cross_ref")
    suspend fun getAllLogEntryTagCrossRefsOnce(): List<LogEntryTagCrossRef>

    @Query("DELETE FROM log_entry_tag_cross_ref")
    suspend fun deleteAllLogEntryTagCrossRefs()

    @Query("DELETE FROM log_entry_tag_cross_ref WHERE logEntryId = :logEntryId")
    suspend fun deleteCrossRefsForLogEntry(logEntryId: Long)

    @Transaction
    @Query("SELECT * FROM log_entries WHERE id = :logEntryId")
    fun getTagsForLogEntry(logEntryId: Long): Flow<LogEntryWithTags?>
}

@Dao
interface FilmRollSessionDao {
    @Query("SELECT * FROM film_roll_sessions")
    suspend fun getAllSessionsOnce(): List<FilmRollSession>

    @Query("SELECT * FROM film_roll_sessions WHERE cameraId = :cameraId AND filmId = :filmId")
    fun getSession(cameraId: Long, filmId: Long): Flow<FilmRollSession?>

    @Query("SELECT * FROM film_roll_sessions WHERE cameraId = :cameraId AND filmId = :filmId")
    suspend fun getSessionOnce(cameraId: Long, filmId: Long): FilmRollSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FilmRollSession)

    @Update
    suspend fun updateSession(session: FilmRollSession)

    @Query("DELETE FROM film_roll_sessions WHERE cameraId = :cameraId AND filmId = :filmId")
    suspend fun deleteSession(cameraId: Long, filmId: Long)

    @Query("DELETE FROM film_roll_sessions")
    suspend fun deleteAllSessions()
}

data class LogEntryWithTags(
    @Embedded val logEntry: LogEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LogEntryTagCrossRef::class,
            parentColumn = "logEntryId",
            entityColumn = "tagId"
        )
    )
    var tags: List<Tag> = emptyList()
)
