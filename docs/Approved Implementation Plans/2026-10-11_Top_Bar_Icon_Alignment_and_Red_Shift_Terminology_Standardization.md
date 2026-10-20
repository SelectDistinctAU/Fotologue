# Top-Bar Module Icon Alignment, Red Shift Terminology & Scope Removal Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 11:30 AM UTC

---

This plan standardizes top-bar icons, renames all top-bar toggles from `"Safelight"` to **`"Red Shift"`**, and removes the unneeded Red Shift toggle from **Contacts Directory**:

1. **Staging Portal (`HubScreen.kt`)**: Use Material Symbols **Night Sight Max** icon (`night_sight_max_24px`) for Red Shift button.
2. **Camera Gear (`GearLibraryScreen.kt`)**: Add `settings_photo_camera_24px` icon to top bar title row next to `Camera Gear`.
3. **Contacts Directory (`AddEditSupplierScreen.kt`)**:
   - Replace warehouse icon with `patient_list_24px` icon.
   - Remove maintenance crossed-tools icon (`ic_handyman`).
   - Remove unneeded Red Shift toggle switch.
4. **Lab, Station & Archive (`LabHomeScreen.kt`, `ContactSheetScreen.kt`, `ArchiveScreen.kt`)**:
   - Standardize top-bar toggle label from `"Safelight"` to **`"Red Shift"`** to match Settings language (`Red Shift (Night Vision)`).

---

## 1. Technical Specifications

### A. Top-Bar Icon Alignment
- **`HubScreen.kt`**: Replace `science_24px` with `R.drawable.night_sight_max_24px` for the top-bar Red Shift button.
- **`GearLibraryScreen.kt`**: Add `R.drawable.settings_photo_camera_24px` vector icon next to `Camera Gear` title.
- **`AddEditSupplierScreen.kt`**: Replace `R.drawable.warehouse_24px` with `R.drawable.patient_list_24px` next to `Contacts Directory` title, and remove `ic_handyman`.

### B. Red Shift Terminology Standardization & Contacts Removal
- **Standardize Text**: Rename `Text("Safelight")` $\to$ `Text("Red Shift")` in:
  - `LabHomeScreen.kt`
  - `ContactSheetScreen.kt` (Station)
  - `ArchiveScreen.kt`
- **Contacts Directory Cleanup (`AddEditSupplierScreen.kt`)**: Remove Red Shift toggle switch and state from Contacts Directory top bar.

---

## 2. Generated Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.1 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |

---

## 3. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :app:testDebugUnitTest`.
2. Launch Mobile App $\to$ inspect Staging Portal top bar $\to$ verify Red Shift button displays **Night Sight Max** icon (`night_sight_max_24px`)!
3. Open `CAMERA GEAR` $\to$ verify top bar displays `← 📷 Camera Gear` with `settings_photo_camera_24px` icon!
4. Open `CONTACTS DIRECTORY` $\to$ verify top bar displays `← 👥 Contacts Directory` with `patient_list_24px` icon and no Red Shift toggle or crossed tools button!
5. Open `LAB`, `STATION`, and `ARCHIVE` $\to$ verify top-bar toggle label reads **`Red Shift`**!
