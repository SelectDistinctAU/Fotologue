package com.fotologue.app.data

import com.fotologue.app.location.LocationProvider
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

class FotologueRepository(
    private val db: AppDatabase,
    private val cameraDao: CameraDao,
    private val filmDao: FilmDao,
    private val lensDao: LensDao,
    private val teleconverterDao: TeleconverterDao,
    private val filterDao: FilterDao,
    private val flashDao: FlashDao,
    private val logEntryDao: LogEntryDao,
    private val cameraKitDao: CameraKitDao,
    private val tagDao: TagDao,
    private val locationProvider: LocationProvider,
    private val settingsRepository: SettingsRepository
) {
    private val _newlyAddedLensId = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val newlyAddedLensId = _newlyAddedLensId.asSharedFlow()

    private val tagMutex = Mutex()

    val allCameras: Flow<List<Camera>> = cameraDao.getAllCameras()
    val allFilms: Flow<List<Film>> = filmDao.getAllFilms()
    val allLenses: Flow<List<Lens>> = lensDao.getAllLenses()
    val allTeleconverters: Flow<List<Teleconverter>> = teleconverterDao.getAllTeleconverters()
    val allFilters: Flow<List<Filter>> = filterDao.getAllFilters()
    val allFlashes: Flow<List<Flash>> = flashDao.getAllFlashes()
    val allLogEntries: Flow<List<LogEntry>> = logEntryDao.getAllLogEntries()
    val allLogEntriesWithGear: Flow<List<LogEntryWithGear>> = logEntryDao.getAllLogEntriesWithGear()
    val allCameraKits: Flow<List<CameraKit>> = cameraKitDao.getAllCameraKits()
    val allCameraKitsWithGear: Flow<List<CameraKitWithGear>> = cameraKitDao.getAllCameraKitsWithGear()
    val allTags: Flow<List<Tag>> = tagDao.getAllTags()
    val allCameraLensCrossRefs: Flow<List<CameraLensCrossRef>> = lensDao.getAllCameraLensCrossRefs()

    val hasCamera: Flow<Boolean> = cameraDao.hasCameras()
    val hasFilm: Flow<Boolean> = filmDao.hasFilms()
    val hasLens: Flow<Boolean> = lensDao.hasLenses()

    val isOnboardingComplete: Flow<Boolean> = combine(hasCamera, hasFilm, hasLens) { camera, film, lens ->
        camera && film && lens
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
    fun getCameraKitById(id: Long): Flow<CameraKit?> = cameraKitDao.getCameraKitById(id)

    suspend fun getSuggestedFilmRoll(cameraId: Long?, filmId: Long?): String {
        if (cameraId == null || filmId == null) {
            val roll = settingsRepository.currentRoll.first()
            val frame = settingsRepository.currentFrame.first()
            return String.format(Locale.US, "%d.%02d", roll, frame)
        }
        
        // We now prioritize the 'Active' session in SettingsRepository
        val roll = settingsRepository.currentRoll.first()
        val frame = settingsRepository.currentFrame.first()
        return String.format(Locale.US, "%d.%02d", roll, frame)
    }

    suspend fun insertCamera(camera: Camera) = cameraDao.insertCamera(camera)
    suspend fun insertFilm(film: Film) = filmDao.insertFilm(film)
    suspend fun insertLens(lens: Lens, cameraIds: List<Long> = emptyList()) {
        db.withTransaction {
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
    suspend fun insertCameraKit(cameraKit: CameraKit) = cameraKitDao.insertCameraKit(cameraKit)
    suspend fun insertTag(tag: Tag) = tagDao.insertTag(tag)

    suspend fun insertDefaultTagsIfNeeded() = tagMutex.withLock {
        val currentTags = tagDao.getAllTags().first()

        // Cleanup duplicates if they already exist from previous buggy runs
        cleanupDuplicateTags(currentTags)
        
        // Refresh tag list after potential cleanup
        val refreshedTags = if (currentTags.isNotEmpty()) tagDao.getAllTags().first() else currentTags

        // 1. Create or Get Subjects (Parents)
        val peopleId = getOrCreateSubjectId("People", refreshedTags)
        val urbanId = getOrCreateSubjectId("Urban/built environment", refreshedTags)
        val eventsId = getOrCreateSubjectId("Events", refreshedTags)
        val natureId = getOrCreateSubjectId("Nature", refreshedTags)

        // 2. Create Related Content (Children) if missing
        val contentNames = refreshedTags.filter { it.category == Tag.CATEGORY_CONTENT }.map { it.name.lowercase() }.toSet()

        // People
        if (!contentNames.contains("names")) {
            tagDao.insertTag(Tag(name = "Names", category = Tag.CATEGORY_CONTENT, parentId = peopleId))
        }

        // Urban
        listOf("architecture", "street", "cityscape", "building", "bridge", "interior", "skyline").forEach { name ->
            if (!contentNames.contains(name.lowercase())) {
                tagDao.insertTag(Tag(name = name, category = Tag.CATEGORY_CONTENT, parentId = urbanId))
            }
        }
        // Events
        listOf("wedding", "concert", "sport", "festival", "protest", "celebration").forEach { name ->
            if (!contentNames.contains(name.lowercase())) {
                tagDao.insertTag(Tag(name = name, category = Tag.CATEGORY_CONTENT, parentId = eventsId))
            }
        }
        // Nature
        listOf("landscape", "waterways", "bushland", "insect", "beach", "sunset/sunrise", "fungi", "bird", "flower", "tree", "wildlife").forEach { name ->
            if (!contentNames.contains(name.lowercase())) {
                tagDao.insertTag(Tag(name = name, category = Tag.CATEGORY_CONTENT, parentId = natureId))
            }
        }
    }

    private suspend fun cleanupDuplicateTags(tags: List<Tag>) {
        val seen = mutableSetOf<String>()
        val duplicates = mutableListOf<Tag>()
        
        tags.forEach { tag ->
            val key = "${tag.category}:${tag.parentId}:${tag.name.lowercase()}"
            if (seen.contains(key)) {
                duplicates.add(tag)
            } else {
                seen.add(key)
            }
        }
        
        if (duplicates.isNotEmpty()) {
            tagDao.deleteTags(duplicates.map { it.id })
        }
    }

    private suspend fun getOrCreateSubjectId(name: String, currentTags: List<Tag>): Long {
        val existing = currentTags.find { it.category == Tag.CATEGORY_SUBJECT && it.name.equals(name, ignoreCase = true) }
        return existing?.id ?: tagDao.insertTag(Tag(name = name, category = Tag.CATEGORY_SUBJECT))
    }

    suspend fun updateCamera(camera: Camera) = cameraDao.updateCamera(camera)
    suspend fun updateFilm(film: Film) = filmDao.updateFilm(film)
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
    suspend fun updateTeleconverter(teleconverter: Teleconverter) = teleconverterDao.updateTeleconverter(teleconverter)
    suspend fun updateFilter(filter: Filter) = filterDao.updateFilter(filter)
    suspend fun updateFlash(flash: Flash) = flashDao.updateFlash(flash)
    suspend fun updateCameraKit(cameraKit: CameraKit) = cameraKitDao.updateCameraKit(cameraKit)
    suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)

    suspend fun deleteCamera(camera: Camera) = cameraDao.deleteCamera(camera)
    suspend fun deleteFilm(film: Film) = filmDao.deleteFilm(film)
    suspend fun deleteLens(lens: Lens) = lensDao.deleteLens(lens)
    suspend fun deleteTeleconverter(teleconverter: Teleconverter) = teleconverterDao.deleteTeleconverter(teleconverter)
    suspend fun deleteFilter(filter: Filter) = filterDao.deleteFilter(filter)
    suspend fun deleteFlash(flash: Flash) = flashDao.deleteFlash(flash)
    suspend fun deleteCameraKit(cameraKit: CameraKit) = cameraKitDao.deleteCameraKit(cameraKit)
    suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)

    suspend fun deleteCameras(ids: List<Long>) = cameraDao.deleteCameras(ids)
    suspend fun deleteFilms(ids: List<Long>) = filmDao.deleteFilms(ids)
    suspend fun deleteLenses(ids: List<Long>) = lensDao.deleteLenses(ids)
    suspend fun deleteTeleconverters(ids: List<Long>) = teleconverterDao.deleteTeleconverters(ids)
    suspend fun deleteFilters(ids: List<Long>) = filterDao.deleteFilters(ids)
    suspend fun deleteFlashes(ids: List<Long>) = flashDao.deleteFlashes(ids)
    suspend fun deleteCameraKits(ids: List<Long>) = cameraKitDao.deleteCameraKits(ids)
    suspend fun deleteTags(ids: List<Long>) = tagDao.deleteTags(ids)

    suspend fun updateLogEntry(logEntry: LogEntry, tagIds: List<Long> = emptyList()) {
        val updatedEntry = logEntry.copy(lastUpdated = System.currentTimeMillis())
        logEntryDao.updateLogEntry(updatedEntry)
        tagDao.deleteCrossRefsForLogEntry(logEntry.id)
        tagIds.forEach { tagId ->
            tagDao.insertLogEntryTagCrossRef(LogEntryTagCrossRef(logEntry.id, tagId))
        }
    }

    suspend fun deleteLogEntries(ids: List<Long>) = logEntryDao.deleteLogEntries(ids)

    suspend fun markAsExportedZip(ids: List<Long>) = logEntryDao.markAsExportedZip(ids)
    suspend fun markAsExportedCsv(ids: List<Long>) = logEntryDao.markAsExportedCsv(ids)
    suspend fun markAsExportedJson(ids: List<Long>) = logEntryDao.markAsExportedJson(ids)

    private val _latestMeteringResult = MutableStateFlow<ExposureSettings?>(null)
    val latestMeteringResult = _latestMeteringResult.asStateFlow()

    fun setMeteringResult(result: ExposureSettings) {
        _latestMeteringResult.value = result
    }

    fun clearMeteringResult() {
        _latestMeteringResult.value = null
    }

    suspend fun getCurrentLocation() = locationProvider.getCurrentLocation()

    suspend fun logExposure(
        notes: String,
        environmentalConditions: String,
        cameraId: Long?,
        filmId: Long?,
        lensId: Long? = null,
        teleconverterId: Long? = null,
        filterId: Long? = null,
        flashId: Long? = null,
        kitId: Long? = null,
        focalLength: Float? = null,
        effectiveFocalLength: Float? = null,
        effectiveAperture: String? = null,
        exposureSettings: ExposureSettings,
        tagIds: List<Long> = emptyList(),
        latitude: Double? = null,
        longitude: Double? = null,
        filmRoll: String? = null
    ) {
        val finalLocation = if (latitude != null && longitude != null) {
            null // We use the passed ones
        } else {
            locationProvider.getCurrentLocation()
        }
        
        val now = System.currentTimeMillis()
        val entry = LogEntry(
            timestamp = now,
            notes = notes,
            environmentalConditions = environmentalConditions,
            cameraId = cameraId,
            filmId = filmId,
            lensId = lensId,
            teleconverterId = teleconverterId,
            filterId = filterId,
            flashId = flashId,
            kitId = kitId,
            focalLength = focalLength,
            effectiveFocalLength = effectiveFocalLength,
            effectiveAperture = effectiveAperture,
            exposureSettings = exposureSettings,
            latitude = latitude ?: finalLocation?.latitude,
            longitude = longitude ?: finalLocation?.longitude,
            filmRoll = filmRoll,
            lastUpdated = now,
            isExportedZip = false,
            isExportedCsv = false,
            isExportedJson = false
        )
        val logId = logEntryDao.insertLogEntry(entry)
        tagIds.forEach { tagId ->
            tagDao.insertLogEntryTagCrossRef(LogEntryTagCrossRef(logId, tagId))
        }
    }
}
