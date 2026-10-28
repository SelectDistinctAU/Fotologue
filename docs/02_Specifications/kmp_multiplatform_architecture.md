# KMP & Compose Multiplatform Architecture Specification (iOS, Ubuntu Linux, Android & Windows)

This specification defines the multiplatform architecture for **Fotologue** across **iPhones (iOS)**, **Ubuntu Linux (`.deb` / `.AppImage`)**, **Android**, and **Windows Desktop**, enabling **85%+ shared code parity** across all 4 target platforms.

---

## 1. System Target Parity Matrix

| Target Platform | UI Technology | Data Layer & ORM | Hardware & System Adapters | Packaging / Distribution Format |
| :--- | :--- | :--- | :--- | :--- |
| **iPhones (iOS 16.0+)** | Compose Multiplatform + SwiftUI (`iosApp/`) | Room KMP + SQLite | `AVFoundation` (Camera), `CoreLocation` (GPS), `AVAudioPlayer` (Metronome) | Apple TestFlight / App Store (`.ipa`) |
| **Ubuntu Linux (22.04 / 24.04)** | Compose Multiplatform (Desktop JVM / Skiko) | Room KMP / SQLite JDBC | WebCam / Sensor, `javax.sound.sampled` (Metronome), Zenity FileChooser | Native `.deb` (`dpkg -i`) & `.AppImage` |
| **Android (API 26+)** | Jetpack Compose / Compose Multiplatform | Room KMP + SQLite | CameraX, PlayServices Location, ToneGenerator | Google Play Store (`.aab` / `.apk`) |
| **Windows (10/11)** | Compose Multiplatform (Desktop JVM / Skiko) | Room KMP / SQLite JDBC | `javax.sound.sampled` (Metronome), Win32 FileChooser | Native `.msi` & `.exe` Installers |

---

## 2. Hardware Adapter Contracts (`expect` / `actual` Interfaces)

### A. Spot Light Meter Camera (`CameraProvider.kt`)
```kotlin
expect class CameraProvider {
    fun startSpotMetering(onLuminanceCalculated: (ev: Double) -> Unit)
    fun stopSpotMetering()
}
```
- **iOS (`iosMain`)**: Implemented via `AVFoundation` (`AVCaptureSession` + `AVCaptureVideoDataOutput`).
- **Ubuntu Linux (`desktopMain`)**: Implemented via Java OpenCV / WebCam capture stream.
- **Android (`androidMain`)**: Implemented via `CameraX` (`ImageAnalysis` + YUV luminance buffer).

### B. Darkroom Agitation Metronome (`AudioMetronome.kt`)
```kotlin
expect class AudioMetronome {
    fun startAgitationTicks(bpm: Int = 60)
    fun playWarningPip()
    fun stop()
}
```
- **iOS (`iosMain`)**: Implemented via `AVAudioPlayer` / `AudioToolbox` short PCM pips.
- **Ubuntu Linux (`desktopMain`)**: Implemented via `javax.sound.sampled.Clip` (JavaSound audio line).
- **Android (`androidMain`)**: Implemented via `ToneGenerator` / `AudioTrack`.

### C. GPS Location Geotagging (`LocationProvider.kt`)
```kotlin
expect class LocationProvider {
    suspend fun getCurrentLocation(): GeoPoint?
}
```
- **iOS (`iosMain`)**: Implemented via `CoreLocation` (`CLLocationManager`).
- **Ubuntu Linux (`desktopMain`)**: Implemented via manual coordinate input / EXIF geotagging / GeoIP fallback.
- **Android (`androidMain`)**: Implemented via `FusedLocationProviderClient`.

---

## 3. Shared Data Layer & Room KMP Architecture

All Room DAOs (`CameraDao`, `LensDao`, `FilmDao`, `InventoryMasterDao`, `LabOrderDao`, `AssetAvailabilityDao`) and the relational `asset_availability` status engine (`IN_TRANSIT`, `LOANED_OUT`, `IN_REPAIR`, `AVAILABLE`) run directly inside `shared/commonMain`, giving Ubuntu Linux, iOS, Android, and Windows 100% data access parity.

---

## 4. One-Click Build & Distribution Commands

- **Ubuntu Linux DEB Package**: `./gradlew :desktop:packageDeb`
- **Ubuntu Linux AppImage**: `./gradlew :desktop:packageAppImage`
- **Android App Bundle**: `./gradlew :app:bundleRelease`
- **Windows MSI Installer**: `./gradlew :desktop:packageMsi`
- **iOS Unit Tests**: `./gradlew :shared:iosX64Test :shared:iosSimulatorArm64Test`
