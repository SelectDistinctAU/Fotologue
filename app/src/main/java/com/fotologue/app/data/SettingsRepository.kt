package com.fotologue.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class PictureExportSize(val label: String) {
    SPACE_SAVER("Space saver (1280 x 720)"),
    NATURAL("Natural Size")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val IS_THUMBNAIL_ENABLED = booleanPreferencesKey("is_thumbnail_enabled")
    private val IS_FACE_DETECTION_ENABLED = booleanPreferencesKey("is_face_detection_enabled")
    private val FACE_DETECTION_DISCLOSURE_SHOWN = booleanPreferencesKey("face_detection_disclosure_shown")
    private val EV_CALIBRATION = floatPreferencesKey("ev_calibration")
    private val BLACK_LEVEL_OFFSET = floatPreferencesKey("black_level_offset")
    private val LUMINANCE_GAIN = floatPreferencesKey("luminance_gain")
    private val PICTURE_EXPORT_SIZE = stringPreferencesKey("picture_export_size")
    private val BLE_SERVICE_UUID = stringPreferencesKey("ble_service_uuid")
    private val BLE_CHARACTERISTIC_UUID = stringPreferencesKey("ble_characteristic_uuid")
    private val BLE_DEVICE_NAME = stringPreferencesKey("ble_device_name")

    private val CREATOR_NAME = stringPreferencesKey("creator_name")
    private val IS_AUTO_COPYRIGHT_ENABLED = booleanPreferencesKey("is_auto_copyright_enabled")
    private val COPYRIGHT_NOTICE = stringPreferencesKey("copyright_notice")
    private val CONTACT_INFO = stringPreferencesKey("contact_info")

    private val LAST_CAMERA_ID = androidx.datastore.preferences.core.longPreferencesKey("last_camera_id")
    private val LAST_FILM_ID = androidx.datastore.preferences.core.longPreferencesKey("last_film_id")
    private val LAST_LENS_ID = androidx.datastore.preferences.core.longPreferencesKey("last_lens_id")
    private val LAST_TC_ID = androidx.datastore.preferences.core.longPreferencesKey("last_tc_id")
    private val LAST_FILTER_ID = androidx.datastore.preferences.core.longPreferencesKey("last_filter_id")
    private val LAST_FLASH_ID = androidx.datastore.preferences.core.longPreferencesKey("last_flash_id")
    private val LAST_VIEWFINDER_ID = longPreferencesKey("last_viewfinder_id")
    private val LAST_KIT_ID = androidx.datastore.preferences.core.longPreferencesKey("last_kit_id")
    private val LAST_ISO = androidx.datastore.preferences.core.intPreferencesKey("last_iso")
    private val LAST_FOCAL_LENGTH = androidx.datastore.preferences.core.floatPreferencesKey("last_focal_length")
    private val LAST_SHUTTER_SPEED = stringPreferencesKey("last_shutter_speed")
    private val LAST_APERTURE = stringPreferencesKey("last_aperture")
    private val CURRENT_ROLL = intPreferencesKey("current_roll")
    private val CURRENT_FRAME = intPreferencesKey("current_frame")
    private val IS_WELCOME_DISMISSED = booleanPreferencesKey("is_welcome_dismissed")

    val isThumbnailEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[IS_THUMBNAIL_ENABLED] ?: false
            } catch (e: ClassCastException) {
                false
            }
        }

    val isFaceDetectionEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[IS_FACE_DETECTION_ENABLED] ?: false
            } catch (e: ClassCastException) {
                false
            }
        }

    val faceDetectionDisclosureShown: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[FACE_DETECTION_DISCLOSURE_SHOWN] ?: false
            } catch (e: ClassCastException) {
                false
            }
        }

    val evCalibration: Flow<Float> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[EV_CALIBRATION] ?: 0.0f
            } catch (e: ClassCastException) {
                0.0f
            }
        }

    val blackLevelOffset: Flow<Float> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[BLACK_LEVEL_OFFSET] ?: 0.0f
            } catch (e: ClassCastException) {
                0.0f
            }
        }

    val luminanceGain: Flow<Float> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[LUMINANCE_GAIN] ?: 1.0f
            } catch (e: ClassCastException) {
                1.0f
            }
        }

    val pictureExportSize: Flow<PictureExportSize> = context.dataStore.data
        .map { preferences ->
            val sizeName = try {
                preferences[PICTURE_EXPORT_SIZE] ?: PictureExportSize.NATURAL.name
            } catch (e: ClassCastException) {
                PictureExportSize.NATURAL.name
            }
            try {
                PictureExportSize.valueOf(sizeName)
            } catch (e: IllegalArgumentException) {
                PictureExportSize.NATURAL
            }
        }

    val bleServiceUuid: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[BLE_SERVICE_UUID] ?: "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
            } catch (e: ClassCastException) {
                "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
            }
        }

    val bleCharacteristicUuid: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[BLE_CHARACTERISTIC_UUID] ?: "beb5483e-36e1-4688-b7f5-ea073791391d"
            } catch (e: ClassCastException) {
                "beb5483e-36e1-4688-b7f5-ea073791391d"
            }
        }

    val bleDeviceName: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[BLE_DEVICE_NAME] ?: "FotologueCameraTrigger"
            } catch (e: ClassCastException) {
                "FotologueCameraTrigger"
            }
        }

    val creatorName: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[CREATOR_NAME] ?: ""
            } catch (e: ClassCastException) {
                ""
            }
        }

    val copyrightNotice: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[COPYRIGHT_NOTICE] ?: ""
            } catch (e: ClassCastException) {
                ""
            }
        }

    val isAutoCopyrightEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[IS_AUTO_COPYRIGHT_ENABLED] ?: false
            } catch (e: ClassCastException) {
                false
            }
        }

    val contactInfo: Flow<String> = context.dataStore.data
        .map { preferences ->
            try {
                preferences[CONTACT_INFO] ?: ""
            } catch (e: ClassCastException) {
                ""
            }
        }

    suspend fun setThumbnailEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_THUMBNAIL_ENABLED] = enabled
        }
    }

    suspend fun setFaceDetectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FACE_DETECTION_ENABLED] = enabled
        }
    }

    suspend fun setFaceDetectionDisclosureShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FACE_DETECTION_DISCLOSURE_SHOWN] = shown
        }
    }

    suspend fun setEvCalibration(calibration: Float) {
        context.dataStore.edit { preferences ->
            preferences[EV_CALIBRATION] = calibration
        }
    }

    suspend fun setCalibrationConstants(blackOffset: Float, gain: Float) {
        context.dataStore.edit { preferences ->
            preferences[BLACK_LEVEL_OFFSET] = blackOffset
            preferences[LUMINANCE_GAIN] = gain
        }
    }

    suspend fun setPictureExportSize(size: PictureExportSize) {
        context.dataStore.edit { preferences ->
            preferences[PICTURE_EXPORT_SIZE] = size.name
        }
    }

    suspend fun setBleServiceUuid(uuid: String) {
        context.dataStore.edit { preferences ->
            preferences[BLE_SERVICE_UUID] = uuid
        }
    }

    suspend fun setBleCharacteristicUuid(uuid: String) {
        context.dataStore.edit { preferences ->
            preferences[BLE_CHARACTERISTIC_UUID] = uuid
        }
    }

    suspend fun setBleDeviceName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[BLE_DEVICE_NAME] = name
        }
    }

    suspend fun setCreatorName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[CREATOR_NAME] = name
        }
    }

    suspend fun setCopyrightNotice(notice: String) {
        context.dataStore.edit { preferences ->
            preferences[COPYRIGHT_NOTICE] = notice
        }
    }

    suspend fun setAutoCopyrightEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_AUTO_COPYRIGHT_ENABLED] = enabled
        }
    }

    suspend fun setContactInfo(info: String) {
        context.dataStore.edit { preferences ->
            preferences[CONTACT_INFO] = info
        }
    }

    val lastCameraId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_CAMERA_ID] } catch (e: ClassCastException) { null } }
    val lastFilmId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_FILM_ID] } catch (e: ClassCastException) { null } }
    val lastLensId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_LENS_ID] } catch (e: ClassCastException) { null } }
    val lastTcId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_TC_ID] } catch (e: ClassCastException) { null } }
    val lastFilterId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_FILTER_ID] } catch (e: ClassCastException) { null } }
    val lastFlashId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_FLASH_ID] } catch (e: ClassCastException) { null } }
    val lastViewfinderId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_VIEWFINDER_ID] } catch (e: ClassCastException) { null } }
    val lastKitId: Flow<Long?> = context.dataStore.data.map { try { it[LAST_KIT_ID] } catch (e: ClassCastException) { null } }
    val lastIso: Flow<Int?> = context.dataStore.data.map { try { it[LAST_ISO] } catch (e: ClassCastException) { null } }
    val lastFocalLength: Flow<Float?> = context.dataStore.data.map { try { it[LAST_FOCAL_LENGTH] } catch (e: ClassCastException) { null } }
    val lastShutterSpeed: Flow<String?> = context.dataStore.data.map { try { it[LAST_SHUTTER_SPEED] } catch (e: ClassCastException) { null } }
    val lastAperture: Flow<String?> = context.dataStore.data.map { try { it[LAST_APERTURE] } catch (e: ClassCastException) { null } }
    val currentRoll: Flow<Int> = context.dataStore.data.map { it[CURRENT_ROLL] ?: 1 }
    val currentFrame: Flow<Int> = context.dataStore.data.map { it[CURRENT_FRAME] ?: 1 }
    val isWelcomeDismissed: Flow<Boolean> = context.dataStore.data.map { it[IS_WELCOME_DISMISSED] ?: false }

    suspend fun setLastGear(
        cameraId: Long?,
        filmId: Long?,
        lensId: Long?,
        tcId: Long?,
        filterId: Long?,
        flashId: Long?,
        viewfinderId: Long?,
        kitId: Long?,
        iso: Int?,
        focalLength: Float?,
        shutterSpeed: String?,
        aperture: String?
    ) {
        context.dataStore.edit { preferences ->
            if (cameraId != null) preferences[LAST_CAMERA_ID] = cameraId else preferences.remove(LAST_CAMERA_ID)
            if (filmId != null) preferences[LAST_FILM_ID] = filmId else preferences.remove(LAST_FILM_ID)
            if (lensId != null) preferences[LAST_LENS_ID] = lensId else preferences.remove(LAST_LENS_ID)
            if (tcId != null) preferences[LAST_TC_ID] = tcId else preferences.remove(LAST_TC_ID)
            if (filterId != null) preferences[LAST_FILTER_ID] = filterId else preferences.remove(LAST_FILTER_ID)
            if (flashId != null) preferences[LAST_FLASH_ID] = flashId else preferences.remove(LAST_FLASH_ID)
            if (viewfinderId != null) preferences[LAST_VIEWFINDER_ID] = viewfinderId else preferences.remove(LAST_VIEWFINDER_ID)
            if (kitId != null) preferences[LAST_KIT_ID] = kitId else preferences.remove(LAST_KIT_ID)
            if (iso != null) preferences[LAST_ISO] = iso else preferences.remove(LAST_ISO)
            if (focalLength != null) preferences[LAST_FOCAL_LENGTH] = focalLength else preferences.remove(LAST_FOCAL_LENGTH)
            if (shutterSpeed != null) preferences[LAST_SHUTTER_SPEED] = shutterSpeed else preferences.remove(LAST_SHUTTER_SPEED)
            if (aperture != null) preferences[LAST_APERTURE] = aperture else preferences.remove(LAST_APERTURE)
        }
    }

    suspend fun setCurrentRollAndFrame(roll: Int, frame: Int) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_ROLL] = roll
            preferences[CURRENT_FRAME] = frame
        }
    }

    suspend fun incrementFrame() {
        context.dataStore.edit { preferences ->
            val current = preferences[CURRENT_FRAME] ?: 1
            preferences[CURRENT_FRAME] = current + 1
        }
    }

    suspend fun setWelcomeDismissed(dismissed: Boolean) {
        context.dataStore.edit { it[IS_WELCOME_DISMISSED] = dismissed }
    }
}
