# Enhance Gear Management and Settings Implementation Plan

This plan outlines the steps to add technical specs (shutter speeds and apertures) to cameras, implement a camera database for easy population of these specs, and add a settings screen using DataStore.

## User Review Required

> [!IMPORTANT]
> The `Camera` Room entity will be updated, which requires a database migration or destructive migration (since it's an early-stage app, I'll use `fallbackToDestructiveMigration`).

## Proposed Changes

### Data Layer

#### [MODIFY] [Camera.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Camera.kt)
- Add `shutterSpeeds: List<String>` and `apertures: List<String>`.

#### [NEW] [CameraSpec.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/CameraSpec.kt)
- Define `CameraSpec` data model.

#### [NEW] [CameraDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/CameraDatabase.kt)
- A hardcoded utility containing specs for common cameras (Leica M6, Nikon F3, etc.).

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/AppDatabase.kt)
- Add `TypeConverters` for `List<String>`.
- Set `fallbackToDestructiveMigration()`.

---

### Settings

#### [NEW] [SettingsRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/SettingsRepository.kt)
- Implement DataStore preferences for `isThumbnailEnabled`.

#### [NEW] [SettingsViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/SettingsViewModel.kt)
- Manage settings state.

#### [NEW] [SettingsScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/SettingsScreen.kt)
- UI for settings with a switch for thumbnail capture.

---

### Gear Library (Update)

#### [MODIFY] [AddEditCameraViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditCameraViewModel.kt)
- Add fields for shutter speeds and apertures.
- Implement `searchDatabase(query: String)` and `applySpec(spec: CameraSpec)`.

#### [MODIFY] [AddEditCameraScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditCameraScreen.kt)
- Add inputs for shutter speeds and apertures.
- Add "Search Database" feature.

#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)
- Update Camera list items to show technical specs.

---

### Navigation

#### [MODIFY] [Routes.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/navigation/Routes.kt)
- Add `Route.Settings`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/MainActivity.kt)
- Handle `Route.Settings`.

#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/TimelineScreen.kt)
- Add Settings icon button to top app bar.

## Verification Plan

### Automated Tests
- Build the project: `./gradlew assembleDebug`
- (Optional) Unit tests for `SettingsRepository` and `CameraDatabase`.

### Manual Verification
- Open Gear Library and add a camera.
- Search for a camera in the database and verify specs auto-populate.
- Save the camera and verify it appears in the list with specs.
- Open Settings and toggle the thumbnail switch.
- Verify navigation to Settings works from Timeline.
