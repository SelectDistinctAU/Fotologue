# Lowercase Package Rename Plan: com.Fotologue.app -> com.fotologue.app

Rename the application package to all lowercase `com.fotologue.app` to ensure full compatibility with Linux build servers and standard Android naming conventions.

## User Review Required

> [!IMPORTANT]
> This is a comprehensive refactor that affects every source file in the project. I will be moving files to new directory paths and updating all internal references.

## Proposed Changes

### Source Code Migration

#### [MODIFY] All Kotlin Source Files
Move all files and update package/import statements from `com.Fotologue.app` to `com.fotologue.app`.

Affected directories:
- `app/src/main/java/com/Fotologue/app` -> `app/src/main/java/com/fotologue/app`
- `app/src/androidTest/java/com/Fotologue/app` -> `app/src/androidTest/java/com/fotologue/app`
- `app/src/test/java/com/Fotologue/app` -> `app/src/test/java/com/fotologue/app`

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build.gradle.kts)
Update `namespace` and `applicationId` to `com.fotologue.app`.

#### [MODIFY] [app/src/main/AndroidManifest.xml](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/AndroidManifest.xml)
Update all class references (Application, Activity, Widget) to use the new lowercase package.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify the build.
- Run unit tests and instrumentation tests.

### Manual Verification
- Verify that the app builds without KSP errors on both Windows and a clean environment.
- Check that navigation and resource lookups (R class) still work correctly.
