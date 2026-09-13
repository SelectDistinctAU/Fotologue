package com.fotologue.app.data

import android.location.Location
import androidx.room.withTransaction
import com.fotologue.app.location.LocationProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FotologueRepository @Inject constructor(
    private val database: AppDatabase,
    private val cameraDao: CameraDao,
    private val filmDao: FilmDao,
    private val lensDao: LensDao,
    private val teleconverterDao: TeleconverterDao,
    private val filterDao: FilterDao,
    private val flashDao: FlashDao,
    private val logEntryDao: LogEntryDao,
    private val cameraKitDao: CameraKitDao,
    private val tagDao: TagDao,
    private val filmRollSessionDao: FilmRollSessionDao,
    private val viewfinderDao: ViewfinderDao,
    private val locationProvider: LocationProvider,
    @Suppress("unused") private val settingsRepository: SettingsRepository,
) {
    private val _newlyAddedLensId = MutableSharedFlow<Long>()
    val newlyAddedLensId: SharedFlow<Long> = _newlyAddedLensId.asSharedFlow()

    private val tagMutex = Mutex()

    val allCameras: Flow<List<Camera>> = cameraDao.getAllCameras()
    val allFilms: Flow<List<Film>> = filmDao.getAllFilms()
    val allLenses: Flow<List<Lens>> = lensDao.getAllLenses()
    val allTeleconverters: Flow<List<Teleconverter>> = teleconverterDao.getAllTeleconverters()
    val allFilters: Flow<List<Filter>> = filterDao.getAllFilters()
    val allFlashes: Flow<List<Flash>> = flashDao.getAllFlashes()
    val allViewfinders: Flow<List<Viewfinder>> = viewfinderDao.getAllViewfinders()
    val allLogEntriesWithGear: Flow<List<LogEntryWithGear>> = logEntryDao.getAllLogEntriesWithGear()
    val allCameraKits: Flow<List<CameraKit>> = cameraKitDao.getAllCameraKits()
    val allCameraKitsWithGear: Flow<List<CameraKitWithGear>> = cameraKitDao.getAllCameraKitsWithGear()
    val allTags: Flow<List<Tag>> = tagDao.getAllTags()
    val allCameraLensCrossRefs: Flow<List<CameraLensCrossRef>> = lensDao.getAllCameraLensCrossRefs()

    val hasCamera: Flow<Boolean> = cameraDao.hasCameras()
    val hasFilm: Flow<Boolean> = filmDao.hasFilms()
    val hasLens: Flow<Boolean> = lensDao.hasLenses()
    val isWelcomeDismissed: Flow<Boolean> = settingsRepository.isWelcomeDismissed

    val isOnboardingComplete: Flow<Boolean> = combine(hasCamera, hasFilm, hasLens, isWelcomeDismissed) { camera, film, lens, dismissed ->
        camera && film && lens && dismissed
    }

    fun getLogEntryById(id: Long): Flow<LogEntry?> = logEntryDao.getLogEntryById(id)
    fun getLogEntryWithGearById(id: Long): Flow<LogEntryWithGear?> = logEntryDao.getLogEntryWithGearById(id)

    fun getCameraById(id: Long): Flow<Camera?> = cameraDao.getCameraById(id)
    fun getFilmById(id: Long): Flow<Film?> = filmDao.getFilmById(id)
    fun getLensById(id: Long): Flow<Lens?> = lensDao.getLensById(id)
    fun getCamerasForLens(lensId: Long): Flow<List<Camera>> = lensDao.getCamerasForLens(lensId)
    fun getTeleconverterById(id: Long): Flow<Teleconverter?> = teleconverterDao.getTeleconverterById(id)
    fun getFilterById(id: Long): Flow<Filter?> = filterDao.getFilterById(id)
    fun getFlashById(id: Long): Flow<Flash?> = flashDao.getFlashById(id)
    fun getViewfinderById(id: Long): Flow<Viewfinder?> = viewfinderDao.getViewfinderById(id)
    fun getCameraKitById(id: Long): Flow<CameraKit?> = cameraKitDao.getCameraKitById(id)

    fun getFilmRollSession(cameraId: Long, filmId: Long): Flow<FilmRollSession?> =
        filmRollSessionDao.getSession(cameraId, filmId)

    fun getSuggestedFilmRollFlow(cameraId: Long?, filmId: Long?): Flow<String> {
        if ((cameraId == null) || (filmId == null)) return flowOf("1.01")
        
        return filmRollSessionDao.getSession(cameraId, filmId).map { session ->
            if (session != null) {
                String.format(Locale.US, "%d.%02d", session.rollNumber, session.nextFrameNumber)
            } else {
                "1.01"
            }
        }
    }

    suspend fun updateFilmRollSession(session: FilmRollSession) =
        filmRollSessionDao.insertSession(session)

    suspend fun insertCamera(camera: Camera) = cameraDao.insertCamera(camera)
    suspend fun insertFilm(film: Film) = filmDao.insertFilm(film)
    suspend fun insertLens(lens: Lens, cameraIds: List<Long> = emptyList()) {
        database.withTransaction {
            val lensId = lensDao.insertLens(lens)
            cameraIds.forEach { cameraId ->
                val camera = cameraDao.getCameraById(cameraId).first()
                if (camera?.type?.isInterchangeable == false) {
                    // If it's a fixed lens camera, it should only have this lens
                    lensDao.deleteCameraLensCrossRefsForCamera(cameraId)
                }
                lensDao.insertCameraLensCrossRef(CameraLensCrossRef(cameraId, lensId))
            }
            _newlyAddedLensId.emit(lensId)
        }
    }
    suspend fun insertTeleconverter(teleconverter: Teleconverter) = teleconverterDao.insertTeleconverter(teleconverter)
    suspend fun insertFilter(filter: Filter) = filterDao.insertFilter(filter)
    suspend fun insertFlash(flash: Flash) = flashDao.insertFlash(flash)
    suspend fun insertViewfinder(viewfinder: Viewfinder) = viewfinderDao.insertViewfinder(viewfinder)
    suspend fun insertCameraKit(cameraKit: CameraKit) = cameraKitDao.insertCameraKit(cameraKit)
    suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    suspend fun insertDefaultTagsIfNeeded() {
        tagMutex.withLock {
            val existingTags = tagDao.getAllTagsOnce()
            
            // Professional categories refresh
            val subjects = listOf("Urban/built environment", "Events", "Nature", "People")
            val contents = mapOf(
                "Urban/built environment" to listOf("Architecture", "Street", "Cityscape", "Building", "Bridge", "Interior", "Skyline"),
                "Events" to listOf("Wedding", "Concert", "Sport", "Festival", "Protest", "Celebration"),
                "Nature" to listOf("Landscape", "Waterways", "Bushland", "Insect", "Beach", "Sunset/sunrise", "Fungi", "Bird", "Flower", "Tree", "Wildlife"),
                "People" to listOf("Portrait", "Candid", "Group", "Family", "Self", "Stranger")
            )

            if (existingTags.isEmpty()) {
                subjects.forEach { sub ->
                    val subId = tagDao.insertTag(Tag(name = sub, category = Tag.CATEGORY_SUBJECT))
                    contents[sub]?.forEach { cont ->
                        tagDao.insertTag(Tag(name = cont, category = Tag.CATEGORY_CONTENT, parentId = subId))
                    }
                }
            } else {
                // If tags already exist, ensure the new professional set is integrated without duplicates
                subjects.forEach { subName ->
                    var subject = existingTags.find { it.name == subName && it.category == Tag.CATEGORY_SUBJECT }
                    if (subject == null) {
                        val newId = tagDao.insertTag(Tag(name = subName, category = Tag.CATEGORY_SUBJECT))
                        subject = Tag(id = newId, name = subName, category = Tag.CATEGORY_SUBJECT)
                    }
                    
                    contents[subName]?.forEach { contName ->
                        val exists = existingTags.any { it.name == contName && it.category == Tag.CATEGORY_CONTENT && it.parentId == subject.id }
                        if (!exists) {
                            tagDao.insertTag(Tag(name = contName, category = Tag.CATEGORY_CONTENT, parentId = subject.id))
                        }
                    }
                }
                cleanupDuplicateTags(tagDao.getAllTagsOnce())
            }
        }
    }

    private suspend fun cleanupDuplicateTags(allTags: List<Tag>) {
        val grouped = allTags.groupBy { it.name.lowercase() + it.category }
        grouped.forEach { (_, tags) ->
            if (tags.size > 1) {
                // Keep the first one, delete others
                val toDelete = tags.drop(1).map { it.id }
                tagDao.deleteTags(toDelete)
            }
        }
    }

    suspend fun updateCamera(camera: Camera) = cameraDao.updateCamera(camera)
    suspend fun updateCameras(cameras: List<Camera>) = cameraDao.updateCameras(cameras)
    suspend fun updateFilm(film: Film) = filmDao.updateFilm(film)
    suspend fun updateFilms(films: List<Film>) = filmDao.updateFilms(films)
    suspend fun updateLens(lens: Lens, cameraIds: List<Long> = emptyList()) {
        lensDao.updateLens(lens)
        lensDao.deleteCameraLensCrossRefsForLens(lens.id)
        cameraIds.forEach { cameraId ->
            val camera = cameraDao.getCameraById(cameraId).first()
            if (camera?.type?.isInterchangeable == false) {
                // If it's a fixed lens camera, it should only have this lens
                lensDao.deleteCameraLensCrossRefsForCamera(cameraId)
            }
            lensDao.insertCameraLensCrossRef(CameraLensCrossRef(cameraId, lens.id))
        }
    }
    suspend fun updateLenses(lenses: List<Lens>) = lensDao.updateLenses(lenses)
    suspend fun updateTeleconverter(teleconverter: Teleconverter) = teleconverterDao.updateTeleconverter(teleconverter)
    suspend fun updateTeleconverters(tcs: List<Teleconverter>) = teleconverterDao.updateTeleconverters(tcs)
    suspend fun updateFilter(filter: Filter) = filterDao.updateFilter(filter)
    suspend fun updateFilters(filters: List<Filter>) = filterDao.updateFilters(filters)
    suspend fun updateFlash(flash: Flash) = flashDao.updateFlash(flash)
    suspend fun updateFlashes(flashes: List<Flash>) = flashDao.updateFlashes(flashes)
    suspend fun updateViewfinder(viewfinder: Viewfinder) = viewfinderDao.updateViewfinder(viewfinder)
    suspend fun updateViewfinders(viewfinders: List<Viewfinder>) = viewfinderDao.updateViewfinders(viewfinders)
    suspend fun updateCameraKit(cameraKit: CameraKit) = cameraKitDao.updateCameraKit(cameraKit)
    suspend fun updateCameraKits(kits: List<CameraKit>) = cameraKitDao.updateCameraKits(kits)
    suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)
    suspend fun updateTags(tags: List<Tag>) = tagDao.updateTags(tags)

    suspend fun deleteCameras(ids: List<Long>) = cameraDao.deleteCameras(ids)
    suspend fun deleteFilms(ids: List<Long>) = filmDao.deleteFilms(ids)
    suspend fun deleteLenses(ids: List<Long>) = lensDao.deleteLenses(ids)
    suspend fun deleteTeleconverters(ids: List<Long>) = teleconverterDao.deleteTeleconverters(ids)
    suspend fun deleteFilters(ids: List<Long>) = filterDao.deleteFilters(ids)
    suspend fun deleteFlashes(ids: List<Long>) = flashDao.deleteFlashes(ids)
    suspend fun deleteViewfinders(ids: List<Long>) = viewfinderDao.deleteViewfinders(ids)
    suspend fun deleteCameraKits(ids: List<Long>) = cameraKitDao.deleteCameraKits(ids)
    suspend fun deleteTags(ids: List<Long>) = tagDao.deleteTags(ids)

    suspend fun updateLogEntry(logEntry: LogEntry, tagIds: List<Long> = emptyList()) {
        val updatedEntry = logEntry.copy(
            lastUpdated = System.currentTimeMillis(),
            isExportedZip = false,
            isExportedCsv = false,
            isExportedJson = false,
            isExportedXmp = false
        )
        database.withTransaction {
            logEntryDao.updateLogEntry(updatedEntry)
            tagDao.deleteCrossRefsForLogEntry(updatedEntry.id)
            tagIds.forEach { tagId ->
                tagDao.insertLogEntryTagCrossRef(LogEntryTagCrossRef(updatedEntry.id, tagId))
            }
        }
    }

    suspend fun deleteLogEntries(ids: List<Long>) = logEntryDao.deleteLogEntries(ids)

    suspend fun markAsExportedZip(ids: List<Long>) = logEntryDao.markAsExportedZip(ids)
    suspend fun markAsExportedCsv(ids: List<Long>) = logEntryDao.markAsExportedCsv(ids)
    suspend fun markAsExportedJson(ids: List<Long>) = logEntryDao.markAsExportedJson(ids)
    suspend fun markAsExportedXmp(ids: List<Long>) = logEntryDao.markAsExportedXmp(ids)

    private val _latestMeteringResult = MutableStateFlow<ExposureSettings?>(null)
    val latestMeteringResult: StateFlow<ExposureSettings?> = _latestMeteringResult.asStateFlow()

    fun setMeteringResult(settings: ExposureSettings) {
        _latestMeteringResult.value = settings
    }

    fun clearMeteringResult() {
        _latestMeteringResult.value = null
    }

    suspend fun getCurrentLocation(): Location? = locationProvider.getCurrentLocation()

    suspend fun logExposure(
        notes: String,
        environmentalConditions: String,
        cameraId: Long?,
        filmId: Long?,
        lensId: Long? = null,
        teleconverterId: Long? = null,
        filterId: Long? = null,
        kitId: Long? = null,
        flashId: Long? = null,
        focalLength: Float? = null,
        effectiveFocalLength: Float? = null,
        effectiveAperture: String? = null,
        exposureSettings: ExposureSettings,
        tagIds: List<Long> = emptyList(),
        latitude: Double? = null,
        longitude: Double? = null,
        filmRoll: String? = null,
    ) {
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            notes = notes,
            environmentalConditions = environmentalConditions,
            cameraId = cameraId,
            lensId = lensId,
            filmId = filmId,
            teleconverterId = teleconverterId,
            filterId = filterId,
            kitId = kitId,
            flashId = flashId,
            focalLength = focalLength,
            effectiveFocalLength = effectiveFocalLength,
            effectiveAperture = effectiveAperture,
            exposureSettings = exposureSettings,
            latitude = latitude,
            longitude = longitude,
            filmRoll = filmRoll,
            lastUpdated = System.currentTimeMillis(),
        )

        database.withTransaction {
            val entryId = logEntryDao.insertLogEntry(entry)
            tagIds.forEach { tagId ->
                tagDao.insertLogEntryTagCrossRef(LogEntryTagCrossRef(entryId, tagId))
            }
            
            // Update film roll session if applicable
            if (filmRoll != null && filmRoll.contains(".") && cameraId != null && filmId != null) {
                val parts = filmRoll.split(".")
                val roll = parts[0].toIntOrNull() ?: 1
                val frame = parts[1].toIntOrNull() ?: 1
                filmRollSessionDao.insertSession(FilmRollSession(cameraId, filmId, roll, frame + 1))
            }
        }
    }
}
