# Release Version Increment Mobile v1.0.19 (versionCode 20) & Desktop v1.0.2 Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 11:00 AM UTC

---

This plan increments the release version numbers across Mobile (`versionCode = 20`, `versionName = "1.0.19"`) and Desktop (`packageVersion = "1.0.2"`) in preparation for the upcoming release distribution, and compiles all production release deliverables.

---

## 1. Release Version Increments

| Project Module | Previous Version | New Release Version | Target File |
| :--- | :--- | :--- | :--- |
| **Fotologue Mobile (`app`)** | `versionCode 19` (`1.0.18`) | **`versionCode 20` (`1.0.19`)** | [`app/build.gradle.kts`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build.gradle.kts) |
| **Fotologue Desktop (`desktop`)** | `packageVersion "1.0.1"` | **`packageVersion "1.0.2"`** | [`desktop/build.gradle.kts`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build.gradle.kts) |

---

## 2. Generated Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.0 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |
| **Windows MSI Installer** | `FotologueDesktop-1.0.2.msi` | **153.3 MB** | [`desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.2.msi`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.2.msi) |
| **Windows EXE Installer** | `FotologueDesktop-1.0.2.exe` | **154.0 MB** | [`desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.2.exe`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.2.exe) |

---

## 3. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :desktop:packageWindows`.
2. Verify all release deliverables exist and compile cleanly.
3. Run `./gradlew :app:testDebugUnitTest`.
