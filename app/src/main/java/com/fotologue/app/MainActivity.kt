package com.fotologue.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.fotologue.app.ui.navigation.Route
import com.fotologue.app.ui.screens.*
import com.fotologue.core.designsystem.FotologueTheme
import com.fotologue.app.ui.viewmodel.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fotologue.app.data.FotologueRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var repository: FotologueRepository

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            FotologueTheme {
                val mainViewModel: MainViewModel = hiltViewModel()
                val isOnboardingComplete by mainViewModel.isOnboardingComplete.collectAsStateWithLifecycle(initialValue = null)

                if (isOnboardingComplete != null) {
                    key(isOnboardingComplete) {
                        val backStack = rememberNavBackStack(if (isOnboardingComplete == true) Route.Timeline else Route.Welcome)

                        val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
                        val directive = remember(windowAdaptiveInfo) {
                            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                                .copy(horizontalPartitionSpacerSize = 0.dp)
                        }
                        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

                        NavDisplay(
                            backStack = backStack,
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            },
                            sceneStrategy = listDetailStrategy,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator(),
                            ),
                            entryProvider = entryProvider {
                                entry<Route.Welcome> {
                                    val viewModel: OnboardingViewModel = hiltViewModel()
                                    WelcomeScreen(
                                        viewModel = viewModel,
                                        onAddCamera = { backStack.add(Route.AddCamera) },
                                        onAddLens = { backStack.add(Route.AddEditLens()) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onCalibrateMeter = { backStack.add(Route.LightMeter(targetIso = 400, autoStartCalibration = true)) },
                                        onShowHelp = { backStack.add(Route.Help) },
                                        onSettingsClick = { backStack.add(Route.Settings) }
                                    )
                                }
                                entry<Route.Timeline>(
                                    metadata = ListDetailSceneStrategy.listPane(
                                        detailPlaceholder = {
                                            androidx.compose.foundation.layout.Box(
                                                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                                                contentAlignment = androidx.compose.ui.Alignment.Center
                                            ) {
                                                androidx.compose.material3.Text("Select a log to see details")
                                            }
                                        }
                                    )
                                ) {
                                    val viewModel: TimelineViewModel = hiltViewModel()
                                    val selectedLogId = backStack.find { it is Route.LogDetail }?.let { (it as Route.LogDetail).logId }
                                    TimelineScreen(
                                        viewModel = viewModel,
                                        onAddLogClick = { backStack.add(Route.AddLog) },
                                        onLogClick = { logId ->
                                            backStack.removeIf { it is Route.LogDetail }
                                            backStack.add(Route.LogDetail(logId))
                                        },
                                        onGearLibraryClick = { backStack.add(Route.GearLibrary) },
                                        onSettingsClick = { backStack.add(Route.Settings) },
                                        onContactSheetClick = { backStack.add(Route.ContactSheet()) },
                                        selectedLogId = selectedLogId
                                    )
                                }
                                entry<Route.GearLibrary> {
                                    val viewModel: GearLibraryViewModel = hiltViewModel()
                                    GearLibraryScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onAddCamera = { backStack.add(Route.AddCamera) },
                                        onEditCamera = { id -> backStack.add(Route.EditCamera(id)) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onEditFilm = { id -> backStack.add(Route.EditFilm(id)) },
                                        onAddLens = { backStack.add(Route.AddEditLens()) },
                                        onEditLens = { id -> backStack.add(Route.AddEditLens(id)) },
                                        onAddTeleconverter = { backStack.add(Route.AddEditTeleconverter()) },
                                        onEditTeleconverter = { id -> backStack.add(Route.AddEditTeleconverter(id)) },
                                        onAddFilter = { backStack.add(Route.AddEditFilter()) },
                                        onEditFilter = { id -> backStack.add(Route.AddEditFilter(id)) },
                                        onAddFlash = { backStack.add(Route.AddEditFlash()) },
                                        onEditFlash = { id -> backStack.add(Route.AddEditFlash(id)) },
                                        onAddViewfinder = { backStack.add(Route.AddEditViewfinder()) },
                                        onEditViewfinder = { id -> backStack.add(Route.AddEditViewfinder(id)) },
                                        onAddKit = { backStack.add(Route.AddEditCameraKit()) },
                                        onEditKit = { id -> backStack.add(Route.AddEditCameraKit(id)) }
                                    )
                                }
                                entry<Route.AddEditFlash> { navRoute ->
                                    val viewModel: AddEditFlashViewModel = hiltViewModel(key = "AddEditFlash_${navRoute.flashId}")
                                    viewModel.setFlashId(navRoute.flashId)
                                    AddEditFlashScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditViewfinder> { navRoute ->
                                    val viewModel: AddEditViewfinderViewModel = hiltViewModel(key = "AddEditViewfinder_${navRoute.viewfinderId}")
                                    viewModel.setViewfinderId(navRoute.viewfinderId)
                                    AddEditViewfinderScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditCameraKit> { navRoute ->
                                    val viewModel: AddEditCameraKitViewModel = hiltViewModel(key = "AddEditCameraKit_${navRoute.kitId}")
                                    viewModel.setKitId(navRoute.kitId)
                                    AddEditCameraKitScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditTeleconverter> { navRoute ->
                                    val viewModel: AddEditTeleconverterViewModel = hiltViewModel(key = "AddEditTeleconverter_${navRoute.teleconverterId}")
                                    viewModel.setTeleconverterId(navRoute.teleconverterId)
                                    AddEditTeleconverterScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditFilter> { navRoute ->
                                    val viewModel: AddEditFilterViewModel = hiltViewModel(key = "AddEditFilter_${navRoute.filterId}")
                                    viewModel.setFilterId(navRoute.filterId)
                                    AddEditFilterScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditLens> { navRoute ->
                                    val viewModel: AddEditLensViewModel = hiltViewModel(key = "AddEditLens_${navRoute.lensId}_${navRoute.cameraId}")
                                    viewModel.setLensId(navRoute.lensId)
                                    navRoute.cameraId?.let { viewModel.onCameraToggled(it) }
                                    AddEditLensScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddCamera> {
                                    val viewModel: AddEditCameraViewModel = hiltViewModel()
                                    viewModel.setCameraId(null)
                                    AddEditCameraScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.EditCamera> { editRoute ->
                                    val viewModel: AddEditCameraViewModel = hiltViewModel(key = "EditCamera_${editRoute.cameraId}")
                                    viewModel.setCameraId(editRoute.cameraId)
                                    AddEditCameraScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddFilm> {
                                    val viewModel: AddEditFilmViewModel = hiltViewModel()
                                    viewModel.setFilmId(null)
                                    AddEditFilmScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.EditFilm> { editRoute ->
                                    val viewModel: AddEditFilmViewModel = hiltViewModel(key = "EditFilm_${editRoute.filmId}")
                                    viewModel.setFilmId(editRoute.filmId)
                                    AddEditFilmScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.LogDetail>(
                                    metadata = ListDetailSceneStrategy.detailPane()
                                ) { detailRoute ->
                                    val viewModel: LogDetailViewModel = hiltViewModel(key = "LogDetail_${detailRoute.logId}")
                                    viewModel.setLogId(detailRoute.logId)
                                    LogDetailScreen(
                                        viewModel = viewModel,
                                        onBack = {
                                            if (backStack.size > 1) {
                                                backStack.removeAt(backStack.size - 1)
                                            }
                                        },
                                        onEdit = { logId ->
                                            backStack.add(Route.EditLog(logId))
                                        },
                                        showBackButton = !windowAdaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(840)
                                    )
                                }
                                entry<Route.AddLog> {
                                    val viewModel: AddLogViewModel = hiltViewModel()
                                    viewModel.setEditingLogId(null)
                                    AddLogScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onMeasureExposure = { iso, cameraId, lensId, tcId, filterId, filmId ->
                                            backStack.add(Route.LightMeter(iso, cameraId, lensId, tcId, filterId, filmId))
                                        },
                                        onAddLens = { cameraId -> backStack.add(Route.AddEditLens(cameraId = cameraId)) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onNavigateToRollCounter = { backStack.add(Route.RollCounter) }
                                    )
                                }
                                entry<Route.EditLog> { editRoute ->
                                    val viewModel: AddLogViewModel = hiltViewModel(key = "EditLog_${editRoute.logId}")
                                    viewModel.setEditingLogId(editRoute.logId)
                                    AddLogScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onMeasureExposure = { iso, cameraId, lensId, tcId, filterId, filmId ->
                                            backStack.add(Route.LightMeter(iso, cameraId, lensId, tcId, filterId, filmId))
                                        },
                                        onAddLens = { cameraId -> backStack.add(Route.AddEditLens(cameraId = cameraId)) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onNavigateToRollCounter = { backStack.add(Route.RollCounter) }
                                    )
                                }
                                entry<Route.LightMeter> { lightRoute ->
                                    val viewModel: LightMeterViewModel = hiltViewModel(
                                        key = "LightMeter_${lightRoute.targetIso}_${lightRoute.cameraId}_${lightRoute.lensId}_${lightRoute.teleconverterId}_${lightRoute.filterId}_${lightRoute.filmId}_${lightRoute.autoStartCalibration}"
                                    )
                                    viewModel.setParams(
                                        targetIso = lightRoute.targetIso,
                                        cameraId = lightRoute.cameraId,
                                        lensId = lightRoute.lensId,
                                        teleconverterId = lightRoute.teleconverterId,
                                        filterId = lightRoute.filterId,
                                        filmId = lightRoute.filmId,
                                        autoStartCalibration = lightRoute.autoStartCalibration
                                    )
                                    LightMeterScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onUseSettings = { settings ->
                                            repository.setMeteringResult(settings)
                                            if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                                        }
                                    )
                                }
                                entry<Route.Settings> {
                                    val viewModel: SettingsViewModel = hiltViewModel()
                                    SettingsScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onNavigateToCalibration = { backStack.add(Route.Calibration) },
                                        onNavigateToRollCounter = { backStack.add(Route.RollCounter) },
                                        onShowHelp = { backStack.add(Route.Help) }
                                    )
                                }
                                entry<Route.RemoteControl> {
                                    val viewModel: RemoteControlViewModel = hiltViewModel()
                                    RemoteControlScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.Calibration> {
                                    val viewModel: SettingsViewModel = hiltViewModel()
                                    CalibrationScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onNavigateToWizard = { backStack.add(Route.LightMeter(targetIso = 400, autoStartCalibration = true)) }
                                    )
                                }
                                entry<Route.RollCounter> {
                                    val viewModel: RollCounterViewModel = hiltViewModel()
                                    RollCounterScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.ContactSheet> {
                                    val viewModel: ContactSheetViewModel = hiltViewModel()
                                    ContactSheetScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onLogClick = { logId ->
                                            backStack.add(Route.LogDetail(logId))
                                        },
                                        onAddLogForFrame = { _ ->
                                            backStack.add(Route.AddLog)
                                        }
                                    )
                                }
                                entry<Route.Help> {
                                    HelpScreen(
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
