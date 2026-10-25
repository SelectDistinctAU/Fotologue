# Central Asset Availability Table & Inbound Register Isolation Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 11:00 PM UTC
- **GitHub Commit & Push**: `5b64381` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan consolidates asset availability across all modules into a **Single Central Availability Table (`asset_availability`)**, providing a 100% indexed, high-performance single source of truth for Pre-Arrival Orders (`IN_TRANSIT`), Custody Loans (`LOANED_OUT`), Maintenance (`IN_REPAIR`), and Disposals (`SOLD`):

1. **Central `asset_availability` Table Schema**:
   - Created `asset_availability` table with Room migration `MIGRATION_52_53`, storing indexed `status` (`AVAILABLE`, `IN_TRANSIT`, `LOANED_OUT`, `IN_REPAIR`, `SOLD`) and `statusReason` per asset.
2. **Inbound Orders Register Isolation (`order_history` Table)**:
   - Selecting or saving a pre-ordered asset in `isPreArrivalMode = true` inserts an `OrderHistory` record directly into the **Pre-Arrival Orders Register (`order_history`)** with status **`IN_TRANSIT`**, leaving active `Camera Gear` lists 100% clean and un-polluted until delivery.
3. **1-Click Delivery Activation**:
   - Marking an order `DELIVERED` flips `asset_availability.status = 'AVAILABLE'`, making the camera, lens, or film instantly available for active shooting in `FIELD`!
4. **Full-Screen Storage Locations**:
   - Converted `StorageLocationsScreen.kt` from an `AlertDialog` modal popup into a clean full-screen form (`← Add Storage Location`).

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
2. Launch Mobile App on `emulator-5554`:
   - Log pre-arrival *Canon EOS 3* $\to$ verify `asset_availability` status is `IN_TRANSIT`.
   - Verify `Camera Gear` displays `🚚 In Transit (Pre-Arrival Order)` badge and `FIELD` hides it from active shooting selectors.
   - Tap `[ Mark Delivered ]` $\to$ verify `asset_availability` flips to `AVAILABLE` and camera becomes available in `FIELD`!
