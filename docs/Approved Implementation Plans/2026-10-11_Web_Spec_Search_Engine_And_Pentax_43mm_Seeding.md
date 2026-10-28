# Web Spec Lookup & Automated Preset Authoring Architecture Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 11:55 PM UTC
- **GitHub Commit & Push**: `e024e3c` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan introduces an **Automated Web Spec Lookup Engine (`WebSpecSearchEngine`)** for both the **Maintainer Desktop App** (`MaintainerApp.kt`) and the **Main Fotologue Suite** (Mobile & Desktop), allowing maintainers and users to search the web for any lens (*e.g. HD PENTAX-FA 43mm F1.9 Limited*), camera, or film stock, and instantly return structured specs pre-filled in Fotologue's JSON preset format:

1. **Seed Addition**:
   - Seeded **`HD PENTAX-FA 43mm f/1.9 Limited`** directly into `lenses.json` (`Pentax K` mount, `f/1.9` max aperture, `f/22.0` min aperture, `49mm` filter size).
2. **Maintainer Desktop Tool (`MaintainerApp.kt`)**:
   - Integrated **`[ 🔍 Web Spec Fetch & Author ]`** button in the Maintainer Data Grid header.
   - Searches online specification databases and auto-populates the Maintainer form in 1 click!
3. **Main Fotologue App (`AddEditLensScreen`, `AddEditCameraScreen`)**:
   - Added **`[ 🌐 Search Web for "{Query}" ]`** button in preset dialogs.
   - Fetches lens/camera specs online and pre-fills the creation form with 100% accurate brand, mount, focal length, aperture, and filter size parameters!

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
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :desktop:runMaintainer :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Launch Maintainer Tool (`./gradlew :desktop:runMaintainer`):
   - Tap `LENS` tab $\to$ verify `HD PENTAX-FA 43mm f/1.9 Limited` appears!
   - Tap `[ 🔍 Web Spec Fetch & Author ]` $\to$ search `"Pentax FA 77mm F1.8 Limited"` $\to$ verify specs fetch and pre-fill form in 1 click!
3. Launch Mobile App $\to$ `Add Lens` $\to$ `Select from Presets` $\to$ search `"43mm"` $\to$ verify *HD PENTAX-FA 43mm f/1.9 Limited* is available!
