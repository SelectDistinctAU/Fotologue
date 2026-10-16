# Documentation Comprehensive Review & Modular Architecture Alignment Plan

- **Approval Status**: Approved
- **Approval Date & Time**: October 11, 2026, 07:00 AM UTC

---

This plan updates `README.md` and core documentation (`docs/user_guide.md`, `docs/training_manual.md`) to reflect the **current modular architecture** of Fotologue, replacing the rigid linear sequence description with the true **Mobile Staging Portal (7 Operational Modules)** and **Desktop Command Center (4 Processing Stages)**.

---

## 1. Architectural Realignment: Modular Portal vs Linear Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     FOTOLOGUE APPLICATION HUB                           │
├─────────────────────────────────────────────────────────────────────────┤
│  MOBILE STAGING PORTAL (7 OPERATIONAL MODULES)                          │
│  [1. Camera Gear]  [2. Taxonomy & Tags]  [3. Field Exposure & Metering]  │
│  [4. Contacts Directory]  [5. Lab Processing]  [6. Station]  [7. Archive] │
├─────────────────────────────────────────────────────────────────────────┤
│  DESKTOP COMMAND CENTER (4 WORKFLOW STAGES)                             │
│  [Stage 1: Ingestion] ➔ [Stage 2: Master QA] ➔ [Stage 3: Image Tagging] │
│  [Stage 4: Reference Data Administration & Data Grid]                    │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Documentation Updates:
1. **Refactor "Division of Labour" in `README.md`**:
   - Re-frame from a rigid linear sequence to a **Modular Operational Architecture**:
     - **Mobile Staging Portal**: 7 specialized modules (`CAMERA GEAR`, `TAXONOMY & TAGS`, `FIELD`, `CONTACTS DIRECTORY`, `LAB`, `STATION`, `ARCHIVE`).
     - **Desktop Command Center**: 4 processing stages (`Stage 1: Ingestion & Vault Inflow`, `Stage 2: Master QA & Detail Workbench`, `Stage 3: Image Tagging`, `Stage 4: Reference Data Administration`).
2. **Incorporate All Advanced Photographically Evocative Capabilities**:
   - **Zone System & Metering**: Ansel Adams 10-Zone System spot metering, dynamic range contrast calculator, $N \pm 1$ push/pull recommendations, real-time 11-Zone histogram, and red/blue zebra clipping overlays.
   - **Lab & Darkroom**: Agitation countdown timer, auto-recommended development times, enlarger print recipe logger (✓ KEEPER / ✕ REJECT, darkroom defects, multi-select learning tags), and `DarkroomLightGuard` sensor monitoring.
   - **3-Pillar Custody Ledger**: Asset + Contact + Location tracking with native Android contact picker and pre-arrival order tracking.
   - **Theme Engine & ISO Canvas**: Live 1-tap theme palette switcher (*System Default*, *Daylight Clean*, *Flat Darcula*, *Flat Mac Dark*, *Flat Nord*, *Silver Gelatin*, *Warm Sepia*, *Red Shift*) and ISO 3664 Photo Review Canvas (*#505050* ISO Mid-Gray).
   - **Presets & Cloud Sync**: 82 Cameras, 26 Lenses, 30 Chemistry/Paper, 37 Lab Equipment presets with 1-tap GitHub Cloud Refresh.
3. **Align `docs/user_guide.md` & `docs/training_manual.md`**:
   - Update user guide and training manual sections to match the current app modules, screens, and features.

---

## 2. Proposed Code Changes

- **[MODIFY]** `README.md`
  - Overhaul architecture, module descriptions, feature lists, and maintainer tooling.
- **[MODIFY]** `docs/user_guide.md`
  - Update user guide workflow and module breakdown.
- **[MODIFY]** `docs/training_manual.md`
  - Update training manual to reflect the 7 Mobile Modules and 4 Desktop Stages.

---

## 3. Verification Plan
1. Run `./gradlew :core:data:assembleMasterCatalog :desktop:compileKotlin :app:assembleDebug :app:testDebugUnitTest :feature:lab:testDebugUnitTest`.
2. Inspect updated `README.md`, `docs/user_guide.md`, and `docs/training_manual.md` for 100% accuracy against the live app codebase!
