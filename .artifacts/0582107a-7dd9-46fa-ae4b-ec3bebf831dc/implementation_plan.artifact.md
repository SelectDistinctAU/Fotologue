# Implementation Plan - Hiding BLE Features and Fixing Backup Issue

This plan covers removing the visible entry points for BLE/Remote Control features, deactivating background BLE logic, and fixing a reported issue where backup files are created with 0-byte content.

## User Review Required

> [!NOTE]
> The "Remote Control" feature will remain in the codebase (`Route.RemoteControl`, `RemoteControlScreen.kt`, etc.) but will no longer be accessible through the UI.

## Proposed Changes

### UI Clean-up

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/SettingsScreen.kt)
- Remove the "Connectivity" section which contains the link to "Remote Control".

#### [MODIFY] [TimelineScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/TimelineScreen.kt)
- Verify and remove any "Remote Control" related entry points if found (though initial analysis shows it might already be absent or located elsewhere).

### Logic Deactivation

#### [MODIFY] [FotologueApplication.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/FotologueApplication.kt)
- Comment out `triggerManager.startObserving()` in `onCreate()` to stop background BLE scanning.

### Fix Backup Issue

#### [MODIFY] [ConfigurationManager.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/ConfigurationManager.kt)
- Update `exportConfig` to ensure the `OutputStream` is flushed and closed properly.
- Switch to `Json.encodeToStream` for better reliability and performance.
- Add error handling to prevent 0-byte files if an error occurs during serialization.

## Verification Plan

### Automated Tests
- Run existing unit tests to ensure no regressions: `./gradlew test`
- Build the app: `./gradlew assembleDebug`

### Manual Verification
- Open Settings and verify the "Connectivity" section is gone.
- Verify the main screen (Timeline) does not show any Remote Control icons.
- Perform a Backup and verify the resulting file is not 0 bytes.
