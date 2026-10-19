# Android 15 (SDK 35) Edge-to-Edge System Bar Appearance Compliance Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 10:00 AM UTC

---

This plan resolves the Google Play Console Android 15 (SDK 35) edge-to-edge warning by integrating `WindowCompat.getInsetsController` appearance controllers in `Theme.kt`, ensuring status and navigation bar icon contrast adapts dynamically across all 8 photographic themes.

---

## 1. Technical Fix Specifications

### A. Edge-to-Edge System Bar Appearance (`Theme.kt` & `MainActivity.kt`)
- **Root Cause**: Un-managed system bar icon contrast when switching between light (`Daylight Clean`) and dark/red (`Red Shift`, `Warm Sepia`, `Silver Gelatin`, `Flat Darcula`, `Flat Mac Dark`, `Flat Nord`) photographic themes.
- **Fix**:
  1. Maintained `enableEdgeToEdge()` in `MainActivity.kt` before `setContent` with `isNavigationBarContrastEnforced = false` for SDK 29+.
  2. Integrated `WindowCompat.getInsetsController` `SideEffect` in `Theme.kt`:
     ```kotlin
     val view = LocalView.current
     if (!view.isInEditMode) {
         SideEffect {
             val window = (view.context as? Activity)?.window ?: return@SideEffect
             val insetsController = WindowCompat.getInsetsController(window, view)
             
             val isLightMode = photographicTheme == PhotographicTheme.DAYLIGHT_LIGHT || 
                 (photographicTheme == PhotographicTheme.SYSTEM_DEFAULT && !darkTheme)
             
             insetsController.isAppearanceLightStatusBars = isLightMode
             insetsController.isAppearanceLightNavigationBars = isLightMode
         }
     }
     ```

---

## 2. Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.0 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |

---

## 3. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :app:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `Settings` $\to$ toggle between `Daylight Clean` and `Red Shift`.
3. Verify status bar icons switch dynamically between dark icons (on light background) and light icons (on dark background).
