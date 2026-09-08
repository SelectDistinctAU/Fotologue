# Implementation Plan - Fix Camera Permissions in LightMeterScreen

The user reported that the camera isn't being used in the `LightMeterScreen`. Investigation revealed that while the `CAMERA` permission is declared in `AndroidManifest.xml`, it is not requested at runtime in the Compose UI.

## Proposed Changes

### UI Layer

#### [MODIFY] [LightMeterScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/src/main/java/com/example/fotologue/ui/screens/LightMeterScreen.kt)
- Integrate `com.google.accompanist.permissions` to handle runtime camera permission.
- Add a permission request flow:
    - If permission is granted: Display the camera preview and start image analysis.
    - If permission is denied or not requested: Display a rationale or a button to request permission.
- Ensure the `LaunchedEffect` that initializes the camera only executes when permission is granted.

## Verification Plan

### Manual Verification
- Launch the app and navigate to the Light Meter screen.
- Verify that a permission request dialog appears.
- Verify that if permission is granted, the camera preview starts.
- Verify that if permission is denied, a proper message/button is shown instead of a black screen.
- Verify that if the user grants permission after a denial, the camera starts correctly.
