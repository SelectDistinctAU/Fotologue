# Chemistry Sub-Category Top-Bar Icon Alignment Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 06:00 PM UTC
- **GitHub Commit & Push**: `1b455dc` & `df8063f` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan aligns the Chemistry detail page top-bar icon with its menu card icon:

1. **Chemistry Icon Alignment (`LabHomeScreen.kt`)**:
   - Updated `headerIconRes` for `"Chemistry"` from `R.drawable.experiment_24px` (Erlenmeyer Flask icon) to `R.drawable.lab_panel_24px` (Test Tube Rack icon).
   - Now, both the Consumables list card for Chemistry and the Chemistry detail page top bar display `R.drawable.lab_panel_24px` with 100% 1-to-1 visual icon parity.

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
2. Launch Mobile App $\to$ `LAB` $\to$ `Consumables` $\to$ tap `Chemistry` $\to$ verify top bar displays Test Tube Rack vector icon (`lab_panel_24px`) matching the menu item icon!
