# Implementation Plan - Trigger Integration and GPS UI

Implement automatic BLE logging based on JSON triggers, persistent gear tracking, and human-readable GPS coordinates in the UI.

## User Review Required

> [!IMPORTANT]
> The app will now automatically log exposures when a specific BLE trigger is received. This requires the BLE device to be connected and the app to be in a state where it can receive these notifications.

## Proposed Changes

### [Component Name] Connectivity

#### [MODIFY] [BleManager.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/connectivity/BleManager.kt)
- No direct changes needed if `receivedData` is already a `SharedFlow`.

#### [NEW] [TriggerManager.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/connectivity/TriggerManager.kt)
- Observes `BleManager.receivedData`.
- Parses JSON: `{"event":"string","count":int}`.
- On trigger, fetches location and current gear, then saves a new `LogEntry`.

### [Component Name] Data

#### [MODIFY] [SettingsRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/SettingsRepository.kt)
- Add DataStore keys and methods to persist last selected gear (Camera, Film, Lens, etc.) and exposure settings.

#### [NEW] [GearConfigurationManager.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/GearConfigurationManager.kt)
- Orchestrates saving and loading the "Current Gear Configuration" using `SettingsRepository`.
- Provides a `StateFlow` of the current configuration.

### [Component Name] UI & ViewModel

#### [MODIFY] [AddLogViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddLogViewModel.kt)
- Update to use `GearConfigurationManager` to persist gear selections whenever they change.
- Initialize `uiState` from `GearConfigurationManager`.

#### [MODIFY] [LogDetailScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/LogDetailScreen.kt)
- Format `latitude` and `longitude` to "Lat: X.XXXX, Long: Y.YYYY".

#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/TimelineScreen.kt)
- Format `latitude` and `longitude` in the list items if displayed, or in the detail view.

### [Component Name] Application

#### [MODIFY] [FotologueApplication.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/FotologueApplication.kt)
- Initialize `GearConfigurationManager` and `TriggerManager`.
- Start `TriggerManager` to listen for BLE events.

## Verification Plan

### Automated Tests
- Unit test for JSON parsing in `TriggerManager`.
- Unit test for `GearConfigurationManager` persistence.

### Manual Verification
- Connect a BLE device and send `{"event":"shot","count":1}`.
- Verify a new log entry is created with current gear and location.
- Verify GPS coordinates are displayed correctly in `LogDetailScreen` and `TimelineScreen`.
- Verify gear selection persists after closing and reopening the app.
