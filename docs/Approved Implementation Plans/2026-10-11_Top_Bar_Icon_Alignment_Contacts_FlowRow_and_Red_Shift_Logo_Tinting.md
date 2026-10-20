# Top-Bar Icon Alignment, Red Shift Icon Standardization & Logo Tinting Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 12:00 PM UTC

---

This plan standardizes top-bar icons across Mobile screens, **standardizes 100% on the Material Symbols Night Sight Max icon (`night_sight_max_24px`) for Red Shift toggles across all top bars**, reinforces its purpose in Settings, applies dynamic Red Shift logo tinting, and records all changes in the permanent Bug Registry & Regression Checklist (`docs/bug_registry_and_regression_checklist.md`):

1. **Standardize Red Shift Top-Bar Control (`night_sight_max_24px`)**:
   - Replace bulky `Text("Red Shift") + Switch` combos across `LabHomeScreen.kt`, `ContactSheetScreen.kt`, and `ArchiveScreen.kt` with a single, elegant 48dp `IconButton` using `R.drawable.night_sight_max_24px` (Crescent Moon +).
   - Tapping the icon instantly toggles Red Shift mode on/off everywhere with 100% top-bar consistency and saves 80dp+ of horizontal space!
2. **Reinforce Night Sight Max Purpose in Settings (`SettingsScreen.kt`)**:
   - Replace `ℹ️` in the `Red Shift & Night Vision Notice` header with `R.drawable.night_sight_max_24px` icon.
   - Add explanation text: *"Tap the Night Sight icon (🌙+) in top bars for 1-tap Red Shift toggling."*
3. **Dynamic Red Shift Logo Tinting (`HubScreen.kt`)**:
   - Apply dynamic `colorFilter = if (isSafelightActive) ColorFilter.tint(Color(0xFFFF0000)) else null` to the top-left Staging Portal logo, converting it to pure crimson red in Red Shift mode so zero white or green light escapes.
4. **Camera Gear Top-Bar Icon Alignment (`GearLibraryScreen.kt`)**:
   - Insert `settings_photo_camera_24px` vector icon directly in the top bar title row next to `Camera Gear` on both list pane and detail pane top bars.
5. **Contacts Directory Cleanup (`AddEditSupplierScreen.kt` & `LabHomeScreen.kt`)**:
   - Replace warehouse icon with `patient_list_24px` icon in top bar.
   - Replace rigid `Row` with `@OptIn(ExperimentalLayoutApi::class) FlowRow` for contacts filter chips to eliminate right-edge chip squashing on physical phones (`[ USA / Global ]`).
   - Remove maintenance crossed-tools icon (`ic_handyman`) and remove Red Shift toggle.
6. **Bug Registry & Feature Register Documentation Update**:
   - Record BUG-131, BUG-132, BUG-133, BUG-134, and BUG-135 in `docs/bug_registry_and_regression_checklist.md`.

---

## 1. Generated Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.1 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |

---

## 2. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :app:testDebugUnitTest`.
2. Launch Mobile App $\to$ open `Settings` $\to$ verify `Red Shift & Night Vision Notice` header renders the `night_sight_max_24px` icon!
3. Inspect `CAMERA GEAR` $\to$ verify top bar displays `← 📷 Camera Gear` on both list and detail panes!
4. Open `CONTACTS DIRECTORY` $\to$ verify `← 👥 Contacts Directory` with `patient_list_24px` icon and `FlowRow` filter chips wrapping cleanly!
5. Inspect `LAB`, `STATION`, and `ARCHIVE` $\to$ verify top-bar Red Shift control renders as the **Crescent Moon + Icon Button** (`night_sight_max_24px`)!
6. Activate **Red Shift Mode** $\to$ verify Staging Portal top-left logo tints to pure crimson red (`#FF0000`) with zero white/green light leakage!
7. Verify `docs/bug_registry_and_regression_checklist.md` is updated with BUG-131 through BUG-135.
