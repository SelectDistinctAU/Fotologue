package com.fotologue.app.ui.viewmodel

import android.graphics.Rect
import android.hardware.camera2.CaptureResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fotologue.core.data.Camera
import com.fotologue.core.data.ExposureSettings
import com.fotologue.core.data.Film
import com.fotologue.core.data.Filter
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.Lens
import com.fotologue.core.data.SettingsRepository
import com.fotologue.core.data.Teleconverter
import com.fotologue.app.util.ExposureSuggester
import com.fotologue.app.util.MeteringMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.pow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LightMeterViewModel @Inject constructor(
    private val repository: FotologueRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    var targetIso: Int = 400
        private set

    val zoomRatio = MutableStateFlow(1.0f)

    fun setParams(
        targetIso: Int,
        cameraId: Long? = null,
        lensId: Long? = null,
        teleconverterId: Long? = null,
        filterId: Long? = null,
        filmId: Long? = null,
        focalLength: Float? = null,
        autoStartCalibration: Boolean = false
    ) {
        this.targetIso = targetIso
        loadData(cameraId, lensId, teleconverterId, filterId, filmId)
        if (focalLength != null && focalLength > 0f) {
            _uiState.update { it.copy(currentFocalLength = focalLength.toInt()) }
            val zoom = (focalLength / 24.0f).coerceIn(1.0f, 8.0f)
            zoomRatio.value = zoom
        }
        if (autoStartCalibration) {
            _uiState.update { it.copy(isWizardOnly = true) }
            startCalibration()
        }
    }

    private val _uiState = MutableStateFlow(LightMeterUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedCamera: Camera? = null
    private var selectedLens: Lens? = null
    private var selectedTeleconverter: Teleconverter? = null
    private var selectedFilter: Filter? = null
    private var selectedFilm: Film? = null

    val isThumbnailEnabled: StateFlow<Boolean> = settingsRepository.isThumbnailEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = false)

    val isFaceDetectionEnabled: StateFlow<Boolean> = settingsRepository.isFaceDetectionEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = false)

    private val evCalibration: StateFlow<Float> = settingsRepository.evCalibration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0f)

    val blackLevelOffset: StateFlow<Float> = settingsRepository.blackLevelOffset
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0f)

    val luminanceGain: StateFlow<Float> = settingsRepository.luminanceGain
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    init {
        viewModelScope.launch {
            evCalibration.collect {
                updateSuggestions()
            }
        }
    }

    private fun loadData(
        cameraId: Long?,
        lensId: Long?,
        teleconverterId: Long?,
        filterId: Long?,
        filmId: Long?
    ) {
        cameraId?.let { id ->
            viewModelScope.launch {
                repository.getCameraById(id).collect {
                    selectedCamera = it
                    updateSuggestions()
                }
            }
        }
        lensId?.let { id ->
            viewModelScope.launch {
                repository.getLensById(id).collect {
                    selectedLens = it
                    if ((_uiState.value.currentFocalLength == null) && it != null) {
                        _uiState.update { state -> state.copy(currentFocalLength = it.minFocalLength) }
                    }
                    updateSuggestions()
                }
            }
        }
        teleconverterId?.let { id ->
            viewModelScope.launch {
                repository.getTeleconverterById(id).collect {
                    selectedTeleconverter = it
                    updateSuggestions()
                }
            }
        }
        filterId?.let { id ->
            viewModelScope.launch {
                repository.getFilterById(id).collect {
                    selectedFilter = it
                    updateSuggestions()
                }
            }
        }
        filmId?.let { id ->
            viewModelScope.launch {
                repository.getFilmById(id).collect {
                    selectedFilm = it
                    updateSuggestions()
                }
            }
        }
    }

    @Volatile
    private var _latestCaptureResult: CaptureResult? = null
    val latestCaptureResult: CaptureResult? get() = _latestCaptureResult

    private var lastSuggestionUpdateTime = 0L

    fun onEvCalculated(ev: Double) {
        val currentHistory = (_uiState.value.spotEvHistory + ev).takeLast(10)
        val baseline = if (_uiState.value.spotEvHistory.isNotEmpty()) _uiState.value.spotEvHistory.first() else ev
        val analysis = com.fotologue.app.util.ZoneSystemAnalysis.calculate(
            spotEv = ev,
            baselineEv = baseline,
            allSpotEvs = currentHistory
        )
        _uiState.update { state ->
            state.copy(
                currentEv = ev,
                spotEvHistory = currentHistory,
                zoneAnalysis = analysis
            )
        }
        
        // Throttle suggestion updates to ~10fps to reduce UI load and power consumption
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSuggestionUpdateTime > 100) {
            updateSuggestions()
            lastSuggestionUpdateTime = currentTime
        }
    }

    fun onRawLuminanceCalculated(luminance: Double) {
        _uiState.update { it.copy(rawLuminance = luminance) }
    }

    fun onHistogramCalculated(
        bins: IntArray,
        highlightClipped: Boolean,
        shadowClipped: Boolean,
        highlightGrid: Array<BooleanArray> = emptyArray(),
        shadowGrid: Array<BooleanArray> = emptyArray()
    ) {
        _uiState.update {
            it.copy(
                histogramBins = bins,
                hasHighlightClipping = highlightClipped,
                hasShadowClipping = shadowClipped,
                highlightGrid = highlightGrid,
                shadowGrid = shadowGrid
            )
        }
    }

    fun onFaceDetected(detected: Boolean) {
        _uiState.update { it.copy(faceDetected = detected) }
        updateSuggestions()
    }

    fun getCalibrationValue(): Float = evCalibration.value

    private fun updateSuggestions() {
        val baseEv = if (_uiState.value.sampledSpots.isNotEmpty()) _uiState.value.averageEv else _uiState.value.currentEv
        val shift = _uiState.value.exposureShiftStops
        val effectiveEv = baseEv + shift

        val baseSuggestions = ExposureSuggester.generateExposureSuggestions(
            effectiveEv, 
            targetIso, 
            selectedCamera, 
            selectedLens,
            selectedTeleconverter,
            selectedFilter
        )
        
        val faceDetected = _uiState.value.faceDetected
        val suggestions = baseSuggestions.map { it.copy(faceDetected = faceDetected) }
        
        val lens = selectedLens
        val tc = selectedTeleconverter
        val currentFL = _uiState.value.currentFocalLength ?: lens?.minFocalLength
        
        val effectiveFL = if (lens != null && currentFL != null) {
            currentFL * (tc?.magnification ?: 1.0)
        } else null
        
        val effectiveMaxAp = lens?.let {
            it.minAperture * 2.0.pow((tc?.lightLossStops ?: 0.0) / 2.0)
        }

        _uiState.update { 
            it.copy(
                suggestions = suggestions,
                effectiveFocalLength = effectiveFL,
                effectiveMaxAperture = effectiveMaxAp,
                lens = selectedLens,
                teleconverter = selectedTeleconverter,
                filter = selectedFilter,
                film = selectedFilm
            )
        }
    }

    fun onFocalLengthChanged(focalLength: Int) {
        _uiState.update { it.copy(currentFocalLength = focalLength) }
        updateSuggestions()
    }

    fun onCaptureResultUpdated(result: CaptureResult) {
        _latestCaptureResult = result
    }

    fun toggleMeteringMode() {
        _uiState.update { 
            val newMode = if (it.meteringMode == MeteringMode.SPOT) {
                MeteringMode.CENTER_WEIGHTED
            } else {
                MeteringMode.SPOT
            }
            val spots = if (newMode == MeteringMode.CENTER_WEIGHTED) emptyList() else it.sampledSpots
            recalculateMultiSpotState(it.copy(meteringMode = newMode), spots)
        }
        updateSuggestions()
    }

    fun updateSpotLocation(x: Int, y: Int, viewWidth: Int, viewHeight: Int) {
        // Create a small rect around the touch point
        val halfSize = 50
        val rect = Rect(
            (x - halfSize).coerceIn(0, viewWidth),
            (y - halfSize).coerceIn(0, viewHeight),
            (x + halfSize).coerceIn(0, viewWidth),
            (y + halfSize).coerceIn(0, viewHeight)
        )
        _uiState.update { it.copy(spotRect = rect) }
    }

    fun startCalibration() {
        _uiState.update { it.copy(calibrationStage = CalibrationStage.BLACK_POINT) }
    }

    fun captureBlackPoint() {
        val currentRaw = _uiState.value.rawLuminance
        _uiState.update { it.copy(capturedBlackPoint = currentRaw, calibrationStage = CalibrationStage.GRAY_POINT) }
    }

    fun captureGrayPoint(targetEv: Double) {
        // targetEv is used for future advanced calibration logic if needed
        val currentRaw = _uiState.value.rawLuminance
        val blackPoint = _uiState.value.capturedBlackPoint
        
        val usefulLuminance = (currentRaw - blackPoint).coerceAtLeast(0.1)
        val targetLuminance = 45.0 
        val calculatedGain = (targetLuminance / usefulLuminance).toFloat()

        viewModelScope.launch {
            settingsRepository.setCalibrationConstants(blackPoint.toFloat(), calculatedGain)
            _uiState.update { it.copy(calibrationStage = CalibrationStage.NONE, calibrationFinished = true) }
        }
    }

    fun onCalibrationFinishedHandled() {
        _uiState.update { it.copy(calibrationFinished = false) }
    }

    fun cancelCalibration() {
        _uiState.update { it.copy(calibrationStage = CalibrationStage.NONE, calibrationCancelled = true) }
    }

    fun onCalibrationCancelledHandled() {
        _uiState.update { it.copy(calibrationCancelled = false) }
    }

    fun toggleFilmSimulation() {
        _uiState.update { it.copy(isSimulatingFilm = !it.isSimulatingFilm) }
    }

    fun adjustEvOffset(delta: Float) {
        _uiState.update { state ->
            val newOffset = (state.evOffset + delta).coerceIn(-3.0f, 3.0f)
            state.copy(evOffset = newOffset)
        }
        updateSuggestions()
    }

    // 5-SPOT OM-4 SAMPLING ENGINE & DUAL BARREL SELECTOR
    fun addSpotSample(x: Float, y: Float, ev: Double) {
        _uiState.update { state ->
            val spotEvOffset = (0.5 - y.toDouble()) * 4.0
            val spotEv = (ev + spotEvOffset).coerceIn(1.0, 22.0)

            val newSample = SpotSample(
                id = System.currentTimeMillis() + (0..999).random(),
                x = x,
                y = y,
                ev = spotEv
            )
            val currentList = state.sampledSpots.toMutableList()
            if (currentList.size >= 5) {
                currentList.removeAt(0) // Maintain 5-spot FIFO queue
            }
            currentList.add(newSample)
            recalculateMultiSpotState(state, currentList)
        }
        updateSuggestions()
    }

    fun removeSpotSample(id: Long) {
        _uiState.update { state ->
            val currentList = state.sampledSpots.filter { it.id != id }
            recalculateMultiSpotState(state, currentList)
        }
        updateSuggestions()
    }

    fun clearSpotSamples() {
        _uiState.update { state ->
            recalculateMultiSpotState(state, emptyList())
        }
        updateSuggestions()
    }

    private fun recalculateMultiSpotState(state: LightMeterUiState, spots: List<SpotSample>): LightMeterUiState {
        if (spots.isEmpty()) {
            return state.copy(
                sampledSpots = emptyList(),
                averageEv = state.currentEv,
                minSpotEv = state.currentEv,
                maxSpotEv = state.currentEv,
                sceneContrastRange = 0.0,
                recommendedDevelopment = "N (Normal)"
            )
        }

        val avgEv = spots.map { it.ev }.average()
        val minEv = spots.minOf { it.ev }
        val maxEv = spots.maxOf { it.ev }
        val range = (maxEv - minEv).coerceAtLeast(0.0)

        val devRec = when {
            range > 10.0 -> "N-1 (Pull)"
            range < 5.0 && range > 0.5 -> "N+1 (Push)"
            else -> "N (Normal)"
        }

        return state.copy(
            sampledSpots = spots,
            averageEv = avgEv,
            minSpotEv = minEv,
            maxSpotEv = maxEv,
            sceneContrastRange = range,
            recommendedDevelopment = devRec
        )
    }

    fun setPriorityMode(mode: PriorityMode) {
        _uiState.update { it.copy(priorityMode = mode) }
    }

    fun onAvIndexChanged(index: Int) {
        _uiState.update { it.copy(selectedAvIndex = index) }
    }

    fun onTvIndexChanged(index: Int) {
        _uiState.update { it.copy(selectedTvIndex = index) }
    }

    // LONG-PRESS DIRECT PARAMETER LOCKING & SHIFT ENGINE
    fun toggleLockAperture(aperture: String) {
        _uiState.update { state ->
            if (state.lockState == ParameterLockState.APERTURE_LOCKED && state.lockedAperture == aperture) {
                state.copy(lockState = ParameterLockState.NONE, lockedAperture = null, exposureShiftStops = 0.0f)
            } else {
                state.copy(
                    lockState = ParameterLockState.APERTURE_LOCKED,
                    lockedAperture = aperture,
                    lockedShutter = null,
                    exposureShiftStops = 0.0f
                )
            }
        }
        updateSuggestions()
    }

    fun toggleLockShutter(shutter: String) {
        _uiState.update { state ->
            if (state.lockState == ParameterLockState.SHUTTER_LOCKED && state.lockedShutter == shutter) {
                state.copy(lockState = ParameterLockState.NONE, lockedShutter = null, exposureShiftStops = 0.0f)
            } else {
                state.copy(
                    lockState = ParameterLockState.SHUTTER_LOCKED,
                    lockedShutter = shutter,
                    lockedAperture = null,
                    exposureShiftStops = 0.0f
                )
            }
        }
        updateSuggestions()
    }

    fun shiftExposureStops(deltaStops: Float) {
        _uiState.update { state ->
            val newShift = (state.exposureShiftStops + deltaStops).coerceIn(-3.0f, 3.0f)
            state.copy(exposureShiftStops = newShift)
        }
        updateSuggestions()
    }

    fun unlockParameters() {
        _uiState.update { state ->
            state.copy(lockState = ParameterLockState.NONE, lockedAperture = null, lockedShutter = null, exposureShiftStops = 0.0f)
        }
        updateSuggestions()
    }
}

