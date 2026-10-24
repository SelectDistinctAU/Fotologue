# Pre-Arrival Orders Full-Screen Form Conversion & Direct MasterPresetEngine Unification Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 08:00 PM UTC
- **GitHub Commit & Push**: `23da816` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan converts Pre-Arrival Asset Orders to a full-screen view (non-modal), connects preset selection directly to `MasterPresetEngine` (restoring instant access to all 82 master camera bodies, 26 lenses, and film presets), and replaces raw emoji icons with M3 vector icons:

1. **Full-Screen Form Conversion (`PreArrivalOrdersScreen.kt`)**:
   - Converted `showAddEditDialog` form from a modal popup `AlertDialog` into a clean full-screen form view (`Scaffold` with `TopAppBar`).
   - Title: `← Log Pre-Arrival Asset Order` (Step 1 of 3: Classification, Step 2 of 3: Asset Specs, Step 3 of 3: Order Details).
2. **Direct `MasterPresetEngine` Integration**:
   - Connected `PreArrivalOrdersScreen.kt` directly to `MasterPresetEngine.getPresetsByCategory(presetCategoryKey)`:
     - `Camera` $\to$ `MasterPresetEngine.getPresetsByCategory("CAMERA")` (returns all 82 master camera bodies).
     - `Lens` $\to$ `MasterPresetEngine.getPresetsByCategory("LENS")` (returns all 26 master lenses).
     - `Film Stock` $\to$ `MasterPresetEngine.getPresetsByCategory("FILM")` (returns all master film stocks).
     - `Paper` / `Consumable` / `Chemical` $\to$ `MasterPresetEngine.getPresetsByCategory("CONSUMABLE")` + `masterList`.
3. **M3 Icon Standardization**:
   - Replaced raw text `"⚙️ Select Camera Preset"` with M3 `Icon(painterResource(R.drawable.settings_photo_camera_24px))`.

---

## 1. Generated Production Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.2 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.2 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |
| **Windows MSI Installer** | `FotologueDesktop-1.0.4.msi` | **153.3 MB** | [`desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.4.msi`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.4.msi) |
| **Windows EXE Installer** | `FotologueDesktop-1.0.4.exe` | **154.0 MB** | [`desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.4.exe`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.4.exe) |

---

## 2. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :desktop:packageWindows :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Mobile App on `emulator-5554` $\to$ `LAB` $\to$ `Pre-Arrival Orders`:
   - Tap `[ Log Pre-Arrival Asset Order ]` $\to$ verify full-screen view opens (non-modal)!
   - Step 1: Select `Camera` $\to$ tap Next.
   - Step 2: Tap `[ Select Camera from Master Presets ]` $\to$ verify preset browser opens showing Pentax LX, Nikon F3, Leica M6, Hasselblad 503CX, etc.!
   - Select *Pentax LX* $\to$ verify Asset Name and Specs populate cleanly!
