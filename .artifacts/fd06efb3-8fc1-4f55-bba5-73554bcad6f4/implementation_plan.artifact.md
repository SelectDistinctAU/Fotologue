# Implementation Plan - Advanced Gear Management

Implement support for Teleconverters and Filters in Fotologue, including data layer updates, UI for management, and integration into the log entry flow.

## Proposed Changes

### Data Layer

#### [NEW] [Teleconverter.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Teleconverter.kt)
- Define `Teleconverter` entity with `id`, `name`, `magnification`, and `lightLossStops`.

#### [NEW] [Filter.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Filter.kt)
- Define `Filter` entity with `id`, `name`, `type`, and `compensationStops`.

#### [MODIFY] [LogEntry.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/LogEntry.kt)
- Add `teleconverterId: Long?` and `filterId: Long?`.
- Add foreign keys and indices for the new fields.

#### [MODIFY] [LogEntryWithGear.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/LogEntryWithGear.kt)
- Add `@Relation` for `Teleconverter` and `Filter`.

#### [MODIFY] [Daos.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Daos.kt)
- Add `TeleconverterDao` and `FilterDao`.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/AppDatabase.kt)
- Add `Teleconverter` and `Filter` to entities list.
- Add DAO abstract methods.
- Increment version to 6.

#### [MODIFY] [FotologueRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/FotologueRepository.kt)
- Add repository methods for Teleconverters and Filters.
- Update `logExposure` and `updateLogEntry` to handle the new fields.

### Navigation

#### [MODIFY] [Routes.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/navigation/Routes.kt)
- Add routes for `AddEditTeleconverter` and `AddEditFilter`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/MainActivity.kt)
- Register new routes in the `NavDisplay`.

### Gear Library UI

#### [MODIFY] [GearLibraryViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/GearLibraryViewModel.kt)
- Update state and methods to handle Teleconverters and Filters.

#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)
- Add tabs for "Teleconverters" and "Filters".
- Implement lists for both.

### CRUD Screens

#### [NEW] [AddEditTeleconverterViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditTeleconverterViewModel.kt)
- ViewModel for adding and editing teleconverters.

#### [NEW] [AddEditTeleconverterScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditTeleconverterScreen.kt)
- UI for adding and editing teleconverters.

#### [NEW] [AddEditFilterViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditFilterViewModel.kt)
- ViewModel for adding and editing filters.

#### [NEW] [AddEditFilterScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditFilterScreen.kt)
- UI for adding and editing filters.

### Log Entry UI

#### [MODIFY] [AddLogViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddLogViewModel.kt)
- Update `AddLogUiState` and `AddLogViewModel` to include optional TC and Filter selection.

#### [MODIFY] [AddLogScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddLogScreen.kt)
- Add dropdowns for Teleconverter and Filter.

### Integration Prep

#### [MODIFY] [LogEntryExporter.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/util/LogEntryExporter.kt)
- Include Teleconverter and Filter info in CSV export.

## Verification Plan

### Automated Tests
- Build the project using `./gradlew :app:assembleDebug`.

### Manual Verification
- Open Gear Library and add a new Teleconverter and Filter.
- Edit and delete them to verify CRUD.
- Create a new Log Entry and select the added Teleconverter and Filter.
- Verify the log details show the selected gear.
- Export logs to CSV and verify the new columns.