data class SpotSample(
    val id: Long = System.currentTimeMillis(),
    val x: Float,
    val y: Float,
    val ev: Double
)

enum class ParameterLockState {
    NONE,
    APERTURE_LOCKED,
    SHUTTER_LOCKED
}

enum class PriorityMode {
    AV_PRIORITY, // Aperture Priority (DoF Centered)
    TV_PRIORITY, // Shutter Priority (Motion Centered)
    PROGRAM      // Auto Balanced
}

data class LightMeterUiState(
    val currentEv: Double = 0.0,
    val evOffset: Float = 0.0f,
    val rawLuminance: Double = 0.0,
    val suggestions: List<ExposureSettings> = emptyList(),
    val meteringMode: MeteringMode = MeteringMode.CENTER_WEIGHTED,
    val spotRect: Rect? = null,
    val zoneAnalysis: com.fotologue.app.util.ZoneSystemAnalysis? = null,
    val spotEvHistory: List<Double> = emptyList(),
    val histogramBins: IntArray = IntArray(11),
    val hasHighlightClipping: Boolean = false,
    val hasShadowClipping: Boolean = false,
    val effectiveFocalLength: Double? = null,
    val effectiveMaxAperture: Double? = null,
    val currentFocalLength: Int? = null,
    val lens: Lens? = null,
    val teleconverter: Teleconverter? = null,
    val filter: Filter? = null,
    val film: Film? = null,
    val faceDetected: Boolean = false,
    val isSimulatingFilm: Boolean = true,
    val calibrationStage: CalibrationStage = CalibrationStage.NONE,
    val capturedBlackPoint: Double = 0.0,
    val isWizardOnly: Boolean = false,
    val calibrationFinished: Boolean = false,
    val calibrationCancelled: Boolean = false,
    val lockState: ParameterLockState = ParameterLockState.NONE,
    val lockedAperture: String? = null,
    val lockedShutter: String? = null,
    val exposureShiftStops: Float = 0.0f,
    val sampledSpots: List<SpotSample> = emptyList(),
    val averageEv: Double = 0.0,
    val minSpotEv: Double = 0.0,
    val maxSpotEv: Double = 0.0,
    val sceneContrastRange: Double = 0.0,
    val recommendedDevelopment: String = "N (Normal)",
    val priorityMode: PriorityMode = PriorityMode.AV_PRIORITY,
    val selectedAvIndex: Int = 0,
    val selectedTvIndex: Int = 0,
    val highlightGrid: Array<BooleanArray> = emptyArray(),
    val shadowGrid: Array<BooleanArray> = emptyArray()
)

enum class CalibrationStage {
    NONE,
    BLACK_POINT,
    GRAY_POINT
}
