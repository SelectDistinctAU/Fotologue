# Gear Library CRUD Implementation Plan

Implement CRUD operations for Cameras and Films, including UI for list management and add/edit forms.

## User Review Required

> [!IMPORTANT]
> The navigation drawer/rail is not yet implemented in the app. I will add a "Gear Library" action to the `TimelineScreen` top bar for now, as it's the primary entry point.

## Proposed Changes

### Data Layer

#### [MODIFY] [Daos.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/Daos.kt)
- Add `getCameraById(id: Long)` to `CameraDao`.
- Add `deleteCameras(ids: List<Long>)` to `CameraDao`.
- Add `getFilmById(id: Long)` to `FilmDao`.
- Add `deleteFilms(ids: List<Long>)` to `FilmDao`.

#### [MODIFY] [FotologueRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/FotologueRepository.kt)
- Expose new DAO methods: `getCameraById`, `updateCamera`, `deleteCamera`, `deleteCameras`, `getFilmById`, `updateFilm`, `deleteFilm`, `deleteFilms`.

---

### Navigation

#### [MODIFY] [Routes.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/navigation/Routes.kt)
- Add `GearLibrary` route.
- Add `AddCamera` and `EditCamera(cameraId: Long)` routes.
- Add `AddFilm` and `EditFilm(filmId: Long)` routes.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/MainActivity.kt)
- Register new routes in `NavDisplay`.
- Provide ViewModels for the new screens.

---

### ViewModels

#### [NEW] [GearLibraryViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/GearLibraryViewModel.kt)
- Manages states for Cameras and Films tabs.
- Handles selection and bulk deletion.

#### [NEW] [AddEditCameraViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditCameraViewModel.kt)
- Handles camera form state and validation.

#### [NEW] [AddEditFilmViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddEditFilmViewModel.kt)
- Handles film form state and validation.

---

### UI Screens

#### [NEW] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/GearLibraryScreen.kt)
- Tabbed interface using `PrimaryTabRow`.
- Lists with selection support.
- FAB for adding items.

#### [NEW] [AddEditCameraScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditCameraScreen.kt)
- Form with Name and Type fields.

#### [NEW] [AddEditFilmScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddEditFilmScreen.kt)
- Form with Name, ISO, and Type fields.

#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/TimelineScreen.kt)
- Add icon button to navigate to `GearLibrary`.

## Verification Plan

### Automated Tests
- Run existing tests to ensure no regressions.
- (Optional) Add unit tests for new ViewModels if time permits.

### Manual Verification
- Navigate to Gear Library from Timeline.
- Add, Edit, and Delete Cameras and Films.
- Verify that "Add Log Entry" dropdowns reflect changes in the Gear Library.
- Test selection and bulk deletion.
