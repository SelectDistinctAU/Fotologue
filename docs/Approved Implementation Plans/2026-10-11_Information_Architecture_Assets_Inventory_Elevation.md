# Information Architecture Elevation: Dedicated `ASSETS & INVENTORY` & `FOTOLOGUE FIELD` Portal Module Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 11:30 PM UTC
- **GitHub Commit & Push**: `f3f85c8` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan refactors Fotologue's Information Architecture by elevating physical inventory and order management out of `LAB` into a dedicated **`ASSETS & INVENTORY`** Staging Portal Module, highlighting **`FOTOLOGUE FIELD`** as the primary Call-To-Action (CTA) for in-situ exposure logging, and streamlining **`LAB`** to focus 100% on wet darkroom processing and enlarger printing:

1. **Staging Portal High-Impact CTA (`FOTOLOGUE FIELD`)**:
   - Promoted main menu card title on `HubScreen.kt` from generic `FIELD` to **`FOTOLOGUE FIELD`**, making it instantly pop out as the primary call-to-action for photographers demonstrating the app on the street.
2. **Dedicated Module: `ASSETS & INVENTORY`**:
   - Elevated physical inventory and order tracking out of `LAB` into a dedicated Staging Portal Module (Module #2) with `inventory_2_24px` M3 vector icon.
   - Houses `Consumables`, `Storage Locations`, `Pre-Arrival Orders`, and `Custody & Loans`.
3. **Streamlined Module: `LAB`**:
   - Streamlined `LAB` module to focus 100% on wet darkroom processing (`Develop Roll`), enlarger printing (`Enlarger Session`), and darkroom equipment & chemical formulas.

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
   - Open Staging Portal $\to$ verify **`FOTOLOGUE FIELD`** card title renders prominently as Module #3!
   - Verify **`ASSETS & INVENTORY`** module card appears with `inventory_2_24px` M3 vector icon!
   - Tap `ASSETS & INVENTORY` $\to$ verify Consumables, Storage Locations, Pre-Arrival Orders, and Custody & Loans render in their dedicated home!
   - Tap `LAB` $\to$ verify `LAB` is streamlined to pure darkroom processing and printing execution!
