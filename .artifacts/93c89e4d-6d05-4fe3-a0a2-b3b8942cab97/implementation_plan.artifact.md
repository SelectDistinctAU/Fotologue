# Implementation Plan - Light Meter Feature

Implement a Light Meter feature to help users determine the correct exposure settings (Aperture, Shutter Speed) for their film photography logs based on the scene luminance measured via the device camera.

## User Review Required

> [!IMPORTANT]
> The Light Meter requires Camera permissions. A permission request will be shown when the user clicks "Measure Exposure".

> [!NOTE]
> The exposure calculation assumes a standard calibration constant (K=12.5) for reflected light metering. Results are suggestions and may vary depending on device sensor calibration.

## Proposed Changes

### Build Configuration & Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/AndroidManifest.xml)
- Add `<uses-feature android:name="android.hardware.camera" />`
- Add `<uses-permission android:name="android.permission.CAMERA" />`

### Data & Navigation

#### [MODIFY] [Routes.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/navigation/Routes.kt)
- Add `LightMeter(val targetIso: Int)` route.

#### [MODIFY] [FotologueRepository.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/data/FotologueRepository.kt)
- Add `latestMeteringResult` StateFlow to communicate findings back to `AddLogScreen`.

### Utilities

#### [NEW] [MeteringMode.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/util/MeteringMode.kt)
- Enum for `SPOT` and `CENTER_WEIGHTED` modes.

#### [NEW] [LuminanceAnalyzer.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/util/LuminanceAnalyzer.kt)
- Implements `ImageAnalysis.Analyzer`.
- Calculates average luminance of the selected region.
- Calculates EV100 using camera metadata (via `Camera2Interop`) and luminance data.

#### [NEW] [ExposureSuggester.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/util/ExposureSuggester.kt)
- Generates Aperture/Shutter Speed pairs for a given EV100 and ISO.

### UI & ViewModels

#### [NEW] [LightMeterViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/LightMeterViewModel.kt)
- Manages metering state: current EV, suggested settings, metering mode, and spot location.

#### [NEW] [LightMeterScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/LightMeterScreen.kt)
- CameraX `PreviewView` integration.
- UI overlay for EV display, settings list, and mode toggle.
- "Use these settings" button.

#### [MODIFY] [AddLogViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/viewmodel/AddLogViewModel.kt)
- Observe `latestMeteringResult` from repository and update UI state.

#### [MODIFY] [AddLogScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/AddLogScreen.kt)
- Add "Measure Exposure" button that navigates to `LightMeter` route.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/MainActivity.kt)
- Register `LightMeter` entry in `NavDisplay`.

## Verification Plan

### Automated Tests
- Unit tests for `ExposureSuggester` to verify EV to settings conversion.
- Unit tests for `LuminanceAnalyzer` math (mocking `ImageProxy`).

### Manual Verification
- Launch the app, go to "Add Log".
- Click "Measure Exposure".
- Grant Camera permission.
- Verify camera preview works.
- Toggle between Spot and Center-weighted metering.
- Tap to move spot and see EV change.
- Select an exposure setting and click "Use these settings".
- Verify "Add Log" fields are populated correctly.
