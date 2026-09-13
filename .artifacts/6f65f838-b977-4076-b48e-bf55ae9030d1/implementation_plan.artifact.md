# Add Viewfinder Accessory Support Implementation Plan

Add a new `Viewfinder` gear entity and link it to camera gear libraries and individual shot logs, allowing users to track which viewfinder was used per shot (e.g. for modular cameras like the Pentax LX).

## Proposed Changes

### [Database & Data Models]

#### [NEW] [Viewfinder.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/data/Viewfinder.kt)
Create the `Viewfinder` entity and cross-ref with cameras if needed (or simple foreign key).

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/data/AppDatabase.kt)
Register `Viewfinder` and its DAO, bump database version and add migration or schema update.

#### [MODIFY] [LogEntry.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/data/LogEntry.kt)
Add `viewfinderId: Long? = null` to `LogEntry`.

### [Gear Library & UI]

#### [NEW] [AddEditViewfinderScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/AddEditViewfinderScreen.kt)
Screen to add/edit viewfinder gear.

#### [NEW] [AddEditViewfinderViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/viewmodel/AddEditViewfinderViewModel.kt)
ViewModel for viewfinder management.

#### [MODIFY] [GearLibraryScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/GearLibraryScreen.kt)
Include Viewfinders in the Gear Library tabs/sections.

#### [MODIFY] [AddLogScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/fotologue/app/ui/screens/AddLogScreen.kt)
Add Viewfinder picker dropdown/selector.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify database schema, Room queries, and UI compilation.

### Manual Verification
- Add a viewfinder (e.g., "Pentax LX FA-1 Finder") in Gear Library.
- Create or edit a shot log and pick the viewfinder.
- Verify it displays correctly in log details and exports.
