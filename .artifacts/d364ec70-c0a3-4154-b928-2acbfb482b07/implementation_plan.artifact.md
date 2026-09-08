# Implementation Plan - Fix Room Relation Crash

The app is crashing on launch with `IllegalStateException` in `CameraKitDao_Impl`. This is likely due to Room's handling of `@Relation` properties that are missing from the database but declared as non-null in the generated DAO implementation, or failing to resolve the entity type correctly.

## Proposed Changes

### Data Components

#### [MODIFY] [CameraKitWithGear.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/CameraKitWithGear.kt)
- Update all `@Relation` properties to explicitly set the `entity` class.
- Ensure all properties are nullable.

#### [MODIFY] [LogEntryWithGear.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/LogEntryWithGear.kt)
- Update all `@Relation` properties to explicitly set the `entity` class.
- Ensure all properties are nullable.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/AppDatabase.kt)
- Increment database version to 16.

### Application Logic

#### [MODIFY] [FotologueApplication.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/FotologueApplication.kt)
- Comment out or delete sample data pre-population in `onCreate()`.

## Verification Plan

### Automated Tests
- Build the project using `./gradlew assembleDebug` to ensure DAO implementation is generated correctly.
- Run the app (if a device/emulator is available) to verify it no longer crashes on launch.

### Manual Verification
- Verify that the app launches into the onboarding flow (as pre-population is removed).
