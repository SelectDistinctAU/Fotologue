# Production Release v1.0.20 (versionCode 21) & Desktop v1.0.3 Plan

- **Approval Status**: Approved & Executed
- **Approval Date & Time**: October 11, 2026, 02:00 PM UTC
- **GitHub Commit & Push**: `6487dd7` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

## 1. Release Version Increments

| Project Module | Previous Version | New Release Version | Target File |
| :--- | :--- | :--- | :--- |
| **Fotologue Mobile (`app`)** | `versionCode 20` (`1.0.19`) | **`versionCode 21` (`1.0.20`)** | [`app/build.gradle.kts`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build.gradle.kts) |
| **Fotologue Desktop (`desktop`)** | `packageVersion "1.0.2"` | **`packageVersion "1.0.3"`** | [`desktop/build.gradle.kts`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build.gradle.kts) |

---

## 2. Generated Production Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.2 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.2 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |
| **Windows MSI Installer** | `FotologueDesktop-1.0.3.msi` | **153.3 MB** | [`desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.3.msi`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.3.msi) |
| **Windows EXE Installer** | `FotologueDesktop-1.0.3.exe` | **154.0 MB** | [`desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.3.exe`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.3.exe) |

---

## 3. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease :desktop:packageWindows`.
2. Verify all 4 release deliverables exist and compile cleanly.
3. Push all commits to `https://github.com/SelectDistinctAU/Fotologue.git`.
