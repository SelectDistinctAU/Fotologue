# Android Release Version Bump v1.0.18 (versionCode 19) Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 09:00 AM UTC

---

This plan increments the Android release version in `app/build.gradle.kts` to **`versionCode = 19`** and **`versionName = "1.0.18"`** for Google Play Store release submission and builds the production Android App Bundle (`.aab`).

---

## 1. Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.0 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |

---

## 2. Proposed Code Changes

- **[MODIFY]** `app/build.gradle.kts`
  - Update `versionCode = 19` and `versionName = "1.0.18"`.

---

## 3. Verification Plan
1. Run `./gradlew :app:bundleRelease :app:assembleRelease`.
2. Verify `app-release.aab` exists in `app/build/outputs/bundle/release/`.
3. Upload `app-release.aab` to Google Play Console under **Production / Release Management**.
