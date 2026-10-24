# Darkroom Rhythmic Agitation Metronome & Acoustic/Haptic Guidance Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 09:00 PM UTC
- **GitHub Commit & Push**: `33760ad` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan implements a low-latency **Acoustic & Haptic Agitation Metronome** in `FilmProcessingSessionScreen.kt` to help darkroom photographers maintain a perfectly consistent inversion tempo by ear and feel without looking at the phone screen:

1. **Acoustic & Haptic Metronome Engine (`FilmProcessingSessionScreen.kt`)**:
   - Integrated Android native `ToneGenerator(AudioManager.STREAM_MUSIC, 80)` for zero-latency acoustic ticks (*TONE_PROP_BEEP*) and `Vibrator` for synchronized tactile pulses.
2. **Pre-Agitation Warning Pips**:
   - 3 seconds before an agitation interval begins (e.g. at 27s of a 30s rest cycle), plays 3 warning pips (*Pip-Pip-Pip* via `TONE_CDMA_PIP`) so the photographer can grab the processing tank in total darkness.
3. **Inversion Tempo Guidance**:
   - During active agitation windows (e.g. 5 seconds every 30 seconds), ticks at a steady 60 BPM rhythm (1 tick per second = 2 ticks per full up-and-down tank inversion).
4. **Metronome Audio Controls**:
   - Added `[ 🎵 Metronome Beats ]` and `[ 📳 Haptic Pulse ]` toggle chips to `FilmProcessingSessionScreen.kt` with safelight-safe red controls.

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
2. Launch Mobile App $\to$ `LAB` $\to$ `Develop Roll`:
   - Toggle `[ 🎵 Metronome Beats: ON ]` $\to$ start 30s timer.
   - At 27s, verify 3 pre-warning pips sound (*Pip-Pip-Pip*)!
   - At 0s agitation interval, verify steady 1-second metronome ticks and vibration pulses guide tank inversion tempo!
