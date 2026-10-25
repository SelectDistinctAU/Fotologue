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
import androidx.compose.runtime.LaunchedEffect
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
import com.fotologue.core.data.FotologueRepository
import com.fotologue.feature.lab.AddEditInventoryMasterScreen
import com.fotologue.feature.lab.AddEditSupplierScreen
import com.fotologue.feature.lab.AddEquipmentScreen
import com.fotologue.feature.lab.AddStockLotScreen
import com.fotologue.feature.lab.EnlargerPrintingSessionScreen
import com.fotologue.feature.lab.FilmProcessingSessionScreen
import com.fotologue.feature.lab.LabHomeScreen
import com.fotologue.feature.lab.LabHomeViewModel
import com.fotologue.feature.lab.LabMaintenanceScreen
import com.fotologue.feature.lab.SupplierIssueLogScreen
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
            val mainViewModel: MainViewModel = hiltViewModel()
            val isOnboardingComplete by mainViewModel.isOnboardingComplete.collectAsStateWithLifecycle(initialValue = null)
            val isSafelightActive by mainViewModel.isSafelightActive.collectAsStateWithLifecycle(initialValue = false)
            val activeThemeString by mainViewModel.appTheme.collectAsStateWithLifecycle(initialValue = "SYSTEM_DEFAULT")

            val activeTheme = remember(activeThemeString, isSafelightActive) {
                if (isSafelightActive) {
                    com.fotologue.core.designsystem.PhotographicTheme.SAFELIGHT_RED
                } else {
                    try {
                        com.fotologue.core.designsystem.PhotographicTheme.valueOf(activeThemeString)
                    } catch (_: Exception) {
                        com.fotologue.core.designsystem.PhotographicTheme.SYSTEM_DEFAULT
                    }
                }
            }

            FotologueTheme(photographicTheme = activeTheme) {

                if (isOnboardingComplete != null) {
                    key(isOnboardingComplete) {
                        val backStack = rememberNavBackStack(if (isOnboardingComplete == true) Route.Hub else Route.Welcome)

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
                                entry<Route.Hub> {
                                    HubScreen(
                                        isSafelightActive = isSafelightActive,
                                        onToggleSafelight = { mainViewModel.toggleSafelight() },
                                        onNavigateToCameraGear = { backStack.add(Route.GearLibrary) },
                                        onNavigateToTaxonomy = { backStack.add(Route.Taxonomy) },
                                        onNavigateToField = { backStack.add(Route.Timeline) },
                                        onNavigateToContacts = { backStack.add(Route.Lab(initialTab = 1)) },
                                        onNavigateToLab = { backStack.add(Route.Lab()) },
                                        onNavigateToStation = { backStack.add(Route.ContactSheet()) },
                                        onNavigateToArchive = { backStack.add(Route.ArchiveVault) },
                                        onNavigateToSettings = { backStack.add(Route.Settings) }
                                    )
                                }
                                entry<Route.Taxonomy> {
                                    val viewModel: TaxonomyViewModel = hiltViewModel()
                                    TaxonomyScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.ArchiveVault> {
                                    val viewModel: SettingsViewModel = hiltViewModel()
                                    ArchiveScreen(
                                        viewModel = viewModel,
                                        isSafelightActive = isSafelightActive,
                                        onToggleSafelight = { mainViewModel.toggleSafelight() },
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
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
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onAddLogClick = { backStack.add(Route.AddLog) },
                                        onLogClick = { logId ->
                                            backStack.removeIf { it is Route.LogDetail }
                                            backStack.add(Route.LogDetail(logId))
                                        },
                                        onGearLibraryClick = { backStack.add(Route.GearLibrary) },
                                        onTaxonomyClick = { backStack.add(Route.Taxonomy) },
                                        onSettingsClick = { backStack.add(Route.Settings) },
                                        onContactSheetClick = { backStack.add(Route.ContactSheet()) },
                                        onLabClick = { backStack.add(Route.Lab()) },
                                        selectedLogId = selectedLogId
                                    )
                                }
                                entry<Route.Lab> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    LabHomeScreen(
                                        initialTab = navRoute.initialTab,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onNavigateToMaintenance = { backStack.add(Route.LabMaintenance) },
                                        onAddItemMaster = { defaultCat -> backStack.add(Route.AddEditItemMaster(defaultCategory = defaultCat)) },
                                        onEditItemMaster = { id -> backStack.add(Route.AddEditItemMaster(id)) },
                                        onAddStockLot = { targetMasterId -> backStack.add(Route.AddStockLot(masterId = targetMasterId)) },
                                        onEditStockLot = { id -> backStack.add(Route.EditStockLot(id)) },
                                        onAddSupplier = { backStack.add(Route.AddEditSupplier()) },
                                        onEditSupplier = { id -> backStack.add(Route.AddEditSupplier(id)) },
                                        onViewSupplierIssues = { id -> backStack.add(Route.SupplierIssueTracker(id)) },
                                        onAddEquipment = { backStack.add(Route.AddEquipment) },
                                        onEditEquipment = { id -> backStack.add(Route.EditEquipment(id)) },
                                        onAddCamera = { backStack.add(Route.AddCamera) },
                                        onAddLens = { backStack.add(Route.AddEditLens()) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onAddFilter = { backStack.add(Route.AddEditFilter()) },
                                        onAddFlash = { backStack.add(Route.AddEditFlash()) },
                                        onAddViewfinder = { backStack.add(Route.AddEditViewfinder()) },
                                        onAddTeleconverter = { backStack.add(Route.AddEditTeleconverter()) },
                                        onStartFilmSession = { backStack.add(Route.FilmProcessingSession()) },
                                        onStartPrintingSession = { backStack.add(Route.EnlargerPrintingSession()) },
                                        onViewLogDetail = { logId -> backStack.add(Route.LogDetail(logId)) }
                                    )
                                }
                                entry<Route.SupplierIssueTracker> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    SupplierIssueLogScreen(
                                        supplierId = navRoute.supplierId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditSupplier> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddEditSupplierScreen(
                                        supplierId = navRoute.supplierId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.LabMaintenance> {
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    LabMaintenanceScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.FilmProcessingSession> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    FilmProcessingSessionScreen(
                                        rollId = navRoute.rollId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.EnlargerPrintingSession> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    EnlargerPrintingSessionScreen(
                                        logId = navRoute.logId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEditItemMaster> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddEditInventoryMasterScreen(
                                        masterId = navRoute.masterId,
                                        defaultCategory = navRoute.defaultCategory,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSaveAndAddStock = { savedMasterId ->
                                            if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                                            backStack.add(Route.AddStockLot(masterId = savedMasterId))
                                        }
                                    )
                                }
                                entry<Route.AddStockLot> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddStockLotScreen(
                                        masterId = navRoute.masterId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onAddNewSupplier = { backStack.add(Route.AddEditSupplier()) }
                                    )
                                }
                                entry<Route.EditStockLot> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddStockLotScreen(
                                        lotId = navRoute.lotId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.AddEquipment> {
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddEquipmentScreen(
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
                                    )
                                }
                                entry<Route.EditEquipment> { navRoute ->
                                    val viewModel: LabHomeViewModel = hiltViewModel()
                                    AddEquipmentScreen(
                                        equipmentId = navRoute.equipmentId,
                                        viewModel = viewModel,
                                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
                                        onSuccess = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) }
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
                                    navRoute.cameraId?.let { viewModel.preSelectCameraId(it) }
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
                                        onMeasureExposure = { iso, cameraId, lensId, tcId, filterId, filmId, focalLength ->
                                            backStack.add(Route.LightMeter(iso, cameraId, lensId, tcId, filterId, filmId, focalLength))
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
                                        onMeasureExposure = { iso, cameraId, lensId, tcId, filterId, filmId, focalLength ->
                                            backStack.add(Route.LightMeter(iso, cameraId, lensId, tcId, filterId, filmId, focalLength))
                                        },
                                        onAddLens = { cameraId -> backStack.add(Route.AddEditLens(cameraId = cameraId)) },
                                        onAddFilm = { backStack.add(Route.AddFilm) },
                                        onNavigateToRollCounter = { backStack.add(Route.RollCounter) }
                                    )
                                }
                                entry<Route.LightMeter> { lightRoute ->
                                    val viewModel: LightMeterViewModel = hiltViewModel(
                                        key = "LightMeter_${lightRoute.targetIso}_${lightRoute.cameraId}_${lightRoute.lensId}_${lightRoute.teleconverterId}_${lightRoute.filterId}_${lightRoute.filmId}_${lightRoute.focalLength}_${lightRoute.autoStartCalibration}"
                                    )
                                    LaunchedEffect(lightRoute) {
                                        viewModel.setParams(
                                            targetIso = lightRoute.targetIso,
                                            cameraId = lightRoute.cameraId,
                                            lensId = lightRoute.lensId,
                                            teleconverterId = lightRoute.teleconverterId,
                                            filterId = lightRoute.filterId,
                                            filmId = lightRoute.filmId,
                                            focalLength = lightRoute.focalLength,
                                            autoStartCalibration = lightRoute.autoStartCalibration
                                        )
                                    }
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
                                        isSafelightActive = isSafelightActive,
                                        onToggleSafelight = { mainViewModel.toggleSafelight() },
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
