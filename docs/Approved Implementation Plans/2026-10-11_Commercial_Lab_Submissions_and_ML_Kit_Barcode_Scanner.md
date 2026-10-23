# Commercial Lab Submissions & ML Kit CameraX Barcode Scanner Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 05:00 PM UTC
- **GitHub Commit & Push**: `342d447` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan introduces Commercial Lab Order Submissions (bypassing home chemistry processing) and integrated CameraX + ML Kit Barcode/QR Code Scanning:

1. **Commercial Lab Order Submissions (`CommercialLabOrderScreen.kt` & `LabOrder` Entity)**:
   - Reuses **Contacts Directory / Lab Suppliers** for lab choice and **Custody Ledger** for order tracking.
   - Captures Service Type (`DEV_ONLY`, `DEV_AND_SCAN`, `PRINT_ONLY`, `DEV_SCAN_PRINT`), Push/Pull ISO instructions (*e.g., Push +1 Stop*), Tracking Numbers, Lab Notes, and Cost.
   - Automatically marks Film Roll custody as **`IN_PROCESSING (Commercial Lab)`**.
2. **CameraX + ML Kit Barcode & QR Code Scanner (`BarcodeScannerDialog.kt`)**:
   - Uses rear camera for instant 1D/2D barcode and QR code scanning via Google ML Kit.
   - EAN-13/UPC film box barcode scanning & gear asset tag QR code lookup.
3. **Lab Titles & Unique Icon Alignment**:
   - Streamlined sub-level section titles (removed `"Lab • "` prefixes).
   - Mapped unique vector icons: `delivery_truck_speed_24px` (Pre-Arrivals), `shelves_24px` (Storage), `ic_call_log` (Custody), `inventory_24px` (Consumables), `camera_indoor_24px` (Lab Equipment).
   - Wrapped Pre-Arrival Orders status filter chips in `FlowRow`.

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
2. Launch Mobile App $\to$ open `LAB` $\to$ tap `[ 🚚 Commercial Lab Submission ]` $\to$ submit order to *Rewind Photo Lab* $\to$ verify order logs cleanly!
3. Tap `[ 📷 Scan Barcode / Asset Tag ]` $\to$ verify CameraX ML Kit barcode scanner launches cleanly.
4. Verify database migration `MIGRATION_51_52` executes cleanly and all unit tests pass 100%!
