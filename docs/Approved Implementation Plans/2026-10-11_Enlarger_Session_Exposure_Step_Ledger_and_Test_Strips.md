# Enlarger Session Exposure Step Ledger & Diagnostic Test Strips Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 04:00 PM UTC
- **GitHub Commit & Push**: `17bc5ef`, `56bec47`, `d306628`, `d142787` & `a1d9085` pushed to `https://github.com/SelectDistinctAU/Fotologue.git`

---

This plan adopts the **v1.3 Final Darkroom Printing Specification** into `EnlargerPrintingSessionScreen.kt` and `core:data`:

1. **Exposure Step Ledger Component (`EnlargerPrintingSessionScreen.kt`)**:
   - Replaced scalar "Base Sec / Grade Filter" row and free-text "Dodge & Burn Map Notes" box with an interactive `ExposureStepLedger` component.
   - Single-Grade vs. Split-Grade toggle: Split-Grade auto-expands to Soft `BASE` (Grade 00) + Hard `BASE` (Grade 5.0) and enforces `grade != null` on every step.
   - `[ ➕ Add Dodge ]` and `[ ➕ Add Burn ]` buttons append compact step rows with `StepType`, `Grade`, `Seconds`, `Region`, `Tool`, and `✕` delete buttons.
   - Preserves Option B legacy free-text notes in a read-only "Previous notes (unstructured)" collapsible card.
2. **Always-Visible Test Strip Diagnostic Fields**:
   - Added `testStripBaseRange`, `testStripSoftRange`, and `testStripHardRange` text fields near the outcome section (visible in both KEEPER and REJECT flows).
3. **Relabels & Advisory Text**:
   - Relabeled `"Enlarger Height (cm)"` $\to$ **`"Lens-to-Paper Distance (cm)"`**.
   - Appended **`"(varies by paper and surface)"`** to dry-down default text.
4. **Standing Architecture Rules**:
   - **RETAINED** `CalculateResizeExposureUseCase.kt`.
   - **DELETED** `ApplyDryDownFactorUseCase.kt` if present.
   - Enforced `PrintSession.negativeFrameId` (`frame_id` FK $\to$ `log_entries.id`) NOT NULL constraint.

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
2. Verify unit test `save_rejected_when_split_grade_null_grade()` passes 100%.
3. Verify `CalculateResizeExposureUseCase` passes 100% of unit tests.
4. Verify database migration `MIGRATION_50_51` executes cleanly.
