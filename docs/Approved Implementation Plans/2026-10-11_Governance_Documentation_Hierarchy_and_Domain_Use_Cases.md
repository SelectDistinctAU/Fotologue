# Fotologue Governance, Documentation Hierarchy & Domain Use Cases Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 12:30 PM UTC

---

This plan adopts key architectural best practices from Distinct Routes into **Fotologue**:
1. **Categorized Documentation Hierarchy**: Re-organized 20+ loose `docs/*.md` files into structured ISO-style subdirectories (`docs/01_Governance/`, `docs/02_Specifications/`, `docs/06_Feature_Log/`, `docs/07_Public_Guides/`).
2. **Open-Source Governance & Community Files**: Added `CHANGELOG.md`, `SECURITY.md`, and `SUPPORT.md` to the repository root.
3. **Domain Layer Use Cases**: Extracted complex color science (`CalculateZoneExposureUseCase`) and wet darkroom chemical timing (`ProcessDarkroomSessionUseCase`) into explicit Domain Use Cases in `core:data`.

---

## 1. Technical Specifications

### A. Categorized Documentation Folder Hierarchy (`docs/`)
Re-organized loose documentation files in `docs/` into 4 clean, structured subdirectories:

```
docs/
├── 01_Governance/
│   ├── architectural_governance_and_best_practice_departures.md
│   └── autonomous_qa_and_verification_protocol.md
├── 02_Specifications/
│   ├── 3_pillar_custody_and_location_architecture.md
│   ├── design_system.md
│   ├── master_preset_json_schema.md
│   ├── maintainer_preset_workflow_architecture.md
│   └── v2_architecture_options.md
├── 06_Feature_Log/
│   ├── bug_registry_and_regression_checklist.md
│   ├── v3_roadmap_film_inventory.md
│   └── v4_desktop_digikam_and_dxo_integration.md
└── 07_Public_Guides/
    ├── dp_and_digital_photographers_guide.md
    ├── user_guide.md
    └── user_journeys_walkthrough.md
```

### B. Open-Source Governance Files (Repository Root)
- **`CHANGELOG.md`**: Record release history from v1.0.0 through v1.0.19 (Mobile) and v1.0.2 (Desktop).
- **`SECURITY.md`**: Security vulnerability reporting policy for `SelectDistinctAU/Fotologue`.
- **`SUPPORT.md`**: Support channels, GitHub Issues link, and troubleshooting resources.

### C. Domain Layer Use Cases (`core:data`)
- **`CalculateZoneExposureUseCase.kt`**: Encapsulates Ansel Adams 10-Zone mapping, scene contrast calculation, and $N \pm 1$ push/pull development recommendations.
- **`ProcessDarkroomSessionUseCase.kt`**: Encapsulates darkroom film chemical timing, agitation interval countdowns, and enlarger print recipe outcome logging.

---

## 2. Generated Release Deliverables

| Deliverable Type | File Name | File Size | Absolute File Location |
| :--- | :--- | :--- | :--- |
| **Android App Bundle (AAB)** | `app-release.aab` | **27.1 MB** | [`app/build/outputs/bundle/release/app-release.aab`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/bundle/release/app-release.aab) |
| **Android Release APK** | `app-release.apk` | **42.1 MB** | [`app/build/outputs/apk/release/app-release.apk`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/app/build/outputs/apk/release/app-release.apk) |
| **Windows MSI Installer** | `FotologueDesktop-1.0.2.msi` | **153.3 MB** | [`desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.2.msi`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/msi/FotologueDesktop-1.0.2.msi) |
| **Windows EXE Installer** | `FotologueDesktop-1.0.2.exe` | **154.0 MB** | [`desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.2.exe`](file:///C:/Users/geewy/AndroidStudioProjects/Fotologue/desktop/build/compose/binaries/main/exe/FotologueDesktop-1.0.2.exe) |

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Inspect `docs/` directory structure $\to$ verify all files are organized into structured subdirectories!
3. Inspect `CHANGELOG.md`, `SECURITY.md`, `SUPPORT.md` $\to$ verify open-source governance files are complete!
4. Verify `CalculateZoneExposureUseCase` and `ProcessDarkroomSessionUseCase` pass unit tests 100%!
