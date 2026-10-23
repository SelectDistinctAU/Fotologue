# Pre-Arrival Orders Master Preset & Auto-Receive Inventory Integration Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 07:00 PM UTC
- **GitHub Commit & Push**: `a7af075` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan connects Pre-Arrival Asset Orders directly to the Master Preset Catalog and auto-generates Lab Inventory Stock Lots upon order delivery:

1. **Preset Catalog Asset Selection in Pre-Arrival Orders (`PreArrivalOrdersScreen.kt`)**:
   - Step 2 (*Asset Specifications*) includes a **`[ ⚙️ Select Asset from Presets / Catalog ]`** action button.
   - Opening the selector allows the photographer to pick any Camera, Lens, Film Stock, Paper Stock, Chemical, or Equipment preset from `MasterPresetEngine`, pre-filling Asset Name, Category, Brand/Manufacturer, and Specs automatically with zero string drift!
2. **1-Click Auto-Receive Inventory Creation upon Delivery**:
   - When a Pre-Arrival Order status is changed to **`DELIVERED`** (or `[ 📦 Activate Asset ]` is tapped), Fotologue automatically:
     a) Creates/resolves the corresponding `InventoryMaster` record in Lab Inventory.
     b) Creates a new `StockLot` record with status **`ON_HAND`** assigned to the selected Target Storage Location (*Dry Cabinet*, *Studio Shelf*, *Paper Safe*).
3. **Double Plus Icon Removal in Preset Selection Views (`AddEditFilmScreen.kt`)**:
   - Removed `➕` emoji from `Text("Add Custom / Unlisted Film Stock")` for single Material plus icon consistency.

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
2. Launch Mobile App $\to$ `LAB` $\to$ `Pre-Arrival Orders`:
   - Tap `[ Log Pre-Arrival Asset Order ]` $\to$ tap `[ ⚙️ Select Asset from Presets ]` $\to$ select *Kodak Portra 400* $\to$ verify Asset Name, Category, and Specs populate cleanly!
   - Activate asset $\to$ verify item is created in Lab Inventory with 1 stock lot ON HAND in target storage location!
